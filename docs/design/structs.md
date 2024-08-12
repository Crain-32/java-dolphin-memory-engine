# Structs
`Structs` are this Library's method of mapping between Java and C Types. There exists several methods of doing this, 
and I'll likely adjust this as time goes on. Do note the rest of this assumes you are familiar with the idea of structs.

## Understanding Structs from the Engine
If you dig into the implementation, you'll see the Engine has an understanding of the provided primitives of a Struct.
However, it is better to understand a Struct as a tagged binary blob. All we're doing when we get a Struct is getting
some data from the target, and checking if we need to follow any Pointers.

## Understanding Structs from the Consumer
In any application that crosses Language borders, type safety is only a facade. Structs defined how the engine should
interpret a blob, and provide a thin layer of type safety on top of the that. Several helper functions exist to help
you define your struct in a composite manner, which should be favoured over inherited structures. 

## Locking Structs
It is recommended to define your struct in the constructor of the extension class, and call `.lockStruct()` after that.
Locked Structs cannot have their structure redefined, however their values can still be mutated. Since all Structs are 
packed by nature locking the Struct will also allow the final size to be cached.

## Struct Helper Functions
Several functions exist on the base Struct class to help you define your Struct in an easy manner.

- `registerValue(String, Value<?>)`
  - Appends the value with the provided key to the Struct definition.
- `registerStruct(String, Struct)`
  - Inlines the Struct into the Definition. Note that the key is still nested, so access is `$.struct.innerVal`
- `registerPadding(int)`
  - Appends padding of length `length` to the definition.
- `registerPointer(String, PointerType, boolean)`
  - Registers a Pointer with the provided key. `loadEager` can be used to override default fetching behavior.
- `reigsterArray(String, Array<?>, boolean)`
  - Registers an array with the provided key, if `Array::isForPointers`, then `loadEager` is applied to all elements
- Static `createNamelessStruct(Map<String, MemoryType)`
  - Returns a struct based on the provided map, note that this is not recommended outside of prototyping, as you lose type
safety and clarity.
