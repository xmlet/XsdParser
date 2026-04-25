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
 * A class representing the xsd:notation element. Declares a notation that can later be referenced
 * by attributes typed as {@code xs:NOTATION}. Must appear as a direct child of {@code xs:schema}.
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#element-notation">xsd:notation description at W3C</a>
 */
public class XsdNotation extends XsdNamedElements {

    public static final String XSD_TAG = "xsd:notation";
    public static final String XS_TAG = "xs:notation";
    public static final String TAG = "notation";

    /**
     * Public identifier of the notation; either {@link #publicId} or {@link #system} must be present.
     */
    private String publicId;

    /**
     * URI to the system identifier of the notation.
     */
    private String system;

    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<>(Arrays.asList(
            ID_TAG, NAME_TAG, PUBLIC_TAG, SYSTEM_TAG));

    private XsdNotation(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        LexicalValidator.rejectUnknownAttributes(attributesMap, ALLOWED_ATTRIBUTES, XSD_TAG);
        LexicalValidator.requireNCNameOrNull(attributesMap.get(ID_TAG), XSD_TAG, ID_TAG);
        LexicalValidator.requireNCNameOrNull(getName(), XSD_TAG, NAME_TAG);

        this.publicId = attributesMap.getOrDefault(PUBLIC_TAG, null);
        this.system = attributesMap.getOrDefault(SYSTEM_TAG, null);

        LexicalValidator.requireTokenOrNull(this.publicId, XSD_TAG, PUBLIC_TAG);

        // Per Part 2 §3.2.17, xs:anyURI is intentionally permissive — any character sequence
        // that survives the XLink Section 5.4 percent-encoding algorithm is a valid lexical
        // form. Java's URI class is stricter than that; rather than reject spec-valid strings,
        // we accept any non-null value here (the empty string is also valid per spec).
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        if (!(parent instanceof XsdSchema)){
            throw new ParsingException(XSD_TAG + " element: parent must be " + XsdSchema.XSD_TAG + ".");
        }
        if (getName() == null){
            throw new ParsingException(XSD_TAG + " element: " + NAME_TAG + " attribute is required.");
        }
        // Per the schema-for-schemas, both public and system are optional (use="optional"). The
        // spec text suggests the public identifier carries semantic meaning, but does not formally
        // require either attribute, so we don't either.
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdNotation(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     */
    @Override
    public XsdNotation clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);

        XsdNotation elementCopy = new XsdNotation(this.getParser(), placeHolderAttributes, visitorFunction);

        elementCopy.cloneOf = this;
        elementCopy.setParent(null);

        return elementCopy;
    }

    @SuppressWarnings("unused")
    public String getPublicId() {
        return publicId;
    }

    @SuppressWarnings("unused")
    public String getSystem() {
        return system;
    }
}
