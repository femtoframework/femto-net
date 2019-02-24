package org.femtoframework.net.niep;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import org.femtoframework.io.Streamable;
import org.femtoframework.io.ser.ObjectSerializer;
import org.femtoframework.io.ser.ObjectSerializerFactory;
import org.femtoframework.io.ser.SerUtil;

import static org.femtoframework.net.niep.NiepConstants.TYPE_STREAMBLE;

/**
 * NIEP与Serialization的简单桥接
 *
 * @author fengyun
 * @version 1.00 2004-2-19 11:45:17
 */
public class NiepMarshalStream
    extends ObjectOutputStream
{
    protected NiepOutputStream nos;

    private ObjectSerializerFactory factory = SerUtil.getSerializerFactory(NiepConstants.NIEP);

    private boolean header = true;

    /**
     * 构造
     *
     * @param out 输出流
     */
    public NiepMarshalStream(OutputStream out)
        throws IOException
    {
        this(out, true);
    }

    /**
     * 构造
     *
     * @param out Niep输出流
     * @throws IOException
     */
    NiepMarshalStream(OutputStream out, boolean header)
        throws IOException
    {
        this(new NiepOutputStream(out), header);
    }

    /**
     * 构造
     *
     * @param out Niep输出流
     * @throws IOException
     */
    public NiepMarshalStream(NiepOutputStream out)
        throws IOException
    {
        this(out, true);
    }

    /**
     * 构造
     *
     * @param out Niep输出流
     * @throws IOException
     */
    NiepMarshalStream(NiepOutputStream out, boolean header)
        throws IOException
    {
        super();
        this.nos = out;
        this.header = header;
        if (header) {
            writeStreamHeader();
        }
    }

    /**
     * Flushes the stream. This will write any buffered output bytes and flush
     * through to the underlying stream.
     *
     * @throws    IOException If an I/O error has occurred.
     */
    public void flush() throws IOException
    {
        nos.flush();
    }

    /**
     * Closes the stream. This method must be called to release any resources
     * associated with the stream.
     *
     * @throws    IOException If an I/O error has occurred.
     */
    public void close() throws IOException
    {
        flush();
        if (header) {
            nos.close();
        }
    }

    /**
     * Writes a boolean.
     *
     * @param    val the boolean to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeBoolean(boolean val) throws IOException
    {
        nos.writeBoolean(val);
    }

    /**
     * Writes an 8 bit byte.
     *
     * @param    val the byte value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeByte(int val) throws IOException
    {
        nos.writeByte(val);
    }

    /**
     * Writes a 16 bit short.
     *
     * @param    val the short value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeShort(int val) throws IOException
    {
        nos.writeShort(val);
    }

    /**
     * Writes a 16 bit char.
     *
     * @param    val the char value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeChar(int val) throws IOException
    {
        nos.writeChar(val);
    }

    /**
     * Writes a 32 bit int.
     *
     * @param    val the integer value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeInt(int val) throws IOException
    {
        nos.writeInt(val);
    }

    /**
     * Writes a 64 bit long.
     *
     * @param    val the long value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeLong(long val) throws IOException
    {
        nos.writeLong(val);
    }

    /**
     * Writes a 32 bit float.
     *
     * @param    val the float value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeFloat(float val) throws IOException
    {
        nos.writeFloat(val);
    }

    /**
     * Writes a 64 bit double.
     *
     * @param    val the double value to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeDouble(double val) throws IOException
    {
        nos.writeDouble(val);
    }

    /**
     * Writes a String as a sequence of bytes.
     *
     * @param    str the String of bytes to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeBytes(String str) throws IOException
    {
        nos.writeBytes(str);
    }

    /**
     * Writes a String as a sequence of chars.
     *
     * @param    str the String of chars to be written
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeChars(String str) throws IOException
    {
        nos.writeChars(str);
    }


    /**
     * Primitive data write of this String in UTF format.  Note that there is a
     * significant difference between writing a String into the stream as
     * primitive data or as an Object. A String instance written by writeObject
     * is written into the stream as a String initially. Future writeObject()
     * calls write references to the string into the stream.
     *
     * @param    str the String in UTF format
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    public void writeUTF(String str) throws IOException
    {
        nos.writeString(str);
    }

    /**
     * Reset will disregard the state of any objects already written to the
     * stream.  The state is reset to be the same as a new ObjectOutputStream.
     * The current point in the stream is marked as reset so the corresponding
     * ObjectInputStream will be reset at the same point.  Objects previously
     * written to the stream will not be refered to as already being in the
     * stream.  They will be written to the stream again.
     *
     * @throws    IOException if reset() is invoked while serializing an object.
     */
    public void reset() throws IOException
    {
    }

    /**
     * Write the non-static and non-transient fields of the current class to
     * this stream.  This may only be called from the writeObject method of the
     * class being serialized. It will throw the NotActiveException if it is
     * called otherwise.
     *
     * @throws    IOException if I/O errors occur while writing to the underlying
     * <code>OutputStream</code>
     */
    public void defaultWriteObject() throws IOException
    {
    }

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     *
     * @param    val the byte to be written to the stream
     * @throws    IOException If an I/O error has occurred.
     */
    public void write(int val) throws IOException
    {
        nos.write(val);
    }

    /**
     * Writes an array of bytes. This method will block until the bytes are
     * actually written.
     *
     * @param    buf the data to be written
     * @throws    IOException If an I/O error has occurred.
     */
    public void write(byte[] buf) throws IOException
    {
        nos.write(buf, 0, buf.length);
    }

    /**
     * Writes a sub array of bytes.
     *
     * @param    buf the data to be written
     * @param    off the start offset in the data
     * @param    len the number of bytes that are written
     * @throws    IOException If an I/O error has occurred.
     */
    public void write(byte[] buf, int off, int len) throws IOException
    {
        int endoff = off + len;
        if (off < 0 || len < 0 || endoff > buf.length || endoff < 0) {
            throw new IndexOutOfBoundsException();
        }
        nos.write(buf, off, len);
    }

    /**
     * The writeStreamHeader method is provided so subclasses can append or
     * prepend their own header to the stream.  It writes the magic number and
     * version to the stream.
     *
     * @throws    IOException if I/O errors occur while writing to the underlying
     * stream
     */
    protected void writeStreamHeader() throws IOException
    {
        nos.writeShort(NiepConstants.STREAM_MAGIC);
        nos.writeShort(NiepConstants.STREAM_VERSION);
    }

    /**
     * Write the specified class descriptor to the ObjectOutputStream.  Class
     * descriptors are used to identify the classes of objects written to the
     * stream.  Subclasses of ObjectOutputStream may override this method to
     * customize the way in which class descriptors are written to the
     * serialization stream.  The corresponding method in ObjectInputStream,
     * <code>readClassDescriptor</code>, should then be overridden to
     * reconstitute the class descriptor from its custom stream representation.
     * By default, this method writes class descriptors according to the format
     * defined in the Object Serialization specification.
     * <p/>
     * <p>Note that this method will only be called if the ObjectOutputStream
     * is not using the old serialization stream format (set by calling
     * ObjectOutputStream's <code>useProtocolVersion</code> method).  If this
     * serialization stream is using the old format
     * (<code>PROTOCOL_VERSION_1</code>), the class descriptor will be written
     * internally in a manner that cannot be overridden or customized.
     *
     * @param    desc class descriptor to write to the stream
     * @throws    IOException If an I/O error has occurred.
     * @see java.io.ObjectInputStream#readClassDescriptor()
     * @see #useProtocolVersion(int)
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_1
     * @since 1.3
     */
    protected void writeClassDescriptor(ObjectStreamClass desc)
        throws IOException
    {

    }

    /**
     * Method used by subclasses to override the default writeObject method.
     * This method is called by trusted subclasses of ObjectInputStream that
     * constructed ObjectInputStream using the protected no-arg constructor.
     * The subclass is expected to provide an override method with the modifier
     * "final".
     *
     * @param    obj object to be written to the underlying stream
     * @throws    IOException if there are I/O errors while writing to the
     * underlying stream
     * @see #writeObject(Object)
     * @since 1.2
     */
    protected void writeObjectOverride(Object obj) throws IOException
    {
        if (obj == null) {
            nos.writeNull();
        }
        else if (obj instanceof Externalizable) {
            nos.writeType(NiepConstants.TYPE_EXTERNALIZABLE);
            nos.writeClass(obj.getClass());
            ((Externalizable)obj).writeExternal(this);
        }
        else if (obj instanceof Streamable) {
            nos.writeType(TYPE_STREAMBLE);
            nos.writeClass(obj.getClass());
            ((Streamable)obj).writeTo(this);
        }
        else {
            ObjectSerializer serializer = factory.getSerializer(obj);
            if (serializer != null) {
                nos.writeType(NiepConstants.TYPE_SERIALIZER);
                nos.writeClass(obj.getClass());
                serializer.marshal(this, obj);
                return;
            }
            else {
                if (obj instanceof Object[]) {
                    Class clazz = obj.getClass().getComponentType();
                    serializer = factory.getSerializer(clazz);
                    if (serializer != null) {
                        nos.writeType(NiepConstants.TYPE_SERIALIZER_ARRAY);
                        nos.writeClass(clazz);
                        Object[] array = (Object[]) obj;
                        int len = array.length;
                        nos.writeInt(len);
                        for (int i = 0; i < len; i++) {
                            serializer.marshal(this, array[i]);
                        }
                        return;
                    }
                }
            }
            nos.writeObject(obj);
        }
    }
}
