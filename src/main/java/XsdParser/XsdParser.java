package XsdParser;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.*;
import XsdElements.XsdRestrictionElements.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    public static List<String> builtInDataTypes;
    private static XsdParser instance;

    private List<ReferenceBase> elements = new ArrayList<>();
    private List<UnsolvedReference> unsolvedElements = new ArrayList<>();
    private Map<String, List<UnsolvedReferenceItem>> parserUnsolvedElementsMap = new HashMap<>();

    static {
        parseMappers = new HashMap<>();
        builtInDataTypes = new ArrayList<>();

        parseMappers.put(XsdAll.TAG, XsdAll::parse);
        parseMappers.put(XsdAttribute.TAG, XsdAttribute::parse);
        parseMappers.put(XsdAttributeGroup.TAG, XsdAttributeGroup::parse);
        parseMappers.put(XsdChoice.TAG, XsdChoice::parse);
        parseMappers.put(XsdComplexType.TAG, XsdComplexType::parse);
        parseMappers.put(XsdElement.TAG, XsdElement::parse);
        parseMappers.put(XsdGroup.TAG, XsdGroup::parse);
        parseMappers.put(XsdSequence.TAG, XsdSequence::parse);
        parseMappers.put(XsdSimpleType.TAG, XsdSimpleType::parse);
        parseMappers.put(XsdList.TAG, XsdList::parse);
        parseMappers.put(XsdRestriction.TAG, XsdRestriction::parse);
        parseMappers.put(XsdUnion.TAG, XsdUnion::parse);

        parseMappers.put(XsdEnumeration.TAG, XsdEnumeration::parse);
        parseMappers.put(XsdFractionDigits.TAG, XsdFractionDigits::parse);
        parseMappers.put(XsdLength.TAG, XsdLength::parse);
        parseMappers.put(XsdMaxExclusive.TAG, XsdMaxExclusive::parse);
        parseMappers.put(XsdMaxInclusive.TAG, XsdMaxInclusive::parse);
        parseMappers.put(XsdMaxLength.TAG, XsdMaxLength::parse);
        parseMappers.put(XsdMinExclusive.TAG, XsdMinExclusive::parse);
        parseMappers.put(XsdMinInclusive.TAG, XsdMinInclusive::parse);
        parseMappers.put(XsdMinLength.TAG, XsdMinLength::parse);
        parseMappers.put(XsdPattern.TAG, XsdPattern::parse);
        parseMappers.put(XsdTotalDigits.TAG, XsdTotalDigits::parse);
        parseMappers.put(XsdWhiteSpace.TAG, XsdWhiteSpace::parse);

        builtInDataTypes.add("xsd:anyURI");
        builtInDataTypes.add("xsd:boolean");
        builtInDataTypes.add("xsd:base64Binary");
        builtInDataTypes.add("xsd:hexBinary");
        builtInDataTypes.add("xsd:date");
        builtInDataTypes.add("xsd:dateTime");
        builtInDataTypes.add("xsd:time");
        builtInDataTypes.add("xsd:duration");
        builtInDataTypes.add("xsd:dayTimeDuration");
        builtInDataTypes.add("xsd:yearMonthDuration");
        builtInDataTypes.add("xsd:gDay");
        builtInDataTypes.add("xsd:gMonth");
        builtInDataTypes.add("xsd:gMonthDay");
        builtInDataTypes.add("xsd:gYear");
        builtInDataTypes.add("xsd:gYearMonth");
        builtInDataTypes.add("xsd:decimal");
        builtInDataTypes.add("xsd:integer");
        builtInDataTypes.add("xsd:nonPositiveInteger");
        builtInDataTypes.add("xsd:negativeInteger");
        builtInDataTypes.add("xsd:long");
        builtInDataTypes.add("xsd:int");
        builtInDataTypes.add("xsd:short");
        builtInDataTypes.add("xsd:byte");
        builtInDataTypes.add("xsd:nonNegativeInteger");
        builtInDataTypes.add("xsd:unsignedLong");
        builtInDataTypes.add("xsd:unsignedInt");
        builtInDataTypes.add("xsd:unsignedShort");
        builtInDataTypes.add("xsd:unsignedByte");
        builtInDataTypes.add("xsd:positiveInteger");
        builtInDataTypes.add("xsd:double");
        builtInDataTypes.add("xsd:float");
        builtInDataTypes.add("xsd:QName");
        builtInDataTypes.add("xsd:NOTATION");
        builtInDataTypes.add("xsd:string");
        builtInDataTypes.add("xsd:normalizedString");
        builtInDataTypes.add("xsd:token");
        builtInDataTypes.add("xsd:language");
        builtInDataTypes.add("xsd:NMTOKEN");
        builtInDataTypes.add("xsd:Name");
        builtInDataTypes.add("xsd:NCName");
        builtInDataTypes.add("xsd:ID");
        builtInDataTypes.add("xsd:IDREF");
        builtInDataTypes.add("xsd:ENTITY");
        builtInDataTypes.add("xsd:untypedAtomic");
    }

    public XsdParser(){
        instance = this;
    }

    /**
     * Parses a Xsd file and all the contained elements. This code iterates on the nodes and parses
     * the supported ones. The supported types are identified by their TAG, in parseMappers which maps
     * the xsd tag to a function that parses that xsd type.
     * @param filePath The file path to the xsd file.
     * @return A stream of parsed wrapped xsd elements.
     */
    public Stream<XsdAbstractElement> parse(String filePath) {
        //https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
        try {
            File xsdFile = new File(filePath);

            if (!xsdFile.exists()){
                throw new RuntimeException("The file doesn't exist");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xsdFile);

            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getFirstChild().getChildNodes();

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

        return elements.stream()
                        .filter(element -> element instanceof ConcreteElement)
                        .map(ReferenceBase::getElement);
    }

    /**
     * This method resolves all the remaining UnsolvedReferences. It starts by gathering all the named
     * elements and then iterates on the unsolvedElements List in order to find if any of the unsolvedReferences
     * can be solved by replacing the unsolvedElement by its matching ConcreteElement, present in the
     * concreteElementsMap. The unsolvedReference matches a ConcreteElement by having its ref attribute
     * with the same value as the name attribute of the ConcreteElement.
     * @param filePath The name of the file being parsed
     */
    private void resolveRefs(String filePath) {
        HashMap<String, ConcreteElement> concreteElementsMap = new HashMap<>();

        elements.stream()
                .filter(concreteElement -> concreteElement instanceof ConcreteElement)
                .map(concreteElement -> (ConcreteElement) concreteElement)
                .forEach(referenceElement -> concreteElementsMap.put(referenceElement.getName(), referenceElement));

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < unsolvedElements.size(); i++) {
            replaceUnsolvedReference(concreteElementsMap, unsolvedElements.get(i), filePath);
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
            HashMap<String, String> oldElementAttributes = unsolvedReference.getElement().getElementFieldsMap();

            XsdAbstractElement substitutionElement = concreteElement.getElement()
                                                                .createCopyWithAttributes(oldElementAttributes);

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
        unsolvedElements.add(unsolvedReference);
    }

    public static List<String> getBuiltInDataTypes() {
        return builtInDataTypes;
    }
}