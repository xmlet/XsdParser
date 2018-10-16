package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AndroidParseTest {

    private static final List<XsdElement> elements;

    static {
        elements = new XsdParser(getFilePath()).getResultXsdElements().collect(Collectors.toList());
    }

    /**
     * Asserts if the hierarchy is being parsed correctly. The hierarchy is implemented with the {@link XsdExtension#base}
     * field.
     * Example: If element A has a {@link XsdExtension} with his {@link XsdExtension#base} with element B that means that
     * element A extends element B.
     */
    @Test
    public void testHierarchy() {
        Optional<XsdElement> relativeLayoutOptional = elements.stream().filter(element -> element.getName().equals("RelativeLayout")).findFirst();

        Assert.assertTrue(relativeLayoutOptional.isPresent());

        XsdElement relativeLayout = relativeLayoutOptional.get();

        XsdComplexType relativeLayoutComplexType = relativeLayout.getXsdComplexType();

        Assert.assertNotNull(relativeLayoutComplexType);

        XsdComplexContent relativeLayoutComplexContent = relativeLayoutComplexType.getComplexContent();

        Assert.assertNotNull(relativeLayoutComplexContent);

        XsdExtension relativeLayoutExtension = relativeLayoutComplexContent.getXsdExtension();

        XsdElement viewGroup = relativeLayoutExtension.getBase();

        Assert.assertNotNull(viewGroup);
        Assert.assertEquals("ViewGroup", viewGroup.getName());

        XsdComplexType viewGroupComplexType = viewGroup.getXsdComplexType();

        Assert.assertNotNull(viewGroupComplexType);

        XsdComplexContent viewGroupComplexContent = viewGroupComplexType.getComplexContent();

        Assert.assertNotNull(viewGroupComplexContent);

        XsdExtension viewGroupExtension = viewGroupComplexContent.getXsdExtension();

        XsdElement view = viewGroupExtension.getBase();

        Assert.assertNotNull(view);
        Assert.assertEquals("View", view.getName());
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = AndroidParseTest.class.getClassLoader().getResource("android.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The android.xsd file is missing from the XsdParser resource folder.");
        }
    }
}