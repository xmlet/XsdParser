package org.xmlet.xsdparser;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.XsdElement;
import org.xmlet.xsdparser.xsdelements.XsdExtension;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class CommentsTest {

    private static final List<XsdElement> elements;

    static {
        elements = new XsdParser(getFilePath()).getResultXsdElements().collect(Collectors.toList());
    }

    @Test
    public void testHierarchy() {
        Assert.assertEquals(1, elements.size());
    }

    /**
     * @return Obtains the filePath of the file associated with this test class.
     */
    private static String getFilePath(){
        URL resource = AndroidParseTest.class.getClassLoader().getResource("comments.xsd");

        if (resource != null){
            return resource.getPath();
        } else {
            throw new RuntimeException("The comments.xsd file is missing from the XsdParser resource folder.");
        }
    }
}
