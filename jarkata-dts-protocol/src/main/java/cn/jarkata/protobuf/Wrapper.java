package cn.jarkata.protobuf;

public class Wrapper {

    private final Object message;

    public Wrapper(Object message) {
        this.message = message;
    }

    public Object getData() {
        return message;
    }
}
