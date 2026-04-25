package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.utils.ConfigEntryData;
import org.xmlet.xsdparser.core.utils.ParseData;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A class representing the xsd:keyref element. Like {@link XsdKey}, but its values must match
 * the values of a referenced {@link XsdKey} or {@link XsdUnique} (named via the {@code refer}
 * attribute).
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#element-keyref">xsd:keyref description at W3C</a>
 */
public class XsdKeyref extends XsdIdentityConstraint {

    public static final String XSD_TAG = "xsd:keyref";
    public static final String XS_TAG = "xs:keyref";
    public static final String TAG = "keyref";

    /**
     * Name of the referenced {@link XsdKey} or {@link XsdUnique}, as a QName.
     */
    private String referName;

    /**
     * Resolved reference to the {@link XsdKey} or {@link XsdUnique} named by {@link #referName};
     * an {@link UnsolvedReference} until the second resolution pass replaces it with a
     * {@link NamedConcreteElement}. {@code null} when {@code refer} was omitted (which is a
     * spec violation flagged in {@link #validateSchemaRules()}).
     */
    private ReferenceBase refer;

    private static final Set<String> ALLOWED_ATTRIBUTES = new HashSet<>(Arrays.asList(
            ID_TAG, NAME_TAG, REFER_TAG));

    private XsdKeyref(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);

        LexicalValidator.rejectUnknownAttributes(attributesMap, ALLOWED_ATTRIBUTES, XSD_TAG);
        LexicalValidator.requireNCNameOrNull(attributesMap.get(ID_TAG), XSD_TAG, ID_TAG);
        LexicalValidator.requireNCNameOrNull(getName(), XSD_TAG, NAME_TAG);
        LexicalValidator.requireQNameOrNull(attributesMap.get(REFER_TAG), XSD_TAG, REFER_TAG);

        this.referName = attributesMap.getOrDefault(REFER_TAG, null);

        if (this.referName != null){
            // Placeholder element type is irrelevant — UnsolvedReference matching is by name; the
            // real replacement is resolved later in replaceUnsolvedElements. Use XsdElement as the
            // stand-in (with its own configured visitor) for the same reason XsdExtension does.
            ConfigEntryData elementConfig = XsdParserCore.getParseMappers(XsdElement.TAG);
            if (elementConfig == null){
                throw new ParsingException("Invalid Parsing Configuration for XsdElement.");
            }
            UnsolvedReference unsolved = new UnsolvedReference(this.referName, new XsdElement(this, parser, new HashMap<>(), elementConfig.visitorFunction));
            this.refer = unsolved;
            parser.addUnsolvedReference(unsolved);
        }
    }

    @Override
    protected String getXsdTag() {
        return XSD_TAG;
    }

    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        if (referName == null){
            throw new ParsingException(XSD_TAG + " element: " + REFER_TAG + " attribute is required.");
        }
    }

    @Override
    public void accept(XsdAbstractElementVisitor visitorParam) {
        super.accept(visitorParam);
        visitorParam.visit(this);
    }

    /**
     * When the {@code refer}-target identity constraint is parsed, replace the placeholder.
     * Only {@link XsdKey} and {@link XsdUnique} are valid targets per the spec; the keyref's
     * field count must additionally match the target's field count (Part 1 §3.11.6).
     */
    @Override
    public boolean replaceUnsolvedElements(NamedConcreteElement elementWrapper) {
        boolean replaced = super.replaceUnsolvedElements(elementWrapper);

        if (refer instanceof UnsolvedReference && compareReference(elementWrapper, (UnsolvedReference) refer)){
            XsdAbstractElement target = elementWrapper.getElement();
            if (target instanceof XsdKey || target instanceof XsdUnique){
                XsdIdentityConstraint targetConstraint = (XsdIdentityConstraint) target;
                int targetFieldCount = targetConstraint.getFields().size();
                int myFieldCount = getFields().size();
                if (targetFieldCount != myFieldCount){
                    throw new ParsingException(XSD_TAG + " element \"" + getName() + "\": " + REFER_TAG
                            + " \"" + referName + "\" has " + targetFieldCount + " "
                            + XsdField.XSD_TAG + " entries; this " + XSD_TAG + " has " + myFieldCount + ".");
                }
                this.refer = elementWrapper;
                replaced = true;
            } else if (target instanceof XsdIdentityConstraint || target instanceof XsdKeyref){
                throw new ParsingException(XSD_TAG + " element: " + REFER_TAG + " \"" + referName
                        + "\" must reference a " + XsdKey.XSD_TAG + " or " + XsdUnique.XSD_TAG
                        + ", not a " + XsdKeyref.XSD_TAG + ".");
            }
        }
        return replaced;
    }

    public static ReferenceBase parse(@NotNull ParseData parseData){
        return xsdParseSkeleton(parseData.node, new XsdKeyref(parseData.parserInstance, convertNodeMap(parseData.node.getAttributes()), parseData.visitorFunction));
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     */
    @Override
    public XsdKeyref clone(@NotNull Map<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(attributesMap);
        placeHolderAttributes.remove(REFER_TAG);

        XsdKeyref elementCopy = new XsdKeyref(this.getParser(), placeHolderAttributes, visitorFunction);
        copyChildrenInto(elementCopy);

        elementCopy.referName = this.referName;
        elementCopy.refer = ReferenceBase.clone(parser, this.refer, elementCopy);

        elementCopy.cloneOf = this;
        elementCopy.setParent(null);

        return elementCopy;
    }

    public String getRefer() {
        return referName;
    }

    /**
     * @return The resolved {@link XsdKey} or {@link XsdUnique} named by {@code refer}, or
     *         {@code null} if not yet resolved.
     */
    public XsdIdentityConstraint getReferElement() {
        if (refer instanceof NamedConcreteElement){
            XsdAbstractElement el = refer.getElement();
            if (el instanceof XsdIdentityConstraint) return (XsdIdentityConstraint) el;
        }
        return null;
    }
}
