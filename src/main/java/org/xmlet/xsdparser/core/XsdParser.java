package org.xmlet.xsdparser.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class XsdParser {

    /**
     * ParseMappers is a map that defines a function to each XsdElement type supported by this mapper,
     * this way, based on the XsdElement TAG, the according parsed is invoked.
     */
    public static HashMap<String, Function<Node, ReferenceBase>> parseMappers;
    private static HashMap<String, String> xsdTypesToJava;
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

        xsdTypesToJava.put("xsd:anyURI", "String");
        xsdTypesToJava.put("xs:anyURI", "String");
        xsdTypesToJava.put("xsd:boolean", "Boolean");
        xsdTypesToJava.put("xs:boolean", "Boolean");
        //xsdTypesToJava.put("xsd:base64Binary", "[B");
        //xsdTypesToJava.put("xsd:hexBinary", "[B");
        xsdTypesToJava.put("xsd:date", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:date", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:dateTime", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:dateTime", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:time", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:time", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:duration", "Duration");
        xsdTypesToJava.put("xs:duration", "Duration");
        xsdTypesToJava.put("xsd:dayTimeDuration", "Duration");
        xsdTypesToJava.put("xs:dayTimeDuration", "Duration");
        xsdTypesToJava.put("xsd:yearMonthDuration", "Duration");
        xsdTypesToJava.put("xs:yearMonthDuration", "Duration");
        xsdTypesToJava.put("xsd:gDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:gDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:gMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gMonthDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:gMonthDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gYear", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:gYear", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gYearMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xs:gYearMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:decimal", "BigDecimal");
        xsdTypesToJava.put("xs:decimal", "BigDecimal");
        xsdTypesToJava.put("xsd:integer", "BigInteger");
        xsdTypesToJava.put("xs:integer", "BigInteger");
        xsdTypesToJava.put("xsd:nonPositiveInteger", "BigInteger");
        xsdTypesToJava.put("xs:nonPositiveInteger", "BigInteger");
        xsdTypesToJava.put("xsd:negativeInteger", "BigInteger");
        xsdTypesToJava.put("xs:negativeInteger", "BigInteger");
        xsdTypesToJava.put("xsd:long", "Long");
        xsdTypesToJava.put("xs:long", "Long");
        xsdTypesToJava.put("xsd:int", "Integer");
        xsdTypesToJava.put("xs:int", "Integer");
        xsdTypesToJava.put("xsd:short", "Short");
        xsdTypesToJava.put("xs:short", "Short");
        xsdTypesToJava.put("xsd:byte", "Byte");
        xsdTypesToJava.put("xs:byte", "Byte");
        xsdTypesToJava.put("xsd:nonNegativeInteger", "BigInteger");
        xsdTypesToJava.put("xs:nonNegativeInteger", "BigInteger");
        xsdTypesToJava.put("xsd:unsignedLong", "BigInteger");
        xsdTypesToJava.put("xs:unsignedLong", "BigInteger");
        xsdTypesToJava.put("xsd:unsignedInt", "Long");
        xsdTypesToJava.put("xs:unsignedInt", "Long");
        xsdTypesToJava.put("xsd:unsignedShort", "Integer");
        xsdTypesToJava.put("xs:unsignedShort", "Integer");
        xsdTypesToJava.put("xsd:unsignedByte", "Short");
        xsdTypesToJava.put("xs:unsignedByte", "Short");
        xsdTypesToJava.put("xsd:positiveInteger", "BigInteger");
        xsdTypesToJava.put("xs:positiveInteger", "BigInteger");
        xsdTypesToJava.put("xsd:double", "Double");
        xsdTypesToJava.put("xs:double", "Double");
        xsdTypesToJava.put("xsd:float", "Float");
        xsdTypesToJava.put("xs:float", "Float");
        xsdTypesToJava.put("xsd:QName", "QName");
        xsdTypesToJava.put("xs:QName", "QName");
        xsdTypesToJava.put("xsd:NOTATION", "QName");
        xsdTypesToJava.put("xs:NOTATION", "QName");
        xsdTypesToJava.put("xsd:string", "String");
        xsdTypesToJava.put("xs:string", "String");
        xsdTypesToJava.put("xsd:normalizedString", "String");
        xsdTypesToJava.put("xs:normalizedString", "String");
        xsdTypesToJava.put("xsd:token", "String");
        xsdTypesToJava.put("xs:token", "String");
        xsdTypesToJava.put("xsd:language", "String");
        xsdTypesToJava.put("xs:language", "String");
        xsdTypesToJava.put("xsd:NMTOKEN", "String");
        xsdTypesToJava.put("xs:NMTOKEN", "String");
        xsdTypesToJava.put("xsd:Name", "String");
        xsdTypesToJava.put("xs:Name", "String");
        xsdTypesToJava.put("xsd:NCName", "String");
        xsdTypesToJava.put("xs:NCName", "String");
        xsdTypesToJava.put("xsd:ID", "String");
        xsdTypesToJava.put("xs:ID", "String");
        xsdTypesToJava.put("xsd:IDREF", "String");
        xsdTypesToJava.put("xs:IDREF", "String");
        xsdTypesToJava.put("xsd:ENTITY", "String");
        xsdTypesToJava.put("xs:ENTITY", "String");
        xsdTypesToJava.put("xsd:untypedAtomic", "String");
        xsdTypesToJava.put("xs:untypedAtomic", "String");
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
                throw new RuntimeException("The file doesn't exist");
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
            e.printStackTrace();
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
        HashMap<String, ConcreteElement> concreteElementsMap = new HashMap<>();

        parseElements
                .get(filePath)
                .stream()
                .filter(concreteElement -> concreteElement instanceof ConcreteElement)
                .map(concreteElement -> (ConcreteElement) concreteElement)
                .forEach(referenceElement -> concreteElementsMap.put(referenceElement.getName(), referenceElement));

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
    private void replaceUnsolvedReference(HashMap<String, ConcreteElement> concreteElementsMap, UnsolvedReference unsolvedReference, String filePath) {
        ConcreteElement concreteElement = concreteElementsMap.get(unsolvedReference.getRef());

        if (concreteElement != null){
            Map<String, String> oldElementAttributes = unsolvedReference.getElement().getElementFieldsMap();

            XsdAbstractElement substitutionElement = concreteElement.getElement()
                    .clone(oldElementAttributes);

            ConcreteElement substitutionElementWrapper = (ConcreteElement) ReferenceBase.createFromXsd(substitutionElement);

            unsolvedReference.getParent().replaceUnsolvedElements(substitutionElementWrapper);
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

    public static HashMap<String, String> getXsdTypesToJava() {
        return xsdTypesToJava;
    }
}