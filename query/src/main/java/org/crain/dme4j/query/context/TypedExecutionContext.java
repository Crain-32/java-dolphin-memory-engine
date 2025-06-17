package org.crain.dme4j.query.context;

import org.crain.dme4j.core.GamecubeMemoryEngine;

public record TypedExecutionContext<T>(
        long consoleAddress, T type, GamecubeMemoryEngine engine, MemoryStructureContext context
) {
}
