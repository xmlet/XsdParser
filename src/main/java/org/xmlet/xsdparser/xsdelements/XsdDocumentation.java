package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdDocumentation extends XsdAnnotationChildren {

    public static final String XSD_TAG = "xsd:documentation";
    public static final String XS_TAG = "xs:documentation";

    private String xmlLang;

    private XsdDocumentation(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.xmlLang = elementFieldsMap.getOrDefault(XML_LANG_TAG, xmlLang);
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    public static ReferenceBase parse(Node node){
        XsdDocumentation documentation = new XsdDocumentation(convertNodeMap(node.getAttributes()));

        documentation.content = xsdRawContentParse(node);

        return ReferenceBase.createFromXsd(documentation);
    }
}
