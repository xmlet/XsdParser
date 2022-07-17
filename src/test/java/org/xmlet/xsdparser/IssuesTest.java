package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

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

        Assert.assertTrue(true);
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
    public void testIssue25ToysBaby(){
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
        }

        Assert.assertTrue(true);
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

    @Test
    public void testIssue25AutoAccessory(){
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
    public void testIssue27Attributes(){
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
    public void testIssue27TransitiveIncludes(){
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
    public void testIssue27TransitiveImports(){
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
    public void testIssue28(){
        XsdParser parser = new XsdParser(getFilePath("issue_28.xsd"));
    }

    @Test
    public void testIssue30(){
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
    public void testIssue34(){
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
    public void testIssue35(){
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

        XsdSimpleType max35TextSimpleType = xsdRestriction.getSimpleType();

        Assert.assertNotNull(max35TextSimpleType);
        Assert.assertEquals("Max35Text", max35TextSimpleType.getName());

        XsdRestriction max35TextRestriction = max35TextSimpleType.getRestriction();

        Assert.assertNotNull(max35TextRestriction);
        Assert.assertEquals("xsd:string", max35TextRestriction.getBase());
    }

    @Test
    public void testIssue37(){
        XsdParser parser = new XsdParser(getFilePath("issue_37/entire/us-gaap-entryPoint-all-2021-01-31.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        List<XsdElement> elements = parser.getResultXsdElements().collect(Collectors.toList());
        List<UnsolvedReferenceItem> unsolvedReferences = parser.getUnsolvedReferences();
    }

    @Test
    public void testIssue43(){
        XsdParser parser = new XsdParser(getFilePath("issue_43/BICEPS_MessageModel.xsd"));

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        List<XsdElement> elements = parser.getResultXsdElements().collect(Collectors.toList());
        List<UnsolvedReferenceItem> unsolvedReferences = parser.getUnsolvedReferences();
    }

    @Test
    public void testPersons(){
        XsdParser parser = new XsdParser(getFilePath("persons/Person.xsd"));

        List<UnsolvedReferenceItem> unsolvedReferences = parser.getUnsolvedReferences();
        
        Assert.assertEquals(0, unsolvedReferences.size());
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
