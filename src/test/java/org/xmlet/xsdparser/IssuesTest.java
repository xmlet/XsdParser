package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IssuesTest {

    private static final List<XsdElement> elements;
    private static final List<XsdSchema> schemas;

    private static final XsdParser parser;

    static {
        parser = new XsdParser(getFilePath("issues.xsd"));

        schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        elements = parser.getResultXsdElements().collect(Collectors.toList());
    }

    @Test
    public void testSubstitutionGroup(){
        List<XsdElement> elements = parser.getResultXsdElements().collect(Collectors.toList());

        Optional<XsdElement> navnOptional = elements.stream().filter(element -> element.getName().equals("navn")).findFirst();
        Optional<XsdElement> kundeOptional = elements.stream().filter(element -> element.getName().equals("kunde")).findFirst();

        Assert.assertTrue(navnOptional.isPresent());
        Assert.assertTrue(kundeOptional.isPresent());

        XsdElement navnElement = navnOptional.get().getXsdSubstitutionGroup();
        XsdElement kundeElement = kundeOptional.get().getXsdSubstitutionGroup();

        Assert.assertEquals("xsd:string", navnElement.getType());

        XsdComplexType kundeComplexType = kundeElement.getXsdComplexType();

        Assert.assertNotNull(kundeComplexType);
        Assert.assertEquals("custinfo", kundeComplexType.getName());

        XsdSequence custinfoSequence = kundeComplexType.getChildAsSequence();

        Assert.assertNotNull(custinfoSequence);
        List<XsdElement> sequenceElements = custinfoSequence.getChildrenElements().collect(Collectors.toList());

        Assert.assertNotNull(sequenceElements);
        Assert.assertEquals(1, sequenceElements.size());

        XsdElement nameElement = sequenceElements.get(0);

        Assert.assertEquals(navnElement.getName(), nameElement.getName());
        Assert.assertEquals(navnElement.getType(), nameElement.getType());
    }

    @Test
    public void testDocumentationWithCDATA(){
        Optional<XsdElement> someElementOpt = elements.stream().filter(e -> e.getName().equals("someElement")).findFirst();

        Assert.assertTrue(someElementOpt.isPresent());

        XsdElement someElement = someElementOpt.get();
        XsdAnnotation annotation = someElement.getAnnotation();
        List<XsdDocumentation> documentations = annotation.getDocumentations();
        XsdDocumentation xsdDocumentation = documentations.get(0);

        Assert.assertEquals("<![CDATA[\r\n" +
                "\t\t\tCDATA line 1\r\n" +
                "\t\t\tCDATA line 2\r\n" +
                "\t\t\t]]>",xsdDocumentation.getContent());
    }

    @Test
    public void testIssue20(){
        Optional<XsdSchema> issuesSchemaOpt = schemas.stream().findFirst();

        Assert.assertTrue(issuesSchemaOpt.isPresent());

        XsdSchema issuesSchema = issuesSchemaOpt.get();

        Optional<XsdComplexType> fooTypeComplexTypeOpt = issuesSchema.getChildrenComplexTypes().filter(xsdComplexType -> xsdComplexType.getName().equals("fooType")).findFirst();
        Assert.assertTrue(fooTypeComplexTypeOpt.isPresent());

        XsdSequence fooTypeSequence = fooTypeComplexTypeOpt.get().getChildAsSequence();
        Assert.assertNotNull(fooTypeSequence);

        Optional<XsdElement> sequenceElementOpt = fooTypeSequence.getChildrenElements().filter(elem -> elem.getName().equals("id")).findFirst();
        Assert.assertTrue(sequenceElementOpt.isPresent());

        XsdElement sequenceElement = sequenceElementOpt.get();

        XsdComplexType complexType = sequenceElement.getXsdComplexType();
        Assert.assertNull(complexType);

        XsdSimpleType simpleType = sequenceElement.getXsdSimpleType();
        Assert.assertNotNull(simpleType);
    }

    @Test
    public void testIssue21(){
        Optional<XsdElement> hoursPerWeekOpt = elements.stream().filter(e -> e.getName().equals("hoursPerWeek")).findFirst();

        Assert.assertTrue(hoursPerWeekOpt.isPresent());

        XsdElement hoursPerWeek = hoursPerWeekOpt.get();
        String type = hoursPerWeek.getAttributesMap().get(XsdAbstractElement.TYPE_TAG);
        String typeOfMethod = hoursPerWeek.getType();

        Assert.assertEquals("xsd:double", type);
        Assert.assertEquals("xsd:double", typeOfMethod);

        XsdNamedElements xsdType = hoursPerWeek.getTypeAsXsd();
        XsdComplexType xsdComplexType = hoursPerWeek.getTypeAsComplexType();
        XsdSimpleType xsdSimpleType = hoursPerWeek.getTypeAsSimpleType();
        XsdBuiltInDataType xsdBuiltInDataType = hoursPerWeek.getTypeAsBuiltInDataType();

        Assert.assertEquals("xsd:double", xsdType.getRawName());
        Assert.assertEquals("xsd:double", xsdBuiltInDataType.getRawName());
        Assert.assertNull(xsdComplexType);
        Assert.assertNull(xsdSimpleType);

        Assert.assertEquals(hoursPerWeek, xsdBuiltInDataType.getParent());
    }

    @Test
    public void testIssue23(){
        XsdParser parser = new XsdParser(getFilePath("issue_23.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
    }

    @Test
    public void testIssue24(){
        XsdParser parser = new XsdParser(getFilePath("issue_24.xsd"));
        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        for (XsdSchema schema : schemas) {
            List<XsdComplexType> cts = schema.getChildrenComplexTypes().collect(Collectors.toList());
            for (XsdComplexType ct : cts) {
//                System.out.println("----------------------------------------------------------");
//                System.out.println("ComplexType " + ct.getName() + "#" + System.identityHashCode(ct));
//                ct.getXsdElements().forEach(e -> System.out.println(" -> children=" + getInfo(e)));
//                System.out.println(" -> parent=" + getInfo(ct.getParent()));
//                ct.getParent()
//                        .getXsdElements()
//                        .forEach(e -> System.out.println(" -> -> Parent Children =" + getInfo(e)));
//                System.out.println(" -> grandparent=" + (ct.getParent() != null ? getInfo(ct.getParent().getParent()) : "null"));
                Assert.assertEquals(schema, ct.getXsdSchema());
            }
        }
    }

    @Test
    public void testIssue25(){
        XsdParser parser = new XsdParser(getFilePath("issue_25_ToysBaby.xsd"));

        testToysBaby(parser);
    }

    @Test
    public void testIssue26_Includes(){
        XsdParser parser = new XsdParser(getFilePath("issue_26_ToysBaby_Includes.xsd"));

        testToysBaby(parser);
    }

    @Test
    public void testIssue26_CustomerTypes(){
        XsdParser parser = new XsdParser(getFilePath("issue_26_CustomerTypes.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        for(XsdSchema schema : schemas){
            Stream<XsdElement> elems = schema.getChildrenElements();

            int b = 5;
        }

        int a = 5;
    }

    private void testToysBaby(XsdParser parser){
        Optional<XsdSchema> mainSchemaOptional = parser.getResultXsdSchemas().filter(schema -> schema.getId() != null && schema.getId().equals("main")).findFirst();

        Assert.assertTrue(mainSchemaOptional.isPresent());

        XsdSchema mainSchema = mainSchemaOptional.get();

        Assert.assertNotNull(mainSchema);

        Optional<XsdElement> toysBabyElementOptional = mainSchema.getChildrenElements().filter(element -> element.getName().equals("ToysBaby")).findFirst();

        Assert.assertTrue(toysBabyElementOptional.isPresent());

        XsdElement toysBabyElement = toysBabyElementOptional.get();

        Assert.assertNotNull(toysBabyElement);

        XsdComplexType toysBabyComplexType = toysBabyElementOptional.get().getXsdComplexType();

        Assert.assertNotNull(toysBabyComplexType);

        XsdSequence toysBabySequence = toysBabyComplexType.getChildAsSequence();

        Assert.assertNotNull(toysBabySequence);

        Optional<XsdElement> ageRecommendationElement = toysBabySequence.getChildrenElements().filter(sequenceElement -> sequenceElement.getName().equals("AgeRecommendation")).findFirst();

        Assert.assertTrue(ageRecommendationElement.isPresent());

        XsdElement ageRecommendation = ageRecommendationElement.get();

        Assert.assertNotNull(ageRecommendation);

        XsdComplexType ageRecommendationComplexType = ageRecommendation.getXsdComplexType();

        Assert.assertNotNull(ageRecommendationComplexType);

        XsdSequence ageRecommendationSequence = ageRecommendationComplexType.getChildAsSequence();

        Assert.assertNotNull(ageRecommendationSequence);

        List<XsdElement> ageRecommendationSequenceElements = ageRecommendationSequence.getChildrenElements().collect(Collectors.toList());

        Assert.assertNotNull(ageRecommendationSequenceElements);
        Assert.assertEquals(4, ageRecommendationSequenceElements.size());

        Optional<XsdElement> minimumManufacturerAgeRecommendedOptional = ageRecommendationSequenceElements.stream().filter(sequenceElement -> sequenceElement.getName().equals("MinimumManufacturerAgeRecommended")).findFirst();

        Assert.assertTrue(minimumManufacturerAgeRecommendedOptional.isPresent());

        XsdElement minimumManufacturerAgeRecommended = minimumManufacturerAgeRecommendedOptional.get();

        Assert.assertNotNull(minimumManufacturerAgeRecommended);

        XsdComplexType minimumManufacturerAgeRecommendedXsdComplexType = minimumManufacturerAgeRecommended.getXsdComplexType();

        Assert.assertNotNull(minimumManufacturerAgeRecommendedXsdComplexType);

        XsdSimpleContent minimumManufacturerAgeRecommendedXsdSimpleContent = minimumManufacturerAgeRecommendedXsdComplexType.getSimpleContent();

        Assert.assertNotNull(minimumManufacturerAgeRecommendedXsdSimpleContent);

        XsdExtension minimumManufacturerAgeRecommendedXsdExtension = minimumManufacturerAgeRecommendedXsdSimpleContent.getXsdExtension();

        Assert.assertNotNull(minimumManufacturerAgeRecommendedXsdExtension);

        List<XsdAttribute> attributes = minimumManufacturerAgeRecommendedXsdExtension.getXsdAttributes().collect(Collectors.toList());

        Assert.assertEquals(1, attributes.size());
    }


    private String getInfo(XsdAbstractElement xae) {
        if (xae == null) {
            return "null";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(xae.getClass().getSimpleName() + "#" + System.identityHashCode(xae) + " " + xae.getAttributesMap());
            return sb.toString();
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
            throw new RuntimeException("The issues.xsd file is missing from the XsdParser resource folder.");
        }
    }
}
