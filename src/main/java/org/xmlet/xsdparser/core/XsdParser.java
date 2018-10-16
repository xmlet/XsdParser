package org.xmlet.xsdparser.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * {@link XsdParser} in the core class of the XsdParser project. It functions as a one shot class, receiving the name
 * of the file to parse in its constructor and storing the parse results in its multiple fields, which can be consulted
 * after the instance is created.
 */
public class XsdParser {

    /**
     * A {@link Map} object that contains a parse function to each {@link XsdAbstractElement} concrete
     * type supported by this mapper, this way based on the concrete {@link XsdAbstractElement} tag the according parse
     * method can be invoked.
     */
    private static final Map<String, BiFunction<XsdParser, Node, ReferenceBase>> parseMappers;

    /**
     * A {@link Map} object that contains the all the XSD types and their respective types in the Java
     * language.
     */
    private static final Map<String, String> xsdTypesToJava;

    /**
     * A {@link List} which contains all the top elements parsed by this class.
     */
    private List<ReferenceBase> parseElements = new ArrayList<>();

    /**
     * A {@link List} of {@link UnsolvedReference} elements that weren't solved. This list is consulted after all the
     * elements are parsed in order to find if there is any suitable parsed element to replace the unsolved element.
     */
    private List<UnsolvedReference> unsolvedElements = new ArrayList<>();

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
    private List<String> schemaLocations = new ArrayList<>();

