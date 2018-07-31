package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdUnionVisitor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A class representing the xsd:union element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_union.asp">xsd:union description and usage at w3c</a>
 */
public class XsdUnion extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:union";
    public static final String XS_TAG = "xs:union";

    /**
     * {@link XsdUnionVisitor} instance which restricts its children to {@link XsdSimpleType} instances.
     */
    private XsdUnionVisitor visitor = new XsdUnionVisitor(this);

    /**
     * A List of {@link XsdSimpleType} instances that represent the {@link XsdUnion}.
     */
    private List<XsdSimpleType> simpleTypeList = new ArrayList<>();

    /**
     * Specifies a list of built-in data types or {@link XsdSimpleType} instance names defined in a XsdSchema.
     */
    private String memberTypes;

    private XsdUnion(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
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

    @SuppressWarnings("unused")
    public List<String> getMemberTypesList() {
        return Arrays.asList(memberTypes.split(" "));
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return xsdParseSkeleton(node, new XsdUnion(parser, convertNodeMap(node.getAttributes())));
    }

    public void add(XsdSimpleType simpleType) {
        simpleTypeList.add(simpleType);
    }
}
