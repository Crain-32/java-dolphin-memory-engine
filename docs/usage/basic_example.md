# Basic Usage Guide

At the beginning Address of every game, there exist the Game ID. This 6-8 byte long string can be read using the 
example.
```java
void example() {
    var engine = (AbstractReadingMemoryEngine) MemoryEngineBuilder.forPlatform().connectOnBuild().build();
    var stringPointer = new StringPointer(0x8000_0000L, 8);
    engine.readIntoPointerType(stringPointer);
    System.out.println(stringPointer.getStringValue());
}
```
We'll now go over each line to showcase various parts of the Library.

## Get Engine from Builder
```java
var engine = (AbstractReadingMemoryEngine) MemoryEngineBuilder.forPlatform().connectOnBuild().build();
```
As a consumer you'll always get an instance of the Engine from the `MemoryEngineBuilder`. This Builder currently exposes
the following options,
- Memory Location (Currently only an instance running on Windows)
- If the Engine provided should attempt to connect.
- If a Cached Instance should be found.

More information on the Builder and the defaults can be found [here](../design/engine_builder.md)

## Create a MemoryType
```java
var stringPointer = new StringPointer(0x8000_0000L, 8);
```
We create a new [MemoryType](../design/memory_type.md) of [StringPointer](../design/pointers.md). We specifically want
to read from the memory address at `0x8000_0000`, and as a precaution we set the max length it will read for this String
at 8 bytes.

## Provide the MemoryType
```java
engine.readIntoPointerType(stringPointer);
```
When we provide our `StringPointer` to the engine, it will read the 8 bytes at the provided address and convert them into
a String. Do note that no null termination byte is checked for in this process. This is a planned feature.

## Read the Value
After being passed through the Engine, the value has been extracted from the Game and can be read.