package org.xmlet.xsdparser.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class XsdParser {

    /**
     * ParseMappers is a map that defines a function to each XsdElement type supported by this mapper,
     * this way, based on the XsdElement TAG, the according parsed is invoked.
     */
    private static final Map<String, Function<Node, ReferenceBase>> parseMappers;
    private static Map<String, String> xsdTypesToJava;
    private static XsdParser instance;

    private Map<String, List<ReferenceBase>> parseElements = new HashMap<>();
    private Map<String, List<UnsolvedReference>> unsolvedElements = new HashMap<>();
    private Map<String, List<UnsolvedReferenceItem>> parserUnsolvedElementsMap = new HashMap<>();
    private String currentFile;

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

    public XsdParser(){
        instance = this;
    }

    /**
     * Parses a Xsd file and all the contained parseElements. This code iterates on the nodes and parses
     * the supported ones. The supported types are identified by their TAG, in parseMappers which maps
     * the xsd tag to a function that parses that xsd type.
     * @param filePath The file path to the xsd file.
     * @return A stream of parsed wrapped xsd parseElements.
     */
    public Stream<XsdAbstractElement> parse(String filePath) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            File xsdFile = new File(filePath);

            if (!xsdFile.exists()){
                throw new FileNotFoundException();
            }

            currentFile = filePath;
            unsolvedElements.put(filePath, new ArrayList<>());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xsdFile);

            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getFirstChild().getChildNodes();

            ArrayList<ReferenceBase> elements = new ArrayList<>();

            parseElements.put(filePath, elements);

            for (int temp = 0; temp < nodes.getLength(); temp++) {
                Node node = nodes.item(temp);
                String nodeName = node.getNodeName();

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Function<Node, ReferenceBase> parseFunction = parseMappers.get(nodeName);

                    if (parseFunction != null){
                        elements.add(parseFunction.apply(node));
                    }
                }
            }
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Exception while parsing.", e);
        }

        resolveRefs(filePath);

        return parseElements.get(filePath).stream()
                .filter(element -> element instanceof ConcreteElement)
                .map(ReferenceBase::getElement);
    }

    /**
     * This method resolves all the remaining UnsolvedReferences. It starts by gathering all the named
     * parseElements and then iterates on the unsolvedElements List in order to find if any of the unsolvedReferences
     * can be solved by replacing the unsolvedElement by its matching ConcreteElement, present in the
     * concreteElementsMap. The unsolvedReference matches a ConcreteElement by having its ref attribute
     * with the same value as the name attribute of the ConcreteElement.
     * @param filePath The name of the file being parsed
     */
    private void resolveRefs(String filePath) {
        HashMap<String, List<NamedConcreteElement>> concreteElementsMap = new HashMap<>();

        parseElements
                .get(filePath)
                .stream()
                .filter(concreteElement -> concreteElement instanceof NamedConcreteElement)
                .map(concreteElement -> (NamedConcreteElement) concreteElement)
                .forEach(referenceElement -> {
                    List<NamedConcreteElement> list = concreteElementsMap.get(referenceElement.getName());

                    if (list != null){
                        list.add(referenceElement);
                    } else {
                        List<NamedConcreteElement> newList = new ArrayList<>();

                        newList.add(referenceElement);

                        concreteElementsMap.put(referenceElement.getName(), newList);
                    }
                });

        List<UnsolvedReference> unsolvedElementsList = unsolvedElements.get(filePath);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < unsolvedElementsList.size(); i++) {
            replaceUnsolvedReference(concreteElementsMap, unsolvedElementsList.get(i), filePath);
        }
    }

    /**
     * Replaces a single unsolved reference, with the respective concreteElement.
     * If there isn't a concreteElement to replace the unsolved reference object, information is stored
     * informing the user of this Project of the occurrence.
     * @param concreteElementsMap The map containing all named concreteElements.
     * @param unsolvedReference The unsolved reference to solve.
     * @param filePath The name of the file being parsed.
     */
    private void replaceUnsolvedReference(HashMap<String, List<NamedConcreteElement>> concreteElementsMap, UnsolvedReference unsolvedReference, String filePath) {
        List<NamedConcreteElement> concreteElements = concreteElementsMap.get(unsolvedReference.getRef());

        if (concreteElements != null){
            Map<String, String> oldElementAttributes = unsolvedReference.getElement().getElementFieldsMap();

            for (NamedConcreteElement concreteElement : concreteElements) {
                XsdReferenceElement substitutionElement = concreteElement.getElement().clone(oldElementAttributes);

                NamedConcreteElement substitutionElementWrapper = (NamedConcreteElement) ReferenceBase.createFromXsd(substitutionElement);

                unsolvedReference.getParent().replaceUnsolvedElements(substitutionElementWrapper);
            }
        } else {
            storeUnsolvedItem(filePath, unsolvedReference);
        }
    }

    /**
     * Saves an occurrence of an element which couldn't be resolved in the parsedUnsolvedElementsMap,
     * which can be accessed at the end of the parsing process in order to verify if were there
     * were any references that couldn't be solved.
     * @param filePath The name of the file being parsed.
     * @param unsolvedReference The unsolved reference which couldn't be resolved.
     */
    private void storeUnsolvedItem(String filePath, UnsolvedReference unsolvedReference) {
        List<UnsolvedReferenceItem> unsolvedReferenceItemList = parserUnsolvedElementsMap.getOrDefault(filePath, new ArrayList<>());

        if (unsolvedReferenceItemList.isEmpty()){
            unsolvedReferenceItemList.add(new UnsolvedReferenceItem(unsolvedReference));

            parserUnsolvedElementsMap.put(filePath, unsolvedReferenceItemList);
        } else {
            Optional<UnsolvedReferenceItem> innerEntry = unsolvedReferenceItemList.stream()
                    .filter(unsolvedReferenceObj -> unsolvedReferenceObj.getUnsolvedReference().getRef().equals(unsolvedReference.getRef()))
                    .findFirst();

            if (innerEntry.isPresent()){
                innerEntry.ifPresent(entry -> entry.getParents().add(unsolvedReference.getParent()));
            } else {
                unsolvedReferenceItemList.add(new UnsolvedReferenceItem(unsolvedReference));
            }
        }
    }

    public List<UnsolvedReferenceItem> getUnsolvedReferencesForFile(String filePath){
        return parserUnsolvedElementsMap.getOrDefault(filePath, new ArrayList<>());
    }

    public static XsdParser getInstance(){
        return instance;
    }

    public void addUnsolvedReference(UnsolvedReference unsolvedReference){
        unsolvedElements.get(currentFile).add(unsolvedReference);
    }

    public static Map<String, String> getXsdTypesToJava() {
        return xsdTypesToJava;
    }

    public static Map<String, Function<Node, ReferenceBase>> getParseMappers() {
        return parseMappers;
    }
}