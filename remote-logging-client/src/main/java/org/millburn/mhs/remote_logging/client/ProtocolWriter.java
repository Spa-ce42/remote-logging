package org.millburn.mhs.remote_logging.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.handler.stream.ChunkedStream;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProtocolWriter extends OutputStream {
    private Channel c;
    private List<ChunkedStream> byteBufs;
    private ByteBuf current;

    public ProtocolWriter(Channel c) {
        this.c = c;
        this.byteBufs = new ArrayList<>();
    }

    public ProtocolWriter() {
        this(null);
    }

    public void setChannel(Channel c) {
        this.c = c;
        this.flush();
    }

    public void beginMessage(byte type) {
        this.current = UnpooledByteBufAllocator.DEFAULT.directBuffer();
        this.current.writeInt(0);
        this.current.writeByte(type);
    }

    public void writeInt(int i) {
        this.current.writeInt(i);
    }

    public void writeString(String s) {
        byte[] b = s.getBytes();
        this.current.writeInt(b.length);
        this.current.writeBytes(b);
    }

    public void endMessage() {
        int oldWriterIndex = this.current.writerIndex();
        this.current.writerIndex(0);
        this.current.writeInt(oldWriterIndex - 4);
        this.current.writerIndex(oldWriterIndex);
        this.byteBufs.add(new ChunkedStream(new ByteBufInputStream(this.current, true)));
    }

    @Override
    public void write(byte[] b) {
        this.current.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.current.writeBytes(b, off, len);
    }

    @Override
    public void write(int b) {
        this.current.writeByte(b);
    }

    public boolean isActive() {
        return this.c.isActive();
    }

    public void flush() {
        if(this.c == null) {
            return;
        }

        for(ChunkedStream cs : this.byteBufs) {
            this.c.write(cs);
        }

        this.byteBufs = new ArrayList<>();
        this.c.flush();
    }
}
