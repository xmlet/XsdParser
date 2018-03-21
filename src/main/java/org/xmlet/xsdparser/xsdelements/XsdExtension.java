package org.xmlet.xsdparser.xsdelements;

import org.w3c.dom.Node;
import org.xmlet.xsdparser.core.XsdParser;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.NamedConcreteElement;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.elementswrapper.UnsolvedReference;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XsdExtension extends XsdAnnotatedElements {

    public static final String XSD_TAG = "xsd:extension";
    public static final String XS_TAG = "xs:extension";

    private ExtensionXsdElementVisitor visitor = new ExtensionXsdElementVisitor();

    private ReferenceBase childElement;
    private ReferenceBase base;

    private XsdExtension(@NotNull Map<String, String> elementFieldsMapParam) {
        super(elementFieldsMapParam);
    }

    @Override
    public void setFields(@NotNull Map<String, String> elementFieldsMapParam) {
        super.setFields(elementFieldsMapParam);

        String baseValue = elementFieldsMap.getOrDefault(BASE_TAG, null);

        if (baseValue != null){
            XsdElement placeHolder = new XsdElement(new HashMap<>());
            placeHolder.setParent(this);
            this.base = new UnsolvedReference(baseValue, placeHolder);
            XsdParser.getInstance().addUnsolvedReference((UnsolvedReference) this.base);
        }
    }

    @Override
    public void replaceUnsolvedElements(NamedConcreteElement element) {
        super.replaceUnsolvedElements(element);

        if (this.base != null && this.base instanceof UnsolvedReference && element.getElement() instanceof XsdElement && ((UnsolvedReference) this.base).getRef().equals(element.getName())){
            this.base = element;
        }

        visitor.replaceUnsolvedAttributes(element);
    }

    @Override
    public XsdElementVisitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(XsdElementVisitor xsdElementVisitor) {
        super.accept(xsdElementVisitor);
        xsdElementVisitor.visit(this);
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return childElement == null ? Collections.emptyList() : childElement.getElement().getElements();
    }

    public XsdElement getBase() {
        return base instanceof ConcreteElement ? (XsdElement) base.getElement() : null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdExtension(convertNodeMap(node.getAttributes())));
    }

    public Stream<XsdAttribute> getXsdAttributes() {
        return visitor.getXsdAttributes();
    }

    public Stream<XsdAttributeGroup> getXsdAttributeGroup() {
        return visitor.getXsdAttributeGroup();
    }

    class ExtensionXsdElementVisitor extends AttributesVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdExtension.this;
        }

        @Override
        public void visit(XsdMultipleElements element) {
            super.visit(element);
            XsdExtension.this.childElement = ReferenceBase.createFromXsd(element);
        }

        @Override
        public void visit(XsdGroup element) {
            super.visit(element);
            XsdExtension.this.childElement = ReferenceBase.createFromXsd(element);
        }
    }
}
