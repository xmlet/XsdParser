package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.*;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ConfigEntryData;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class serves as a base to every element class, i.e. {@link XsdElement}, {@link XsdAttribute}, etc.
 */
public abstract class XsdAbstractElement {

    /**
     * A {@link Map} object containing the keys/values of the attributes that belong to the concrete element instance.
     */
    protected Map<String, String> attributesMap;

    public static final String ATTRIBUTE_FORM_DEFAULT = "attribtueFormDefault";
    public static final String ELEMENT_FORM_DEFAULT = "elementFormDefault";
    public static final String BLOCK_DEFAULT = "blockDefault";
    public static final String FINAL_DEFAULT = "finalDefault";
    public static final String TARGET_NAMESPACE = "targetNamespace";
    public static final String VERSION = "version";
    public static final String XMLNS = "xmlns";

    public static final String ID_TAG = "id";
    public static final String NAME_TAG = "name";
    public static final String ABSTRACT_TAG = "abstract";
    public static final String DEFAULT_ELEMENT_TAG = "default";
    public static final String FIXED_TAG = "fixed";
    public static final String TYPE_TAG = "type";
    public static final String MIXED_TAG = "mixed";
    public static final String BLOCK_TAG = "block";
    public static final String FINAL_TAG = "final";
    public static final String USE_TAG = "use";
    public static final String SUBSTITUTION_GROUP_TAG = "substitutionGroup";
    public static final String DEFAULT_TAG = "default";
    public static final String FORM_TAG = "form";
    public static final String NILLABLE_TAG = "nillable";
    public static final String MIN_OCCURS_TAG = "minOccurs";
    public static final String MAX_OCCURS_TAG = "maxOccurs";
    public static final String ITEM_TYPE_TAG = "itemType";
    public static final String BASE_TAG = "base";
    public static final String SOURCE_TAG = "source";
    public static final String XML_LANG_TAG = "xml:lang";
    public static final String MEMBER_TYPES_TAG = "memberTypes";
    public static final String SCHEMA_LOCATION = "schemaLocation";
    public static final String NAMESPACE = "namespace";
    public static final String REF_TAG = "ref";
    protected static final String VALUE_TAG = "value";

    /**
     * The instance which contains the present element.
     */
    XsdAbstractElement parent;

    /**
     * The {@link XsdParserCore} instance that parsed this element.
     */
    XsdParserCore parser;

    /**
     * The visitor instance for this element.
     */
    XsdAbstractElementVisitor visitor;

    protected final Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction;

    protected XsdAbstractElement(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction){
        this.parser = parser;
        this.attributesMap = attributesMap;
        this.visitorFunction = visitorFunction;

        if(visitorFunction != null){
            this.visitor = visitorFunction.apply(this);
        }
    }

    public Map<String, String> getAttributesMap() {
        return attributesMap;
    }

    /**
     * Obtains the visitor of a concrete {@link XsdAbstractElement} instance.
     * @return The concrete visitor instance.
     */
    public XsdAbstractElementVisitor getVisitor(){
        return visitor;
    }

    /**
     * Runs verifications on each concrete element to ensure that the XSD schema rules are verified.
     */
    public void validateSchemaRules(){ }

