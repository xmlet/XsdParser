package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Represents a value that a given XSD type is allowed to take. The value is defined as a {@link String}.
 */
public class XsdEnumeration extends XsdStringRestrictions {

    public static final String XSD_TAG = "xsd:enumeration";
    public static final String XS_TAG = "xs:enumeration";

    private XsdEnumeration(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return ReferenceBase.createFromXsd(new XsdEnumeration(parser, convertNodeMap(node.getAttributes())));
    }
}