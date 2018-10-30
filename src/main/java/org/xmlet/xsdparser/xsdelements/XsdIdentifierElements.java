package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;

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

    XsdIdentifierElements(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);
        this.id = attributesMap.getOrDefault(ID_TAG, id);
    }

    public String getId() {
        return id;
    }
}
