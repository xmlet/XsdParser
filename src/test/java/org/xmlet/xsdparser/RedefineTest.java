package org.xmlet.xsdparser;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.core.utils.UnsolvedReferenceItem;
import org.xmlet.xsdparser.xsdelements.*;

/**
 * Tests for xsd:redefine support
 */
public class RedefineTest {

    @Test
    public void testBasicRedefine() {
        String filePath = getFilePath("redefine_test_main.xsd");
        XsdParser parser = new XsdParser(filePath);

        // Check that there are no unsolved references
        List<UnsolvedReferenceItem> unsolvedReferenceItemList = parser.getUnsolvedReferences();
        assertTrue("There should be no unsolved references", unsolvedReferenceItemList.isEmpty());

        // Get the schemas
        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        assertFalse("Should have parsed schemas", schemas.isEmpty());

        // Find the main schema
        XsdSchema mainSchema = schemas.stream()
                .filter(schema -> schema.getFilePath().endsWith("redefine_test_main.xsd"))
                .findFirst()
                .orElse(null);
        assertNotNull("Main schema should be found", mainSchema);

        // Check that redefine elements are present
        List<XsdRedefine> redefines = mainSchema.getChildrenRedefines().collect(Collectors.toList());
        assertFalse("Should have redefine elements", redefines.isEmpty());
        assertEquals("Should have exactly one redefine", 1, redefines.size());

        XsdRedefine redefine = redefines.get(0);
        assertEquals("redefine_test_base.xsd", redefine.getSchemaLocation());

        // Check that redefined types are present
        List<XsdComplexType> redefinedComplexTypes = redefine.getChildrenComplexTypes().collect(Collectors.toList());
        assertFalse("Should have redefined complex types", redefinedComplexTypes.isEmpty());

        List<XsdSimpleType> redefinedSimpleTypes = redefine.getChildrenSimpleTypes().collect(Collectors.toList());
        assertFalse("Should have redefined simple types", redefinedSimpleTypes.isEmpty());

        List<XsdGroup> redefinedGroups = redefine.getChildrenGroups().collect(Collectors.toList());
        assertFalse("Should have redefined groups", redefinedGroups.isEmpty());

        List<XsdAttributeGroup> redefinedAttributeGroups = redefine.getChildrenAttributeGroups().collect(Collectors.toList());
        assertFalse("Should have redefined attribute groups", redefinedAttributeGroups.isEmpty());
    }

    @Test
    public void testChainedRedefine() {
        String filePath = getFilePath("redefine_test_chained_top.xsd");
        XsdParser parser = new XsdParser(filePath);

        // Check that there are no unsolved references
        List<UnsolvedReferenceItem> unsolvedReferenceItemList = parser.getUnsolvedReferences();
        assertTrue("There should be no unsolved references in chained redefines",
                   unsolvedReferenceItemList.isEmpty());

        // Get all schemas
        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());

        // Should have parsed all three schemas (top, middle, base)
        assertTrue("Should have parsed at least 3 schemas", schemas.size() >= 3);

        // Verify the top schema has a redefine
        XsdSchema topSchema = schemas.stream()
                .filter(schema -> schema.getFilePath().endsWith("redefine_test_chained_top.xsd"))
                .findFirst()
                .orElse(null);
        assertNotNull("Top schema should be found", topSchema);

