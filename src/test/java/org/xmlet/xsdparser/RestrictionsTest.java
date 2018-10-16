package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Multiple tests to verify Restrictions and {@link XsdAnnotation} behaviour.
 */
public class RestrictionsTest {

    private static final List<XsdElement> elements;
    private static final List<XsdSchema> schemas;

    private static final XsdParser parser;

    static {
        parser = new XsdParser(getFilePath());

        schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        elements = parser.getResultXsdElements().collect(Collectors.toList());
    }

    /**
     * Asserts if all the {@link Integer} based restrictions, i.e. {@link XsdIntegerRestrictions}, parse their values
     * properly.
     */
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

        Assert.assertEquals(100d, maxExclusive.getValue(), 0);
        Assert.assertTrue(maxExclusive.isFixed());

        Assert.assertEquals(0d, minExclusive.getValue(), 0);
        Assert.assertTrue(minExclusive.isFixed());

        Assert.assertEquals(99d, maxInclusive.getValue(), 0);
        Assert.assertFalse(maxInclusive.isFixed());

        Assert.assertEquals(1d, minInclusive.getValue(), 0);
        Assert.assertFalse(minInclusive.isFixed());

        Assert.assertEquals(2d, fractionDigits.getValue(), 0);
        Assert.assertTrue(fractionDigits.isFixed());

        Assert.assertEquals(10d, totalDigits.getValue(), 0);
        Assert.assertFalse(totalDigits.isFixed());
    }

    /**
     * Asserts if all the {@link String} based restrictions, i.e. {@link XsdStringRestrictions}, parse their values
     * properly.
     */
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

        Assert.assertEquals(10d, xsdLength.getValue(), 0);
        Assert.assertTrue(xsdLength.isFixed());

        Assert.assertEquals(10d, xsdLength.getValue(), 0);
        Assert.assertTrue(xsdLength.isFixed());

        Assert.assertEquals(10d, xsdLength.getValue(), 0);
        Assert.assertTrue(xsdLength.isFixed());

        Assert.assertEquals(".*", xsdPattern.getValue());

        Assert.assertEquals("preserve", xsdWhiteSpace.getValue().getValue());
        Assert.assertFalse(xsdWhiteSpace.isFixed());
    }

    /**
     * Tests {@link XsdAnnotation} contents.
     */
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

    /**
     * Tests a {@link XsdList} value with multiple restrictions.
     */
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

        XsdSimpleType listSimpleType = list.getXsdSimpleType();

        Assert.assertNotNull(listSimpleType);

        List<XsdRestriction> listRestrictions = listSimpleType.getAllRestrictions();

        Assert.assertEquals(1, listRestrictions.size());

        XsdRestriction restriction = listRestrictions.get(0);

        Assert.assertNotNull(restriction);

        XsdLength length = restriction.getLength();
        XsdMaxLength maxLength = restriction.getMaxLength();

        Assert.assertNotNull(length);
        Assert.assertEquals(5d, length.getValue(), 0);
        Assert.assertEquals(5d, maxLength.getValue(), 0);
    }

    /**
     * Asserts if all the {@link Double} based restrictions, i.e. {@link XsdDoubleRestrictions}, parse their values
     * properly.
     */
    @Test
    public void testDoubleRestrictions() {
        Optional<XsdSchema> xsdSchemaOptional = parser.getResultXsdSchemas().findFirst();

        Assert.assertTrue(xsdSchemaOptional.isPresent());

        XsdSchema xsdSchema = xsdSchemaOptional.get();

        Optional<XsdSimpleType> simpleTypeObj = xsdSchema.getChildrenSimpleTypes()
                    .filter(simpleType -> simpleType.getName().equals("IDContatto"))
                    .findFirst();

        Assert.assertTrue(simpleTypeObj.isPresent());

        XsdSimpleType simpleType = simpleTypeObj.get();

        XsdRestriction restriction = simpleType.getRestriction();

        Assert.assertNotNull(restriction);

        XsdMinInclusive minInclusive = restriction.getMinInclusive();
        XsdMaxInclusive maxInclusive = restriction.getMaxInclusive();

        Assert.assertNotNull(minInclusive);
        Assert.assertNotNull(maxInclusive);

        Assert.assertEquals(99999999999999d, minInclusive.getValue(), 0);
        Assert.assertEquals(99999999999999.9d, maxInclusive.getValue(), 0);
    }

    @Test
    public void remainingTypesVerification(){
        XsdSchema schema = schemas.get(0);

        Optional<XsdGroup> groupOptional = schema.getChildrenGroups().findFirst();

        Assert.assertTrue(groupOptional.isPresent());

        XsdGroup group = groupOptional.get();

        Assert.assertEquals("randomGroup", group.getName());

        XsdAll all = group.getChildAsAll();

        Assert.assertNull(group.getChildAsChoice());
        Assert.assertNull(group.getChildAsSequence());
        Assert.assertNotNull(all);

        List<XsdElement> allChildren = all.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, allChildren.size());

        XsdElement element = allChildren.get(0);

        XsdComplexType complexType = element.getXsdComplexType();

        Assert.assertNotNull(complexType);

        XsdSimpleContent simpleContent = complexType.getSimpleContent();

        Assert.assertNotNull(simpleContent);

        XsdExtension extension = simpleContent.getXsdExtension();

        Assert.assertNotNull(extension);

        XsdElement base = extension.getBase();

        Assert.assertNotNull(base);
        Assert.assertEquals("annotatedElement", base.getName());
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = HtmlParseTest.class.getClassLoader().getResource("test.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The test.xsd file is missing from the XsdParser resource folder.");
        }
    }
}
