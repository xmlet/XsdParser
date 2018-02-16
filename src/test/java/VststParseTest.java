import XsdElements.*;
import XsdParser.*;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class VststParseTest {
    private static final String VSTST_FILE_NAME = HtmlParseTest.class.getClassLoader().getResource("vstst.xsd").getPath();
    private static final List<XsdElement> elements;
    private static final XsdParser parser;

    static{
        parser = new XsdParser();

        elements = parser.parse(VSTST_FILE_NAME)
                .filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());
    }

    @Test
    public void testElementCount(){

    }
}
