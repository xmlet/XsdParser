package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Common shape of {@link XsdUnique}, {@link XsdKey}, and {@link XsdKeyref}: a named element with
 * exactly one {@link XsdSelector} child followed by one or more {@link XsdField} children.
 *
 * @see <a href="https://www.w3.org/TR/xmlschema-1/#cIdentity-constraint_Definitions">Identity Constraint Definitions at W3C</a>
 */
public abstract class XsdIdentityConstraint extends XsdNamedElements {

    /**
     * Single {@code xs:selector} child; required by the spec.
     */
    private XsdSelector selector;

    /**
     * One or more {@code xs:field} children; at least one is required by the spec.
     */
    private List<XsdField> fields = new ArrayList<>();

    protected XsdIdentityConstraint(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull Function<XsdAbstractElement, XsdAbstractElementVisitor> visitorFunction) {
        super(parser, attributesMap, visitorFunction);
    }

    @Override
    public void validateSchemaRules() {
        super.validateSchemaRules();

        if (!(parent instanceof XsdElement)){
            throw new ParsingException(getXsdTag() + " element: parent must be " + XsdElement.XSD_TAG + ".");
        }
        if (getName() == null){
            throw new ParsingException(getXsdTag() + " element: " + NAME_TAG + " attribute is required.");
        }
        if (selector == null){
            throw new ParsingException(getXsdTag() + " element: a " + XsdSelector.XSD_TAG + " child is required.");
        }
        if (fields.isEmpty()){
            throw new ParsingException(getXsdTag() + " element: at least one " + XsdField.XSD_TAG + " child is required.");
        }
    }

    /** The XSD tag (e.g. {@code "xsd:unique"}) of the concrete subclass — used in error messages. */
    protected abstract String getXsdTag();

    public void setSelector(XsdSelector selector) {
        this.selector = selector;
    }

    public void addField(XsdField field) {
        this.fields.add(field);
    }

    public XsdSelector getSelector() {
        return selector;
    }

    public List<XsdField> getFields() {
        return fields;
    }

    /** Copy selector + fields onto a freshly constructed clone of the same concrete type. */
    protected void copyChildrenInto(XsdIdentityConstraint copy) {
        if (this.selector != null) {
            copy.selector = (XsdSelector) this.selector.clone(this.selector.getAttributesMap(), copy);
        }
        for (XsdField field : this.fields) {
            copy.fields.add((XsdField) field.clone(field.getAttributesMap(), copy));
        }
    }
}
