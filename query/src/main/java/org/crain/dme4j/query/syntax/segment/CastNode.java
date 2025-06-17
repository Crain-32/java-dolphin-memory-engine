package org.crain.dme4j.query.syntax.segment;

import org.crain.dme4j.query.context.TypedExecutionContext;
import org.crain.dme4j.query.syntax.SyntaxException;
import org.crain.dme4j.query.syntax.SyntaxNode;
import org.crain.dme4j.core.types.MemoryType;

import java.util.List;
import java.util.Objects;

class CastNode<T extends MemoryType> extends AbstractSegmentNode {
    private Class<T> clazz;

    protected CastNode(SyntaxNode child, Class<T> clazz) {
        super(child);
        this.clazz = clazz;
    }


    public TypedExecutionContext<T> cast(TypedExecutionContext<?> context) throws SyntaxException {
        return null;
    }


    @Override
    public String getRepresentation() {
        return "Cast (" + clazz.getSimpleName() + ")";
    }

    private static final List<SegmentType> VALID_SEGMENTS =
            List.of(SegmentType.SELECT, SegmentType.SUB_SELECT, SegmentType.EXPRESSION);
    @Override
    List<SegmentType> validSegments() {
        return VALID_SEGMENTS;
    }

    @Override
    SegmentType getSegmentType() {
        return SegmentType.CAST;
    }

    @Override
    public void canBeChild(SyntaxNode child) throws SyntaxException {
        if (Objects.requireNonNull(child) instanceof AbstractSegmentNode) {
            throw new SyntaxException("Segment nodes cannot come after a Cast Node");
        }
    }
}
