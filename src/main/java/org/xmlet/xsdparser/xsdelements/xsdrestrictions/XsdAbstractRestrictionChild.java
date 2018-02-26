package org.xmlet.xsdparser.xsdelements.xsdrestrictions;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAnnotatedElements;
import org.xmlet.xsdparser.xsdelements.XsdAnnotation;
import org.xmlet.xsdparser.xsdelements.elementswrapper.ReferenceBase;
import org.xmlet.xsdparser.xsdelements.visitors.XsdElementVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class XsdAbstractRestrictionChild extends XsdAnnotatedElements {

    private XsdElementVisitor xsdElementVisitor = new AbstractRestrictionChildXsdElementVisitor();

    public XsdAbstractRestrictionChild(){
        super(Collections.emptyMap());
    }

    public XsdAbstractRestrictionChild(Map<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    @Override
    public XsdElementVisitor getXsdElementVisitor() {
        return xsdElementVisitor;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return Collections.emptyList();
    }

    class AbstractRestrictionChildXsdElementVisitor extends XsdElementVisitor {

        @Override
        public XsdAbstractElement getOwner() {
            return XsdAbstractRestrictionChild.this;
        }

        @Override
        public void visit(XsdAnnotation element) {
            super.visit(element);

            setAnnotation(element);
        }
    }
}
