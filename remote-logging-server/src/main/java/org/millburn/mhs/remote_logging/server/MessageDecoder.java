package org.millburn.mhs.remote_logging.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int initialReaderIndex = in.readerIndex();

        if(in.readableBytes() < 4) {
            return;
        }

        int length = in.readInt();

        if(in.readableBytes() < length) {
            in.readerIndex(initialReaderIndex);
            return;
        }

        out.add(in.readBytes(length));
    }
}
