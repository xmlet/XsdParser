package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States the maximum length of a given type, either a String, a List or another measurable type. This limit including
 * the respective value. The value is defined as an Integer.
 * e.g.
 *  * If the type has a xsd:maxLength value="5" it means that the String, List or another measurable type should have a
 *      maximum length of 5.
 */
public class XsdMaxLength extends XsdIntegerRestrictions {

    public static final String XSD_TAG = "xsd:maxLength";
    public static final String XS_TAG = "xs:maxLength";

    private XsdMaxLength(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdMaxLength(convertNodeMap(node.getAttributes())));
    }
}
