package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XsdElement extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:element";
    public static final String XS_TAG = "xs:element";

    private XsdElementVisitor xsdElementVisitor = new ElementXsdElementVisitor();

    private ReferenceBase complexType;

    private ReferenceBase type;
    private String substitutionGroup;
    private String defaultObj;
    private String fixed;
    private String form;
    private boolean nillable;
    private boolean abstractObj;
    private String block;
    private String finalObj;
    private Integer minOccurs;
    private String maxOccurs;

    private XsdElement(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    public XsdElement(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    public void setFields(Map<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            String type = elementFieldsMap.get(TYPE);

            if (type != null){
                if (XsdParser.getXsdTypesToJava().containsKey(type)){
                    HashMap<String, String> attributes = new HashMap<>();
                    attributes.put(NAME, type);
                    this.type = ReferenceBase.createFromXsd(new XsdComplexType(this, attributes));
                } else {
                    XsdElement placeHolder = new XsdElement(this, null);
                    this.type = new UnsolvedReference(type, placeHolder);
                    XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.type);
                }
            }

            this.substitutionGroup = elementFieldsMap.getOrDefault(SUBSTITUTION_GROUP, substitutionGroup);
            this.defaultObj = elementFieldsMap.getOrDefault(DEFAULT, defaultObj);
            this.fixed = elementFieldsMap.getOrDefault(FIXED, fixed);
            this.form = elementFieldsMap.getOrDefault(FORM, form);
            this.nillable = Boolean.parseBoolean(elementFieldsMap.getOrDefault(NILLABLE, "false"));
            this.abstractObj = Boolean.parseBoolean(elementFieldsMap.getOrDefault(ABSTRACT, "false"));
            this.block = elementFieldsMap.getOrDefault(BLOCK, block);
            this.finalObj = elementFieldsMap.getOrDefault(FINAL, finalObj);
            this.minOccurs = Integer.parseInt(elementFieldsMap.getOrDefault(MIN_OCCURS, "1"));
            this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS, "1");
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    @Override
    public XsdElement clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        return new XsdElement(this.getParent(), placeHolderAttributes);
    }

    @Override
    public void replaceUnsolvedElements(ConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.type != null && this.type instanceof UnsolvedReference && ((UnsolvedReference) this.type).getRef().equals(element.getName())){
            this.type = element;
            element.getElement().setParent(this);
        }
    }

    public XsdComplexType getXsdComplexType() {
        return complexType == null ? type == null ? null : (XsdComplexType) type.getElement() : (XsdComplexType) complexType.getElement();
    }

    @SuppressWarnings("unused")
    public XsdAbstractElement getXsdType(){
        if (type != null && type instanceof ConcreteElement){
            return type.getElement();
        }

        return null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdElement(convertNodeMap(node.getAttributes())));
    }

    public String getFinal() {
        return finalObj;
    }

    @SuppressWarnings("unused")
    public boolean isNillable() {
        return nillable;
    }

    @SuppressWarnings("unused")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @SuppressWarnings("unused")
    public String getMaxOccurs() {
        return maxOccurs;
    }

    @SuppressWarnings("unused")
    public boolean isAbstractObj() {
        return abstractObj;
    }

    class ElementXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdElement.this;
        }

        @Override
        public void visit(XsdComplexType element) {
            super.visit(element);
            XsdElement.this.complexType = ReferenceBase.createFromXsd(element);
        }
    }
}
