package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class serves as a base to every element class, i.e. {@link XsdElement} or {@link XsdAttribute} etc.
 */
public abstract class XsdAbstractElement {

    /**
     * A Map object containing the keys/values of the attributes that belong to the concrete element instance.
     */
    protected Map<String, String> elementFieldsMap = new HashMap<>();

    static final String ID_TAG = "id";
    public static final String NAME_TAG = "name";
    static final String ABSTRACT_TAG = "abstract";
    static final String DEFAULT_ELEMENT_TAG = "defaultElement";
    protected static final String FIXED_TAG = "fixed";
    static final String TYPE_TAG = "type";
    static final String MIXED_TAG = "mixed";
    static final String BLOCK_TAG = "block";
    static final String FINAL_TAG = "final";
    static final String USE_TAG = "use";
    static final String SUBSTITUTION_GROUP_TAG = "substitutionGroup";
    static final String DEFAULT_TAG = "default";
    static final String FORM_TAG = "form";
    static final String NILLABLE_TAG = "nillable";
    static final String MIN_OCCURS_TAG = "minOccurs";
    static final String MAX_OCCURS_TAG = "maxOccurs";
    static final String ITEM_TYPE_TAG = "itemType";
    static final String BASE_TAG = "base";
    static final String SOURCE_TAG = "source";
    static final String XML_LANG_TAG = "xml:lang";
    static final String MEMBER_TYPES_TAG = "memberTypes";
    static final String SCHEMA_LOCATION = "schemaLocation";
    static final String NAMESPACE = "namespace";
    public static final String REF_TAG = "ref";
    protected static final String VALUE_TAG = "value";

    /**
     * The instance which contains the present element.
     */
    XsdAbstractElement parent;

    protected XsdAbstractElement(@NotNull Map<String, String> elementFieldsMapParam){
        setFields(elementFieldsMapParam);
    }

    /**
     * This method serves as a base to all {@link XsdAbstractElement} concrete instances which need to set their class
     * specific fields.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam){
        this.elementFieldsMap = elementFieldsMapParam;
    }

    public Map<String, String> getElementFieldsMap() {
        return elementFieldsMap;
    }

    /**
     * Obtains the visitor of a concrete {@link XsdAbstractElement} instance.
     * @return The concrete visitor instance.
     */
    public abstract XsdAbstractElementVisitor getVisitor();

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
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                Function<Node, ReferenceBase> parserFunction = XsdParser.getParseMappers().get(nodeName);

                if (parserFunction != null){
                    parserFunction.apply(child).getElement().accept(element.getVisitor());
                }
            }

            child = child.getNextSibling();
        }

        return ReferenceBase.createFromXsd(element);
    }

    /**
     * Converts a {@link NamedNodeMap} to a Java Map object. This is meant to simplify the manipulation of the
     * information.
     * @param nodeMap The NamedNodeMap to convert to a Map object.
     * @return The Map object that was generated by the conversion.
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
                .filter(unsolvedReference -> unsolvedReference.getRef().equals(element.getName()))
                .findFirst()
                .ifPresent(oldElement -> elements.set(elements.indexOf(oldElement), element));
        }
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
     * @param node The node containing the xsd:appInfo or xsd:documentation information.
     * @return The textual value contained in the node parameter.
     */
    static String xsdRawContentParse(Node node) {
        Node child = node.getFirstChild();
        StringBuilder stringBuilder = new StringBuilder();

        while (child != null) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                stringBuilder.append(child.getTextContent());
            }

            child = child.getNextSibling();
        }

        return stringBuilder.toString();
    }
}
