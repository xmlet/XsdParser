package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdComplexType;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdNamedElements;
import org.xmlet.xsdparser.xsdelements.XsdSequence;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.enums.FormEnum;
import org.xmlet.xsdparser.xsdelements.exceptions.ParsingException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    public void testSubstitutionGroup(){
        XsdParser parser = new XsdParser(XsdLanguageRestrictionsTest.class.getClassLoader().getResource("substitutionGroup.xsd").getPath());

        List<XsdElement> elements = parser.getResultXsdElements().collect(Collectors.toList());

        Optional<XsdElement> navnOptional = elements.stream().filter(element -> element.getName().equals("navn")).findFirst();
        Optional<XsdElement> kundeOptional = elements.stream().filter(element -> element.getName().equals("kunde")).findFirst();

        Assert.assertTrue(navnOptional.isPresent());
        Assert.assertTrue(kundeOptional.isPresent());

        XsdElement navnElement = navnOptional.get();
        XsdElement kundeElement = kundeOptional.get();

        Assert.assertEquals("xs:string", navnElement.getType());

        XsdComplexType kundeComplexType = kundeElement.getXsdComplexType();

        Assert.assertNotNull(kundeComplexType);
        Assert.assertEquals("custinfo", kundeComplexType.getName());

        XsdSequence custinfoSequence = kundeComplexType.getChildAsSequence();

        Assert.assertNotNull(custinfoSequence);
        List<XsdElement> sequenceElements = custinfoSequence.getChildrenElements().collect(Collectors.toList());

        Assert.assertNotNull(sequenceElements);
        Assert.assertEquals(1, sequenceElements.size());

        XsdElement nameElement = sequenceElements.get(0);

        Assert.assertTrue(navnElement.getSubstitutionGroup() instanceof NamedConcreteElement);
        Assert.assertTrue(navnElement.getSubstitutionGroup().getElement() instanceof XsdNamedElements);
        Assert.assertEquals(nameElement.getName(), ((XsdNamedElements)navnElement.getSubstitutionGroup().getElement()).getName());
    }
}
