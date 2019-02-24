package org.femtoframework.net.niep;

import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.Streamable;
import org.femtoframework.io.ser.ObjectDeserializer;
import org.femtoframework.io.ser.ObjectDeserializerFactory;
import org.femtoframework.io.ser.SerUtil;

/**
 * NIEP与ObjectInputStream的桥接
 *
 * @author fengyun
 * @version 1.00 2004-2-19 12:05:18
 */
public class NiepDemarshalStream
    extends ObjectInputStream
{
    protected NiepInputStream nis;

    private boolean header = true;

    private ObjectDeserializerFactory factory = SerUtil.getDeserializerFactory(NiepConstants.NIEP);

    /**
     * 构造
     *
     * @param in 输入流
     */
    public NiepDemarshalStream(InputStream in)
        throws IOException
    {
        this(in, true);
    }

    /**
     * 构造
     *
     * @param nis
     */
    public NiepDemarshalStream(NiepInputStream nis)
        throws IOException
    {
        this(nis, true);
    }

    /**
     * 构造
     *
     * @param in 输入流
     */
    NiepDemarshalStream(InputStream in, boolean header)
        throws IOException
    {
        this(new NiepInputStream(in), header);
    }

    /**
     * 构造
     *
     * @param nis
     */
    NiepDemarshalStream(NiepInputStream nis, boolean header)
        throws IOException
    {
        super();
        this.nis = nis;
        this.header = header;
        if (header) {
            readStreamHeader();
        }
    }

    /**
     * Read the non-static and non-transient fields of the current class from
     * this stream.  This may only be called from the readObject method of the
     * class being deserialized. It will throw the NotActiveException if it is
     * called otherwise.
     *
     * @throws ClassNotFoundException     if the class of a serialized object
     *                                    could not be found.
     * @throws IOException                if an I/O error occurs.
     * @throws java.io.NotActiveException if the stream is not currently reading
     *                                    objects.
     */
    public void defaultReadObject()
        throws IOException, ClassNotFoundException
    {

    }

    /**
     * The readStreamHeader method is provided to allow subclasses to read and
     * verify their own stream headers. It reads and verifies the magic number
     * and version number.
     *
     * @throws IOException if there are I/O errors while reading from the
     *                     underlying <code>InputStream</code>
     * @throws java.io.StreamCorruptedException
     *                     if control information in the stream
     *                     is inconsistent
     */
    protected void readStreamHeader()
        throws IOException, StreamCorruptedException
    {
        if (nis.readShort() != NiepConstants.STREAM_MAGIC ||
            nis.readShort() != NiepConstants.STREAM_VERSION) {
            throw new StreamCorruptedException("invalid stream header");
        }
    }

    /**
     * Read a class descriptor from the serialization stream.  This method is
     * called when the ObjectInputStream expects a class descriptor as the next
     * item in the serialization stream.  Subclasses of ObjectInputStream may
     * override this method to read in class descriptors that have been written
     * in non-standard formats (by subclasses of ObjectOutputStream which have
     * overridden the <code>writeClassDescriptor</code> method).  By default,
     * this method reads class descriptors according to the format defined in
     * the Object Serialization specification.
     *
     * @return the class descriptor read
     * @throws IOException            If an I/O error has occurred.
     * @throws ClassNotFoundException If the Class of a serialized object used
     *                                in the class descriptor representation cannot be found
     * @see java.io.ObjectOutputStream#writeClassDescriptor(java.io.ObjectStreamClass)
     * @since 1.3
     */
    protected ObjectStreamClass readClassDescriptor()
        throws IOException, ClassNotFoundException
    {
        return null;
    }

    /**
     * This method is called by trusted subclasses of ObjectOutputStream that
     * constructed ObjectOutputStream using the protected no-arg constructor.
     * The subclass is expected to provide an override method with the modifier
     * "final".
     *
     * @return the Object read from the stream.
     * @throws ClassNotFoundException        Class definition of a serialized object
     *                                       cannot be found.
     * @throws java.io.OptionalDataException Primitive data was found in the stream
     *                                       instead of objects.
     * @throws IOException                   if I/O errors occurred while reading from the
     *                                       underlying stream
     * @see #readObject()
     * @since 1.2
     */
    protected Object readObjectOverride()
        throws IOException, ClassNotFoundException
    {
        int type = nis.readType();
        if (type == NiepConstants.TYPE_EXTERNALIZABLE) {
            Class clazz = nis.readClass();
            Externalizable obj;
            try {
                obj = (Externalizable) clazz.newInstance();
                obj.readExternal(this);
                return obj;
            }
            catch (ClassNotFoundException cnfe) {
                throw new IOException("Class not found exception:" + cnfe.getMessage());
            }
            catch (IllegalAccessException e) {
                throw new IOException("Can't access the constructor");
            }
            catch (InstantiationException e) {
                throw new IOException("Instantiation exception");
            }
        }
        else if (type == NiepConstants.TYPE_STREAMBLE) {
            Class clazz = nis.readClass();
            Streamable obj;
            try {
                obj = (Streamable) clazz.newInstance();
                obj.readFrom(this);
                return obj;
            }
            catch (ClassNotFoundException cnfe) {
                throw new IOException("Class not found exception:" + cnfe.getMessage());
            }
            catch (IllegalAccessException e) {
                throw new IOException("Can't access the constructor");
            }
            catch (InstantiationException e) {
                throw new IOException("Instantiation exception");
            }
        }
        else if (type == NiepConstants.TYPE_SERIALIZER) {
            Class clazz = nis.readClass();
            ObjectDeserializer deserializer = factory.getDeserializer(clazz);
            return deserializer.demarshal(this, clazz);
        }
        else if (type == NiepConstants.TYPE_SERIALIZER_ARRAY) {
            Class clazz = nis.readClass();
            int len = nis.readInt();
            if (len < 0) {
                return null;
            }
            ObjectDeserializer deserializer = factory.getDeserializer(clazz);
            Object array = Array.newInstance(clazz, len);
            for (int i = 0; i < len; i++) {
                Array.set(array, i, deserializer.demarshal(this, clazz));
            }
            return array;
        }
        else {
            return nis.readObject(type);
        }
    }

    /**
     * Reads a byte of data. This method will block if no input is available.
     *
     * @return the byte read, or -1 if the end of the stream is reached.
     * @throws IOException If an I/O error has occurred.
     */
    public int read() throws IOException
    {
        return nis.read();
    }

    /**
     * Reads into an array of bytes.  This method will block until some input
     * is available. Consider using java.io.DataInputStream.readFully to read
     * exactly 'length' bytes.
     *
     * @param buf the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return the actual number of bytes read, -1 is returned when the end of
     *         the stream is reached.
     * @throws IOException If an I/O error has occurred.
     * @see java.io.DataInputStream#readFully(byte[],int,int)
     */
    public int read(byte[] buf, int off, int len) throws IOException
    {
        int endoff = off + len;
        if (off < 0 || len < 0 || endoff > buf.length || endoff < 0) {
            throw new IndexOutOfBoundsException();
        }
        return nis.read(buf, off, len);
    }

    /**
     * Returns the number of bytes that can be read without blocking.
     *
     * @return the number of available bytes.
     * @throws IOException if there are I/O errors while reading from the
     *                     underlying <code>InputStream</code>
     */
    public int available() throws IOException
    {
        return nis.available();
    }

    /**
     * Closes the input stream. Must be called to release any resources
     * associated with the stream.
     *
     * @throws IOException If an I/O error has occurred.
     */
    public void close() throws IOException
    {
        if (header) {
            nis.close();
        }
    }

    /**
     * Reads in a boolean.
     *
     * @return the boolean read.
     * @throws java.io.EOFException If end of file is reached.
     * @throws IOException          If other I/O error has occurred.
     */
    public boolean readBoolean() throws IOException
    {
        return nis.readBoolean();
    }

    /**
     * Reads an 8 bit byte.
     *
     * @return the 8 bit byte read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public byte readByte() throws IOException
    {
        return nis.readByte();
    }

    /**
     * Reads an unsigned 8 bit byte.
     *
     * @return the 8 bit byte read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public int readUnsignedByte() throws IOException
    {
        return nis.readUnsignedByte();
    }

    /**
     * Reads a 16 bit char.
     *
     * @return the 16 bit char read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public char readChar() throws IOException
    {
        return nis.readChar();
    }

    /**
     * Reads a 16 bit short.
     *
     * @return the 16 bit short read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public short readShort() throws IOException
    {
        return nis.readShort();
    }

    /**
     * Reads an unsigned 16 bit short.
     *
     * @return the 16 bit short read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public int readUnsignedShort() throws IOException
    {
        return nis.readUnsignedShort();
    }

    /**
     * Reads a 32 bit int.
     *
     * @return the 32 bit integer read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public int readInt() throws IOException
    {
        return nis.readInt();
    }

    /**
     * Reads a 64 bit long.
     *
     * @return the read 64 bit long.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public long readLong() throws IOException
    {
        return nis.readLong();
    }

    /**
     * Reads a 32 bit float.
     *
     * @return the 32 bit float read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public float readFloat() throws IOException
    {
        return nis.readFloat();
    }

    /**
     * Reads a 64 bit double.
     *
     * @return the 64 bit double read.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public double readDouble() throws IOException
    {
        return nis.readDouble();
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     *
     * @param buf the buffer into which the data is read
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public void readFully(byte[] buf) throws IOException
    {
        nis.readFully(buf, 0, buf.length);
    }

    /**
     * Reads bytes, blocking until all bytes are read.
     *
     * @param buf the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes to read
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public void readFully(byte[] buf, int off, int len) throws IOException
    {
        int endoff = off + len;
        if (off < 0 || len < 0 || endoff > buf.length || endoff < 0) {
            throw new IndexOutOfBoundsException();
        }
        nis.readFully(buf, off, len);
    }

    /**
     * Skips bytes, block until all bytes are skipped.
     *
     * @param len the number of bytes to be skipped
     * @return the actual number of bytes skipped.
     * @throws EOFException If end of file is reached.
     * @throws IOException  If other I/O error has occurred.
     */
    public int skipBytes(int len) throws IOException
    {
        return nis.skipBytes(len);
    }

    /**
     * Reads in a line that has been terminated by a \n, \r, \r\n or EOF.
     *
     * @return a String copy of the line.
     * @throws IOException if there are I/O errors while reading from the
     *                     underlying <code>InputStream</code>
     * @deprecated This method does not properly convert bytes to characters.
     *             see DataInputStream for the details and alternatives.
     */
    public String readLine() throws IOException
    {
        return nis.readLine();
    }

    /**
     * Reads a UTF format String.
     *
     * @return the String.
     * @throws IOException if there are I/O errors while reading from the
     *                     underlying <code>InputStream</code>
     */
    public String readUTF() throws IOException
    {
        return nis.readString();
    }
}
