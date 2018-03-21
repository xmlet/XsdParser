package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OddTest {

    private static final String FILE_NAME = HtmlParseTest.class.getClassLoader().getResource("test.xsd").getPath();
    private static final List<XsdElement> elements;
    private static final XsdParser parser;

    static {
        parser = new XsdParser();

        elements = parser.parse(FILE_NAME)
                .filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());
    }

    @Test
    public void testIntRestrictions() {
        Optional<XsdElement> restrictedNumberOptional = elements.stream().filter(element -> element.getName().equals("restrictedNumber")).findFirst();

        Assert.assertTrue(restrictedNumberOptional.isPresent());

        XsdElement restrictedNumber = restrictedNumberOptional.get();

        XsdComplexType complexType = restrictedNumber.getXsdComplexType();

        Assert.assertNotNull(complexType);

        List<XsdAttribute> attributes = complexType.getXsdAttributes().collect(Collectors.toList());

        Assert.assertNotNull(attributes);
        Assert.assertEquals(1, attributes.size());

        XsdAttribute attribute = attributes.get(0);

        Assert.assertEquals("restrictedNumberAttr", attribute.getName());
        Assert.assertNull(attribute.getType());
        Assert.assertEquals("required", attribute.getUse());
        Assert.assertEquals("restrictedNumberId", attribute.getId());
        Assert.assertEquals("qualified", attribute.getForm());
        Assert.assertEquals("true", attribute.getFixed());

        XsdSimpleType simpleType = attribute.getXsdSimpleType();

        Assert.assertNotNull(simpleType);

        XsdRestriction restriction = simpleType.getRestriction();

        Assert.assertNotNull(restriction);

        XsdMaxInclusive maxInclusive = restriction.getMaxInclusive();
        XsdMaxExclusive maxExclusive = restriction.getMaxExclusive();
        XsdMinInclusive minInclusive = restriction.getMinInclusive();
        XsdMinExclusive minExclusive = restriction.getMinExclusive();
        XsdTotalDigits totalDigits = restriction.getTotalDigits();
        XsdFractionDigits fractionDigits = restriction.getFractionDigits();

        Assert.assertNotNull(maxInclusive);
        Assert.assertNotNull(maxExclusive);
        Assert.assertNotNull(minInclusive);
        Assert.assertNotNull(minExclusive);
        Assert.assertNotNull(totalDigits);
        Assert.assertNotNull(fractionDigits);

        Assert.assertEquals(100, maxExclusive.getValue());
        Assert.assertTrue(maxExclusive.isFixed());

        Assert.assertEquals(0, minExclusive.getValue());
        Assert.assertTrue(minExclusive.isFixed());

        Assert.assertEquals(99, maxInclusive.getValue());
        Assert.assertFalse(maxInclusive.isFixed());

        Assert.assertEquals(1, minInclusive.getValue());
        Assert.assertFalse(minInclusive.isFixed());

        Assert.assertEquals(2, fractionDigits.getValue());
        Assert.assertTrue(fractionDigits.isFixed());

        Assert.assertEquals(10, totalDigits.getValue());
        Assert.assertFalse(totalDigits.isFixed());
    }

    @Test
    public void testStringRestrictions() {
        Optional<XsdElement> restrictedStringOptional = elements.stream().filter(element -> element.getName().equals("restrictedString")).findFirst();

        Assert.assertTrue(restrictedStringOptional.isPresent());

        XsdElement restrictionString = restrictedStringOptional.get();

        XsdComplexType complexType = restrictionString.getXsdComplexType();

        Assert.assertNotNull(complexType);

        List<XsdAttribute> attributes = complexType.getXsdAttributes().collect(Collectors.toList());

        Assert.assertNotNull(attributes);
        Assert.assertEquals(1, attributes.size());

        XsdAttribute attribute = attributes.get(0);

        Assert.assertEquals("restrictedStringAttr", attribute.getName());

        XsdSimpleType simpleType = attribute.getXsdSimpleType();

        Assert.assertNotNull(simpleType);

        XsdRestriction restriction = simpleType.getRestriction();

        Assert.assertNotNull(restriction);

        XsdLength xsdLength = restriction.getLength();
        XsdMaxLength xsdMaxLength = restriction.getMaxLength();
        XsdMinLength xsdMinLength = restriction.getMinLength();
        XsdPattern xsdPattern = restriction.getPattern();
        XsdWhiteSpace xsdWhiteSpace = restriction.getWhiteSpace();

        Assert.assertNotNull(xsdLength);
        Assert.assertNotNull(xsdMaxLength);
        Assert.assertNotNull(xsdMinLength);
        Assert.assertNotNull(xsdPattern);
        Assert.assertNotNull(xsdWhiteSpace);

        Assert.assertEquals(10, xsdLength.getValue());
        Assert.assertTrue(xsdLength.isFixed());

        Assert.assertEquals(10, xsdLength.getValue());
        Assert.assertTrue(xsdLength.isFixed());

        Assert.assertEquals(10, xsdLength.getValue());
        Assert.assertTrue(xsdLength.isFixed());

        Assert.assertEquals(".*", xsdPattern.getValue());

        Assert.assertEquals("preserve", xsdWhiteSpace.getValue());
        Assert.assertFalse(xsdWhiteSpace.isFixed());
    }

    @Test
    public void testAnnotations() {
        Optional<XsdElement> annotatedElementOptional = elements.stream().filter(element -> element.getName().equals("annotatedElement")).findFirst();

        Assert.assertTrue(annotatedElementOptional.isPresent());

        XsdElement annotatedElement = annotatedElementOptional.get();

        XsdAnnotation annotation = annotatedElement.getAnnotation();

        Assert.assertNotNull(annotation);

        List<XsdAppInfo> appInfoList = annotation.getAppInfoList();
        List<XsdDocumentation> documentations = annotation.getDocumentations();

        Assert.assertNotNull(appInfoList);
        Assert.assertNotNull(documentations);

        Assert.assertEquals(1, appInfoList.size());
        Assert.assertEquals(1, documentations.size());

        XsdAppInfo appInfo = appInfoList.get(0);
        XsdDocumentation documentation = documentations.get(0);

        Assert.assertEquals("source", appInfo.getSource());
        Assert.assertEquals("source", documentation.getSource());

        Assert.assertEquals("Some text.", appInfo.getContent());
        Assert.assertEquals("Some documentation.", documentation.getContent());
    }

    @Test
    public void testList() {
        Optional<XsdElement> restrictedListOptional = elements.stream().filter(element -> element.getName().equals("restrictedList")).findFirst();

        Assert.assertTrue(restrictedListOptional.isPresent());

        XsdElement restrictedListElement = restrictedListOptional.get();

        XsdSimpleType simpleType = restrictedListElement.getXsdSimpleType();

        Assert.assertNotNull(simpleType);

        XsdList list = simpleType.getList();

        Assert.assertNotNull(list);
        Assert.assertEquals("listId", list.getId());
        Assert.assertEquals("xsd:int", list.getItemType());

        XsdSimpleType listSimpleType = list.getXsdSimpleType();

        Assert.assertNotNull(listSimpleType);

        List<XsdRestriction> listRestrictions = listSimpleType.getAllRestrictions();

        Assert.assertEquals(1, listRestrictions.size());

        XsdRestriction restriction = listRestrictions.get(0);

        Assert.assertNotNull(restriction);

        XsdLength length = restriction.getLength();
        XsdMaxLength maxLength = restriction.getMaxLength();

        Assert.assertNotNull(length);
        Assert.assertEquals(5, length.getValue());
        Assert.assertEquals(5, maxLength.getValue());
    }
}
