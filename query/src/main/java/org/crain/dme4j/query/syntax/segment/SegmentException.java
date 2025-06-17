package org.crain.dme4j.query.syntax.segment;

import org.crain.dme4j.query.syntax.SyntaxException;

public class SegmentException extends SyntaxException {
    public SegmentException(String message) {
        super(message);
    }

    public static SegmentException invalidChain(SegmentType parent, SegmentType child) {
        return new SegmentException("Invalid chain of segments from " + parent + " to " + child);
    }
}
