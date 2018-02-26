package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class XsdUnion extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:union";
    public static final String XS_TAG = "xs:union";

    private UnionXsdElementVisitor visitor = new UnionXsdElementVisitor();

    private List<XsdSimpleType> simpleTypeList = new ArrayList<>();
    private String memberTypes;

    private XsdUnion(XsdAbstractElement parent, Map<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdUnion(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public void setFields(Map<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.memberTypes = elementFieldsMap.getOrDefault(MEMBER_TYPES, memberTypes);
        }
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        xsdElementVisitor.visit(this);
        this.setParent(xsdElementVisitor.getOwner());
    }

    @Override
    public XsdUnion clone(Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdUnion elementCopy = new XsdUnion(this.getParent(), placeHolderAttributes);

        elementCopy.simpleTypeList = this.simpleTypeList;

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    public List<XsdSimpleType> getUnionElements(){
        return simpleTypeList;
    }

    @SuppressWarnings("unused")
    public List<String> getMemberTypesList() {
        return Arrays.asList(memberTypes.split(" "));
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdUnion(convertNodeMap(node.getAttributes())));
    }

    class UnionXsdElementVisitor extends AnnotatedXsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdUnion.this;
        }

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);

            XsdUnion.this.simpleTypeList.add(element);
        }
    }
}
