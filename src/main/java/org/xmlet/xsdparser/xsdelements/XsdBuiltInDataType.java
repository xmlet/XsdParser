package org.xmlet.xsdparser.xsdelements;

import org.xmlet.xsdparser.core.XsdParserCore;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class XsdBuiltInDataType extends XsdNamedElements{

    public XsdBuiltInDataType(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap, @NotNull XsdAbstractElement parent){
        super(parser, attributesMap, null);
        setParent(parent);
    }

    @Override
    public XsdNamedElements clone(Map<String, String> placeHolderAttributes) {
        return new XsdBuiltInDataType(parser, attributesMap, parent);
    }
}
