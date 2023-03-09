import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.OutputStream;

public class RemoteOutputStream extends OutputStream {
    private final Channel channel;
    private ByteBuf buffer;
    private int size;

    private void reset() {
        this.buffer = this.channel.alloc().ioBuffer();
        this.buffer.writerIndex(4);
        this.size = 4;
    }

    public RemoteOutputStream(Channel channel) {
        this.channel = channel;
        this.reset();
    }

    @Override
    public void write(int b) {
        ++this.size;
        this.buffer.writeByte(b);
    }

    @Override
    public void write(byte[] b) {
        this.size = this.size + b.length;
        this.buffer.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.size = this.size + len;
        this.buffer.writeBytes(b, off, len);
    }

    @Override
    public void flush() {
        this.buffer.writerIndex(0);
        this.buffer.writeInt(this.size - Integer.BYTES);
        this.buffer.writerIndex(this.size);
        this.channel.write(this.buffer);
        this.reset();
        this.channel.flush();
    }

    @Override
    public void close() {
        this.channel.close();
    }
}
