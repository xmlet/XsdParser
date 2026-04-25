package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A class representing the xsd:unique element. Asserts that the value(s) selected by its selector
 * + fields are unique within the scope established by its enclosing {@link XsdElement}.
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#element-unique">xsd:unique description at W3C</a>
 */
public class XsdUnique extends XsdIdentityConstraint {

    public static final String XSD_TAG = "xsd:unique";
    public static final String XS_TAG = "xs:unique";
    public static final String TAG = "unique";

    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<>(Arrays.asList(ID_TAG, NAME_TAG));

    private XsdUnique(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        LexicalValidator.rejectUnknownAttributes(attributesMap, ALLOWED_ATTRIBUTES, XSD_TAG);
        LexicalValidator.requireNCNameOrNull(attributesMap.get(ID_TAG), XSD_TAG, ID_TAG);
        LexicalValidator.requireNCNameOrNull(getName(), XSD_TAG, NAME_TAG);
    }

    @Override
    protected String getXsdTag() {
        return XSD_TAG;
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdUnique(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     */
    @Override
    public XsdUnique clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdUnique elementCopy = new XsdUnique(this.getParser(), placeHolderAttributes, visitorFunction);
        copyChildrenInto(elementCopy);

        elementCopy.cloneOf = this;
        elementCopy.setParent(null);

        return elementCopy;
    }
}
