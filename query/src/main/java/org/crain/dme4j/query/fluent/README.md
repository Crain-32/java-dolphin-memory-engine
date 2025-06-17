# Query

A query should represent a series of operations from Memory Address A to Result B.

For example

FROM ROOT (0x8000_0000)
WITH OFFSET 0X1234
SELECT 56 BYTES AS MY_OBJECT

Fluent API wise that would be something like

```java
rootQuery().offset(0x1234).into(MyObject.class).read();
```

Finally you need a way to execute the query against some sort of engine and context

```java
import org.crain.memory.query.context.MemoryStructureContext;

MemoryStructureContext.executeReadQuery(myQuery, engine);
MemoryStructureContext.executeSetQuery(myQuery, engine, value);
```

Created by referencing the Criteria/Abstract Query from Jakarta, and the Example Matcher from Spring Data Commons

