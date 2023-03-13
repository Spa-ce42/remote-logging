package org.millburn.mhs.remote_logging.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;

import java.io.OutputStream;

/**
 * RemoteOutputStream describes an object that temporarily stores all outgoing outputs from a connection,
 * the object will send the bytes to the server if and only if flush() is called.
 *
 * @author Keming Fei
 */
public class RemoteOutputStream extends OutputStream {
    private final Channel channel;
    private ByteBuf buffer;

    private void reset() {
        this.buffer = UnpooledByteBufAllocator.DEFAULT.directBuffer();
//        this.buffer.writerIndex(4);
    }

    /**
     * Constructs a RemoteOutputStream instance given a channel
     * @param channel a channel provided by some network connection
     */
    public RemoteOutputStream(Channel channel) {
        this.channel = channel;
        this.reset();
    }

    /**
     * @return buffered data waiting to be flushed
     */
    public ByteBuf getBuffer() {
        return this.buffer;
    }

    /**
     * Write a single byte to the buffer
     * @param b a byte
     */
    @Override
    public void write(int b) {
        this.buffer.writeByte(b);
    }

    /**
     * Write an array of bytes
     * @param b   the data.
     */
    @Override
    public void write(byte[] b) {
        this.buffer.writeBytes(b);
    }

    /**
     * Write a portion of a byte array
     * @param b     the data.
     * @param off   the start offset in the data.
     * @param len   the number of bytes to write.
     */
    @Override
    public void write(byte[] b, int off, int len) {
        this.buffer.writeBytes(b, off, len);
    }

    /**
     * Writes a string with the length of string described
     * @param s a string
     */
    public void writeString(String s) {
        byte[] b = s.getBytes();
        this.buffer.writeInt(b.length);
        this.buffer.writeBytes(b);
    }

    /**
     * Send the data in the following format:
     *     (commented out)a 4-byte-long integer that denotes the length of the byte buffer
     *     the byte buffer
     * Resets the buffer
     */
    @Override
    public void flush() {/*
        int size = this.buffer.writerIndex();
        this.buffer.writerIndex(0);
        this.buffer.writeInt(size - Integer.BYTES);
        this.buffer.writerIndex(size);*/
        this.channel.write(this.buffer);
        this.reset();
        this.channel.flush();

    }

    /**
     * Discards the buffer, closes the underlying channel
     */
    @Override
    public void close() {
        this.channel.close();
    }
}