    static {
        parseMappers = new HashMap<>();
        xsdTypesToJava = new HashMap<>();

        parseMappers.put(XsdAll.XSD_TAG, XsdAll::parse);
        parseMappers.put(XsdAll.XS_TAG, XsdAll::parse);
        parseMappers.put(XsdAttribute.XSD_TAG, XsdAttribute::parse);
        parseMappers.put(XsdAttribute.XS_TAG, XsdAttribute::parse);
        parseMappers.put(XsdAttributeGroup.XSD_TAG, XsdAttributeGroup::parse);
        parseMappers.put(XsdAttributeGroup.XS_TAG, XsdAttributeGroup::parse);
        parseMappers.put(XsdChoice.XSD_TAG, XsdChoice::parse);
        parseMappers.put(XsdChoice.XS_TAG, XsdChoice::parse);
        parseMappers.put(XsdComplexType.XSD_TAG, XsdComplexType::parse);
        parseMappers.put(XsdComplexType.XS_TAG, XsdComplexType::parse);
        parseMappers.put(XsdElement.XSD_TAG, XsdElement::parse);
        parseMappers.put(XsdElement.XS_TAG, XsdElement::parse);
        parseMappers.put(XsdGroup.XSD_TAG, XsdGroup::parse);
        parseMappers.put(XsdGroup.XS_TAG, XsdGroup::parse);
        parseMappers.put(XsdInclude.XSD_TAG, XsdInclude::parse);
        parseMappers.put(XsdInclude.XS_TAG, XsdInclude::parse);
        parseMappers.put(XsdImport.XSD_TAG, XsdImport::parse);
        parseMappers.put(XsdImport.XS_TAG, XsdImport::parse);
        parseMappers.put(XsdSequence.XSD_TAG, XsdSequence::parse);
        parseMappers.put(XsdSequence.XS_TAG, XsdSequence::parse);
        parseMappers.put(XsdSimpleType.XSD_TAG, XsdSimpleType::parse);
        parseMappers.put(XsdSimpleType.XS_TAG, XsdSimpleType::parse);
        parseMappers.put(XsdList.XSD_TAG, XsdList::parse);
        parseMappers.put(XsdList.XS_TAG, XsdList::parse);
        parseMappers.put(XsdRestriction.XSD_TAG, XsdRestriction::parse);
        parseMappers.put(XsdRestriction.XS_TAG, XsdRestriction::parse);
        parseMappers.put(XsdUnion.XSD_TAG, XsdUnion::parse);
        parseMappers.put(XsdUnion.XS_TAG, XsdUnion::parse);

        parseMappers.put(XsdAnnotation.XSD_TAG, XsdAnnotation::parse);
        parseMappers.put(XsdAnnotation.XS_TAG, XsdAnnotation::parse);
        parseMappers.put(XsdAppInfo.XSD_TAG, XsdAppInfo::parse);
        parseMappers.put(XsdAppInfo.XS_TAG, XsdAppInfo::parse);
        parseMappers.put(XsdComplexContent.XSD_TAG, XsdComplexContent::parse);
        parseMappers.put(XsdComplexContent.XS_TAG, XsdComplexContent::parse);
        parseMappers.put(XsdDocumentation.XSD_TAG, XsdDocumentation::parse);
        parseMappers.put(XsdDocumentation.XS_TAG, XsdDocumentation::parse);
        parseMappers.put(XsdExtension.XSD_TAG, XsdExtension::parse);
        parseMappers.put(XsdExtension.XS_TAG, XsdExtension::parse);
        parseMappers.put(XsdSimpleContent.XSD_TAG, XsdSimpleContent::parse);
        parseMappers.put(XsdSimpleContent.XS_TAG, XsdSimpleContent::parse);

        parseMappers.put(XsdEnumeration.XSD_TAG, XsdEnumeration::parse);
        parseMappers.put(XsdEnumeration.XS_TAG, XsdEnumeration::parse);
        parseMappers.put(XsdFractionDigits.XSD_TAG, XsdFractionDigits::parse);
        parseMappers.put(XsdFractionDigits.XS_TAG, XsdFractionDigits::parse);
        parseMappers.put(XsdLength.XSD_TAG, XsdLength::parse);
        parseMappers.put(XsdLength.XS_TAG, XsdLength::parse);
        parseMappers.put(XsdMaxExclusive.XSD_TAG, XsdMaxExclusive::parse);
        parseMappers.put(XsdMaxExclusive.XS_TAG, XsdMaxExclusive::parse);
        parseMappers.put(XsdMaxInclusive.XSD_TAG, XsdMaxInclusive::parse);
        parseMappers.put(XsdMaxInclusive.XS_TAG, XsdMaxInclusive::parse);
        parseMappers.put(XsdMaxLength.XSD_TAG, XsdMaxLength::parse);
        parseMappers.put(XsdMaxLength.XS_TAG, XsdMaxLength::parse);
        parseMappers.put(XsdMinExclusive.XSD_TAG, XsdMinExclusive::parse);
        parseMappers.put(XsdMinExclusive.XS_TAG, XsdMinExclusive::parse);
        parseMappers.put(XsdMinInclusive.XSD_TAG, XsdMinInclusive::parse);
        parseMappers.put(XsdMinInclusive.XS_TAG, XsdMinInclusive::parse);
        parseMappers.put(XsdMinLength.XSD_TAG, XsdMinLength::parse);
        parseMappers.put(XsdMinLength.XS_TAG, XsdMinLength::parse);
        parseMappers.put(XsdPattern.XSD_TAG, XsdPattern::parse);
        parseMappers.put(XsdPattern.XS_TAG, XsdPattern::parse);
        parseMappers.put(XsdTotalDigits.XSD_TAG, XsdTotalDigits::parse);
        parseMappers.put(XsdTotalDigits.XS_TAG, XsdTotalDigits::parse);
        parseMappers.put(XsdWhiteSpace.XSD_TAG, XsdWhiteSpace::parse);
        parseMappers.put(XsdWhiteSpace.XS_TAG, XsdWhiteSpace::parse);

        String string = "String";
        String xmlGregorianCalendar = "XMLGregorianCalendar";
        String duration = "Duration";
        String bigInteger = "BigInteger";
        String integer = "Integer";
        String shortString = "Short";
        String qName = "QName";
        String longString = "Long";
        String byteString = "Byte";

        xsdTypesToJava.put("xsd:anyURI", string);
        xsdTypesToJava.put("xs:anyURI", string);
        xsdTypesToJava.put("xsd:boolean", "Boolean");
        xsdTypesToJava.put("xs:boolean", "Boolean");
        xsdTypesToJava.put("xsd:date", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:date", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:dateTime", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:time", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:time", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:duration", duration);
        xsdTypesToJava.put("xs:duration", duration);
        xsdTypesToJava.put("xsd:dayTimeDuration", duration);
        xsdTypesToJava.put("xs:dayTimeDuration", duration);
        xsdTypesToJava.put("xsd:yearMonthDuration", duration);
        xsdTypesToJava.put("xs:yearMonthDuration", duration);
        xsdTypesToJava.put("xsd:gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gMonthDay", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gYear", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xs:gYearMonth", xmlGregorianCalendar);
        xsdTypesToJava.put("xsd:decimal", "BigDecimal");
        xsdTypesToJava.put("xs:decimal", "BigDecimal");
        xsdTypesToJava.put("xsd:integer", bigInteger);
        xsdTypesToJava.put("xs:integer", bigInteger);
        xsdTypesToJava.put("xsd:nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("xs:nonPositiveInteger", bigInteger);
        xsdTypesToJava.put("xsd:negativeInteger", bigInteger);
        xsdTypesToJava.put("xs:negativeInteger", bigInteger);
        xsdTypesToJava.put("xsd:long", longString);
        xsdTypesToJava.put("xs:long", longString);
        xsdTypesToJava.put("xsd:int", integer);
        xsdTypesToJava.put("xs:int", integer);
        xsdTypesToJava.put("xsd:short", shortString);
        xsdTypesToJava.put("xs:short", shortString);
        xsdTypesToJava.put("xsd:byte", byteString);
        xsdTypesToJava.put("xs:byte", byteString);
        xsdTypesToJava.put("xsd:nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("xs:nonNegativeInteger", bigInteger);
        xsdTypesToJava.put("xsd:unsignedLong", bigInteger);
        xsdTypesToJava.put("xs:unsignedLong", bigInteger);
        xsdTypesToJava.put("xsd:unsignedInt", longString);
        xsdTypesToJava.put("xs:unsignedInt", longString);
        xsdTypesToJava.put("xsd:unsignedShort", integer);
        xsdTypesToJava.put("xs:unsignedShort", integer);
        xsdTypesToJava.put("xsd:unsignedByte", shortString);
        xsdTypesToJava.put("xs:unsignedByte", shortString);
        xsdTypesToJava.put("xsd:positiveInteger", bigInteger);
        xsdTypesToJava.put("xs:positiveInteger", bigInteger);
        xsdTypesToJava.put("xsd:double", "Double");
        xsdTypesToJava.put("xs:double", "Double");
        xsdTypesToJava.put("xsd:float", "Float");
        xsdTypesToJava.put("xs:float", "Float");
        xsdTypesToJava.put("xsd:QName", qName);
        xsdTypesToJava.put("xs:QName", qName);
        xsdTypesToJava.put("xsd:NOTATION", qName);
        xsdTypesToJava.put("xs:NOTATION", qName);
        xsdTypesToJava.put("xsd:string", string);
        xsdTypesToJava.put("xs:string", string);
        xsdTypesToJava.put("xsd:normalizedString", string);
        xsdTypesToJava.put("xs:normalizedString", string);
        xsdTypesToJava.put("xsd:token", string);
        xsdTypesToJava.put("xs:token", string);
        xsdTypesToJava.put("xsd:language", string);
        xsdTypesToJava.put("xs:language", string);
        xsdTypesToJava.put("xsd:NMTOKEN", string);
        xsdTypesToJava.put("xs:NMTOKEN", string);
        xsdTypesToJava.put("xsd:Name", string);
        xsdTypesToJava.put("xs:Name", string);
        xsdTypesToJava.put("xsd:NCName", string);
        xsdTypesToJava.put("xs:NCName", string);
        xsdTypesToJava.put("xsd:ID", string);
        xsdTypesToJava.put("xs:ID", string);
        xsdTypesToJava.put("xsd:IDREF", string);
        xsdTypesToJava.put("xs:IDREF", string);
        xsdTypesToJava.put("xsd:ENTITY", string);
        xsdTypesToJava.put("xs:ENTITY", string);
        xsdTypesToJava.put("xsd:untypedAtomic", string);
        xsdTypesToJava.put("xs:untypedAtomic", string);
    }

    /**
     * The XsdParser constructor will parse the XSD file with the {@code filepath} and will also parse all the subsequent
     * XSD files with their path present in xsd:import and xsd:include tags. After parsing all the XSD files present it
     * resolves the references existent in the XSD language, represented by the ref attribute. When this method finishes
     * the parse results and remaining unsolved references are accessible by the {@link XsdParser#getResultXsdSchemas()},
     * {@link XsdParser#getResultXsdElements()} and {@link XsdParser#getUnsolvedReferences()}.
     * @param filePath States the path of the XSD file to be parsed.
     */
    public XsdParser(String filePath){
        schemaLocations.add(filePath);
        int index = 0;

        while (schemaLocations.size() > index){
            String schemaLocation = schemaLocations.get(index);
            parseFile(schemaLocation);
            ++index;
        }

        resolveRefs();
    }

    /**
     * Parses a XSD file and all its containing XSD elements. This code iterates on the nodes and parses the supported
     * ones. The supported types are all the XSD types that have their tag present in the {@link XsdParser#parseMappers}
     * field.
     * @param filePath The path to the XSD file.
     */
    private void parseFile(String filePath) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            if (!new File(filePath).exists()){
                throw new FileNotFoundException();
            }

            Node schemaNode = getSchemaNode(filePath);

            if (isXsdSchema(schemaNode)){
                XsdSchema.parse(this, schemaNode);
            } else {
                throw new ParsingException("The top level element of a XSD file should be the xsd:schema node.");
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
        }
    }

    /**
     * Verifies if a given {@link Node} object, i.e. {@code node} is a xsd:schema node.
     * @param node The node to verify.
     * @return True if the node is a xsd:schema or xs:schema. False otherwise.
     */
    private boolean isXsdSchema(Node node){
        String schemaNodeName = node.getNodeName();

        return schemaNodeName.equals(XsdSchema.XSD_TAG) || schemaNodeName.equals(XsdSchema.XS_TAG);
    }

    /**
     * This function uses DOM to obtain a list of nodes from a XSD file.
     * @param filePath The path to the XSD file.
     * @throws IOException If the file parsing throws {@link IOException}.
     * @throws SAXException if the file parsing throws {@link SAXException}.
     * @throws ParserConfigurationException If the {@link DocumentBuilderFactory#newDocumentBuilder()} throws
     *      {@link ParserConfigurationException}.
     * @return A list of nodes that represent the node tree of the XSD file with the path received.
     */
    private Node getSchemaNode(String filePath) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(filePath);

        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        return doc.getFirstChild();
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
    private void resolveRefs() {
        Map<String, List<NamedConcreteElement>> concreteElementsMap =
                parseElements.stream()
                               .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                               .map(concreteElement -> (NamedConcreteElement) concreteElement)
                               .collect(groupingBy(NamedConcreteElement::getName));

        Map<String, NamedConcreteElement> typeReferredElements = new HashMap<>();



        unsolvedElements.forEach(unsolvedElement -> replaceUnsolvedReference(concreteElementsMap, unsolvedElement));
    }

    /**
     * Replaces a single {@link UnsolvedReference} object, with the respective {@link NamedConcreteElement} object. If
     * there isn't a {@link NamedConcreteElement} object to replace the {@link UnsolvedReference} object, information
     * is stored informing the user of this Project of the occurrence.
     * @param concreteElementsMap The map containing all named concreteElements.
     * @param unsolvedReference The unsolved reference to solve.
     */
    private void replaceUnsolvedReference(Map<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference) {
        if (unsolvedReference.getRef().equals("matchingOperations") && unsolvedReference.getParent() instanceof XsdComplexType &&
                ((XsdComplexType)unsolvedReference.getParent()).getName() != null &&
                ((XsdComplexType)unsolvedReference.getParent()).getName().equals("matching")){
            int a = 5;
        }

        if (unsolvedReference.getRef().equals("matching") && unsolvedReference.getParent() instanceof XsdElement){
            int a = 5;
        }

        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef());

        if (concreteElements != null){
            Map<String, String> oldElementAttributes = unsolvedReference.getElement().getElementFieldsMap();

            for (NamedConcreteElement concreteElement : concreteElements) {
                NamedConcreteElement substitutionElementWrapper;

                if (!unsolvedReference.isTypeRef()){
                    XsdNamedElements substitutionElement = concreteElement.getElement().clone(oldElementAttributes);

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
                .stream()
                .filter(element -> element.getElement() instanceof XsdSchema)
                .map(element -> (XsdSchema) element.getElement());
    }

    /**
     * Adds an {@link UnsolvedReference} object to the {@link XsdParser#unsolvedElements} list which should be solved
     * at a later time in the parsing process.
     * @param unsolvedReference The unsolvedReference to add to the unsolvedElements list.
     */
    public void addUnsolvedReference(UnsolvedReference unsolvedReference){
        unsolvedElements.add(unsolvedReference);
    }

    /**
     * Adds a new file to the parsing queue. This new file appears by having xsd:import or xsd:include tags in the
     * original file to parse.
     * @param schemaLocation A new file path of another XSD file to parse.
     */
    public void addFileToParse(String schemaLocation) {
        if (!schemaLocations.contains(schemaLocation)){
            schemaLocations.add(schemaLocation);
        }
    }

    public static Map<String, String> getXsdTypesToJava() {
        return xsdTypesToJava;
    }

    public static Map<String, BiFunction<XsdParser, Node, ReferenceBase>> getParseMappers() {
        return parseMappers;
    }

    public void addParsedElement(ReferenceBase wrappedElement) {
        parseElements.add(wrappedElement);
    }

}