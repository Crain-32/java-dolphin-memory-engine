package org.crain.memory.engine;

import org.crain.memory.engine.dolphin.MemoryEngineFactory;

public class MemoryEngineBuilder {
    private final EngineType target;
    private boolean connectOnBuild;
    private boolean withCache;

    private MemoryEngineBuilder(EngineType type) {
        this.target = type;
    }

    private enum EngineType {
        PLATFORM,
        CONSOLE,
        FILE
    }
    // Meh function name
    public static MemoryEngineBuilder forPlatform() {
        return new MemoryEngineBuilder(EngineType.PLATFORM);
    }

    public static MemoryEngineBuilder forConsole() {
        return new MemoryEngineBuilder(EngineType.CONSOLE);
    }

    public MemoryEngineBuilder connectOnBuild() {
        this.connectOnBuild = true;
        return this;
    }

    public MemoryEngineBuilder withCache(boolean withCache) {
        this.withCache = withCache;
        return this;
    }

    public GamecubeMemoryEngine build() throws IllegalStateException {
        if (target == null) {
            throw new IllegalStateException("Engine Type cannot be null");
        }
        GamecubeMemoryEngine engine = switch (target) {
            case PLATFORM -> MemoryEngineFactory.getDolphinEngine(withCache);
            case CONSOLE, FILE -> throw new IllegalArgumentException("CONSOLE IS NOT YET SUPPORTED");
        };
        if (connectOnBuild) engine.connect();
        return engine;
    }
}
