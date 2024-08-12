# Handling Structs

In the [basic example](./basic_example.md) we read from a simple pointer. However, this library's real power comes from
being able to define and provide [Structs](../design/structs.md) to read from.

## Quick note on Structs

There are two ways to define a struct, with one being highly recommended, and the other being for fast prototyping or 
interactive shell environments. For this section we'll use the prototyping method for ease of use.

```java
var myStruct = Struct.createNamelessStruct(
    Map.of(
        "intValue", new IntValue(), 
        "shortValue", new ShortValue()
    )
);
```

## Provide Struct to Engine

There are three ways to have the engine read this value.
```java
engine.readIntoMemoryType(0x8000_1234L, myStruct);
engine.readIntoMemoryType(0x8000_1234L, Pointer.atNull(myStruct)); // Note, the following two are subject to change.
engine.readIntoPointerType(new Pointer<>(0x8000_1234L, myStruct));
```
After which we can read from it.
```java
IntValue value = myStruct.getUncheckedValue("intValue");
System.out.println(value.getValue());
```
### Pointers
For handling Pointers, review their design [overview](../design/pointers.md).