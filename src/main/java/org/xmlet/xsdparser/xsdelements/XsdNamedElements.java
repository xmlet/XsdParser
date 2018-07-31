package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;

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
    private String name;

    XsdNamedElements(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    /**
     * Performs a copy of the current object for replacing purposes. The cloned objects are used to replace
     * {@link UnsolvedReference} objects in the reference solving process.
     * @param placeHolderAttributes The additional attributes to add to the clone.
     * @return A copy of the object from which is called upon.
     */
    public abstract XsdNamedElements clone(@NotNull Map<String, String> placeHolderAttributes);

    /**
     * Sets the name field with the value present in the Map containing the Node object.
     * @param elementFieldsMapParam The Map object containing the information previously contained in the Node object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.name = elementFieldsMap.getOrDefault(NAME_TAG, name);
    }

    /**
     * @return The name of the element, with all the special characters replaced with the '_' char.
     */
    public String getName() {
        return name == null ? null : name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    String getRawName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