        List<XsdRedefine> topRedefines = topSchema.getChildrenRedefines().collect(Collectors.toList());
        assertEquals("Top schema should have one redefine", 1, topRedefines.size());
    }

    @Test
    public void testRedefineWithElements() {
        String filePath = getFilePath("redefine_test_main.xsd");
        XsdParser parser = new XsdParser(filePath);

        List<XsdSchema> schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
        XsdSchema mainSchema = schemas.stream()
                .filter(schema -> schema.getFilePath().endsWith("redefine_test_main.xsd"))
                .findFirst()
                .orElse(null);

        assertNotNull("Main schema should be found", mainSchema);

        // Check that elements using redefined types are present
        List<XsdElement> elements = mainSchema.getChildrenElements().collect(Collectors.toList());
        assertFalse("Should have elements defined", elements.isEmpty());

        // Find the Person element
        XsdElement personElement = elements.stream()
                .filter(el -> "Person".equals(el.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Person element should be found", personElement);

        XsdComplexType personTypeComplexType = personElement.getXsdComplexType();
        assertNotNull(personTypeComplexType);

        XsdComplexContent personTypeComplexContent = personTypeComplexType.getComplexContent();
        assertNotNull(personTypeComplexContent);

        XsdExtension personTypeExtension = personTypeComplexContent.getXsdExtension();
        assertNotNull(personTypeExtension);
        
        List<XsdElement> extensionElements = personTypeExtension.getBaseAsComplexType().getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals(2, extensionElements.size());
        assertEquals(1, extensionElements.stream().filter(element -> element.getName().equals("name")).count());
        assertEquals(1, extensionElements.stream().filter(element -> element.getName().equals("age")).count());

        XsdSequence personTypeSequence = personTypeExtension.getChildAsSequence();
        assertNotNull(personTypeSequence);

        List<XsdElement> personTypeSequenceElements = personTypeSequence.getChildrenElements().collect(Collectors.toList());
        assertFalse(personTypeSequenceElements.isEmpty());
        assertEquals(2, personTypeSequenceElements.size());
        assertEquals(1, personTypeSequenceElements.stream().filter(element -> element.getName().equals("email")).count());
        assertEquals(1, personTypeSequenceElements.stream().filter(element -> element.getName().equals("phone")).count());
    }

    /**
     * A schema (C) that includes a redefining schema (B) must observe the redefined version
     * of the component, even though C itself declares no redefine. Per XSD 1.0 §4.2.2.
     */
    @Test
    public void testExternalImporterSeesRedefinition() {
        String filePath = getFilePath("redefine_external_c.xsd");
        XsdParser parser = new XsdParser(filePath);

        assertTrue("There should be no unsolved references",
                   parser.getUnsolvedReferences().isEmpty());

        XsdSchema schemaC = parser.getResultXsdSchemas()
                .filter(schema -> schema.getFilePath().endsWith("redefine_external_c.xsd"))
                .findFirst()
                .orElse(null);
        assertNotNull("Schema C should be found", schemaC);

        XsdElement p = schemaC.getChildrenElements()
                .filter(el -> "P".equals(el.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("P element should be found", p);

        XsdComplexType pType = p.getXsdComplexType();
        assertNotNull("P's type should resolve", pType);

        XsdExtension extension = pType.getComplexContent().getXsdExtension();
        assertNotNull("P's type should be the redefined version with an extension", extension);

        List<XsdElement> addedElements = extension.getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Redefined PersonType should add exactly 'email'", 1, addedElements.size());
        assertEquals("email", addedElements.get(0).getName());

        List<XsdElement> originalElements = extension.getBaseAsComplexType().getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Original PersonType should have exactly 'name'", 1, originalElements.size());
        assertEquals("name", originalElements.get(0).getName());
    }

    /**
     * A schema (D) that directly includes the redefined schema (A), bypassing the redefiner
     * (B), must see the ORIGINAL component — not the redefined version.
     */
    @Test
    public void testDirectBaseImporterUnaffected() {
        String filePath = getFilePath("redefine_external_d.xsd");
        XsdParser parser = new XsdParser(filePath);

        assertTrue("There should be no unsolved references",
                   parser.getUnsolvedReferences().isEmpty());

        XsdSchema schemaD = parser.getResultXsdSchemas()
                .filter(schema -> schema.getFilePath().endsWith("redefine_external_d.xsd"))
                .findFirst()
                .orElse(null);
        assertNotNull("Schema D should be found", schemaD);

        XsdElement q = schemaD.getChildrenElements()
                .filter(el -> "Q".equals(el.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Q element should be found", q);

        XsdComplexType qType = q.getXsdComplexType();
        assertNotNull("Q's type should resolve", qType);

        assertNull("Original PersonType has no extension",
                qType.getComplexContent());

        List<XsdElement> elements = qType.getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Original PersonType should have exactly 'name'", 1, elements.size());
        assertEquals("name", elements.get(0).getName());
    }

    /**
     * A redefine's {@code <xs:extension base="T"/>} must resolve to the ORIGINAL {@code T}
     * in the redefined schema — never to the redefining type itself (which would be
     * infinite recursion).
     */
    @Test
    public void testCircularBaseInRedefineResolvesToOriginal() {
        String filePath = getFilePath("redefine_test_main.xsd");
        XsdParser parser = new XsdParser(filePath);

        XsdSchema mainSchema = parser.getResultXsdSchemas()
                .filter(schema -> schema.getFilePath().endsWith("redefine_test_main.xsd"))
                .findFirst()
                .orElse(null);
        assertNotNull(mainSchema);

        XsdComplexType redefinedPersonType = mainSchema.getChildrenRedefines()
                .flatMap(XsdRedefine::getChildrenComplexTypes)
                .filter(ct -> "PersonType".equals(ct.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("Redefined PersonType should exist", redefinedPersonType);

        XsdComplexType baseResolved = redefinedPersonType.getComplexContent().getXsdExtension().getBaseAsComplexType();
        assertNotNull("Extension base must resolve", baseResolved);

        // The resolved base must be the ORIGINAL — it has a direct sequence, no extension.
        assertNull("Extension base must be the original, not the redefined self",
                baseResolved.getComplexContent());

        List<XsdElement> baseChildren = baseResolved.getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Original should have exactly {name, age}", 2, baseChildren.size());
        assertEquals(1, baseChildren.stream().filter(el -> "name".equals(el.getName())).count());
        assertEquals(1, baseChildren.stream().filter(el -> "age".equals(el.getName())).count());
    }

    /**
     * When the redefine chain is top → middle → base, a ref from the top schema must
     * resolve to the topmost (top's) redefinition, which in turn chains through middle
     * to base's original.
     */
    @Test
    public void testChainedRedefineResolvesToTopmost() {
        String filePath = getFilePath("redefine_test_chained_top.xsd");
        XsdParser parser = new XsdParser(filePath);

        assertTrue("There should be no unsolved references",
                   parser.getUnsolvedReferences().isEmpty());

        XsdSchema topSchema = parser.getResultXsdSchemas()
                .filter(schema -> schema.getFilePath().endsWith("redefine_test_chained_top.xsd"))
                .findFirst()
                .orElse(null);
        assertNotNull(topSchema);

        XsdElement extendedPerson = topSchema.getChildrenElements()
                .filter(el -> "ExtendedPerson".equals(el.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull("ExtendedPerson element should be found", extendedPerson);

        // ExtendedPerson's type = top's redefined PersonType (adds nickname).
        XsdExtension topExtension = extendedPerson.getXsdComplexType().getComplexContent().getXsdExtension();
        List<XsdElement> topAdded = topExtension.getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Top's redefine should add exactly 'nickname'", 1, topAdded.size());
        assertEquals("nickname", topAdded.get(0).getName());

        // Top's extension base = middle's redefined PersonType (adds middleName).
        XsdComplexType middleType = topExtension.getBaseAsComplexType();
        assertNotNull(middleType);
        XsdExtension middleExtension = middleType.getComplexContent().getXsdExtension();
        assertNotNull("Middle should also be a redefined (extension) type", middleExtension);
        List<XsdElement> middleAdded = middleExtension.getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Middle's redefine should add exactly 'middleName'", 1, middleAdded.size());
        assertEquals("middleName", middleAdded.get(0).getName());

        // Middle's extension base = base's original PersonType ({name, age}).
        XsdComplexType baseType = middleExtension.getBaseAsComplexType();
        assertNotNull(baseType);
        assertNull("Base must be the original, no further extension", baseType.getComplexContent());
        List<XsdElement> baseChildren = baseType.getChildAsSequence().getChildrenElements().collect(toList());
        assertEquals("Base should have {name, age}", 2, baseChildren.size());
    }

    /**
     * Resolution must be deterministic — parsing the same redefine schema repeatedly must
     * produce identical resolved type children, regardless of hash/iteration order.
     */
    @Test
    public void testDeterministicRedefineResolution() {
        String filePath = getFilePath("redefine_test_main.xsd");

        List<String> expectedChildren = null;
        for (int i = 0; i < 5; i++) {
            XsdParser parser = new XsdParser(filePath);
            XsdSchema mainSchema = parser.getResultXsdSchemas()
                    .filter(schema -> schema.getFilePath().endsWith("redefine_test_main.xsd"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(mainSchema);

            XsdElement person = mainSchema.getChildrenElements()
                    .filter(el -> "Person".equals(el.getName()))
                    .findFirst()
                    .orElse(null);
            assertNotNull(person);

            List<String> children = person.getXsdComplexType().getComplexContent().getXsdExtension()
                    .getChildAsSequence().getChildrenElements()
                    .map(XsdElement::getName)
                    .collect(toList());

            if (expectedChildren == null) {
                expectedChildren = children;
            } else {
                assertEquals("Redefine resolution must be deterministic across runs",
                        expectedChildren, children);
            }
        }
    }

    /**
     * @param fileName The name of the test file
     * @return Obtains the filePath of the file from the test resources folder
     */
    private static String getFilePath(String fileName) {
        URL resource = RedefineTest.class.getClassLoader().getResource(fileName);

        if (resource != null) {
            return resource.getPath();
        } else {
            throw new RuntimeException("The " + fileName + " file is missing from the XsdParser resource folder.");
        }
    }
}
