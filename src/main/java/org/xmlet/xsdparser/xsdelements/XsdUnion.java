package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdUnionVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class XsdUnion extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:union";
    public static final String XS_TAG = "xs:union";

    private XsdUnionVisitor visitor = new XsdUnionVisitor(this);

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
    public XsdUnionVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
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

    public void add(XsdSimpleType simpleType) {
        simpleTypeList.add(simpleType);
    }
}
