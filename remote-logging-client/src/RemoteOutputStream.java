import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.OutputStream;

public class RemoteOutputStream extends OutputStream {
    private final Channel channel;
    private ByteBuf buffer;

    public RemoteOutputStream(Channel channel) {
        this.channel = channel;
        this.buffer = this.channel.alloc().ioBuffer();
    }

    @Override
    public void write(int b) {
        this.buffer.writeByte(b);
    }

    @Override
    public void write(byte[] b) {
        this.buffer.writeInt(b.length);
        this.buffer.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.buffer.writeInt(len);
        this.buffer.writeBytes(b, off, len);
    }

    @Override
    public void flush() {
        this.channel.write(this.buffer);
        this.buffer = this.channel.alloc().ioBuffer();
        this.channel.flush();
    }

    @Override
    public void close() {
        this.channel.close();
    }
}
