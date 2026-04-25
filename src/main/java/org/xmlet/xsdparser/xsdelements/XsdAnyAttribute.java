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
 * A class representing the xsd:anyAttribute element. Functions as the attribute counterpart of
 * {@link XsdAny}: it allows extending a complexType / attributeGroup / restriction / extension
 * with attributes from any namespace, optionally constrained by the {@code namespace} attribute.
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#element-anyAttribute">xsd:anyAttribute description at W3C</a>
 */
public class XsdAnyAttribute extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:anyAttribute";
    public static final String XS_TAG = "xs:anyAttribute";
    public static final String TAG = "anyAttribute";

    /**
     * Constraint on the namespaces of the matched attributes. Defaults to {@code "##any"}.
     */
    private String namespace;

    /**
     * One of {@code "strict"}, {@code "lax"}, or {@code "skip"}. Defaults to {@code "strict"}.
     */
    private String processContents;

    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<>(Arrays.asList(
            ID_TAG, NAMESPACE, PROCESS_CONTENTS));

    private XsdAnyAttribute(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction){
        super(parser, attributesMap, visitorFunction);

        LexicalValidator.rejectUnknownAttributes(attributesMap, ALLOWED_ATTRIBUTES, XSD_TAG);
        LexicalValidator.requireNCNameOrNull(attributesMap.get(ID_TAG), XSD_TAG, ID_TAG);

        this.namespace = AttributeValidations.validateAnyNamespace(XSD_TAG, attributesMap.getOrDefault(NAMESPACE, "##any"));
        this.processContents = attributesMap.getOrDefault(PROCESS_CONTENTS, "strict");

        if (!this.processContents.equals("strict") && !this.processContents.equals("lax") && !this.processContents.equals("skip")){
            throw new ParsingException(XSD_TAG + " element: " + PROCESS_CONTENTS + " must be one of \"strict\", \"lax\", or \"skip\".");
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        // Per spec, xs:anyAttribute is allowed under xs:complexType (directly), xs:attributeGroup,
        // or under xs:restriction/xs:extension nested inside xs:simpleContent or xs:complexContent.
        // The "restriction-inside-simpleType is illegal" half is enforced in
        // {@link XsdRestriction#validateSchemaRules}, where the grandparent has been wired up by
        // the time validation runs (children validate before their parent's parent is set).
        if (!(parent instanceof XsdComplexType
                || parent instanceof XsdRestriction
                || parent instanceof XsdExtension
                || parent instanceof XsdAttributeGroup)){
            throw new ParsingException(XSD_TAG + " element: parent must be one of "
                    + XsdComplexType.XSD_TAG + ", " + XsdRestriction.XSD_TAG + ", "
                    + XsdExtension.XSD_TAG + " or " + XsdAttributeGroup.XSD_TAG + ".");
        }
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdAnyAttribute(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     */
    @Override
    public XsdAnyAttribute clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdAnyAttribute elementCopy = new XsdAnyAttribute(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.cloneOf = this;
        elementCopy.setParent(null);

        return elementCopy;
    }

    @SuppressWarnings("unused")
    public String getNamespace() {
        return namespace;
    }

    @SuppressWarnings("unused")
    public String getProcessContents() {
        return processContents;
    }
}
