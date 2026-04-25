package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A class representing the xsd:selector element. Carries an XPath subset expression that
 * selects the set of elements an identity constraint applies to. Parent must be one of
 * {@link XsdUnique}, {@link XsdKey}, or {@link XsdKeyref}.
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#element-selector">xsd:selector description at W3C</a>
 */
public class XsdSelector extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:selector";
    public static final String XS_TAG = "xs:selector";
    public static final String TAG = "selector";

    /**
     * The XPath subset expression that selects the target nodes for the enclosing identity
     * constraint. Required.
     */
    private String xpath;

    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<>(Arrays.asList(ID_TAG, XPATH_TAG));

    private XsdSelector(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        LexicalValidator.rejectUnknownAttributes(attributesMap, ALLOWED_ATTRIBUTES, XSD_TAG);
        LexicalValidator.requireNCNameOrNull(attributesMap.get(ID_TAG), XSD_TAG, ID_TAG);

        this.xpath = attributesMap.getOrDefault(XPATH_TAG, null);

        if (this.xpath == null){
            throw new ParsingException(XSD_TAG + " element: The " + XPATH_TAG + " attribute is required.");
        }
        XPathSubsetValidator.validateSelector(this.xpath, XSD_TAG);
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        if (!(parent instanceof XsdIdentityConstraint)){
            throw new ParsingException(XSD_TAG + " element: parent must be one of "
                    + XsdUnique.XSD_TAG + ", " + XsdKey.XSD_TAG + " or " + XsdKeyref.XSD_TAG + ".");
        }
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdSelector(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    @Override
    public XsdSelector clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdSelector elementCopy = new XsdSelector(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.cloneOf = this;
        elementCopy.setParent(null);

        return elementCopy;
    }

    public String getXpath() {
        return xpath;
    }
}
