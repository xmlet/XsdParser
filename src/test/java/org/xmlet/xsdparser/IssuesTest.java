package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public void testSubstitutionGroup() {
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
    public void testDocumentationWithCDATA() {
        Optional<XsdElement> someElementOpt = elements.stream().filter(e -> e.getName().equals("someElement")).findFirst();

        Assert.assertTrue(someElementOpt.isPresent());

        XsdElement someElement = someElementOpt.get();
        XsdAnnotation annotation = someElement.getAnnotation();
        List<XsdDocumentation> documentations = annotation.getDocumentations();
        XsdDocumentation xsdDocumentation = documentations.get(0);

        Assert.assertEquals("<![CDATA[\r\n" +
                "\t\t\tCDATA line 1\r\n" +
                "\t\t\tCDATA line 2\r\n" +
                "\t\t\t]]>", xsdDocumentation.getContent());
    }

    @Test
    public void testIssue20() {
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
    public void testIssue21() {
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
    public void testIssue23() {
        XsdParser parser = new XsdParser(getFilePath("issue_23.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        Assert.assertTrue(true);
    }

    @Test
    public void testIssue24() {
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
    public void testIssue25ToysBaby() {
        XsdParser parser = new XsdParser(getFilePath("issue_25_ToysBaby.xsd"));

        testToysBaby(parser);
    }

    @Test
    public void testIssue26_Includes() {
        XsdParser parser = new XsdParser(getFilePath("issue_26_ToysBaby_Includes.xsd"));

        testToysBaby(parser);
    }

    @Test
    public void testIssue26_CustomerTypes() {
        XsdParser parser = new XsdParser(getFilePath("issue_26_CustomerTypes.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        for (XsdSchema schema : schemas) {
            Stream<XsdElement> elems = schema.getChildrenElements();
        }

        Assert.assertTrue(true);
    }

    private void testToysBaby(XsdParser parser) {
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

    @Test
    public void testIssue25AutoAccessory() {
        XsdParser parser = new XsdParser(getFilePath("issue_25_AutoAccessory.xsd"));

        XsdSchema amz = parser.getResultXsdSchemas().filter(schema -> schema.getId() == null).findFirst().get();
        Optional<XsdSchema> mainSchemaOptional = parser.getResultXsdSchemas().filter(schema -> schema.getId() != null && schema.getId().equals("main")).findFirst();

        Assert.assertTrue(mainSchemaOptional.isPresent());

        XsdSchema mainSchema = mainSchemaOptional.get();

        Assert.assertNotNull(mainSchema);

        Optional<XsdElement> toysBabyElementOptional = mainSchema.getChildrenElements().filter(element -> element.getName().equals("AutoAccessory")).findFirst();

        Assert.assertTrue(toysBabyElementOptional.isPresent());

        XsdElement toysBabyElement = toysBabyElementOptional.get();

        Assert.assertNotNull(toysBabyElement);

        XsdComplexType toysBabyComplexType = toysBabyElementOptional.get().getXsdComplexType();

        Assert.assertNotNull(toysBabyComplexType);

        XsdSequence toysBabySequence = toysBabyComplexType.getChildAsSequence();

        Assert.assertNotNull(toysBabySequence);

        Optional<XsdElement> ageRecommendationElement = toysBabySequence.getChildrenElements().filter(sequenceElement -> sequenceElement.getName().equals("ProductType")).findFirst();

        Assert.assertTrue(ageRecommendationElement.isPresent());

        XsdElement ageRecommendation = ageRecommendationElement.get();

        Assert.assertNotNull(ageRecommendation);

        XsdComplexType ageRecommendationComplexType = ageRecommendation.getXsdComplexType();

        Assert.assertNotNull(ageRecommendationComplexType);

        XsdChoice ageRecommendationSequence = ageRecommendationComplexType.getChildAsChoice();

        Assert.assertNotNull(ageRecommendationSequence);

        XsdElement autoAccessoryMiscElement = ageRecommendationSequence.getChildrenElements().filter(element -> element.getName().equals("AutoAccessoryMisc")).findFirst().get();

        XsdElement amperage = autoAccessoryMiscElement.getXsdComplexType().getChildAsSequence().getChildrenElements().filter(element -> element.getName().equals("Amperage")).findFirst().get();

        List<ReferenceBase> x = parser.parseElements.get("https://raw.githubusercontent.com/xmlet/XsdParser/master/src/test/resources/issue_25_amzn-base.xsd");

//        x.stream().findFirst((ReferenceBase a) -> ((NamedConcreteElement) a).getName().equals("")).


        Optional<XsdElement> a = mainSchema.getChildrenElements().filter(element -> element.getName().equals("AutoAccessoryMisc")).findFirst();
        Stream<XsdElement> b = a.get().getXsdComplexType().getChildAsSequence().getChildrenElements();
        XsdElement Amperage = b.filter(c -> c.getName().equals("Amperage")).findFirst().get();

        Assert.assertNotNull(amperage.getTypeAsComplexType());
    }

    @Test
    public void testIssue27Attributes() {
        XsdParser parser = new XsdParser(getFilePath("issue_27_attributes.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        Assert.assertEquals(1, schemas.size());

        XsdSchema schema = schemas.get(0);

        List<XsdElement> schemaElements = schema.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, schemaElements.size());

        XsdElement elementA = schemaElements.get(0);

        XsdComplexType complexTypeA = elementA.getTypeAsComplexType();

        Assert.assertNotNull(complexTypeA);

        XsdSimpleContent simpleContentA = complexTypeA.getSimpleContent();

        Assert.assertNotNull(simpleContentA);

        XsdExtension xsdExtensionA = simpleContentA.getXsdExtension();

        Assert.assertNotNull(xsdExtensionA);

        List<XsdAttribute> xsdExtensionAttributes = xsdExtensionA.getXsdAttributes().collect(Collectors.toList());

        Assert.assertEquals(2, xsdExtensionAttributes.size());

        XsdAttribute attributeA = xsdExtensionAttributes.get(0);
        XsdAttribute attributeB = xsdExtensionAttributes.get(1);

        Assert.assertEquals("AttributeA", attributeA.getName());
        Assert.assertEquals("AttributeB", attributeB.getName());
    }

    @Test
    public void testIssue27TransitiveIncludes() {
        XsdParser parser = new XsdParser(getFilePath("issue_27_Includes_A.xsd"));

        Optional<XsdSchema> mainSchemaOptional = parser.getResultXsdSchemas().filter(schema -> schema.getId() != null && schema.getId().equals("main")).findFirst();

        Assert.assertTrue(mainSchemaOptional.isPresent());

        XsdSchema mainSchema = mainSchemaOptional.get();

        List<XsdElement> schemaElements = mainSchema.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, schemaElements.size());

        Optional<XsdElement> optionalElementA = schemaElements.stream().filter(element -> element.getName() != null && element.getName().equals("A")).findFirst();

        Assert.assertTrue(optionalElementA.isPresent());

        XsdElement elementA = optionalElementA.get();

        XsdComplexType complexTypeA = elementA.getTypeAsComplexType();

        Assert.assertNotNull(complexTypeA);

        XsdAll allA = complexTypeA.getChildAsAll();

        Assert.assertNotNull(allA);

        List<XsdElement> allAChildren = allA.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(2, allAChildren.size());

        XsdElement elementB = allAChildren.get(0);

        Assert.assertEquals("B", elementB.getName());

        XsdComplexType complexTypeB = elementB.getTypeAsComplexType();

        Assert.assertNotNull(complexTypeB);

        XsdChoice choiceB = complexTypeB.getChildAsChoice();

        Assert.assertNotNull(choiceB);

        List<XsdElement> choiceBChildren = choiceB.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, choiceBChildren.size());

        XsdElement elementC = choiceBChildren.get(0);

        Assert.assertEquals("C", elementC.getName());

        XsdComplexType complexTypeC = elementC.getTypeAsComplexType();

        Assert.assertNotNull(complexTypeC);

        XsdSequence sequenceC = complexTypeC.getChildAsSequence();

        Assert.assertNotNull(sequenceC);

        List<XsdElement> sequenceCChildren = sequenceC.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, sequenceCChildren.size());

        XsdElement elementD = sequenceCChildren.get(0);

        Assert.assertEquals("D", elementD.getName());
    }

    @Test
    public void testIssue27TransitiveImports() {
        XsdParser parser = new XsdParser(getFilePath("issue_27_Import_A.xsd"));

        Optional<XsdSchema> mainSchemaOptional = parser.getResultXsdSchemas().filter(schema -> schema.getId() != null && schema.getId().equals("main")).findFirst();

        Assert.assertTrue(mainSchemaOptional.isPresent());

        XsdSchema mainSchema = mainSchemaOptional.get();

        List<XsdElement> schemaElements = mainSchema.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, schemaElements.size());

        Optional<XsdElement> optionalElementA = schemaElements.stream().filter(element -> element.getName() != null && element.getName().equals("A")).findFirst();

        Assert.assertTrue(optionalElementA.isPresent());

        XsdElement elementA = optionalElementA.get();

        XsdComplexType complexTypeA = elementA.getTypeAsComplexType();

        Assert.assertNotNull(complexTypeA);

        XsdAll allA = complexTypeA.getChildAsAll();

        Assert.assertNotNull(allA);

        List<XsdElement> allAChildren = allA.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, allAChildren.size());

        XsdElement elementB = allAChildren.get(0);

        Assert.assertEquals("B", elementB.getName());

        XsdComplexType complexTypeB = elementB.getXsdComplexType();

        Assert.assertNotNull(complexTypeB);

        XsdChoice choiceB = complexTypeB.getChildAsChoice();

        Assert.assertNotNull(choiceB);

        List<XsdElement> choiceBChildren = choiceB.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, choiceBChildren.size());

        XsdElement elementC = choiceBChildren.get(0);

        Assert.assertEquals("C", elementC.getName());

        XsdComplexType complexTypeC = elementC.getTypeAsComplexType();

        Assert.assertNotNull(complexTypeC);

        XsdSequence sequenceC = complexTypeC.getChildAsSequence();

        Assert.assertNotNull(sequenceC);

        List<XsdElement> sequenceCChildren = sequenceC.getChildrenElements().collect(Collectors.toList());

        Assert.assertEquals(1, sequenceCChildren.size());

        XsdElement elementD = sequenceCChildren.get(0);

        Assert.assertEquals("D", elementD.getName());
    }

    @Test
    public void testIssue28() {
        XsdParser parser = new XsdParser(getFilePath("issue_28.xsd"));
    }

    @Test
    public void testIssue30() {
        XsdParser parser = new XsdParser(getFilePath("issue_30.xsd"));

        Optional<XsdSchema> optionalXsdSchema = parser.getResultXsdSchemas().findFirst();

        Assert.assertTrue(optionalXsdSchema.isPresent());

        XsdSchema schema = optionalXsdSchema.get();

        List<XsdComplexType> complexTypeStream = schema.getChildrenComplexTypes().collect(Collectors.toList());

        Optional<XsdComplexType> optionalXsdComplexTypeAll = complexTypeStream.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("Norwegian_customer_All")).findFirst();
        Optional<XsdComplexType> optionalXsdComplexTypeChoice = complexTypeStream.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("Norwegian_customer_Choice")).findFirst();
        Optional<XsdComplexType> optionalXsdComplexTypeGroup = complexTypeStream.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("Norwegian_customer_Group")).findFirst();
        Optional<XsdComplexType> optionalXsdComplexTypeSequence = complexTypeStream.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("Norwegian_customer_Sequence")).findFirst();

        Assert.assertTrue(optionalXsdComplexTypeAll.isPresent());
        Assert.assertTrue(optionalXsdComplexTypeChoice.isPresent());
        Assert.assertTrue(optionalXsdComplexTypeGroup.isPresent());
        Assert.assertTrue(optionalXsdComplexTypeSequence.isPresent());

        XsdComplexType xsdComplexTypeAll = optionalXsdComplexTypeAll.get();
        XsdComplexType xsdComplexTypeChoice = optionalXsdComplexTypeChoice.get();
        XsdComplexType xsdComplexTypeGroup = optionalXsdComplexTypeGroup.get();
        XsdComplexType xsdComplexTypeSequence = optionalXsdComplexTypeSequence.get();

        XsdRestriction xsdRestrictionAll = xsdComplexTypeAll.getComplexContent().getXsdRestriction();
        XsdRestriction xsdRestrictionChoice = xsdComplexTypeChoice.getComplexContent().getXsdRestriction();
        XsdRestriction xsdRestrictionGroup = xsdComplexTypeGroup.getComplexContent().getXsdRestriction();
        XsdRestriction xsdRestrictionSequence = xsdComplexTypeSequence.getComplexContent().getXsdRestriction();

        Assert.assertNotNull(xsdRestrictionAll);
        Assert.assertNotNull(xsdRestrictionChoice);
        Assert.assertNotNull(xsdRestrictionGroup);
        Assert.assertNotNull(xsdRestrictionSequence);

        XsdAll xsdAll = xsdRestrictionAll.getAll();
        XsdChoice xsdChoice = xsdRestrictionChoice.getChoice();
        XsdGroup xsdGroup = xsdRestrictionGroup.getGroup();
        XsdSequence xsdSequence = xsdRestrictionSequence.getSequence();

        Assert.assertNotNull(xsdAll);
        Assert.assertNotNull(xsdChoice);
        Assert.assertNotNull(xsdGroup);
        Assert.assertNotNull(xsdSequence);

        Assert.assertEquals(3, xsdAll.getElements().size());
        Assert.assertEquals(3, xsdChoice.getElements().size());
        Assert.assertEquals(3, xsdSequence.getElements().size());
        Assert.assertEquals(1, xsdGroup.getElements().size());

        XsdAll xsdGroupAll = xsdGroup.getChildAsAll();

        Assert.assertNotNull(xsdGroupAll);

        Assert.assertEquals(3, xsdGroupAll.getElements().size());
    }

    @Test
    public void testIssue34() {
        XsdParser parser = new XsdParser(getFilePath("issue_34.xsd"));

        Optional<XsdSchema> optionalXsdSchema = parser.getResultXsdSchemas().findFirst();

        Assert.assertTrue(optionalXsdSchema.isPresent());

        XsdSchema schema = optionalXsdSchema.get();

        List<XsdSimpleType> simpleTypeList = schema.getChildrenSimpleTypes().collect(Collectors.toList());

        Optional<XsdSimpleType> optionalAuthorization1Code = simpleTypeList.stream().filter(simpleType -> simpleType.getName().equals("Authorisation1Code")).findFirst();

        Assert.assertTrue(optionalAuthorization1Code.isPresent());

        XsdSimpleType xsdSimpleType = optionalAuthorization1Code.get();

        XsdRestriction xsdRestriction = xsdSimpleType.getRestriction();

        Assert.assertNotNull(xsdRestriction);

        List<XsdEnumeration> xsdEnumerationList = xsdRestriction.getEnumeration();

        Assert.assertEquals(4, xsdEnumerationList.size());

        Optional<XsdEnumeration> optionalAuthEnumeration = xsdEnumerationList.stream().filter(xsdEnumeration -> xsdEnumeration.getValue().equals("AUTH")).findFirst();

        Assert.assertTrue(optionalAuthEnumeration.isPresent());

        XsdEnumeration authEnumeration = optionalAuthEnumeration.get();

        XsdAnnotation xsdAnnotation = authEnumeration.getAnnotation();

        Assert.assertNotNull(xsdAnnotation);

        List<XsdDocumentation> authDocumentations = xsdAnnotation.getDocumentations();

        Assert.assertEquals(2, authDocumentations.size());

        Optional<XsdDocumentation> optionalAuthNameDocumentation = authDocumentations.stream().filter(authDocumentation -> authDocumentation.getSource().equals("Name")).findFirst();
        Optional<XsdDocumentation> optionalAuthDefinitionDocumentation = authDocumentations.stream().filter(authDocumentation -> authDocumentation.getSource().equals("Definition")).findFirst();

        Assert.assertTrue(optionalAuthNameDocumentation.isPresent());
        Assert.assertTrue(optionalAuthDefinitionDocumentation.isPresent());

        XsdDocumentation authNameDocumentation = optionalAuthNameDocumentation.get();
        XsdDocumentation authDefinitionDocumentation = optionalAuthDefinitionDocumentation.get();

        Assert.assertEquals("PreAuthorisedFile", authNameDocumentation.getContent());
        Assert.assertEquals("Indicates a file has been pre authorised or approved within the originating customer environment and no further approval is required.", authDefinitionDocumentation.getContent());
    }

    @Test
    public void testIssue35() {
        XsdParser parser = new XsdParser(getFilePath("issue_35.xsd"));

        Optional<XsdSchema> optionalXsdSchema = parser.getResultXsdSchemas().findFirst();

        Assert.assertTrue(optionalXsdSchema.isPresent());

        XsdSchema schema = optionalXsdSchema.get();

        List<XsdSimpleType> simpleTypeList = schema.getChildrenSimpleTypes().collect(Collectors.toList());

        Optional<XsdSimpleType> optionalmax35TextCh = simpleTypeList.stream().filter(simpleType -> simpleType.getName().equals("Max35Text_CH_camt052")).findFirst();

        Assert.assertTrue(optionalmax35TextCh.isPresent());

        XsdSimpleType xsdSimpleType = optionalmax35TextCh.get();

        XsdRestriction xsdRestriction = xsdSimpleType.getRestriction();

        Assert.assertNotNull(xsdRestriction);

        XsdSimpleType max35TextSimpleType = xsdRestriction.getBaseAsSimpleType();

        Assert.assertNotNull(max35TextSimpleType);
        Assert.assertEquals("Max35Text", max35TextSimpleType.getName());

        XsdRestriction max35TextRestriction = max35TextSimpleType.getRestriction();

        Assert.assertNotNull(max35TextRestriction);
        Assert.assertEquals("xsd:string", max35TextRestriction.getBase());
    }

    @Test
    public void testIssue37() {
        XsdParser parser = new XsdParser(getFilePath("issue_37/entire/us-gaap-entryPoint-all-2021-01-31.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        List<XsdElement> elements = parser.getResultXsdElements().collect(Collectors.toList());
        List<UnsolvedReferenceItem> unsolvedReferences = parser.getUnsolvedReferences();
    }

    @Test
    public void testIssue44() {
        XsdParser parser = new XsdParser(getFilePath("issue_44.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        Optional<XsdSchema> schemaOptional = schemas.stream().findFirst();

        Assert.assertTrue(schemaOptional.isPresent());

        XsdSchema schema = schemaOptional.get();

        Assert.assertNotNull(schema);

        List<XsdComplexType> xsdComplexTypes = schema.getChildrenComplexTypes().collect(Collectors.toList());

        Optional<XsdComplexType> r1ContainerOptional = xsdComplexTypes.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("R1_Container")).findFirst();
        Optional<XsdComplexType> r2ContainerOptional = xsdComplexTypes.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("R2_Container")).findFirst();

        Assert.assertTrue(r1ContainerOptional.isPresent());
        Assert.assertTrue(r2ContainerOptional.isPresent());

        XsdComplexType r1Container = r1ContainerOptional.get();
        XsdComplexType r2Container = r2ContainerOptional.get();

        Assert.assertNotNull(r1Container);
        Assert.assertNotNull(r2Container);

        XsdComplexContent r1ComplexContent = r1Container.getComplexContent();
        XsdComplexContent r2ComplexContent = r2Container.getComplexContent();

        Assert.assertNotNull(r1ComplexContent);
        Assert.assertNotNull(r2ComplexContent);

        XsdRestriction ctRestriction = r1ComplexContent.getXsdRestriction();
        XsdRestriction stRestriction = r2ComplexContent.getXsdRestriction();

        Assert.assertNotNull(ctRestriction);
        Assert.assertNotNull(stRestriction);

        XsdSimpleType ctRestrictionSimpleType = ctRestriction.getBaseAsSimpleType();
        XsdComplexType ctRestrictionComplexType = ctRestriction.getBaseAsComplexType();

        XsdSimpleType stRestrictionSimpleType = stRestriction.getBaseAsSimpleType();
        XsdComplexType stRestrictionComplexType = stRestriction.getBaseAsComplexType();

        Assert.assertNull(ctRestrictionSimpleType);
        Assert.assertNotNull(ctRestrictionComplexType);
        Assert.assertNotNull(stRestrictionSimpleType);
        Assert.assertNull(stRestrictionComplexType);
    }

    @Test
    public void testIssue49() {
        XsdParser parser = new XsdParser(getFilePath("issue_49.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        Optional<XsdSchema> schemaOptional = schemas.stream().findFirst();

        Assert.assertTrue(schemaOptional.isPresent());

        XsdSchema schema = schemaOptional.get();

        Assert.assertNotNull(schema);

        List<XsdComplexType> xsdComplexTypes = schema.getChildrenComplexTypes().collect(Collectors.toList());

        Optional<XsdComplexType> nameTypeOptional = xsdComplexTypes.stream().filter(xsdComplexType -> xsdComplexType.getName().equals("NameType")).findFirst();

        Assert.assertTrue(nameTypeOptional.isPresent());

        XsdComplexType nameTypeComplexType = nameTypeOptional.get();

        Assert.assertNotNull(nameTypeComplexType);

        XsdSimpleContent nameTypeSimpleContent = nameTypeComplexType.getSimpleContent();

        Assert.assertNotNull(nameTypeSimpleContent);

        XsdExtension xsdTokenExtension = nameTypeSimpleContent.getXsdExtension();

        Assert.assertNotNull(xsdTokenExtension);

        List<XsdAttribute> xsdExtensionAttributesList = xsdTokenExtension.getXsdAttributes().collect(Collectors.toList());

        Assert.assertEquals(1, xsdExtensionAttributesList.size());
    }

    @Test()
    public void testIssue50() {

        // ensure the OCX Schema is parserd without null pointer exception

        File xsdFileIn = new File("src/test/resources/issue_50/OCX_Schema.xsd");

        XsdParser parser = new XsdParser(xsdFileIn.getAbsolutePath());

    }

    @Test()
    public void testIssue53() {
        XsdParser parser = new XsdParser(getFilePath("issue_53.xsd"));

        XsdSchema schema = parser.getResultXsdSchemas().findFirst().get();
        XsdComplexType type = (XsdComplexType) schema.getXsdElements().findFirst().get();
        XsdSequence seq = type.getChildAsSequence();

        List<ReferenceBase> elements = seq.getElements();
        Assert.assertEquals(3, elements.size());
        XsdAbstractElement elem1 = elements.get(0).getElement();
        Assert.assertEquals(XsdElement.class, elem1.getClass());
        Assert.assertEquals("elem1", ((XsdElement) elem1).getName());
        XsdAbstractElement myGroup = elements.get(1).getElement();
        Assert.assertEquals(XsdGroup.class, myGroup.getClass());
        Assert.assertEquals("myGroup", ((XsdGroup) myGroup).getName());
        XsdAbstractElement elem2 = elements.get(2).getElement();
        Assert.assertEquals(XsdElement.class, elem2.getClass());
        Assert.assertEquals("elem2", ((XsdElement) elem2).getName());
    }

    @Test
    public void testIssue55_ResolveUnion() {
        XsdParser parser = new XsdParser(getFilePath("issue_25_amzn-base.xsd"));
        XsdSchema schema = parser.getResultXsdSchemas().findFirst().orElse(null);

        Assert.assertNotNull(schema);
        XsdUnion result =
                schema
                        .getXsdElements()
                        .filter(XsdSimpleType.class::isInstance)
                        .map(e -> (XsdSimpleType) e)
                        .filter(ct -> ct.getName().endsWith("VolumeAndVolumeRateUnitOfMeasure"))
                        .findFirst()
                        .map(XsdSimpleType::getUnion)
                        .orElse(null);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getUnionElements().size());
        Assert.assertTrue(result.getUnsolvedMemberTypesList().isEmpty());
    }

    @Test()
    public void testIssue58(){
        XsdParser parser = new XsdParser(getFilePath("issue_58/a.xsd"));

        XsdElement elem = parser.getResultXsdElements().findFirst().get();
        XsdNamedElements type = elem.getTypeAsXsd();
        Assert.assertNotNull(type);
        Assert.assertEquals("Foo", type.getName());
    }

    @Test()
    public void testIssue60(){
        XsdParser parser = new XsdParser(getFilePath("issue_60/a.xsd"));

        XsdElement elem1 = parser.getResultXsdElements().filter(e -> e.getName().equals("elem1")).findFirst().get();
        XsdElement elem2 = parser.getResultXsdElements().filter(e -> e.getName().equals("elem2")).findFirst().get();

        XsdNamedElements type1 = elem1.getTypeAsXsd();
        Assert.assertNotNull(type1);
        Assert.assertEquals("Beer", type1.getName());

        XsdNamedElements type2 = elem2.getTypeAsXsd();
        Assert.assertNotNull(type2);
        Assert.assertEquals("Pong", type2.getName());
    }

    @Test()
    public void testIssue62(){
        XsdParser parser = new XsdParser(getFilePath("issue_62/a.xsd"));

        Optional<XsdSchema> schemaAOptional = parser.getResultXsdSchemas().filter(schema -> schema.getTargetNamespace().equals("http://a.com")).findFirst();

        Assert.assertTrue(schemaAOptional.isPresent());

        XsdSchema schemaA = schemaAOptional.get();

        Stream<XsdSimpleType> schemaAXsdSimpleTypes = schemaA.getChildrenSimpleTypes();

        Optional<XsdSimpleType> testaOptional = schemaAXsdSimpleTypes.filter(e -> e.getName().equals("testa")).findFirst();
        Optional<XsdSimpleType> uniontestOptional = schemaA.getChildrenSimpleTypes().filter(e -> e.getName().equals("uniontest")).findFirst();

        Assert.assertTrue(testaOptional.isPresent());
        Assert.assertTrue(uniontestOptional.isPresent());

        XsdSimpleType testa = testaOptional.get();

        XsdRestriction testaRestriction = testa.getRestriction();

        Assert.assertNotNull(testaRestriction);

        XsdSimpleType testc = testaRestriction.getBaseAsSimpleType();

        Assert.assertNotNull(testc);

        XsdSimpleType uniontest = uniontestOptional.get();

        Assert.assertNotNull(uniontest);

        XsdUnion union = uniontest.getUnion();

        Assert.assertNotNull(union);

        List<XsdSimpleType> unionElements = union.getUnionElements();

        Assert.assertEquals(2, unionElements.size());

        Optional<XsdSimpleType> unionImport = unionElements.stream().filter(unionElement -> unionElement.getName().equals("unionimport")).findFirst();
        Optional<XsdSimpleType> unionMemberTesta = unionElements.stream().filter(unionElement -> unionElement.getName().equals("testa")).findFirst();

        Assert.assertTrue(unionImport.isPresent());
        Assert.assertTrue(unionMemberTesta.isPresent());
    }

    @Test
    public void testIssue63() {
        XsdParser xsdParser = new XsdParser( getFilePath("issue_63/a.xsd"));
        List<XsdElement> elements = xsdParser.getResultXsdElements().collect(Collectors.toList());
        for (XsdElement currentElement : elements) {
            XsdComplexType complexType = currentElement.getXsdComplexType();
            if ( complexType != null ) {
                complexType.getXsdAttributes().forEach( a -> a.getAllRestrictions() );
                Optional<XsdAttribute> firstAttributeOptional = complexType.getXsdAttributes().findFirst();

                Assert.assertTrue(firstAttributeOptional.isPresent());

                XsdAttribute firstAttribute = firstAttributeOptional.get();

                Assert.assertNotNull(firstAttribute);

                XsdBuiltInDataType firstAttributeBuiltInType = firstAttribute.getTypeAsBuiltInType();

                Assert.assertNotNull(firstAttributeBuiltInType);

                Assert.assertEquals("xsd:string", firstAttributeBuiltInType.getRawName());
            }
        }
    }

    @Test
    public void testIssue67() {
        XsdParser xsdParser = new XsdParser( getFilePath("issue_67/a.xsd"));
        List<XsdElement> elements = xsdParser.getResultXsdElements().collect(Collectors.toList());
        for (XsdElement currentElement : elements) {
            XsdSimpleType simpleType = currentElement.getXsdSimpleType();
            if ( simpleType != null ) {
                 /*
                  * 1.2.12 code : restrictions.getOrDefault( XsdParserCore.getXsdTypeToJava(unionMemberRestriction.getBase(), null)
                  * patch proposal : restrictions.getOrDefault( unionMemberRestriction.getBase() == null ? null : XsdParserCore.getXsdTypeToJava(unionMemberRestriction.getBase()), null)
                  */
                 // before the path here there is a NullPointerException on simpleType.getAllRestrictions()
                 Assert.assertEquals( 2, simpleType.getAllRestrictions().size() );
            }
        }
    }

    @Test
    public void testIssue69_Folders() {
        XsdParser xsdParser = new XsdParser( getFilePath("issue_69/A/Main.xsd"));
        Optional<XsdElement> optionalA = xsdParser.getResultXsdElements().filter(xsdElement -> xsdElement.getName().equals("A")).findFirst();

        Assert.assertTrue(optionalA.isPresent());

        XsdElement a = optionalA.get();

        Assert.assertNotNull(a);

        XsdComplexType aComplexType = a.getTypeAsComplexType();

        Assert.assertNotNull(aComplexType);

        Assert.assertEquals("TypeA", aComplexType.getName());
    }

    @Test
    public void testIssue69_Direct() {
        XsdParser xsdParser = new XsdParser( getFilePath("issue_69/Main.xsd"));
        Optional<XsdElement> optionalA = xsdParser.getResultXsdElements().filter(xsdElement -> xsdElement.getName().equals("A")).findFirst();

        Assert.assertTrue(optionalA.isPresent());

        XsdElement a = optionalA.get();

        Assert.assertNotNull(a);

        XsdComplexType aComplexType = a.getTypeAsComplexType();

        Assert.assertNotNull(aComplexType);

        Assert.assertEquals("TypeA", aComplexType.getName());
    }

    @Test(timeout=3000)
    public void testIssue70() {
        XsdParser xsdParser = new XsdParser( getFilePath("issue_70/a.xsd"));
        final List<XsdElement> result = xsdParser.getResultXsdElements().collect(Collectors.toList());
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(0, xsdParser.getUnsolvedReferences().size());

    }

    @Test
    public void testPersons() {
        XsdParser parser = new XsdParser(getFilePath("persons/Person.xsd"));

        List<UnsolvedReferenceItem> unsolvedReferences = parser.getUnsolvedReferences();

        Assert.assertEquals(0, unsolvedReferences.size());
    }

    @Test
    public void testIssue65() {
        XsdParser xsdParser = new XsdParser( getFilePath("issue_65.xsd"));
        XsdElement element = xsdParser.getResultXsdSchemas().findFirst().get().getChildrenElements().findFirst().get();
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
    private static String getFilePath(String fileName) {
        URL resource = HtmlParseTest.class.getClassLoader().getResource(fileName);

        if (resource != null) {
            return resource.getPath();
        } else {
            throw new RuntimeException("The issues.xsd file is missing from the XsdParser resource folder.");
        }
    }
}
