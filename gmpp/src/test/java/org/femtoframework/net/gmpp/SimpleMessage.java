package org.femtoframework.net.gmpp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.femtoframework.bean.Identifiable;
import org.femtoframework.bean.Nameable;
import org.femtoframework.bean.NamedBean;
import org.femtoframework.io.DataCodec;
import org.femtoframework.net.message.Message;
import org.femtoframework.util.nutlet.NutletUtil;

public class SimpleMessage implements Message, Nameable, NamedBean, Identifiable, Externalizable {
    private int id;
    private String name;

    private byte[] bytes;

    private static int next = 1;

    public SimpleMessage() {
        setName(NutletUtil.nextCode(8));
        synchronized (SimpleMessage.class) {
            setId(next++);
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String toString() {
        return "SimpleMessage {" + super.toString() + " " + getId() + "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeUTF(name);
        DataCodec.writeBytes(out, bytes);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        name = in.readUTF();
        bytes = DataCodec.readBytes(in);
    }
}
