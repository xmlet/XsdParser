package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
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

    private XsdUnion(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam){
        super.setFields(elementFieldsMapParam);

        this.memberTypes = elementFieldsMap.getOrDefault(MEMBER_TYPES_TAG, memberTypes);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    public List<XsdSimpleType> getUnionElements(){
        return simpleTypeList;
    }

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
