package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAllVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdAll extends XsdMultipleElements {

    public static final String XSD_TAG = "xsd:all";
    public static final String XS_TAG = "xs:all";

    private final XsdAllVisitor visitor = new XsdAllVisitor(this);

    private XsdAll(@NotNull Map<String, String> elementFieldsMapParam){
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdAllVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node) {
        return xsdParseSkeleton(node, new XsdAll(convertNodeMap(node.getAttributes())));
    }

}
