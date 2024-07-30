package org.crain.memory;

import org.crain.dolphin.DolphinFactory;

public class MemoryEngineBuilder {
    private EngineType engineType;
    private boolean connectOnBuild;
    private boolean withCache;

    private MemoryEngineBuilder(EngineType type) {
        this.engineType = type;
    }

    private enum EngineType {
        PLATFORM,
        CONSOLE
    }

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
        if (engineType == null) {
            throw new IllegalStateException("Engine Type cannot be null");
        }
        GamecubeMemoryEngine engine = switch (engineType) {
            case PLATFORM -> DolphinFactory.getDolphinEngine(withCache);
            case CONSOLE -> throw new IllegalArgumentException("CONSOLE IS NOT YET SUPPORTED");
        };
        if (connectOnBuild) engine.connect();
        return engine;
    }
}
