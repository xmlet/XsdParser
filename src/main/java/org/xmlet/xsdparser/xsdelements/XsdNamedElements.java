package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class serves as a base to concrete {@link XsdAbstractElement} classes that can have a name attribute. This is
 * helpful in resolving the references present at the end of the parsing process.
 */
public abstract class XsdNamedElements extends XsdAnnotatedElements {

    /**
     * The name of the element.
     */
    String name;

    XsdNamedElements(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);

        this.name = attributesMap.getOrDefault(NAME_TAG, name);
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    public abstract XsdNamedElements clone(@NotNull Map<String, String> placeHolderAttributes);

    /**
     * Runs verifications on each concrete element to ensure that the XSD schema rules are verified.
     */
    @Override
    public void validateSchemaRules() {
        rule1();
    }

    /**
     * Asserts that the current element doesn't have both ref and name attributes at the same time. Throws an exception
     * if they are both present.
     */
    private void rule1() {
        if (name != null && attributesMap.containsKey(REF_TAG)){
            throw new ParsingException(NAME_TAG + " and " + REF_TAG + " attributes cannot both be present at the same time.");
        }
    }

    /**
     * @return The name of the element, with all the special characters replaced with the '_' char.
     */
    public String getName() {
        return name == null ? null : name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    @SuppressWarnings("WeakerAccess")
    public String getRawName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
