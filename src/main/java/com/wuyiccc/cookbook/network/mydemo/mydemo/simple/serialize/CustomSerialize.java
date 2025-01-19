package com.wuyiccc.cookbook.network.mydemo.mydemo.simple.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wuyiccc
 * @date 2025/1/19 15:58
 */
public class CustomSerialize {

    public static byte[] serialize(Object object) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(byteArrayOutputStream);
        ho.writeObject(object);

        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {

        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(bi);

        return (T) hi.readObject(clazz);
    }
}
