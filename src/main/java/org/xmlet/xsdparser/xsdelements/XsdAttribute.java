package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XsdAttribute extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:attribute";
    public static final String XS_TAG = "xs:attribute";

    private AttributeXsdElementVisitor visitor = new AttributeXsdElementVisitor();

    private ReferenceBase simpleType;

    private String defaultElement;
    private String fixed;
    private String type;
    private String form;
    private String use;

    private XsdAttribute(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdAttribute(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap) {
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            super.setFields(elementFieldsMap);

            this.defaultElement = elementFieldsMap.getOrDefault(DEFAULT_ELEMENT, defaultElement);
            this.fixed = elementFieldsMap.getOrDefault(FIXED, fixed);
            this.type = elementFieldsMap.getOrDefault(TYPE, type);
            this.form = elementFieldsMap.getOrDefault(FORM, form);
            this.use = elementFieldsMap.getOrDefault(USE, "optional");

            if (type != null && !XsdParser.getXsdTypesToJava().containsKey(type)){
                XsdAttribute placeHolder = new XsdAttribute(this, null);
                this.simpleType = new UnsolvedReference(type, placeHolder);
                XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.simpleType);
            }
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return visitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    @Override
    public XsdAttribute clone(Map<String, String> placeHolderAttributes) {
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
    public String getUse() {
        return use;
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

    class AttributeXsdElementVisitor extends AnnotatedXsdElementVisitor {

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
