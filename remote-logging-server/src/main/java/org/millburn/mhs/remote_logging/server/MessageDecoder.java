package org.millburn.mhs.remote_logging.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDecoder.class);
    private ByteBuf iab;
    private int lengthNeeded;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if(this.lengthNeeded <= 0) {
            if(in.readableBytes() < 4) {
                return;
            }

            int length = in.readInt();

            if(in.readableBytes() < length) {
                int readableBytes = in.readableBytes();
                this.iab = UnpooledByteBufAllocator.DEFAULT.directBuffer();
                this.iab.writeBytes(in.readBytes(readableBytes));
                this.lengthNeeded = length - readableBytes;
                return;
            }

            ByteBuf bb = in.readBytes(length);
            bb.retain();
            out.add(bb);
            return;
        }

        int readableBytes = in.readableBytes();
        if(this.lengthNeeded < readableBytes) {
            this.iab.writeBytes(in.readBytes(this.lengthNeeded));
            this.lengthNeeded = 0;
            out.add(this.iab);
            return;
        }

        if(this.lengthNeeded == readableBytes) {
            this.iab.writeBytes(in.readBytes(this.lengthNeeded));
            this.lengthNeeded = 0;
            out.add(this.iab);
            return;
        }

        this.iab.writeBytes(in.readBytes(readableBytes));
        this.lengthNeeded = this.lengthNeeded - readableBytes;
    }
}
