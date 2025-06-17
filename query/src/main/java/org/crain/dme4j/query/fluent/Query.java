package org.crain.dme4j.query.fluent;


import java.util.function.Function;
import java.util.function.Predicate;

public interface Query<T> {

    <U> Query<U> as(Class<U> reference);

    <U> Query<U> select(Function<T, U> mappingFunction);

    <U> Query<U> contextMap(Function<T, U> executionContext);

    Query<T> filter(Predicate<T> predicate);

    <S extends T> Query<S> discriminator(Function<T, S> discriminatorFunction);

    Query<T> tap(T value);

    void set(T value);
    void setByte(byte value);
    void setShort(short value);
    void setInt(int value);
    void setLong(long value);
    void setDouble(double value);
    void setFloat(float value);
}
