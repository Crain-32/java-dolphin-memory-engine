# Why Annotations?


When dealing with any sort of Object Mapping, the mapper must have a way to handle the following.
- How does the data from A map to B?
- Are there differences in Memory Representation to account for?
- How can that be exposed to the user?

## How does the data map?
If we take JSON, we typically approach field key == object field. For example,
```json
{
  "foo": "bar",
  "fizz": 4
}
```
```java
record MyObj(String foo, Integer fizz) {}
```
This allows us to easily map source -> sink, even though it's implicit in how that works.
