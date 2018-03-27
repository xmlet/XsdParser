package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdAppInfo extends XsdAnnotationChildren {

    public static final String XSD_TAG = "xsd:appinfo";
    public static final String XS_TAG = "xs:appinfo";

    private XsdAppInfo(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    public static ReferenceBase parse(Node node){
        XsdAppInfo appInfo = new XsdAppInfo(convertNodeMap(node.getAttributes()));

        appInfo.content = xsdRawContentParse(node);

        return ReferenceBase.createFromXsd(appInfo);
    }
}
