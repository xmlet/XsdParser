package org.xmlet.xsdparser.core;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.utils.*;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
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
     * A {@link List} which contains all the top elements parsed by this class.
     */
    private Map<String, List<ReferenceBase>> parseElements = new HashMap<>();

    /**
     * A {@link List} of {@link UnsolvedReference} elements that weren't solved. This list is consulted after all the
     * elements are parsed in order to find if there is any suitable parsed element to replace the unsolved element.
     */
    private Map<String, List<UnsolvedReference>> unsolvedElements = new HashMap<>();

    /**
     * A {@link List} containing all the elements that even after parsing all the elements on the file, don't have a
     * suitable object to replace the reference. This list can be consulted after the parsing process to assert if there
     * is any missing information in the XSD file.
     */
    private List<UnsolvedReferenceItem> parserUnsolvedElementsMap = new ArrayList<>();

    /**
     * A {@link List} containing the paths of files that were present in either {@link XsdInclude} or {@link XsdImport}
     * objects that are present in the original or subsequent files. These paths are stored to be parsed as well, the
     * parsing process only ends when all the files present in this {@link List} are parsed.
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
     * @param node The node to verify.
     * @return True if the node is a xsd:schema or xs:schema. False otherwise.
     */
    boolean isXsdSchema(Node node){
        String schemaNodeName = node.getNodeName();

        return schemaNodeName.equals(XsdSchema.XSD_TAG) || schemaNodeName.equals(XsdSchema.XS_TAG);
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
    }

    private void resolveOtherNamespaceRefs() {
        List<XsdSchema> schemas = getResultXsdSchemas().collect(Collectors.toList());

        for(XsdSchema schema : schemas){
            List<XsdImport> imports = schema.getChildrenImports().collect(Collectors.toList());

            for(XsdImport xsdImport : imports){
                schema.resolveNameSpace(xsdImport.getNamespace(), xsdImport.getSchemaLocation());
            }
        }

        parseElements
                .keySet()
                .forEach(fileName -> {
                    XsdSchema xsdSchema =
                            parseElements.get(fileName)
                                    .stream()
                                    .filter(referenceBase -> referenceBase instanceof ConcreteElement && referenceBase.getElement() instanceof XsdSchema)
                                    .map(referenceBase -> (((XsdSchema) referenceBase.getElement())))
                                    .findFirst()
                                    .get();

                    Map<String, NamespaceInfo> ns = xsdSchema.getNamespaces();

                    unsolvedElements
                            .getOrDefault(fileName, new ArrayList<>())
                            .stream()
                            .filter(unsolvedElement -> unsolvedElement.getRef().contains(":"))
                            .forEach(unsolvedElement -> {
                                String unsolvedElementNamespace = unsolvedElement.getRef().substring(0, unsolvedElement.getRef().indexOf(":"));

                                Optional<String> foundNamespaceId = ns.keySet().stream().filter(namespaceId -> namespaceId.equals(unsolvedElementNamespace)).findFirst();

                                if (foundNamespaceId.isPresent()){
                                    String importedFileLocation = ns.get(foundNamespaceId.get()).getFile();

                                    String importedFileName = importedFileLocation;

                                    if(!isAbsolutePath(importedFileName)) {
                                        String parentFile = schemaLocationsMap.get(importedFileName);

                                        importedFileName = parentFile.substring(0, parentFile.lastIndexOf('/') + 1).concat(importedFileName);
                                    }

                                    String finalImportedFileName = importedFileName;
                                    List<ReferenceBase> importedElements = parseElements.getOrDefault(importedFileLocation, parseElements.get(parseElements.keySet().stream().filter(k -> k.endsWith(finalImportedFileName)).findFirst().get()));

                                    Map<String, List<NamedConcreteElement>> concreteElementsMap =
                                            importedElements.stream()
                                                    .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                                                    .map(concreteElement -> (NamedConcreteElement) concreteElement)
                                                    .collect(groupingBy(NamedConcreteElement::getName));

                                    replaceUnsolvedImportedReference(concreteElementsMap, unsolvedElement);
                                }
                            });
                });
    }

    private void replaceUnsolvedImportedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference) {
        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef().substring(unsolvedReference.getRef().indexOf(":") + 1));

        if (concreteElements != null){
            Map<String, String> oldElementAttributes = unsolvedReference.getElement().getAttributesMap();

            for (NamedConcreteElement concreteElement : concreteElements) {
                NamedConcreteElement substitutionElementWrapper;

                if (!unsolvedReference.isTypeRef()){
                    XsdNamedElements substitutionElement = (XsdNamedElements) concreteElement.getElement().clone(oldElementAttributes, concreteElement.getElement().getParent());

                    substitutionElementWrapper = (NamedConcreteElement) ReferenceBase.createFromXsd(substitutionElement);
                } else {
                    substitutionElementWrapper = concreteElement;
                }

                unsolvedReference.getParent().replaceUnsolvedElements(substitutionElementWrapper);
            }
        } else {
            storeUnsolvedItem(unsolvedReference);
        }
    }

    private void resolveInnerRefs() {
        parseElements
            .keySet()
            .forEach(fileName -> {
                List<String> includedFiles =
                        parseElements.get(fileName)
                                    .stream()
                                    .filter(referenceBase -> referenceBase instanceof ConcreteElement && referenceBase.getElement() instanceof XsdInclude)
                                    .map(referenceBase -> (((XsdInclude) referenceBase.getElement()).getSchemaLocation()))
                                    .collect(Collectors.toList());

                List<ReferenceBase> includedElements = new ArrayList<>(parseElements.get(fileName));

                includedFiles.forEach(includedFile ->{
                    String includedFilename = includedFile.substring(includedFile.lastIndexOf("/")+1);

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

                for (UnsolvedReference unsolvedReference : unsolvedReferenceList) {
                    replaceUnsolvedReference(concreteElementsMap, unsolvedReference);
                }
            });
    }

    /**
     * Replaces a single {@link UnsolvedReference} object, with the respective {@link NamedConcreteElement} object. If
     * there isn't a {@link NamedConcreteElement} object to replace the {@link UnsolvedReference} object, information
     * is stored informing the user of this Project of the occurrence.
     * @param concreteElementsMap The map containing all named concreteElements.
     * @param unsolvedReference The unsolved reference to solve.
     */
    private void replaceUnsolvedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference) {
        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef());

        if (concreteElements != null){
            Map<String, String> oldElementAttributes = unsolvedReference.getElement().getAttributesMap();

            for (NamedConcreteElement concreteElement : concreteElements) {
                NamedConcreteElement substitutionElementWrapper;

                if (!unsolvedReference.isTypeRef()){
                    XsdNamedElements substitutionElement = (XsdNamedElements) concreteElement.getElement().clone(oldElementAttributes, concreteElement.getElement().getParent());

                    substitutionElementWrapper = (NamedConcreteElement) ReferenceBase.createFromXsd(substitutionElement);
                } else {
                    substitutionElementWrapper = concreteElement;
                }

                unsolvedReference.getParent().replaceUnsolvedElements(substitutionElementWrapper);
            }
        } else {
            storeUnsolvedItem(unsolvedReference);
        }
    }

    /**
     * Saves an occurrence of an element which couldn't be resolved in the {@link XsdParser#replaceUnsolvedReference}
     * method, which can be accessed at the end of the parsing process in order to verify if were there were any
     * references that couldn't be solved.
     * @param unsolvedReference The unsolved reference which couldn't be resolved.
     */
    private void storeUnsolvedItem(UnsolvedReference unsolvedReference) {
        if (parserUnsolvedElementsMap.isEmpty()){
            parserUnsolvedElementsMap.add(new UnsolvedReferenceItem(unsolvedReference));
        } else {
            Optional<UnsolvedReferenceItem> innerEntry =
                    parserUnsolvedElementsMap.stream()
                            .filter(unsolvedReferenceObj ->
                                    unsolvedReferenceObj.getUnsolvedReference()
                                            .getRef()
                                            .equals(unsolvedReference.getRef()))
                            .findFirst();

            if (innerEntry.isPresent()){
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
    public List<UnsolvedReferenceItem> getUnsolvedReferences(){
        return parserUnsolvedElementsMap;
    }

    /**
     * @return A list of all the top level parsed xsd:elements by this class. It doesn't return any other elements apart
     * from xsd:elements. To access the whole element tree use {@link XsdParser#getResultXsdSchemas()}
     */
    public Stream<XsdElement> getResultXsdElements(){
        List<XsdElement> elements = new ArrayList<>();

        getResultXsdSchemas().forEach(schema -> schema.getChildrenElements().forEach(elements::add));

        return elements.stream();
    }

    /**
     * @return A {@link List} of all the {@link XsdSchema} elements parsed by this class. You can use the {@link XsdSchema}
     * instances to navigate through the whole element tree.
     */
    public Stream<XsdSchema> getResultXsdSchemas(){
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
     * @param unsolvedReference The unsolvedReference to add to the unsolvedElements list.
     */
    public void addUnsolvedReference(UnsolvedReference unsolvedReference){
        List<UnsolvedReference> unsolved = unsolvedElements.computeIfAbsent(currentFile, k -> new ArrayList<>());

        unsolved.add(unsolvedReference);
    }

    /**
     * Adds a new file to the parsing queue. This new file appears by having xsd:import or xsd:include tags in the
     * original file to parse.
     * @param schemaLocation A new file path of another XSD file to parse.
     */
    public void addFileToParse(String schemaLocation) {
        String fileName = schemaLocation.substring(schemaLocation.lastIndexOf("/")+1);

        if (!schemaLocations.contains(schemaLocation) && schemaLocation.endsWith(".xsd") && schemaLocations.stream().noneMatch(sl -> sl.endsWith(fileName))){
            schemaLocations.add(schemaLocation);
            schemaLocationsMap.put(schemaLocation, currentFile);
        }
    }
//
//    /**
//     * @param schemaLocation A new file path of another XSD file to parse.
//     */
//    public void addImportedLocation(String namespace, String schemaLocation) {
//        int a = 5;
//    }

    public static Map<String, String> getXsdTypesToJava() {
        return xsdTypesToJava;
    }

    public static Map<String, ConfigEntryData> getParseMappers() {
        return parseMappers;
    }

    public void addParsedElement(ReferenceBase wrappedElement) {
        List<ReferenceBase> elements = parseElements.computeIfAbsent(currentFile, k -> new ArrayList<>());

        elements.add(wrappedElement);
    }

    void updateConfig(ParserConfig config) {
        xsdTypesToJava = config.getXsdTypesToJava();
        parseMappers = config.getParseMappers();
    }

    protected boolean isAbsolutePath(String filePath) {
        return filePath.matches(".*:.*");
    }
}
