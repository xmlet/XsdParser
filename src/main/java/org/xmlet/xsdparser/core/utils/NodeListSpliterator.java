package org.xmlet.xsdparser.core.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * This class serves as an utility class to be able to manipulate a {@link NodeList} object as a {@link java.util.stream.Stream} object.
 */
public class NodeListSpliterator extends Spliterators.AbstractSpliterator<Node> {

    private final NodeList nodes;
    private int index;

    public NodeListSpliterator(NodeList nodeList){
        super(nodeList.getLength(), SIZED);
        this.nodes = nodeList;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Node> consumer) {
        if(index >= nodes.getLength()) return false;
        Node node = nodes.item(index);
        ++index;
        consumer.accept(node);
        return true;
    }
}

