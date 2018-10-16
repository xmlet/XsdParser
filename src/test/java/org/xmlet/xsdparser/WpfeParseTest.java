package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple test which asserts the parsed element count of another XSD file.
 */
public class WpfeParseTest {

    private static final List<XsdElement> elements;
    private static final XsdParser parser;

    static{
        parser = new XsdParser(getFilePath());

        elements = parser.getResultXsdElements().collect(Collectors.toList());
    }

    @Test
    public void testElementCount(){
        Assert.assertEquals(89, elements.size());
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = HtmlParseTest.class.getClassLoader().getResource("wpfe.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The wpfe.xsd file is missing from the XsdParser resource folder.");
        }
    }
}
