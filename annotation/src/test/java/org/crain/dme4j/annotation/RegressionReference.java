package org.crain.dme4j.annotation;

/**
 * Just Compiler/Default level test to ensure regressions are covered.
 */
@SuppressWarnings("unused")
@Static(location = 0x8000_0000L, alias = "regressionTest")
public class RegressionReference {
    @Statics({
            @Static(location = 0x0101L, alias = "testClass2"),
            @Static(location = 0x1010L, alias = "testClass3")
    })
    public record TestRecord(@Order @Pointer @Field Integer a) {}

    public static class TestClass {

        @Field
        @Order
        @Pointer
        private final String fieldA;

        public TestClass(String fieldA) {
            this.fieldA = fieldA;
        }

        @Field(
                padding = @Padding(4)
        )
        @Order
        @Pointer
        public void pointerCheck() {

        }
    }
}
