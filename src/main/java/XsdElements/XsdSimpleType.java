package XsdElements;

//TODO XsdRestrictions uma classe estática com métodos para cada uma das restrições, se o parametro passar na restrição tudo bem, senão excepção.
//TODO Cada classe que tenha uma restrição tem de invocar o respectivo método com a restrição presente em XsdAttribute.

//TODO Unions basicamente são List<XsdSimpleType>

//TODO XsdLists tem um built in type ou outro ?simple type?, que já está resolvido e devem poder ter XsdRestriction lá dentro.

import XsdElements.ElementsWrapper.ReferenceBase;
import XsdElements.Visitors.Visitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XsdSimpleType extends XsdAbstractElement {

    public static final String TAG = "xsd:simpleType";

    private SimpleTypeVisitor visitor = new SimpleTypeVisitor();

    private XsdRestriction restriction;
    private XsdUnion union;
    private XsdList list;

    private String name;
    private String finalObj;

    private XsdSimpleType(XsdAbstractElement parent, HashMap<String, String> elementFieldsMap) {
        super(parent, elementFieldsMap);
    }

    public XsdSimpleType(HashMap<String, String> elementFieldsMap) {
        super(elementFieldsMap);
    }

    private XsdSimpleType(XsdAbstractElement parent) {
        super(parent);
    }

    public void setFields(HashMap<String, String> elementFieldsMap){
        super.setFields(elementFieldsMap);

        if (elementFieldsMap != null){
            this.name = elementFieldsMap.getOrDefault(NAME, name);
            this.finalObj = elementFieldsMap.getOrDefault(FINAL, finalObj);
        }
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
    public XsdAbstractElement createCopyWithAttributes(HashMap<String, String> placeHolderAttributes) {
        placeHolderAttributes.putAll(this.getElementFieldsMap());
        XsdSimpleType copy = new XsdSimpleType(this.getParent(), placeHolderAttributes);

        copy.union = this.union;
        copy.list = this.list;
        copy.restriction = this.restriction;

        return copy;
    }

    @Override
    protected List<ReferenceBase> getElements() {
        return null;
    }

    public static ReferenceBase parse(Node node){
        return xsdParseSkeleton(node, new XsdSimpleType(convertNodeMap(node.getAttributes())));
    }

    public XsdRestriction getRestriction() {
        return restriction;
    }

    public XsdUnion getUnion() {
        return union;
    }

    public XsdList getList() {
        return list;
    }

    List<XsdRestriction> getAllRestrictions() {
        List<XsdRestriction> restrictions = new ArrayList<>();

        if (restriction != null){
            restrictions.add(restriction);
        }

        if (union != null){
            union.getUnionElements().forEach(unionMember -> restrictions.addAll(unionMember.getAllRestrictions()));
        }

        return restrictions;
    }

    class SimpleTypeVisitor extends Visitor{

        @Override
        public XsdAbstractElement getOwner() {
            return XsdSimpleType.this;
        }

        @Override
        public void visit(XsdList element) {
            super.visit(element);

            XsdSimpleType.this.list = element;
        }

        @Override
        public void visit(XsdUnion element) {
            super.visit(element);

            XsdSimpleType.this.union = element;
        }

        @Override
        public void visit(XsdRestriction element) {
            super.visit(element);

            XsdSimpleType.this.restriction = element;
        }
    }
}
