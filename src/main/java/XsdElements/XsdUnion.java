package XsdElements;

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class XsdUnion extends XsdAbstractElement {

    public static final String XSD_TAG = "xsd:union";
    public static final String XS_TAG = "xs:union";

    private UnionVisitor visitor = new UnionVisitor();

    private List<XsdSimpleType> simpleTypeList = new ArrayList<>();
    private String memberTypes;

    private XsdUnion(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    private XsdUnion(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.memberTypes = elementFieldsMap.getOrDefault(MEMBER_TYPES, memberTypes);
        }
    }

    public List<String> getMemberTypesList() {
        return Arrays.asList(memberTypes.split(" "));
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        this.setParent(visitor.getOwner());
    }

    @Override
    public XsdUnion clone(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdUnion elementCopy = new XsdUnion(this.getParent(), placeHolderAttributes);

        elementCopy.simpleTypeList = this.simpleTypeList;

        return elementCopy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    public List<XsdSimpleType> getUnionElements(){
        return simpleTypeList;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdUnion(convertNodeMap(node.getAttributes())));
    }

    class UnionVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdUnion.this;
        }

        @Override
        public void visit(XsdSimpleType element) {
            super.visit(element);

            XsdUnion.this.simpleTypeList.add(element);
        }
    }
}
