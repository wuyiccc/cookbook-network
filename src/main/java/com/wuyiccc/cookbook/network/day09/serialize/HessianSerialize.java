package com.wuyiccc.cookbook.network.day09.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wuyiccc
 * @date 2024/11/14 07:10
 */
public class HessianSerialize {

    public static byte[] serialize(Object object) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(byteArrayOutputStream);
        ho.writeObject(object);

        // 序列化为二进制字节数组
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }


    public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        Object object = hessianInput.readObject(clazz);

        return (T) object;
    }


}
