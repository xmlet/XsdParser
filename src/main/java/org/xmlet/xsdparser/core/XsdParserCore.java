package org.xmlet.xsdparser.core;

import java.io.IOException;
import java.io.File;
import java.util.function.Function;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.utils.*;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParentAvailableException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public abstract class XsdParserCore {

    /**
     * A {@link Map} object that contains a parse function to each {@link XsdAbstractElement} concrete
     * type supported by this mapper, this way based on the concrete {@link XsdAbstractElement} tag the according parse
     * method can be invoked.
     */
    static Map<String, ConfigEntryData> parseMappers;

    /**
     * A {@link Map} object that contains the all the XSD types and their respective types in the Java
     * language.
     */
    private static Map<String, String> xsdTypesToJava;

    /**
     * A {@link LinkedHashMap} which contains all the top elements parsed by this class.
     */
    public Map<String, List<ReferenceBase>> parseElements = new LinkedHashMap<>();

    /**
     * A {@link LinkedHashMap} of {@link UnsolvedReference} elements that weren't solved. This list is consulted after all the
     * elements are parsed in order to find if there is any suitable parsed element to replace the unsolved element.
     */
    private Map<String, List<UnsolvedReference>> unsolvedElements = new LinkedHashMap<>();

    /**
     * A {@link List} containing all the elements that even after parsing all the elements on the file, don't have a
     * suitable object to replace the reference. This list can be consulted after the parsing process to assert if there
     * is any missing information in the XSD file.
     */
    private List<UnsolvedReferenceItem> parserUnsolvedElementsMap = new ArrayList<>();

    /**
     * A {@link List} containing the paths of files that were present in either {@link XsdInclude}, {@link XsdImport},
     * or {@link XsdRedefine} objects that are present in the original or subsequent files. These paths are stored to
     * be parsed as well, the parsing process only ends when all the files present in this {@link List} are parsed.
     */
    List<String> schemaLocations = new ArrayList<>();
    Map<String, String> schemaLocationsMap = new HashMap<>();

    protected String currentFile;

    static {
        DefaultParserConfig config = new DefaultParserConfig();

        parseMappers = config.getParseMappers();
        xsdTypesToJava = config.getXsdTypesToJava();
    }

    /**
     * Verifies if a given {@link Node} object, i.e. {@code node} is a xsd:schema node.
     *
     * @param node The node to verify.
     * @return True if the node is a xsd:schema or xs:schema. False otherwise.
     */
    boolean isXsdSchema(Node node) {
        String schemaNodeName = node.getNodeName();

        return schemaNodeName.equals(XsdSchema.XSD_TAG) || schemaNodeName.equals(XsdSchema.XS_TAG) || schemaNodeName.equals(XsdSchema.TAG) || schemaNodeName.contains(XsdSchema.TAG);
    }

    /**
     * This method resolves all the remaining {@link UnsolvedReference} objects present after all the elements are parsed.
     * It starts by iterating all {@link XsdParser#parseElements} and inserting all the parsed elements with a name
     * attribute in the concreteElementsMap variable. After that it iterates on the {@link XsdParser#unsolvedElements}
     * list in order to find if any of the unsolvedReferences can be solved by replacing the unsolvedElement by its
     * matching {@link NamedConcreteElement} object, present in the concreteElementsMap. The {@link UnsolvedReference}
     * objects matches a {@link NamedConcreteElement} object by having its ref attribute with the same value as the
     * name attribute of the {@link NamedConcreteElement}.
     */
    void resolveRefs() {
        resolveInnerRefs();
        resolveOtherNamespaceRefs();
        resolveUnion();
    }

    private void resolveUnion() {
        for (List<ReferenceBase> parsedElements : parseElements.values()) {
            parsedElements.stream()
                    .map(ReferenceBase::getElement)
                    .filter(XsdSimpleType.class::isInstance)
                    .map(el -> (XsdSimpleType) el)
                    .map(XsdSimpleType::getUnion)
                    .filter(Objects::nonNull)
                    .forEach(this::resolveMemberTypes);
        }
    }

    private void resolveMemberTypes(XsdUnion union) {
        if (union != null) {
            List<String> originalMemberTypes = union.getMemberTypesList();
            for (String memberType : originalMemberTypes) {
                XsdSchema schema = union.getXsdSchema();
                String ref = null;
                String[] split = memberType.split(":");
                int length = split.length;
                if (length == 1) {
                    ref = split[0];
                }
                if (length == 2 && schema != null) {
                    NamespaceInfo nsInfo = schema.getNamespaces().get(split[0]);
                    if (nsInfo != null) {
                        ref = split[1];
                        schema = getSchema(nsInfo.getFile());
                    } else {
                        schema = null;
                    }
                }
                if (ref != null) {
                    XsdAbstractElement element = findElement(schema, ref);
                    if (element instanceof XsdSimpleType) {
                        union.add((XsdSimpleType) element);
                    }
                }
            }
        }
    }

    /**
     * Returns the first matching {@link XsdAbstractElement} from the {@link XsdSchema} and all {@link XsdInclude}'s
     * and {@link XsdRedefine}'s with the given name. Redefines take priority over includes.
     *
     * @param schema The initial {@link XsdSchema} to look up the file.
     * @param name   The name of the element which is searched.
     * @return The first {@link XsdAbstractElement} with the given name or null if no element was found.
     */
    private XsdAbstractElement findElement(XsdSchema schema, String name) {
        if (schema == null || name == null) {
            return null;
        }

        // First check local schema definitions
        XsdAbstractElement element = getElementFromSchema(schema, name);
        if (element != null) {
            return element;
        }

        // Then check redefines (they have priority over includes)
        element = findElementInRedefines(schema, name);
        if (element != null) {
            return element;
        }

        // Finally check includes
        List<XsdInclude> includesFromSchema =
                schema.getChildrenIncludes().collect(Collectors.toList());
        Set<XsdInclude> visitedIncludes = new HashSet<>();
        while (!includesFromSchema.isEmpty()) {
            final XsdInclude topSchemaInclude = includesFromSchema.remove(0);
            XsdSchema resolvedSchema = getSchema(getSchemaLocation(topSchemaInclude));
            if (resolvedSchema != null && visitedIncludes.add(topSchemaInclude)) {
                element = getElementFromSchema(resolvedSchema, name);
                if (element == null) {
                    resolvedSchema.getChildrenIncludes().forEach(inc -> {
                        if (!visitedIncludes.add(inc)) {
                            includesFromSchema.add(0, inc);
                        }
                    });
                } else {
                    includesFromSchema.clear();
                }
            }
        }
        return element;
    }

    /**
     * Finds an element in redefine declarations. Redefines contain both the redefined type
     * and reference to the original schema, so redefined types take priority.
     *
     * @param schema The schema to search for redefines
     * @param name   The name of the element to find
     * @return The redefined element or null if not found
     */
    private XsdAbstractElement findElementInRedefines(XsdSchema schema, String name) {
        if (schema == null || name == null) {
            return null;
        }

        List<XsdRedefine> redefinesFromSchema = schema.getChildrenRedefines().collect(Collectors.toList());

        for (XsdRedefine redefine : redefinesFromSchema) {
            // First check if the element is redefined locally in the redefine element itself
            XsdAbstractElement redefinedElement = redefine.getXsdElements()
                    .filter(elem -> {
                        if (elem instanceof XsdNamedElements) {
                            String elementName = ((XsdNamedElements) elem).getName();
                            return name.equals(elementName);
                        }
                        return false;
                    })
                    .findFirst()
                    .orElse(null);

            if (redefinedElement != null) {
                return redefinedElement.clone(redefinedElement.getAttributesMap(), redefinedElement.getParent());
            }

            // If not in the redefine's local definitions, check the redefined schema
            XsdSchema redefinedSchema = getSchema(redefine.getSchemaLocation());
            if (redefinedSchema != null) {
                XsdAbstractElement element = getElementFromSchema(redefinedSchema, name);
                if (element != null) {
                    return element;
                }
            }
        }

        return null;
    }

    private XsdAbstractElement getElementFromSchema(XsdSchema schema, String name) {
        if (name == null || schema == null) {
            return null;
        }

        XsdAbstractElement foundElement = schema
                .getXsdElements()
                .filter(
                        st -> {
                            Map<String, String> attributes = st.getAttributesMap();
                            String nameAttributeValue = attributes.get(XsdAbstractElement.NAME_TAG);
                            return name.equals(nameAttributeValue);
                        })
                .findFirst()
                .orElse(null);

        if (foundElement != null) {
            foundElement = foundElement.clone(foundElement.getAttributesMap(), foundElement.getParent());
        }

        return foundElement;
    }

    /**
     * Get the SchemaLocation of an {@link XsdImport}, {@link XsdInclude}, or {@link XsdRedefine}.
     *
     * @param importOrIncludeOrRedefine A XsdAbstractElement to get the SchemaLocation.
     * @return the SchemaLocation or null
     */
    private static String getSchemaLocation(XsdAbstractElement importOrIncludeOrRedefine) {
        String schemaLocation = null;
        if (importOrIncludeOrRedefine instanceof XsdInclude) {
            XsdInclude include = (XsdInclude) importOrIncludeOrRedefine;
            schemaLocation = include.getSchemaLocation();
        }
        if (importOrIncludeOrRedefine instanceof XsdImport) {
            XsdImport xsdImport = (XsdImport) importOrIncludeOrRedefine;
            schemaLocation = xsdImport.getSchemaLocation();
        }
        if (importOrIncludeOrRedefine instanceof XsdRedefine) {
            XsdRedefine redefine = (XsdRedefine) importOrIncludeOrRedefine;
            schemaLocation = redefine.getSchemaLocation();
        }
        return schemaLocation;
    }

    private XsdSchema getSchema(String fileName) {
        return getResultXsdSchemas()
                .collect(Collectors.toMap(XsdSchema::getFilePath, Function.identity()))
                .entrySet()
                .stream()
                .filter(stringXsdSchemaEntry -> stringXsdSchemaEntry.getKey().endsWith(fileName))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private void resolveOtherNamespaceRefs() {
        List<XsdSchema> schemas = getResultXsdSchemas().collect(Collectors.toList());

        for (XsdSchema schema : schemas) {
            List<XsdImport> imports = schema.getChildrenImports().collect(Collectors.toList());

            for (XsdImport xsdImport : imports) {
                schema.resolveNameSpace(xsdImport.getNamespace(), xsdImport.getSchemaLocation());
            }
            Map<String, NamespaceInfo> ns = schema.getNamespaces();

            for (Entry<String, NamespaceInfo> e : ns.entrySet()) {
                schema.resolveNameSpace(e.getKey(), schema.getFilePath().substring(schema.getFilePath().lastIndexOf("/") + 1));
            }
        }

        parseElements
                .keySet()
                .forEach(fileName -> {
                    currentFile = fileName;
                    XsdSchema xsdSchema =
                            parseElements.get(fileName)
                                    .stream()
                                    .filter(referenceBase -> referenceBase instanceof ConcreteElement && referenceBase.getElement() instanceof XsdSchema)
                                    .map(referenceBase -> (XsdSchema) referenceBase.getElement())
                                    .findFirst()
                                    .get();

                    Map<String, NamespaceInfo> ns = xsdSchema.getNamespaces();

                    List<UnsolvedReference> unsolvedReferenceList = unsolvedElements
                            .getOrDefault(fileName, new ArrayList<>())
                            .stream()
                            .filter(unsolvedElement -> unsolvedElement.getRef().contains(":"))
                            .collect(Collectors.toList());

                    boolean replacedAtLeastOne;
                    do {
                        replacedAtLeastOne = false;
                        for (UnsolvedReference unsolvedReference : unsolvedReferenceList) {
                            String unsolvedElementNamespace = unsolvedReference.getRef().substring(0, unsolvedReference.getRef().indexOf(":"));

                            Optional<String> foundNamespaceId = ns.keySet().stream().filter(namespaceId -> namespaceId.equals(unsolvedElementNamespace)).findFirst();

                            if (foundNamespaceId.isPresent()) {
                                NamespaceInfo namespaceInfo = ns.get(foundNamespaceId.get());
                                List<ReferenceBase> importedElements;
                                XsdSchema unsolvedElementSchema = unsolvedReference.getElement().getXsdSchema();

                                if (unsolvedElementSchema == null) {
                                    unsolvedElementSchema = xsdSchema;
                                }

                                if (unsolvedElementSchema != null && unsolvedElementSchema.getTargetNamespace() != null && unsolvedElementSchema.getTargetNamespace().equals(namespaceInfo.getName())) {
                                    importedElements = unsolvedElementSchema.getElements();
                                } else {
                                    importedElements = findElementTree(ns, foundNamespaceId.get(), fileName);
                                }

                                Map<String, List<NamedConcreteElement>> concreteElementsMap =
                                        importedElements.stream()
                                                .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                                                .map(concreteElement -> (NamedConcreteElement) concreteElement)
                                                .collect(groupingBy(NamedConcreteElement::getName));

                                replacedAtLeastOne |= replaceUnsolvedImportedReference(concreteElementsMap, unsolvedReference, fileName);
                            }
                        }

                        unsolvedReferenceList = unsolvedElements
                                .getOrDefault(fileName, new ArrayList<>())
                                .stream()
                                .filter(unsolvedElement -> unsolvedElement.getRef().contains(":"))
                                .collect(Collectors.toList());

                    } while (replacedAtLeastOne);
                });
    }

    private List<ReferenceBase> findElementTree(Map<String, NamespaceInfo> ns, String namespaceId, String fileName) {
        List<ReferenceBase> importedElements = new ArrayList<>();
        String importedFileLocation = ns.get(namespaceId).getFile();

        String importedFileName = importedFileLocation;

        if (isRelativePath(importedFileName)) {
            String parentFile = schemaLocationsMap.get(importedFileName);

            if (parentFile == null) {
                parentFile = fileName;
            }

            importedFileName = parentFile.substring(0, parentFile.lastIndexOf('/') + 1).concat(importedFileName);
        }

        String finalImportedFileName = importedFileName;
        importedElements.addAll(parseElements.getOrDefault(importedFileLocation,
                parseElements.get(parseElements.keySet()
                        .stream()
                        .filter(k -> cleanPath(k).endsWith(cleanPath(finalImportedFileName)))
                        .findFirst()
                        .orElse(null))));

        // Process both includes and redefines
        List<XsdAbstractElement> schemaIncludesAndRedefines = importedElements.stream()
                .filter(referenceBase -> referenceBase instanceof ConcreteElement &&
                        (referenceBase.getElement() instanceof XsdInclude ||
                         referenceBase.getElement() instanceof XsdRedefine))
                .map(referenceBase -> referenceBase.getElement())
                .collect(Collectors.toList());
        List<String> includedSchemaLocations = new ArrayList<>();

        while (!schemaIncludesAndRedefines.isEmpty()){
            XsdAbstractElement includeOrRedefine = schemaIncludesAndRedefines.remove(0);
            String schemaLocation = getSchemaLocation(includeOrRedefine);
            XsdSchema xsdSchema = getSchema(schemaLocation);
            includedSchemaLocations.add(schemaLocation);

            if (xsdSchema != null) {
                if (includeOrRedefine instanceof XsdRedefine) {
                    // For redefines, add both the redefined types and the base schema elements
                    XsdRedefine redefine = (XsdRedefine) includeOrRedefine;
                    // Add redefined elements first (they take priority)
                    redefine.getXsdElements().forEach(elem ->
                        importedElements.add(ReferenceBase.createFromXsd(elem))
                    );
                }

                importedElements.addAll(xsdSchema.getElements());

                // Add more includes and redefines to process
                xsdSchema.getChildrenIncludes()
                        .filter(moreInclude -> !includedSchemaLocations.contains(moreInclude.getSchemaLocation()))
                        .forEach(schemaIncludesAndRedefines::add);
                xsdSchema.getChildrenRedefines()
                        .filter(moreRedefine -> !includedSchemaLocations.contains(moreRedefine.getSchemaLocation()))
                        .forEach(schemaIncludesAndRedefines::add);
            }
        }

        return importedElements;
    }

    private boolean replaceUnsolvedImportedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference, String fileName) {
        boolean replaced = false;
        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef().substring(unsolvedReference.getRef().indexOf(":") + 1));

        if (concreteElements != null) {
            Map<String, String> oldElementAttributes = unsolvedReference.getElement().getAttributesMap();

            for (NamedConcreteElement concreteElement : concreteElements) {
                NamedConcreteElement substitutionElementWrapper;

                if (!unsolvedReference.isTypeRef()) {
                    XsdNamedElements substitutionElement = (XsdNamedElements) concreteElement.getElement().clone(oldElementAttributes, concreteElement.getElement().getParent());

                    substitutionElementWrapper = (NamedConcreteElement) ReferenceBase.createFromXsd(substitutionElement);
                } else {
                    substitutionElementWrapper = concreteElement;
                }

                replaced |= unsolvedReference.getParent().replaceUnsolvedElements(substitutionElementWrapper);
            }

            unsolvedElements.get(fileName).remove(unsolvedReference);

            if (unsolvedElements.get(fileName).isEmpty()){
                unsolvedElements.remove(fileName);
            }
        } else {
            storeUnsolvedItem(unsolvedReference);
        }
        return replaced;
    }

    /**
     * Clean the path by useless ../
     * example /A/B/../B/C into /A/B/C
     */
    private String cleanPath(String pathValue) {
        List<String> source = Arrays.asList(pathValue.split("/"));
        List<String> results = new ArrayList<>(source);
        int index = 0;
        for (String value : source) {
            if (value.equals("..")) {
                results.remove(index);
                if (index > 0) {
                    results.remove(index - 1);
                    index--;
                }
            } else {
                index++;
            }
        }
        return results.stream().map(Object::toString).collect(Collectors.joining("/"));
    }

    private void resolveInnerRefs() {
        ArrayList<Boolean> doneList = new ArrayList<>();
        ArrayList<String> fileNameList = new ArrayList<>(parseElements.keySet());

        for (int i = 0; i < fileNameList.size(); i++) {
            doneList.add(false);
        }

        while (doneList.contains(Boolean.FALSE)) {
            for (int i = 0; i < fileNameList.size(); i++) {
                String fileName = fileNameList.get(i);
                currentFile = fileName;

                if (!doneList.get(i)){
                    Set<String> includedFiles = new HashSet<>();
                    includedFiles.add(fileName);
                    findTransitiveDependencies(fileName, includedFiles);

                    // Add files that include or redefine this file
                    includedFiles.addAll(getResultXsdSchemas()
                            .filter(schema -> schema.getChildrenIncludes().anyMatch(xsdInclude -> xsdInclude.getSchemaLocation().equals(fileName)) ||
                                    schema.getChildrenRedefines().anyMatch(xsdRedefine -> xsdRedefine.getSchemaLocation().equals(fileName)))
                            .map(XsdSchema::getFilePath)
                            .distinct()
                            .collect(Collectors.toList()));

                    List<ReferenceBase> includedElements = new ArrayList<>(parseElements.get(fileName));

                    includedFiles.stream().filter(Objects::nonNull).forEach(includedFile -> {
                        String includedFilename = includedFile.substring(includedFile.lastIndexOf("/") + 1);

                        includedElements.addAll(parseElements.getOrDefault(includedFile, parseElements.get(parseElements.keySet().stream().filter(k -> k.endsWith(includedFilename)).findFirst().get())));
                    });

                    Map<String, List<NamedConcreteElement>> concreteElementsMap =
                            includedElements.stream()
                                    .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                                    .map(concreteElement -> (NamedConcreteElement) concreteElement)
                                    .collect(groupingBy(NamedConcreteElement::getName));

                    List<UnsolvedReference> unsolvedReferenceList = unsolvedElements.getOrDefault(fileName, new ArrayList<>())
                            .stream()
                            .filter(unsolvedElement -> !unsolvedElement.getRef().contains(":"))
                            .collect(Collectors.toList());

                    boolean replacedAtLeastOne;
                    boolean doneSomething = false;
                    do {
                        replacedAtLeastOne = false;
                        unsolvedReferenceList = unsolvedReferenceList.stream().filter(u -> parserUnsolvedElementsMap.stream().noneMatch(u1 -> u == u1.getUnsolvedReference())).collect(Collectors.toList());

                        Collections.sort(unsolvedReferenceList, (UnsolvedReference item1, UnsolvedReference item2) ->  Boolean.compare(item2.isTypeRef(), item1.isTypeRef()));

                        for (UnsolvedReference unsolvedReference : unsolvedReferenceList) {
                            replacedAtLeastOne |= replaceUnsolvedReference(concreteElementsMap, unsolvedReference, fileName);
                        }

                        if (replacedAtLeastOne) {
                            doneSomething = true;
                            unsolvedReferenceList = unsolvedElements.getOrDefault(fileName, new ArrayList<>())
                                .stream()
                                .filter(unsolvedElement -> !unsolvedElement.getRef().contains(":"))
                                .collect(Collectors.toList());
                        }
                    } while (replacedAtLeastOne);

                    doneList.set(i, true);

                    if (doneSomething) {
                        for (String includedFile : includedFiles) {
                            int idx = fileNameList.indexOf(includedFile);

                            if (idx != -1) {
                                doneList.set(idx, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void findTransitiveDependencies(String fileName, Set<String> dependencies) {
        // Collect both includes and redefines
        List<String> includedFiles =
                parseElements.get(fileName)
                        .stream()
                        .filter(referenceBase -> referenceBase instanceof ConcreteElement &&
                                (referenceBase.getElement() instanceof XsdInclude ||
                                 referenceBase.getElement() instanceof XsdRedefine))
                        .map(referenceBase -> {
                            XsdAbstractElement element = referenceBase.getElement();
                            if (element instanceof XsdInclude) {
                                return ((XsdInclude) element).getSchemaLocation();
                            } else if (element instanceof XsdRedefine) {
                                return ((XsdRedefine) element).getSchemaLocation();
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .map(schemaLocation -> toRealFileName(fileName, schemaLocation))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
        for (String includedFile : includedFiles) {
            if (dependencies.add(includedFile)) {
                findTransitiveDependencies(includedFile, dependencies);
            }
        }
    }
    private Optional<String> toRealFileName(String currentFileStr, String fileNameStr) {
        try {
            File fileBeingParsed = new File(currentFileStr);
            File fileBeingParsedFolder = new File(fileBeingParsed.getParent());
            File includedFile = new File(fileBeingParsedFolder, fileNameStr);

            fileNameStr = includedFile.getCanonicalPath();
        } catch (IOException ignored) { }

        String finalFileNameStr = fileNameStr;
        return parseElements.keySet()
                .stream()
                .filter(fileNameAux -> {
                    if (fileNameAux.contains("/") && finalFileNameStr.contains("\\")){
                        fileNameAux = fileNameAux.replace("/", "\\");
                    }

                    if (fileNameAux.contains("\\") && finalFileNameStr.contains("/")){
                        fileNameAux = fileNameAux.replace("\\", "/");
                    }

                    return fileNameAux.endsWith(finalFileNameStr);
                })
                .findFirst();
    }

    /**
     * Replaces a single {@link UnsolvedReference} object, with the respective {@link NamedConcreteElement} object. If
     * there isn't a {@link NamedConcreteElement} object to replace the {@link UnsolvedReference} object, information
     * is stored informing the user of this Project of the occurrence.
     *
     * @param concreteElementsMap The map containing all named concreteElements.
     * @param unsolvedReference   The unsolved reference to solve.
     * @return whether the unsolved reference was successfully replaced
     */
    private boolean replaceUnsolvedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference, String fileName) {
        boolean replaced = false;
        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef());

        if (concreteElements != null) {
            Map<String, String> oldElementAttributes = new HashMap<>(unsolvedReference.getElement().getAttributesMap());

            for (NamedConcreteElement concreteElement : concreteElements) {
                NamedConcreteElement substitutionElementWrapper;

                if (!unsolvedReference.isTypeRef()) {
                    XsdNamedElements substitutionElement = (XsdNamedElements) concreteElement.getElement().clone(oldElementAttributes, concreteElement.getElement().getParent());

                    substitutionElementWrapper = (NamedConcreteElement) ReferenceBase.createFromXsd(substitutionElement);
                } else {
                    substitutionElementWrapper = concreteElement;
                }

                replaced |= unsolvedReference.getParent().replaceUnsolvedElements(substitutionElementWrapper);
            }

            unsolvedElements.get(fileName).remove(unsolvedReference);

            if (unsolvedElements.get(fileName).isEmpty()){
                unsolvedElements.remove(fileName);
            }
        } else {
            storeUnsolvedItem(unsolvedReference);
        }
        return replaced;
    }

    /**
     * Saves an occurrence of an element which couldn't be resolved in the {@link XsdParser#replaceUnsolvedReference}
     * method, which can be accessed at the end of the parsing process in order to verify if were there were any
     * references that couldn't be solved.
     *
     * @param unsolvedReference The unsolved reference which couldn't be resolved.
     */
    private void storeUnsolvedItem(UnsolvedReference unsolvedReference) {
        if (parserUnsolvedElementsMap.isEmpty()) {
            parserUnsolvedElementsMap.add(new UnsolvedReferenceItem(unsolvedReference));
        } else {
            Optional<UnsolvedReferenceItem> innerEntry =
                    parserUnsolvedElementsMap.stream()
                            .filter(unsolvedReferenceObj ->
                                    unsolvedReferenceObj.getUnsolvedReference()
                                            .getRef()
                                            .equals(unsolvedReference.getRef()))
                            .findFirst();

            if (innerEntry.isPresent()) {
                innerEntry.ifPresent(entry -> entry.getParents().add(unsolvedReference.getParent()));
            } else {
                parserUnsolvedElementsMap.add(new UnsolvedReferenceItem(unsolvedReference));
            }
        }
    }

    /**
     * @return The {@link List} of {@link UnsolvedReferenceItem} that represent all the objects with a reference that couldn't
     * be solved.
     */
    public List<UnsolvedReferenceItem> getUnsolvedReferences() {
        return parserUnsolvedElementsMap;
    }

    /**
     * @return A list of all the top level parsed xsd:elements by this class. It doesn't return any other elements apart
     * from xsd:elements. To access the whole element tree use {@link XsdParser#getResultXsdSchemas()}
     */
    public Stream<XsdElement> getResultXsdElements() {
        List<XsdElement> elements = new ArrayList<>();

        getResultXsdSchemas().forEach(schema -> schema.getChildrenElements().forEach(elements::add));

        return elements.stream();
    }

    /**
     * @return A {@link List} of all the {@link XsdSchema} elements parsed by this class. You can use the {@link XsdSchema}
     * instances to navigate through the whole element tree.
     */
    public Stream<XsdSchema> getResultXsdSchemas() {
        return parseElements
                .values()
                .stream()
                .flatMap(List::stream)
                .filter(element -> element.getElement() instanceof XsdSchema)
                .map(element -> (XsdSchema) element.getElement());
    }

    /**
     * Adds an {@link UnsolvedReference} object to the {@link XsdParser#unsolvedElements} list which should be solved
     * at a later time in the parsing process.
     *
     * @param unsolvedReference The unsolvedReference to add to the unsolvedElements list.
     */
    public void addUnsolvedReference(UnsolvedReference unsolvedReference) {
        XsdSchema schema;

        try {
            schema = XsdAbstractElement.getXsdSchema(unsolvedReference.getElement(), new ArrayList<>());
        } catch (ParentAvailableException e) {
            schema = null;
        }

        String localCurrentFile = currentFile;

        if (schema != null && schema.getFilePath() != null) {
            String schemaFilePath = schema.getFilePath().replace("\\", "/");

            if (!localCurrentFile.equals(schemaFilePath)) {
                localCurrentFile = schemaFilePath;
            }
        }

        List<UnsolvedReference> unsolved = unsolvedElements.computeIfAbsent(localCurrentFile, k -> new ArrayList<>());

        unsolved.add(unsolvedReference);
    }

    /**
     * Adds a new file to the parsing queue. This new file appears by having xsd:import or xsd:include tags in the
     * original file to parse.
     *
     * @param schemaLocation A new file path of another XSD file to parse.
     */
    public void addFileToParse(String schemaLocation) {
        String fullSchemaLocation = currentFile.substring(0, currentFile.lastIndexOf('/') + 1) + schemaLocation;
        boolean urlSchemaLoction = false;

        if (!schemaLocations.contains((urlSchemaLoction = schemaLocation.startsWith("http")) ? schemaLocation
                : (fullSchemaLocation = cleanPath(fullSchemaLocation))) && schemaLocation.endsWith(".xsd")) {
            if (urlSchemaLoction) {
                schemaLocations.add(schemaLocation);
                schemaLocationsMap.put(schemaLocation, currentFile);
            } else {
                schemaLocations.add(fullSchemaLocation);
                schemaLocationsMap.put(fullSchemaLocation, currentFile);
            }
        }
    }

    public static Map<String, String> getXsdTypesToJava() {
        return xsdTypesToJava;
    }

    public static boolean isXsdTypeToJava(String type) {
        String typeAux = type.contains(":") ? type.substring(type.indexOf(":") + 1) : type;

        return xsdTypesToJava.keySet().stream().anyMatch(xsdType -> {
            xsdType = xsdType.contains(":") ? xsdType.substring(xsdType.indexOf(":") + 1) : xsdType;

            return xsdType.equals(typeAux);
        });
    }

    public static String getXsdTypeToJava(String type) {
        if (type == null){
            return null;
        }

        String typeAux = type.contains(":") ? type.substring(type.indexOf(":") + 1) : type;

        String key = xsdTypesToJava.keySet().stream().filter(xsdType -> {
            xsdType = xsdType.contains(":") ? xsdType.substring(xsdType.indexOf(":") + 1) : xsdType;

            return xsdType.equals(typeAux);
        }).findFirst().orElse(null);

        if (key == null){
            return null;
        }

        return xsdTypesToJava.get(key);
    }

    public static Map<String, ConfigEntryData> getParseMappers() {
        return parseMappers;
    }

    public static ConfigEntryData getParseMappers(String name) {
        if (name == null) {
            return null;
        }

        String nameAux = name.contains(":") ? name.substring(name.indexOf(":") + 1) : name;

        String key = parseMappers.keySet().stream().filter(xsdType -> {
            xsdType = xsdType.contains(":") ? xsdType.substring(xsdType.indexOf(":") + 1) : xsdType;

            return xsdType.equals(nameAux);
        }).findFirst().orElse(null);

        if (key == null){
            return null;
        }

        return parseMappers.get(key);
    }

    public void addParsedElement(ReferenceBase wrappedElement) {
        List<ReferenceBase> elements = parseElements.computeIfAbsent(currentFile, k -> new ArrayList<>());

        elements.add(wrappedElement);
    }

    static void updateConfig(ParserConfig config) {
        xsdTypesToJava = config.getXsdTypesToJava();
        parseMappers = config.getParseMappers();
    }

    protected boolean isRelativePath(String filePath) {
        return !filePath.matches(".*:.*");
    }

    protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setNamespaceAware(true);

        return documentBuilderFactory.newDocumentBuilder();
    }
}
