package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.List;
import java.util.stream.Collectors;

public class WpfeParseTest {
    private static final String WPFE_FILE_NAME = HtmlParseTest.class.getClassLoader().getResource("wpfe.xsd").getPath();
    private static final List<XsdElement> elements;
    private static final XsdParser parser;

    static{
        parser = new XsdParser(WPFE_FILE_NAME);

        elements = parser.getParseResult().collect(Collectors.toList());
    }

    @Test
    public void testElementCount(){
        Assert.assertEquals(89, elements.size());
    }
}
