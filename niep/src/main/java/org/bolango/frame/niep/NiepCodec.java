package org.femtoframework.frame.niep;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.femtoframework.io.ObjectCodec;

/**
 * 基于NIEP的对象编码和解码器
 *
 * @author fengyun
 * @version 1.00 2004-3-16 18:52:06
 */
public class NiepCodec implements ObjectCodec
{
    /**
     * 根据输入流创建对象输入流
     *
     * @param input 输入流
     */
    public ObjectInputStream getObjectInput(InputStream input) throws IOException
    {
        return new NiepDemarshalStream(input);
    }

    /**
     * 根据输出流创建对象输出流
     *
     * @param output 输出流
     */
    public ObjectOutputStream getObjectOutput(OutputStream output) throws IOException
    {
        return new NiepMarshalStream(output);
    }

    /**
     * 从输入流中读取一个对象
     *
     * @param input 输入流
     * @return
     * @throws java.io.IOException
     */
    public Object readObject(InputStream input)
        throws IOException, ClassNotFoundException
    {
        NiepDemarshalStream nds;
        if (input instanceof NiepDemarshalStream) {
            nds = (NiepDemarshalStream) input;
        }
        else {
            nds = new NiepDemarshalStream(input);
        }
        return nds.readObject();
    }

    /**
     * 将对象写出去
     *
     * @param output 输出流
     * @param obj    对象
     * @throws java.io.IOException
     */
    public void writeObject(OutputStream output, Object obj)
        throws IOException
    {
        NiepMarshalStream nms;
        if (output instanceof NiepMarshalStream) {
            nms = (NiepMarshalStream) output;
        }
        else {
            nms = new NiepMarshalStream(output);
        }
        nms.writeObject(obj);
        nms.flush();
    }
}
