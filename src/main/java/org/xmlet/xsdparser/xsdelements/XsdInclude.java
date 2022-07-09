package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

/**
 * A class representing the xsd:include element.
 *
 * @see <a href="https://www.w3schools.com/xml/el_include.asp">xsd:include description and usage at w3c</a>
 */
public class XsdInclude extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:include";
    public static final String XS_TAG = "xs:include";
    public static final String TAG = "include";

    /**
     * Specifies the URI to the schema for the imported namespace.
     * In this project this attribute is used to specify another file location that contains more element definitions
     * that belong to the same XSD language definition.
     */
    private String schemaLocation;

    private XsdInclude(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        this.schemaLocation = attributesMap.getOrDefault(SCHEMA_LOCATION, schemaLocation);

        if (this.schemaLocation != null){
            parser.addFileToParse(this.schemaLocation);
        }
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdInclude(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @SuppressWarnings("unused")
    public String getSchemaLocation() {
        return schemaLocation;
    }
}
