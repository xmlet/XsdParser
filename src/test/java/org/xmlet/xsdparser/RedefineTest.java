package org.xmlet.xsdparser;

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

        XsdSequence personTypeSequence = personTypeExtension.getChildAsSequence();
        assertNotNull(personTypeSequence);

        List<XsdElement> personTypeSequenceElements = personTypeSequence.getChildrenElements().collect(Collectors.toList());
        assertFalse(personTypeSequenceElements.isEmpty());
        assertEquals(2, personTypeSequenceElements.size());
        assertEquals(1, personTypeSequenceElements.stream().filter(element -> element.getName().equals("email")).count());
        assertEquals(1, personTypeSequenceElements.stream().filter(element -> element.getName().equals("phone")).count());
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
