package org.crain.dme4j.core;

import org.crain.dme4j.core.types.IntValue;

import java.lang.reflect.ParameterizedType;

public class RandomTest {

    public static void main(String[] args) {
        var what = IntValue.class.getGenericSuperclass();
        System.out.println(((ParameterizedType) what).getActualTypeArguments()[0]);
    }
}
