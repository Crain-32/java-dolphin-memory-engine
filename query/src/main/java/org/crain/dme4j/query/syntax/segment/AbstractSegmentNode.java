package org.crain.dme4j.query.syntax.segment;

import org.crain.dme4j.query.syntax.AbstractSyntaxNode;
import org.crain.dme4j.query.syntax.SyntaxException;
import org.crain.dme4j.query.syntax.SyntaxNode;

import java.util.List;

abstract class AbstractSegmentNode extends AbstractSyntaxNode {

    protected AbstractSegmentNode(SyntaxNode child) {
        super(child);
    }

    abstract List<SegmentType> validSegments();

    abstract SegmentType getSegmentType();

    @Override
    public void canBeChild(SyntaxNode node) throws SyntaxException {
        switch (node) {
            case AbstractSegmentNode segmentNode when !validSegments().contains(getSegmentType()) ->
                    throw SegmentException.invalidChain(this.getSegmentType(), segmentNode.getSegmentType());
            case AbstractSegmentNode _ -> {}
            default -> throw new SyntaxException("Invalid Node Type passed to the SegmentNode");
        }
    }
}
