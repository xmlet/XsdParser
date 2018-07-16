package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * States how the whiteSpace characters should be treated. The value is defined as an String.
 */
public class XsdWhiteSpace extends XsdStringRestrictions {

    public static final String XSD_TAG = "xsd:whiteSpace";
    public static final String XS_TAG = "xs:whiteSpace";

    private boolean fixed;

    private XsdWhiteSpace(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        fixed = Boolean.parseBoolean(elementFieldsMap.getOrDefault(FIXED_TAG, "false"));
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(Node node){
        return ReferenceBase.createFromXsd(new XsdWhiteSpace(convertNodeMap(node.getAttributes())));
    }

    public boolean isFixed() {
        return fixed;
    }
}
