package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NamespaceTest {

    private static final List<XsdElement> elements;
    private static final List<XsdSchema> schemas;

    static {
        XsdParser parser = new XsdParser(getFilePath());
        elements = parser.getResultXsdElements().collect(Collectors.toList());
        schemas = parser.getResultXsdSchemas().collect(Collectors.toList());
    }

    @Test
    public void testHierarchy() {
        Optional<XsdElement> motherOfBtnsOpt = elements.stream().filter(element -> element.getName().equals("MotherOfBtns")).findFirst();

        Assert.assertTrue(motherOfBtnsOpt.isPresent());

        XsdElement motherOfBtns = motherOfBtnsOpt.get();

        XsdComplexType complexType = motherOfBtns.getXsdComplexType();

        Assert.assertNotNull(complexType);

        XsdSequence sequence = complexType.getChildAsSequence();

        Assert.assertNotNull(sequence);

        List<XsdElement> sequenceMembers = sequence.getChildrenElements().collect(Collectors.toList());

        XsdElement member1 = sequenceMembers.get(0);
        XsdElement member2 = sequenceMembers.get(1);
        XsdElement member3 = sequenceMembers.get(2);

        Assert.assertNotNull(member1);
        Assert.assertNotNull(member2);
        Assert.assertNotNull(member3);

        XsdComplexType complexType1 = member1.getXsdComplexType();
        XsdComplexType complexType2 = member2.getXsdComplexType();
        XsdComplexType complexType3 = member3.getXsdComplexType();

        Assert.assertNotNull(complexType1);
        Assert.assertNotNull(complexType2);
        Assert.assertNotNull(complexType3);

        List<XsdAttribute> attributes1 = complexType1.getXsdAttributes().collect(Collectors.toList());
        List<XsdAttribute> attributes2 = complexType2.getXsdAttributes().collect(Collectors.toList());
        List<XsdAttribute> attributes3 = complexType3.getXsdAttributes().collect(Collectors.toList());

        Assert.assertEquals(1, attributes1.size());
        Assert.assertEquals(1, attributes2.size());
        Assert.assertEquals(1, attributes3.size());

        Assert.assertEquals("attr1", attributes1.get(0).getName());
        Assert.assertEquals("attr2", attributes2.get(0).getName());
        Assert.assertEquals("attr3", attributes3.get(0).getName());
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = AndroidParseTest.class.getClassLoader().getResource("ns1.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The comments.xsd file is missing from the XsdParser resource folder.");
        }
    }

}
