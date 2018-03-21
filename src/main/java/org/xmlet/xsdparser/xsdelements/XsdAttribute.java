package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
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

    private XsdAttribute(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.defaultElement = elementFieldsMap.getOrDefault(DEFAULT_ELEMENT_TAG, defaultElement);
        this.fixed = elementFieldsMap.getOrDefault(FIXED_TAG, fixed);
        this.type = elementFieldsMap.getOrDefault(TYPE_TAG, type);
        this.form = elementFieldsMap.getOrDefault(FORM_TAG, form);
        this.use = elementFieldsMap.getOrDefault(USE_TAG, "optional");

        if (type != null && !XsdParser.getXsdTypesToJava().containsKey(type)){
            XsdAttribute placeHolder = new XsdAttribute(new HashMap<>());
            placeHolder.setParent(this);
            this.simpleType = new UnsolvedReference(type, placeHolder);
            XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.simpleType);
        }
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public XsdAttribute clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(elementFieldsMap);
        placeHolderAttributes.remove(REF_TAG);

        XsdAttribute copy = new XsdAttribute(placeHolderAttributes);
        copy.setParent(this.parent);

        copy.type = this.type;
        copy.simpleType = this.simpleType;

        return copy;
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement elementWrapper) {
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

    public String getUse() {
        return use;
    }

    public String getForm() {
        return form;
    }

    public String getFixed() {
        return fixed;
    }

    public List<XsdRestriction> getAllRestrictions(){
        XsdSimpleType simpleTypeObj = getXsdSimpleType();

        if (simpleTypeObj != null){
            return simpleTypeObj.getAllRestrictions();
        }

        return Collections.emptyList();
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
