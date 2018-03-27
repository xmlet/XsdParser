package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;
import org.xmlet.xsdparser.xsdelements.visitors.XsdChoiceVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdChoice extends XsdMultipleElements{

    public static final String XSD_TAG = "xsd:choice";
    public static final String XS_TAG = "xs:choice";

    private XsdChoiceVisitor visitor = new XsdChoiceVisitor(this);

    private XsdChoice(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public XsdChoiceVisitor getVisitor() {
        return visitor;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdChoice(convertNodeMap(node.getAttributes())));
    }

}
