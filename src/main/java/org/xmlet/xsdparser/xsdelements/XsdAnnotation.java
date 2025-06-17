package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A class representing the xsd:annotation element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_annotation.asp">xsd:annotation element description and usage at w3c</a>
 */
public class XsdAnnotation extends XsdIdentifierElements {

    public static final String XSD_TAG = "xsd:annotation";
    public static final String XS_TAG = "xs:annotation";
    public static final String TAG = "annotation";

    /**
     * The list of {@link XsdAppInfo} children.
     */
    private List<XsdAppInfo> appInfoList = new ArrayList<>();

    /**
     * The list of {@link XsdDocumentation} children.
     */
    private List<XsdDocumentation> documentations = new ArrayList<>();

    private XsdAnnotation(@NotNull XsdParserCore parser, @NotNull Map<String, String> elementFieldsMapParam, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, elementFieldsMapParam, visitorFunction);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    public List<XsdAppInfo> getAppInfoList() {
        return appInfoList;
    }

    public List<XsdDocumentation> getDocumentations() {
        return documentations;
    }

    public void add(XsdAppInfo appInfo){
        appInfoList.add(appInfo);
    }

    public void add(XsdDocumentation documentation){
        documentations.add(documentation);
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdAnnotation(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }
}
