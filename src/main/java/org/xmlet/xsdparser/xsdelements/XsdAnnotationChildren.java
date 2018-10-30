package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParserCore;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.exceptions.VisitorNotFoundException;
import org.xmlet.xsdparser.xsdelements.visitors.XsdAbstractElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class serves as a base to both {@link XsdAppInfo} and {@link XsdDocumentation} since they share similarities.
 */
public abstract class XsdAnnotationChildren extends XsdAbstractElement {

    /**
     * An URI that specifies a source for the application information.
     */
    private String source;

    /**
     * The textual content of the current element, either {@link XsdAppInfo} or {@link XsdDocumentation}.
     */
    private String content;

    XsdAnnotationChildren(@NotNull XsdParserCore parser, @NotNull Map<String, String> attributesMap) {
        super(parser, attributesMap);
        this.source = attributesMap.getOrDefault(SOURCE_TAG, source);
    }

    /**
     * @return Always returns a {@link VisitorNotFoundException} since the descendants of this class shouldn't be
     * visited since they aren't allowed to have children.
     */
    @Override
    public XsdAbstractElementVisitor getVisitor() {
        throw new VisitorNotFoundException("AppInfo/Documentation can't have children.");
    }

    public String getSource() {
        return source;
    }

    public String getContent() {
        return content;
    }

    /**
     * This method is used to parse {@link XsdAnnotationChildren} instances.
     * @param node The node containing the information to parse.
     * @param annotationChildren An instance of {@link XsdAnnotationChildren} (either {@link XsdAppInfo} or
     *                           {@link XsdDocumentation}).
     * @return The annotationChildren wrapped in the correct {@link ReferenceBase} wrapper.
     */
    static ReferenceBase xsdAnnotationChildrenParse(Node node, XsdAnnotationChildren annotationChildren){
        annotationChildren.content = xsdRawContentParse(node);

        return ReferenceBase.createFromXsd(annotationChildren);
    }

}
