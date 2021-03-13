package cn.jarkata.dts.common.stream;

@FunctionalInterface
public interface PreFunction<T> {

    void apply(T t);
}
