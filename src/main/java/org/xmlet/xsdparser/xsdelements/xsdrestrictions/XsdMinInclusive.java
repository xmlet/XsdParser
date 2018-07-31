package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States the minimum numeric value that a given type might take, including the respective value. The value is defined
 * as an Integer.
 * e.g.
 *  * If the type has a xsd:minInclusive value="0" it means that the minimum value it can take is 0.
 */
public class XsdMinInclusive extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:minInclusive";
    public static final String XS_TAG = "xs:minInclusive";

    private XsdMinInclusive(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(@NotNull XsdParser parser, Node node){
        return ReferenceBase.createFromXsd(new XsdMinInclusive(parser, convertNodeMap(node.getAttributes())));
    }
}
