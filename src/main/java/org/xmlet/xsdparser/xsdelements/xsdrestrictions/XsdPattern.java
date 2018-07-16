package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States a pattern that a given attribute must match in order to be considered valid. The value is defined as a String.
 * This value is usually defined as a regular expression.
 */
public class XsdPattern extends XsdStringRestrictions {

    public static final String XSD_TAG = "xsd:pattern";
    public static final String XS_TAG = "xs:pattern";

    private XsdPattern(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdPattern(convertNodeMap(node.getAttributes())));
    }
}
