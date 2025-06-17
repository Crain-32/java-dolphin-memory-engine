package org.crain.dme4j.query.syntax.segment;

import org.crain.dme4j.core.GamecubeMemoryEngine;
import org.crain.dme4j.core.exception.DmeCoreModuleException;
import org.crain.dme4j.core.types.MemoryType;
import org.crain.dme4j.core.types.ClassRefPointer;
import org.crain.dme4j.core.types.PointerType;
import org.crain.dme4j.core.types.Struct;
import org.crain.dme4j.query.syntax.SyntaxNode;
import org.crain.dme4j.query.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class PathSegmentNode extends AbstractSegmentNode {
    private final List<String> pathSegments;

    protected PathSegmentNode(SyntaxNode child, List<Token> pathTokens) {
        super(child);
        this.pathSegments = pathTokens.stream().map(Token::literal).toList();
    }

    static final List<SegmentType> SUB_SEGMENTS = List.of(
            SegmentType.FILTER, SegmentType.CAST,
            SegmentType.SUB_SELECT, SegmentType.EXPRESSION
    );

    @Override
    List<SegmentType> validSegments() {
        return SUB_SEGMENTS;
    }

    @Override
    SegmentType getSegmentType() {
        return SegmentType.PATH;
    }

    @Override
    public String getRepresentation() {
        return "Path (" + String.join(", ", pathSegments) + ")";
    }

    //
    public Function<ClassRefPointer<MemoryType>, PointerType> calculatePath() {
        return (pointer) -> {
            PointerType result;
            var memory = pointer.getValue();
            var consoleAddress = pointer.getPointerAddress();
            if (consoleAddress < 0) throw new IllegalStateException("Provided Pointer is null!");
            int offset = 0;
            for (var pathSegment : pathSegments) {
                switch (memory) {
                    case ClassRefPointer<?> pointy -> {
                        throw new IllegalStateException("I just want to ");
                    }
                    default -> throw new IllegalStateException("Unknown memory type: " + memory);
                }
            }
            return null;
        };
    }

    private static MemoryType handleFromStruct(Struct struct, PathSegment segment) {
        MemoryType type = struct.checkYourselfValue(segment.key());
        return switch (segment.nullable()) {
            case STRICT_NON_NULL:
                if (type == null) throw new IllegalStateException("Path failed the null check");
            default:
                yield type;
        };
    }

    private static MemoryType handleFromPointer(GamecubeMemoryEngine engine, PointerType pointerType,
                                                String segment, long referenceAddress
    ) {
        pointerType.shouldFollowPointer(true);
        try {
            engine.readIntoMemoryType(referenceAddress, pointerType);
        } catch (DmeCoreModuleException _) {
            // TODO - add logging
        }
        return null;
    }

    private static List<PathSegment> parseSubToken(List<Token> tokens) {
        List<PathSegment> pathSegments = new ArrayList<>();
        var builder = PathSegment.builder();
        for (var token : tokens) {
            switch (token.type()) {
                case DOT -> {
                    builder.build();
                    pathSegments.add(builder.build());
                    builder = PathSegment.builder();
                }
                case WORD -> builder.key(token.literal());
            }
        }
        builder.build();
        pathSegments.add(builder.build());
        return pathSegments;
    }

    /**
     * We should <emp>assume</emp> we only have valid path tokens.
     * Probably?
     */
    private record PathSegment(Nullability nullable, boolean followPointer, String key) {
        private static PathSegmentBuilder builder() {
            return new PathSegmentBuilder();
        }
    }

    private static class PathSegmentBuilder {
        private Nullability nullability = Nullability.NULL_CHECK;
        private boolean followPointer = false;
        private String key = null;

        private PathSegmentBuilder() {

        }

        private void nullability(Nullability nullability) {
            if (nullability != Nullability.NULL_CHECK)
                throw new IllegalArgumentException("Nullability cannot be set more than once.");
            this.nullability = nullability;
        }

        private void followPointer(boolean followPointer) {
            this.followPointer = followPointer;
        }

        private void key(String key) {
            this.key = key;
        }

        private PathSegment build() {
            if (nullability == Nullability.STRICT_NON_NULL && followPointer)
                throw new IllegalArgumentException("Nullability is strict");
            return new PathSegment(nullability, followPointer, key);
        }
    }

    private enum Nullability {
        STRICT_NON_NULL,
        NULL_CHECK,
        ASSUME
    }
}
