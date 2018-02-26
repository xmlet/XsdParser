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
        parser = new XsdParser();

        elements = parser.parse(WPFE_FILE_NAME)
                .filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());

        XsdElement elem = elements.stream().filter(element -> element.getName().equals("Canvas")).findFirst().get();
    }

    @Test
    public void testElementCount(){
        Assert.assertEquals(89, elements.size());
    }
}
