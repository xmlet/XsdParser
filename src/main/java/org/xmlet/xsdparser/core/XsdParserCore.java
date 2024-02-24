package org.xmlet.xsdparser.core;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.utils.*;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public abstract class XsdParserCore {

    private static final String UNKNOWN_FILE = "DEFAULT";
    public  static final String XML_NAMESPACE = "https://www.w3.org/2001/xml.xsd";

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
    public Map<String, List<ReferenceBase>> parseElements = new HashMap<>();

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

    private Map<String, List<NamedConcreteElement>> xmlConcreteNamespace = new HashMap<>();

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

        return schemaNodeName.equals(XsdSchema.XSD_TAG) || schemaNodeName.equals(XsdSchema.XS_TAG) || schemaNodeName.equals(XsdSchema.TAG);
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
        computeUnsolved();
        resolveXmlNamespace();
        computeUnsolved();
        resolveInnerRefs();
        resolveOtherNamespaceRefs();
        out();
    }

    private void out() {
        unsolvedElements.values().stream().flatMap(List::stream).forEach(this::storeUnsolvedItem);
    }

    private void resolveXmlNamespace() {
        List<UnsolvedReference> original = new ArrayList<>(unsolvedElements.getOrDefault(XML_NAMESPACE, new ArrayList<>()));
        for(UnsolvedReference unsolvedReference : original) {
            Map<String, List<NamedConcreteElement>> concreteElementsMap =
                    parseElements.get(XML_NAMESPACE)
                            .stream()
                            .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                            .map(concreteElement -> (NamedConcreteElement) concreteElement)
                            .collect(groupingBy(NamedConcreteElement::getName));
            replaceUnsolvedImportedReference(concreteElementsMap, unsolvedReference, XML_NAMESPACE);
        }
        xmlConcreteNamespace = parseElements.get(XML_NAMESPACE)
                .stream()
                .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                .map(concreteElement -> (NamedConcreteElement) concreteElement)
                .collect(groupingBy(NamedConcreteElement::getName));
    }

    private void resolveOtherNamespaceRefs() {
        List<XsdSchema> schemas = getResultXsdSchemas().collect(Collectors.toList());

        for(XsdSchema schema : schemas){
            List<XsdImport> imports = schema.getChildrenImports().collect(Collectors.toList());

            for(XsdImport xsdImport : imports){
                schema.resolveNameSpace(xsdImport.getNamespace(), xsdImport.getSchemaLocation());
            }
            Map<String, NamespaceInfo> ns = schema.getNamespaces();
            
            for(Entry<String, NamespaceInfo> e: ns.entrySet()) {
            	schema.resolveNameSpace(e.getKey(), schema.getFilePath().substring(schema.getFilePath().lastIndexOf("/")+1));
            }
        }

        parseElements
                .keySet()
                .forEach(fileName -> {
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

                    long startingUnsolvedReferenceListSize = unsolvedReferenceList.size();
                    long currentUnsolvedReferenceListSize;
                    boolean solveMore = true;

                    do {
                        for (UnsolvedReference unsolvedReference : unsolvedReferenceList) {
                            String unsolvedElementNamespace = unsolvedReference.getRef().substring(0, unsolvedReference.getRef().indexOf(":"));
                            Optional<String> foundNamespaceId = ns.keySet().stream().filter(namespaceId -> namespaceId.equals(unsolvedElementNamespace)).findFirst();
                            if(unsolvedElementNamespace.equals("xml")) {
                                replaceUnsolvedImportedReference(xmlConcreteNamespace, unsolvedReference, fileName);
                            } else if (foundNamespaceId.isPresent()) {
                                NamespaceInfo namespaceInfo = ns.get(foundNamespaceId.get());
                                List<ReferenceBase> relevantElements;
                                XsdSchema unsolvedElementSchema = unsolvedReference.getElement().getXsdSchema();

                                if (unsolvedElementSchema == null){
                                    unsolvedElementSchema = xsdSchema;
                                }
                                Set<String> includedFiles = new HashSet<>();
                                if (unsolvedElementSchema != null && unsolvedElementSchema.getTargetNamespace() != null && unsolvedElementSchema.getTargetNamespace().equals(namespaceInfo.getName())) {
                                    relevantElements = new ArrayList<>(unsolvedElementSchema.getElements());
                                    findTransitiveDependencies(unsolvedElementSchema.getFilePath(), includedFiles);
                                    fillRelevantElementsWithAllIncluded(relevantElements, includedFiles);
                                } else {
                                    String importedFileLocation = ns.get(foundNamespaceId.get()).getFile();

                                    String importedFileName = importedFileLocation;

                                    if (isRelativePath(importedFileName)) {
                                        String parentFile = schemaLocationsMap.get(importedFileName);

                                        if (parentFile == null){
                                            parentFile = fileName;
                                        }

                                        importedFileName = parentFile.substring(0, parentFile.lastIndexOf('/') + 1).concat(importedFileName);
                                    }

                                    String finalImportedFileName = importedFileName;
                                    relevantElements = new ArrayList<>(parseElements.getOrDefault(importedFileLocation,
                                        parseElements.get(parseElements.keySet()
                                                                       .stream()
                                                                       .filter(k -> cleanPath(k).endsWith(cleanPath(finalImportedFileName)))
                                                                       .findFirst()
                                                                       .orElse(null))));
                                    findTransitiveDependencies(finalImportedFileName, includedFiles);
                                    fillRelevantElementsWithAllIncluded(relevantElements, includedFiles);

                                }



                                Map<String, List<NamedConcreteElement>> concreteElementsMap =
                                        relevantElements.stream()
                                                .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                                                .map(concreteElement -> (NamedConcreteElement) concreteElement)
                                                .collect(groupingBy(NamedConcreteElement::getName));

                                replaceUnsolvedImportedReference(concreteElementsMap, unsolvedReference, fileName);
                            }
                        }
                        computeUnsolved();
                        unsolvedReferenceList = unsolvedElements
                                .getOrDefault(fileName, new ArrayList<>())
                                .stream()
                                .filter(unsolvedElement -> unsolvedElement.getRef().contains(":"))
                                .collect(Collectors.toList());

                        currentUnsolvedReferenceListSize = unsolvedReferenceList.size();

                        if (currentUnsolvedReferenceListSize == startingUnsolvedReferenceListSize){
                            solveMore = false;
                        }

                        startingUnsolvedReferenceListSize = currentUnsolvedReferenceListSize;
                    } while(solveMore);
                });
    }

    private void replaceUnsolvedImportedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference, String fileName) {
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

            unsolvedElements.get(fileName).remove(unsolvedReference);
        }
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

        while (doneList.contains(Boolean.FALSE)){
            for (int i = 0; i < fileNameList.size(); i++) {
                String fileName = fileNameList.get(i);

                if (!doneList.get(i)){
                    List<ReferenceBase> relevantElements = new ArrayList<>(parseElements.getOrDefault(fileName, new ArrayList<>()));
                    Set<String> includedFiles = new HashSet<>();
                    includedFiles.add(fileName);
                    findTransitiveDependencies(fileName, includedFiles);

                    includedFiles.addAll(getResultXsdSchemas().filter(schema -> schema.getChildrenIncludes().anyMatch(xsdInclude -> xsdInclude.getSchemaLocation().equals(fileName))).map(XsdSchema::getFilePath).distinct().collect(Collectors.toList()));

                    fillRelevantElementsWithAllIncluded(relevantElements, includedFiles);
                    Map<String, List<NamedConcreteElement>> concreteElementsMap =
                            relevantElements.stream()
                                    .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                                    .map(concreteElement -> (NamedConcreteElement) concreteElement)
                                    .collect(groupingBy(NamedConcreteElement::getName));

                    List<UnsolvedReference> unsolvedReferenceList = unsolvedElements.getOrDefault(fileName, new ArrayList<>())
                            .stream()
                            .filter(unsolvedElement -> !unsolvedElement.getRef().contains(":"))
                            .collect(Collectors.toList());

                    long startingUnsolvedReferenceListSize = unsolvedReferenceList.size();
                    long currentUnsolvedReferenceListSize = 0;
                    boolean solveMore = true;
                    boolean doneSomething = false;

                    do {
                        unsolvedReferenceList = unsolvedReferenceList.stream().filter(u -> parserUnsolvedElementsMap.stream().noneMatch(u1 -> u == u1.getUnsolvedReference())).collect(Collectors.toList());

                        for (UnsolvedReference unsolvedReference : unsolvedReferenceList) {
                            replaceUnsolvedReference(concreteElementsMap, unsolvedReference, fileName);
                        }

                        unsolvedReferenceList = unsolvedElements.getOrDefault(fileName, new ArrayList<>())
                                .stream()
                                .filter(unsolvedElement -> !unsolvedElement.getRef().contains(":"))
                                .collect(Collectors.toList());

                        currentUnsolvedReferenceListSize = unsolvedReferenceList.size();
                        computeUnsolved();
                        if (currentUnsolvedReferenceListSize == startingUnsolvedReferenceListSize){
                            solveMore = false;
                        } else {
                          doneSomething = true;
                        }

                        startingUnsolvedReferenceListSize = currentUnsolvedReferenceListSize;
                    } while(solveMore);

                    doneList.set(i, true);

                    if (doneSomething){
                        for(String includedFile: includedFiles){
                            int idx = fileNameList.indexOf(includedFile);

                            if (idx != -1){
                                doneList.set(idx, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void findTransitiveDependencies(String fileName, Set<String> dependencies) {
        List<String> includedFiles =
                parseElements.get(fileName)
                    .stream()
                    .filter(referenceBase -> referenceBase instanceof ConcreteElement && referenceBase.getElement() instanceof XsdInclude)
                    .map(referenceBase -> (((XsdInclude) referenceBase.getElement()).getSchemaLocation()))
                    .map(this::toRealFileName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        for (String includedFile : includedFiles) {
            if (dependencies.add(includedFile)) {
                findTransitiveDependencies(includedFile, dependencies);
            }
        }
    }
    private Optional<String> toRealFileName(String fileName) {
        return parseElements.keySet()
                .stream()
                .filter(fileNameAux -> fileNameAux.endsWith(fileName))
                .findFirst();
    }


    /**
     * Replaces a single {@link UnsolvedReference} object, with the respective {@link NamedConcreteElement} object. If
     * there isn't a {@link NamedConcreteElement} object to replace the {@link UnsolvedReference} object, information
     * is stored informing the user of this Project of the occurrence.
     * @param concreteElementsMap The map containing all named concreteElements.
     * @param unsolvedReference The unsolved reference to solve.
     */
    private void replaceUnsolvedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference, String fileName) {
        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef());

        if (concreteElements != null){
            Map<String, String> oldElementAttributes = new HashMap<>(unsolvedReference.getElement().getAttributesMap());

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

            unsolvedElements.get(fileName).remove(unsolvedReference);
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
        XsdSchema schema = extractXsdSchema(unsolvedReference);
        String localCurrentFile;

        if (schema != null) {
            localCurrentFile = schema.getFilePath();
        } else {
            localCurrentFile = UNKNOWN_FILE;
        }
        List<UnsolvedReference> unsolved = unsolvedElements.computeIfAbsent(localCurrentFile, k -> new ArrayList<>());

        unsolved.add(unsolvedReference);
    }

    /**
     * Adds a new file to the parsing queue. This new file appears by having xsd:import or xsd:include tags in the
     * original file to parse.
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

    public static Map<String, ConfigEntryData> getParseMappers() {
        return parseMappers;
    }

    public void addParsedElement(ReferenceBase wrappedElement) {
        List<ReferenceBase> elements = parseElements.computeIfAbsent(currentFile, k -> new ArrayList<>());

        elements.add(wrappedElement);
    }

    private void computeUnsolved() {
        Map<String, List<UnsolvedReference>> original = new HashMap<>(unsolvedElements);
        unsolvedElements.clear();
        for (List<UnsolvedReference> value : original.values()) {
            for (UnsolvedReference unsolvedReference : value) {
                XsdSchema schema = extractXsdSchema(unsolvedReference);
                if(schema != null) {
                    String fileName = schema.getFilePath();
                    List<UnsolvedReference> unsolvedReferences = unsolvedElements.getOrDefault(fileName, new ArrayList<>());
                    if (unsolvedReferences.isEmpty()) {
                        unsolvedElements.put(fileName, unsolvedReferences);
                    }
                    unsolvedReferences.add(unsolvedReference);
                } else {
                    List<UnsolvedReference> unsolvedReferences = unsolvedElements.getOrDefault(UNKNOWN_FILE, new ArrayList<>());
                    if (unsolvedReferences.isEmpty()) {
                        unsolvedElements.put(UNKNOWN_FILE, unsolvedReferences);
                    }
                    unsolvedReferences.add(unsolvedReference);
                }
            }
        }
    }

    private static XsdSchema extractXsdSchema(ReferenceBase unsolvedReference) {
        List<XsdAbstractElement> allElementsInRow = new ArrayList<>();
        XsdAbstractElement current = unsolvedReference.getElement();
        XsdAbstractElement parent = current.getParent();
        if(parent == null) {
            current.setParentAvailable(true);
            parent = current.getParent();
            current.setParentAvailable(false);
        }
        allElementsInRow.add(current);
        while(parent != null) {
            current = parent;
            parent = current.getParent();
            if(parent == null) {
                current.setParentAvailable(true);
                parent = current.getParent();
                current.setParentAvailable(false);
            }
            allElementsInRow.add(0, current);
        }
        XsdAbstractElement firstElement = allElementsInRow.get(0);
        if(firstElement instanceof  XsdSchema) {
            return (XsdSchema) firstElement;
        } else {
            return null;
        }
    }

    private void fillRelevantElementsWithAllIncluded(List<ReferenceBase> relevantElements, Set<String> includedFiles) {
        for(String includedFile : includedFiles) {
            relevantElements.addAll(
                    parseElements.getOrDefault(includedFile,
                            parseElements.getOrDefault(
                                    parseElements.keySet()
                                            .stream()
                                            .filter(fileName -> fileName.endsWith(includedFile)).findFirst().orElse(null),
                                    new ArrayList<>()
                            )
                    )
            );
        }
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
