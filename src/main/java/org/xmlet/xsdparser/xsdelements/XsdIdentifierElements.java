package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * A class that serves as a base to every {@link XsdAbstractElement} concrete type that contains an id field.
 */
public abstract class XsdIdentifierElements extends XsdAbstractElement {

    /**
     * Specifies a unique ID for the element.
     */
    private String id;

    XsdIdentifierElements(@NotNull XsdParser parser, @NotNull Map<String, String> elementFieldsMapParam) {
        super(parser, elementFieldsMapParam);
    }

    /**
     * Sets the id field with the value present in the {@link Map} with the {@link Node} information.
     * @param elementFieldsMapParam The {@link Map} object containing the information previously contained in the
     *                              {@link Node} object.
     */
    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        this.id = elementFieldsMap.getOrDefault(ID_TAG, id);
    }

    public String getId() {
        return id;
    }
}
