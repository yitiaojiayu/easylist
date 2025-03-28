package com.yitiaojiayu.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
@SuppressWarnings({"unchecked", "AlibabaConstantFieldShouldBeUpperCase"})
public class KryoSimple {

    private KryoSimple(){
    }

    private static final Kryo kryo = new Kryo();
    private static final Output output = new Output(4096, -1);
    private static final Input input = new Input();
    private static final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    static {
        kryo.setRegistrationRequired(false);
        output.setOutputStream(stream);
    }

    public static <T> byte[] asByteArray(T o) {
        stream.reset();
        kryo.writeClassAndObject(output, o);
        output.flush();
        return stream.toByteArray();
    }

    public static <T> T asObject(byte[] b) {
        input.setBuffer(b);
        return (T) kryo.readClassAndObject(input);
    }
}