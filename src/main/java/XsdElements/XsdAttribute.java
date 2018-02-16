package XsdElements;

import XsdElements.ElementsWrapper.ConcreteElement;
import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.Visitors.Visitor;
import XsdParser.XsdParser;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XsdAttribute extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:attribute";
    public static final String XS_TAG = "xs:attribute";

    private AttributeVisitor visitor = new AttributeVisitor();

    private ReferenceBase simpleType;

    private String defaultElement;
    private String fixed;
    private String type;

    private XsdAttribute(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdAttribute(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    private XsdAttribute(XsdAbstractElement parent) {
        super(parent);
    }

    @Override
    public void setFields(HashMap<String, String> elementFieldsMap) {
        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            this.defaultElement = elementFieldsMap.getOrDefault(DEFAULT_ELEMENT, defaultElement);
            this.fixed = elementFieldsMap.getOrDefault(FIXED, fixed);
            this.type = elementFieldsMap.getOrDefault(TYPE, type);

            if (type != null && !XsdParser.getXsdTypesToJava().containsKey(type)){
                XsdAttribute placeHolder = new XsdAttribute(this);
                this.simpleType = new UnsolvedReference(type, placeHolder);
                XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.simpleType);
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    @Override
    public XsdAttribute clone(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdAttribute copy = new XsdAttribute(this.getParent(), placeHolderAttributes);

        copy.simpleType = this.simpleType;

        return copy;
    }

    @Override
    protected void setParent(XsdAbstractElement parent) {
        super.setParent(parent);
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement elementWrapper) {
        super.replaceUnsolvedElements(elementWrapper);

        XsdAbstractElement element = elementWrapper.getElement();

        if (element instanceof XsdSimpleType && simpleType != null && type.equals(elementWrapper.getName())){
            this.simpleType = elementWrapper;
        }
    }

    public XsdSimpleType getXsdSimpleType(){
        return simpleType instanceof ConcreteElement ? (XsdSimpleType) simpleType.getElement() : null;
    }

    public String getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public List<XsdRestriction> getAllRestrictions(){
        XsdSimpleType simpleType = getXsdSimpleType();

        if (simpleType != null){
            return simpleType.getAllRestrictions();
        }

        return new ArrayList<>();
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAttribute(convertNodeMap(node.getAttributes())));
    }

    class AttributeVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAttribute.this;
        }

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);

            XsdAttribute.this.simpleType = ReferenceBase.createFromXsd(element);
        }
    }
}
