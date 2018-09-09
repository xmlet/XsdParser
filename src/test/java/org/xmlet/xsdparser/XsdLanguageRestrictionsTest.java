package org.xmlet.xsdparser;

import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.enums.FormEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

/**
 * Each test represents an example of errors that might be present in a XSD file and test for the expected exception.
 */
public class XsdLanguageRestrictionsTest {

    /**
     * The parsed file has an invalid attribute, the top level xsd:element element shouldn't have a ref attribute,
     * an exception is expected.
     */
    @Test(expected = ParsingException.class)
    public void testLanguageRestriction1(){
        new XsdParser(HtmlParseTest.class.getClassLoader().getResource("language_restriction_1.xsd").getPath());
    }

    /**
     * The value passed in the minOccurs attribute should be a non negative integer, therefore the parsing throws
     * a parsing exception.
     */
    @Test(expected = ParsingException.class)
    public void testLanguageRestriction2(){
        new XsdParser(HtmlParseTest.class.getClassLoader().getResource("language_restriction_2.xsd").getPath());
    }

    /**
     * The value passed in the form attribute should belong to the {@link FormEnum}. Since it doesn't belong a parsing
     * exception is expected.
     */
    @Test(expected = ParsingException.class)
    public void testLanguageRestriction3(){
        new XsdParser(HtmlParseTest.class.getClassLoader().getResource("language_restriction_3.xsd").getPath());
    }
}
