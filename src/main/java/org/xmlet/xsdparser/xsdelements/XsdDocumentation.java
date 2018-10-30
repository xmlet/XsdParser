package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class representing the xsd:documentation element. This class extends from {@link XsdAnnotationChildren} since it
 * shares a few similarities with {@link XsdAppInfo}, which is the other possible children of {@link XsdAnnotation}
 * elements. For more information check {@link XsdAnnotationChildren}.
 *
 * @see <a href="https://www.w3schools.com/xml/el_documentation.asp">xsd:documentation element description and usage at w3c</a>
 */
public class XsdDocumentation extends XsdAnnotationChildren {

    public static final String XSD_TAG = "xsd:documentation";
    public static final String XS_TAG = "xs:documentation";

    /**
     * Specifies the language used in the {@link XsdAnnotationChildren#content}
     */
    private String xmlLang;

    private XsdDocumentation(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);
        this.xmlLang = attributesMap.getOrDefault(XML_LANG_TAG, xmlLang);
    }

    @Override
    public void accept(XsdAbstractElementVisitor xsdAbstractElementVisitor) {
        super.accept(xsdAbstractElementVisitor);
        xsdAbstractElementVisitor.visit(this);
    }

    public static ReferenceBase parse(@NotNull XsdParserCore parser, Node node){
        return xsdAnnotationChildrenParse(node, new XsdDocumentation(parser, convertNodeMap(node.getAttributes())));
    }
}
