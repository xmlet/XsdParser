package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import XsdParser.XsdParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class XsdAbstractElement {

    protected HashMap<String, String> elementFieldsMap = new HashMap<>();

    private static final String ID = "id";
    public static final String NAME = "name";
    static final String ABSTRACT = "abstract";
    static final String DEFAULT_ELEMENT = "defaultElement";
    static final String FIXED = "fixed";
    static final String TYPE = "type";
    static final String MIXED = "mixed";
    static final String BLOCK = "block";
    static final String FINAL = "final";
    static final String SUBSTITUTION_GROUP = "substitutionGroup";
    static final String DEFAULT = "default";
    static final String FORM = "form";
    static final String NILLABLE = "nillable";
    static final String MIN_OCCURS = "maxOccurs";
    static final String MAX_OCCURS = "minOccurs";
    static final String ITEM_TYPE = "itemType";
    static final String BASE = "base";
    static final String MEMBER_TYPES = "memberTypes";
    public static final String VALUE = "value";

    private String id;
    private XsdAbstractElement parent;

    protected XsdAbstractElement(){

    }

    protected XsdAbstractElement(XsdAbstractElement parent) {
        setParent(parent);
    }

    protected XsdAbstractElement(HashMap<String, String> elementFieldsMap){
        setFields(elementFieldsMap);
    }

    protected XsdAbstractElement(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap){
        setParent(parent);
        setFields(elementFieldsMap);
    }

    /**
     * This method serves as a base to all XsdElements which need to set their class specific attributes.
     * @param elementFieldsMap The node map containing all attributes of a XSDElement
     */
    public void setFields(HashMap<String, String> elementFieldsMap){
        if (elementFieldsMap != null){
            this.elementFieldsMap = elementFieldsMap;

            this.id = elementFieldsMap.getOrDefault(ID, id);
        }
    }

    public HashMap<String, String> getElementFieldsMap() {
        return elementFieldsMap;
    }

    /**
     * Obtains the visitor of an XsdAbstractElement instance.
     * @return The concrete visitor instance.
     */
    public abstract Visitor getVisitor();

    public abstract void accept(Visitor visitor);

    /**
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A deep copy of the object from which is called upon.
     */
    public abstract XsdAbstractElement clone(HashMap<String, String> placeHolderAttributes);

    protected abstract List<ReferenceBase> getElements();

    public Stream<XsdAbstractElement> getXsdElements(){
        List<ReferenceBase> elements = getElements();

        if (elements == null){
            return new ArrayList<XsdAbstractElement>().stream();
        }

        return elements.stream().filter(element -> element instanceof ConcreteElement).map(ReferenceBase::getElement);
    }

    /**
     * The base code for parsing an XsdAbstractElement. All the concrete implementations of this
     * class should call this method in order to parse its contents.
     * @param node The node from where the element will be parsed
     * @param element The concrete element that will be populated and returned
     * @return A wrapper object that contains the parsed XSD object.
     */
    static ReferenceBase xsdParseSkeleton(Node node, XsdAbstractElement element){
        Node child = node.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();

                Function<Node, ReferenceBase> parserFunction = XsdParser.parseMappers.get(nodeName);

                if (parserFunction != null){
                    parserFunction.apply(child).getElement().accept(element.getVisitor());
                }
            }

            child = child.getNextSibling();
        }

        return ReferenceBase.createFromXsd(element);
    }

    protected static HashMap<String, String> convertNodeMap(NamedNodeMap nodeMap){
        HashMap<String, String> attributesMapped = new HashMap<>();

        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            attributesMapped.put(node.getNodeName(), node.getNodeValue());
        }

        return attributesMapped;
    }

    /**
     * This method iterates on the current element children and replaces any UnsolvedReference object that has a
     * ref attribute that matches a ConcreteElement name attribute
     * @param element A concrete element that will replace a unsolved reference, if existent
     */
    public void replaceUnsolvedElements(ConcreteElement element){
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
     * @return The parent of the current XsdAbstractElement.
     */
    public XsdAbstractElement getParent() {
        return parent;
    }

    protected void setParent(XsdAbstractElement parent) {
        this.parent = parent;
    }

}