    /**
     * Base method for all accept methods. It serves as a way to guarantee that every accept call assigns the parent
     * field.
     * @param xsdAbstractElementVisitor The visitor that is visiting the current instance.
     */
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor){
        this.setParent(xsdAbstractElementVisitor.getOwner());
    }

    public List<ReferenceBase> getElements(){
        return Collections.emptyList();
    }

    /**
     * @return All the {@link ConcreteElement} objects present in the concrete implementation of the
     * {@link XsdAbstractElement} class. It doesn't return the {@link UnsolvedReference} objects.
     */
    public Stream<XsdAbstractElement> getXsdElements(){
        List<ReferenceBase> elements = getElements();

        if (elements == null){
            return new ArrayList<XsdAbstractElement>().stream();
        }

        return elements.stream().filter(element -> element instanceof ConcreteElement).map(ReferenceBase::getElement);
    }

    /**
     * The base code for parsing any {@link XsdAbstractElement}. All the concrete implementations of this class should
     * call this method in order to parse its children.
     * @param node The node from where the element will be parsed.
     * @param element The concrete element that will be populated and returned.
     * @return A wrapper object that contains the parsed XSD object.
     */
    static ReferenceBase xsdParseSkeleton(Node node, XsdAbstractElement element){
        XsdParserCore parser = element.getParser();
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                ConfigEntryData configEntryData = XsdParserCore.getParseMappers().get(nodeName);

                if (configEntryData != null && configEntryData.parserFunction != null){
                    XsdAbstractElement childElement = configEntryData.parserFunction.apply(new ParseData(parser, child, configEntryData.visitorFunction)).getElement();

                    childElement.accept(element.getVisitor());

                    childElement.validateSchemaRules();
                }
            }

            child = child.getNextSibling();
        }

        ReferenceBase wrappedElement = ReferenceBase.createFromXsd(element);

        parser.addParsedElement(wrappedElement);

        return wrappedElement;
    }

    public XsdParserCore getParser() {
        return parser;
    }

    /**
     * Converts a {@link NamedNodeMap} to a {@link Map} object. This is meant to simplify the manipulation of the
     * information.
     * @param nodeMap The {@link NamedNodeMap} to convert to a {@link Map} object.
     * @return The {@link Map} object that was generated by the conversion.
     */
    protected static Map<String, String> convertNodeMap(NamedNodeMap nodeMap){
        HashMap<String, String> attributesMapped = new HashMap<>();

        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            attributesMapped.put(node.getNodeName(), node.getNodeValue());
        }

        return attributesMapped;
    }

    /**
     * This method iterates on the current element children and replaces any {@link UnsolvedReference} object that has a
     * ref attribute that matches the receiving {@link NamedConcreteElement} name attribute.
     * @param element A fully parsed element with a name that will replace an {@link UnsolvedReference} object, if a
     *                match between the {@link NamedConcreteElement} name attribute and the {@link UnsolvedReference}
     *                ref attribute.
     */
    public void replaceUnsolvedElements(NamedConcreteElement element){
        List<ReferenceBase> elements = this.getElements();

        if (elements != null){
            elements.stream()
                .filter(referenceBase -> referenceBase instanceof UnsolvedReference)
                .map(referenceBase -> (UnsolvedReference) referenceBase)
                .filter(unsolvedReference -> compareReference(element, unsolvedReference))
                .findFirst()
                .ifPresent(oldElement -> elements.set(elements.indexOf(oldElement), element));
        }
    }

    public static boolean compareReference(NamedConcreteElement element, UnsolvedReference reference){
        return compareReference(element, reference.getRef());
    }

    static boolean compareReference(NamedConcreteElement element, String unsolvedRef){
        if (unsolvedRef.contains(":")){
            unsolvedRef = unsolvedRef.substring(unsolvedRef.indexOf(":") + 1);
        }

        return element.getName().equals(unsolvedRef);
    }

    /**
     * @return The parent of the current {@link XsdAbstractElement} object.
     */
    public XsdAbstractElement getParent() {
        return parent;
    }

    public void setParent(XsdAbstractElement parent) {
        this.parent = parent;
    }

    /**
     * In special cases such as {@link XsdAppInfo} and {@link XsdDocumentation} the contents are a simple text node,
     * in which case this function is more suited than using the {@link XsdAbstractElement#xsdParseSkeleton} since
     * those types of elements can't have children nodes.
     * @param node The {@link Node} containing either a {@link XsdAppInfo} or {@link XsdDocumentation}.
     * @return The textual value contained in the {@link Node} parameter.
     */
    static String xsdRawContentParse(Node node) {
        StringBuilder stringBuilder = new StringBuilder();

        NodeList children = node.getChildNodes();

        try {
            for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
                Node child = children.item(childIndex);

                StringWriter writer = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(new DOMSource(child), new StreamResult(writer));
                String output = writer.toString().trim();
                output = output.substring(output.indexOf('>') + 1).trim();
                stringBuilder.append(output);
            }
        } catch (Exception e){
            throw new ParsingException(e.getMessage());
        }

        return stringBuilder.toString();
    }
}
