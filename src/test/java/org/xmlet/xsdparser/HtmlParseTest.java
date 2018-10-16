package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class HtmlParseTest {

    private static final List<XsdElement> elements;
    private static final List<XsdSchema> schemas;
    private static final XsdParser parser;

    static{
        parser = new XsdParser(getFilePath());

        elements = parser.getResultXsdElements().collect(Collectors.toList());
        schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
    }

    @Test
    public void testSchemaGetMethods(){
        XsdSchema schema = schemas.get(0);

        Assert.assertEquals( 104, schema.getChildrenElements().count());
        Assert.assertEquals( 5, schema.getChildrenSimpleTypes().count());
        Assert.assertEquals( 1, schema.getChildrenAnnotations().count());
        Assert.assertEquals( 11, schema.getChildrenAttributeGroups().count());
        Assert.assertEquals( 0, schema.getChildrenAttributes().count());
        Assert.assertEquals( 2, schema.getChildrenComplexTypes().count());
        Assert.assertEquals( 8, schema.getChildrenGroups().count());
        Assert.assertEquals( 0, schema.getChildrenImports().count());
        Assert.assertEquals( 0, schema.getChildrenIncludes().count());
    }

    /**
     * Verifies the excepted element count.
     */
    @Test
    public void testElementCount() {
        Assert.assertEquals(104, elements.size());
        Assert.assertEquals(1, schemas.size());
    }

    /**
     * Tests if the first element, which should be the html as all the expected contents.
     */
    @Test
    public void testFirstElementContents() {
        XsdElement htmlElement = elements.get(0);

        Assert.assertEquals("html", htmlElement.getName());
        Assert.assertEquals(1, (int) htmlElement.getMinOccurs());
        Assert.assertEquals("1", htmlElement.getMaxOccurs());

        XsdComplexType firstElementChild = htmlElement.getXsdComplexType();

        Assert.assertEquals(firstElementChild.getXsdChildElement().getClass(), XsdChoice.class);

        XsdChoice complexTypeChild = firstElementChild.getChildAsChoice();
        Assert.assertNull(firstElementChild.getChildAsAll());
        Assert.assertNull(firstElementChild.getChildAsGroup());
        Assert.assertNull(firstElementChild.getChildAsSequence());

        List<XsdElement> choiceElements = complexTypeChild.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(2, choiceElements.size());

        XsdElement child1 = choiceElements.get(0);
        XsdElement child2 = choiceElements.get(1);

        Assert.assertEquals("body", child1.getName());
        Assert.assertEquals("head", child2.getName());
    }

    /**
     * Tests the first element attribute count against the expected count.
     */
    @Test
    public void testFirstElementAttributes() {
        XsdElement htmlElement = elements.get(0);

        Assert.assertEquals("html", htmlElement.getName());

        XsdComplexType firstElementChild = htmlElement.getXsdComplexType();

        List<XsdAttribute> elementAttributes = firstElementChild.getXsdAttributes().collect(Collectors.toList());

        Assert.assertEquals(84, elementAttributes.size());
    }

    /**
     * Verifies if there is any unexpected unsolved references in the parsing.
     */
    @Test
    public void testUnsolvedReferences() {
        List<UnsolvedReferenceItem> unsolvedReferenceList = parser.getUnsolvedReferences();

        Assert.assertEquals(4, unsolvedReferenceList.size());

        List<XsdAbstractElement> parents = unsolvedReferenceList.get(0).getParents();

        Assert.assertEquals(4, parents.size());

        XsdAbstractElement parent1 = parents.get(0);
        XsdAbstractElement parent2 = parents.get(1);
        XsdAbstractElement parent3 = parents.get(2);
        XsdAbstractElement parent4 = parents.get(3);

        Assert.assertEquals(XsdAttribute.class, parent1.getClass());
        Assert.assertEquals(XsdAttribute.class, parent2.getClass());
        Assert.assertEquals(XsdAttribute.class, parent3.getClass());
        Assert.assertEquals(XsdAttribute.class, parent4.getClass());

        XsdAttribute parent1Attr = (XsdAttribute) parent1;
        XsdAttribute parent2Attr = (XsdAttribute) parent2;
        XsdAttribute parent3Attr = (XsdAttribute) parent3;
        XsdAttribute parent4Attr = (XsdAttribute) parent4;

        Assert.assertEquals("lang", parent1Attr.getName());
        Assert.assertEquals("hreflang", parent2Attr.getName());
        Assert.assertEquals("hreflang", parent3Attr.getName());
        Assert.assertEquals("hreflang", parent4Attr.getName());
    }

    /**
     * Verifies the contents of a {@link XsdSimpleType} object against the expected values.
     */
    @Test
    public void testSimpleTypes() {
        XsdElement htmlElement = elements.get(5);

        Assert.assertEquals("meta", htmlElement.getName());

        XsdComplexType metaChild = htmlElement.getXsdComplexType();

        Optional<XsdAttribute> attributeOptional = metaChild.getXsdAttributes().filter(attribute1 -> attribute1.getName().equals("http_equiv")).findFirst();

        Assert.assertTrue(attributeOptional.isPresent());

        XsdAttribute attribute = attributeOptional.get();

        Assert.assertEquals(true, attribute.getXsdSimpleType() != null);

        XsdSimpleType simpleType = attribute.getXsdSimpleType();

        Assert.assertNull(simpleType.getRestriction());
        Assert.assertNull(simpleType.getList());
        Assert.assertNotNull(simpleType.getUnion());

        XsdUnion union = simpleType.getUnion();

        Assert.assertEquals(2, union.getUnionElements().size());

        XsdSimpleType innerSimpleType1 = union.getUnionElements().get(0);

        Assert.assertNotNull(innerSimpleType1.getRestriction());
        Assert.assertNull(innerSimpleType1.getList());
        Assert.assertNull(innerSimpleType1.getUnion());

        XsdRestriction restriction = innerSimpleType1.getRestriction();

        List<XsdEnumeration> enumeration = restriction.getEnumeration();

        Assert.assertEquals(4, enumeration.size());

        Assert.assertEquals("content-language", enumeration.get(0).getValue());
        Assert.assertEquals("content-type", enumeration.get(1).getValue());
        Assert.assertEquals("default-style", enumeration.get(2).getValue());
        Assert.assertEquals("refresh", enumeration.get(3).getValue());

        Assert.assertNull(restriction.getFractionDigits());
        Assert.assertNull(restriction.getLength());
        Assert.assertNull(restriction.getMaxExclusive());
        Assert.assertNull(restriction.getMaxInclusive());
        Assert.assertNull(restriction.getMaxLength());
        Assert.assertNull(restriction.getMinExclusive());
        Assert.assertNull(restriction.getMinInclusive());
        Assert.assertNull(restriction.getMinLength());
        Assert.assertNull(restriction.getPattern());
        Assert.assertNull(restriction.getTotalDigits());
        Assert.assertNull(restriction.getWhiteSpace());
    }

    /**
     * Verifies if all the html_5 elements have a attribute with the name 'class'.
     */
    @Test
    public void testClassAttribute(){
        elements.forEach(element ->
                Assert.assertTrue(element.getXsdComplexType()
                        .getXsdAttributeGroup()
                        .anyMatch(attributeGroup ->
                                attributeGroup.getAllAttributes()
                                        .anyMatch(attribute -> attribute.getName().equals("class"))))
        );
    }

    /**
     * Verifies if there is an attributeGroup named classAttributeGroup that is the parent of all the existing attributeGroups.
     */
    @Test
    public void testClassParent(){
        Optional<XsdAttribute> classAttribute = elements.get(0).getXsdComplexType().getXsdAttributes().filter(attribute -> attribute.getName() != null && attribute.getName().equals("class")).findFirst();

        Assert.assertTrue(classAttribute.isPresent());

        XsdAttribute classAttributeXsd = classAttribute.get();

        Assert.assertEquals("classAttributeGroup", ((XsdAttributeGroup)classAttributeXsd.getParent()).getName());
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = HtmlParseTest.class.getClassLoader().getResource("html_5.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The html_5.xsd file is missing from the XsdParser resource folder.");
        }
    }
}