package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class XsdElement extends XsdReferenceElement {

    public static final String XSD_TAG = "xsd:element";
    public static final String XS_TAG = "xs:element";

    private XsdElementVisitor xsdElementVisitor = new ElementXsdElementVisitor();

    private ReferenceBase complexType;
    private ReferenceBase simpleType;

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

    public XsdElement(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam){
        super.setFields(elementFieldsMapParam);

        String typeString = elementFieldsMap.get(TYPE_TAG);

        if (typeString != null){
            if (XsdParser.getXsdTypesToJava().containsKey(typeString)){
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put(NAME_TAG, typeString);
                XsdComplexType placeHolder = new XsdComplexType(attributes);
                placeHolder.setParent(this);
                this.type = ReferenceBase.createFromXsd(placeHolder);
            } else {
                XsdElement placeHolder = new XsdElement( new HashMap<>());
                placeHolder.setParent(this);
                this.type = new UnsolvedReference(typeString, placeHolder);
                XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.type);
            }
        }

        this.substitutionGroup = elementFieldsMap.getOrDefault(SUBSTITUTION_GROUP_TAG, substitutionGroup);
        this.defaultObj = elementFieldsMap.getOrDefault(DEFAULT_TAG, defaultObj);
        this.fixed = elementFieldsMap.getOrDefault(FIXED_TAG, fixed);
        this.form = elementFieldsMap.getOrDefault(FORM_TAG, form);
        this.nillable = Boolean.parseBoolean(elementFieldsMap.getOrDefault(NILLABLE_TAG, "false"));
        this.abstractObj = Boolean.parseBoolean(elementFieldsMap.getOrDefault(ABSTRACT_TAG, "false"));
        this.block = elementFieldsMap.getOrDefault(BLOCK_TAG, block);
        this.finalObj = elementFieldsMap.getOrDefault(FINAL_TAG, finalObj);
        this.minOccurs = Integer.parseInt(elementFieldsMap.getOrDefault(MIN_OCCURS_TAG, "1"));
        this.maxOccurs = elementFieldsMap.getOrDefault(MAX_OCCURS_TAG, "1");
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return xsdElementVisitor;
    }

    @Override
    public XsdElement clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(TYPE_TAG);
        placeHolderAttributes.remove(REF_TAG);

        XsdElement elementCopy = new XsdElement(placeHolderAttributes);
        elementCopy.setParent(this.parent);

        elementCopy.type = this.type;

        return elementCopy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.type != null && this.type instanceof UnsolvedReference && element.getElement() instanceof XsdComplexType && ((UnsolvedReference) this.type).getRef().equals(element.getName())){
            this.type = element;
            element.getElement().setParent(this);
        }
    }

    public XsdComplexType getXsdComplexType() {
        return complexType == null ? getXsdType() : (XsdComplexType) complexType.getElement();
    }

    public XsdSimpleType getXsdSimpleType(){
        return simpleType instanceof ConcreteElement ? (XsdSimpleType) simpleType.getElement() : null;
    }

    private XsdComplexType getXsdType(){
        if (type != null && type instanceof ConcreteElement){
            return (XsdComplexType) type.getElement();
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

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);
            XsdElement.this.simpleType = ReferenceBase.createFromXsd(element);
        }
    }
}
