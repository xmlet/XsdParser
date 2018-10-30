package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.core.XsdParserJar;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HtmlParseTest {

    private static final List<ParserResult> parserResults = new ArrayList<>();

    private static final List<ParserResult> parserNonPartedResults = new ArrayList<>();
    private static final List<ParserResult> parserPartedResults = new ArrayList<>();

    static{
        ParserResult html5 = new ParserResult(getFilePath("html_5.xsd"));
        ParserResult partedHtml5 = new ParserResult(getFilePath("html_5_types.xsd"));
        ParserResult html5Jar = new ParserResult("html_5.jar", "html_5_jar.xsd");
        ParserResult partedHtml5Jar = new ParserResult("html_5.jar", "html_5_types_jar.xsd");

        parserResults.add(html5);
        parserResults.add(partedHtml5);
        parserResults.add(html5Jar);
        parserResults.add(partedHtml5Jar);

        parserNonPartedResults.add(html5);
        parserNonPartedResults.add(html5Jar);

        parserPartedResults.add(partedHtml5);
        parserPartedResults.add(partedHtml5Jar);
    }

    @Test
    public void testSchemaGetMethods(){
        for(ParserResult parserResult : parserNonPartedResults){
            XsdSchema schema = parserResult.getSchemas().get(0);

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

        for(ParserResult parserResult : parserPartedResults){
            XsdSchema schema0 = parserResult.getSchemas().get(0);

            Assert.assertEquals( 0, schema0.getChildrenElements().count());
            Assert.assertEquals( 5, schema0.getChildrenSimpleTypes().count());
            Assert.assertEquals( 1, schema0.getChildrenAnnotations().count());
            Assert.assertEquals( 11, schema0.getChildrenAttributeGroups().count());
            Assert.assertEquals( 0, schema0.getChildrenAttributes().count());
            Assert.assertEquals( 2, schema0.getChildrenComplexTypes().count());
            Assert.assertEquals( 8, schema0.getChildrenGroups().count());
            Assert.assertEquals( 0, schema0.getChildrenImports().count());
            Assert.assertEquals( 0, schema0.getChildrenIncludes().count());

            XsdSchema schema1 = parserResult.getSchemas().get(1);

            Assert.assertEquals( 104, schema1.getChildrenElements().count());
            Assert.assertEquals( 0, schema1.getChildrenSimpleTypes().count());
            Assert.assertEquals( 0, schema1.getChildrenAnnotations().count());
            Assert.assertEquals( 0, schema1.getChildrenAttributeGroups().count());
            Assert.assertEquals( 0, schema1.getChildrenAttributes().count());
            Assert.assertEquals( 0, schema1.getChildrenComplexTypes().count());
            Assert.assertEquals( 0, schema1.getChildrenGroups().count());
            Assert.assertEquals( 0, schema1.getChildrenImports().count());
            Assert.assertEquals( 0, schema1.getChildrenIncludes().count());
        }
    }

    /**
     * Verifies the excepted element count.
     */
    @Test
    public void testElementCount() {
        for(ParserResult parserResult : parserNonPartedResults) {
            Assert.assertEquals(104, parserResult.getElements().size());
            Assert.assertEquals(1, parserResult.getSchemas().size());
        }

        for(ParserResult parserResult : parserPartedResults) {
            Assert.assertEquals(104, parserResult.getElements().size());
            Assert.assertEquals(2, parserResult.getSchemas().size());
        }
    }

    /**
     * Tests if the first element, which should be the html as all the expected contents.
     */
    @Test
    public void testFirstElementContents() {
        for(ParserResult parserResult : parserResults) {
            XsdElement htmlElement = parserResult.getElements().get(0);

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
    }

    /**
     * Tests the first element attribute count against the expected count.
     */
    @Test
    public void testFirstElementAttributes() {
        for(ParserResult parserResult : parserResults){
            XsdElement htmlElement = parserResult.getElements().get(0);

            Assert.assertEquals("html", htmlElement.getName());

            XsdComplexType firstElementChild = htmlElement.getXsdComplexType();

            List<XsdAttribute> elementAttributes = firstElementChild.getXsdAttributes().collect(Collectors.toList());

            Assert.assertEquals(84, elementAttributes.size());
        }
    }

    /**
     * Verifies if there is any unexpected unsolved references in the parsing.
     */
    @Test
    public void testUnsolvedReferences() {
        for(ParserResult parserResult : parserResults){
            List<UnsolvedReferenceItem> unsolvedReferenceList = parserResult.getUnsolved();

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
    }

    /**
     * Verifies the contents of a {@link XsdSimpleType} object against the expected values.
     */
    @Test
    public void testSimpleTypes() {
        for (ParserResult parserResult : parserResults){
            XsdElement htmlElement = parserResult.getElements().get(5);

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
    }

    /**
     * Verifies if all the html_5 html5Elements have a attribute with the name 'class'.
     */
    @Test
    public void testClassAttribute(){
        for (ParserResult parserResult : parserResults) {
            parserResult.getElements().forEach(element ->
                    Assert.assertTrue(element.getXsdComplexType()
                            .getXsdAttributeGroup()
                            .anyMatch(attributeGroup ->
                                    attributeGroup.getAllAttributes()
                                            .anyMatch(attribute -> attribute.getName().equals("class"))))
            );
        }
    }

    /**
     * Verifies if there is an attributeGroup named classAttributeGroup that is the parent of all the existing attributeGroups.
     */
    @Test
    public void testClassParent(){
        for (ParserResult parserResult : parserResults){
            Optional<XsdAttribute> classAttribute = parserResult.getElements().get(0).getXsdComplexType().getXsdAttributes().filter(attribute -> attribute.getName() != null && attribute.getName().equals("class")).findFirst();

            Assert.assertTrue(classAttribute.isPresent());

            XsdAttribute classAttributeXsd = classAttribute.get();

            Assert.assertEquals("classAttributeGroup", ((XsdAttributeGroup)classAttributeXsd.getParent()).getName());
        }
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(String fileName){
        URL resource = HtmlParseTest.class.getClassLoader().getResource(fileName);

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The " + fileName + " file is missing from the XsdParser resource folder.");
        }
    }
}



class ParserResult{

    private final List<XsdElement> elements;
    private final List<XsdSchema> schemas;
    private final List<UnsolvedReferenceItem> unsolved;

    ParserResult(String fileName){
        XsdParserCore parser = new XsdParser(fileName);

        elements = parser.getResultXsdElements().collect(Collectors.toList());
        schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        unsolved = parser.getUnsolvedReferences();
    }

    ParserResult(String jarName, String fileName){
        XsdParserCore parser = new XsdParserJar(jarName, fileName);

        elements = parser.getResultXsdElements().collect(Collectors.toList());
        schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        unsolved = parser.getUnsolvedReferences();
    }

    List<XsdElement> getElements() {
        return elements;
    }

    List<XsdSchema> getSchemas() {
        return schemas;
    }

    List<UnsolvedReferenceItem> getUnsolved() {
        return unsolved;
    }
}