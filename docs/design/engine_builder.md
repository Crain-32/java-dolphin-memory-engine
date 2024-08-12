# Understanding the Memory Engine Builder

The `MemoryEngineBuilder` is the easiest way as a consumer to access this Library. Although the `MemoryEngineFactory`
is exposed, it is subject to breaking changes as it is not the primary access point. (Author's note, might be worth
adjusting the package structure for factory visibility).

## Options
### Target
Where traditionally builders are accessed by calling `.builder()` on a class, we break from tradition and require a 
target to be selected. The functions for this are the following.
- `forPlatform()`
  - The Engine will attempt to connect to a running instance of Dolphin when `GamecubeMemoryEngine::connect` is called.
- `forConsole()`
  - The Engine will attempt to connect to a console using the provided connection details.
  - Author's note, currently this is unsupported, and there exists no way to specify connection details.
- `forFile(Path path)`|`forFile(String absolutePath)`|`forFile(File file)`
  - Creates a read only engine for a memory dump at the provided file location.
  - Currently in development, as this will be the backbone of the tests, as we can handcraft testing files.

There is no default for this option, as the builder requires it be selected in order to exist.

### Connect on Build
By nature of the Engine, what we want to read from is outside the JVM. A consumer may wish to connect on creation of the
Engine, due to their usecase. If connecting fails, the Consumer can still check using `GamecubeMemoryEngine::getStatus`
- `connectOnBuild()`
  - Engine wil attempt to connect when `.build()` is called.

Note, if the Builder was created with `forFile`, the engine will attempt to connect by default.

### Engine Cache
As creating an Engine is an expensive operation, a cache is provided in the factory to reduce that overhead.
- `withCache(booean withCache)`
  - If a cache should be used when finding the Engine. Default is `false`