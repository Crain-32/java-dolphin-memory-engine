package org.crain.dme4j.query.syntax.segment;

import org.crain.dme4j.query.syntax.SyntaxNode;

import java.util.List;

import static org.crain.dme4j.query.syntax.segment.SegmentType.*;

class SelectSegmentNode extends AbstractSegmentNode {
    enum SelectNodeType {
        DIRECT(CAST, EXPRESSION),
        ROOT(CAST, PATH);
        final List<SegmentType> selectNodeSubTypes;
        SelectNodeType(SegmentType... selectNodeSubTypes) {
            this.selectNodeSubTypes = List.of(selectNodeSubTypes);
        }
    }
    private final SelectNodeType type;

    SelectSegmentNode(SyntaxNode child, SelectNodeType type) {
        super(child);
        this.type = type;
    }

    @Override
    public String getRepresentation() {
        return "select (" + type.name() + ")";
    }


    @Override
    List<SegmentType> validSegments() {
        return this.type.selectNodeSubTypes;
    }

    @Override
    SegmentType getSegmentType() {
        return SegmentType.SELECT;
    }
}

