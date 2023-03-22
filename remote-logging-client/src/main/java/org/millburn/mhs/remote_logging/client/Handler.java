package org.millburn.mhs.remote_logging.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Netty framework requires the construction of a Handler, it is currently useless in the Client side.
 *
 * @author Keming Fei, Alex Kolodkin
 */
public class Handler extends ChannelInboundHandlerAdapter {
    private final RemoteLogger rl;

    public Handler(RemoteLogger rl) {
        this.rl = rl;
    }

//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) {
//        System.out.println(ctx.channel());
//        System.out.println("Handler.channelRegistered");
//    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Reconnected!");
        {
            ByteBuf bb = UnpooledByteBufAllocator.DEFAULT.directBuffer();
            bb.writeByte(MessageType.SPECIFY_NAME);
            byte[] b = this.rl.getName().getBytes();
            bb.writeInt(b.length);
            bb.writeBytes(b);
            ctx.channel().write(bb);
        }

        {
            ByteBuf bc = UnpooledByteBufAllocator.DEFAULT.directBuffer();
            bc.writeByte(MessageType.KEY);
            byte[] c = "1234".getBytes();
            bc.writeInt(c.length);
            bc.writeBytes(c);
            ctx.channel().write(bc);
        }

        ctx.channel().flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("The following error has occured: " + cause.getClass());
        System.out.println("The following was the error message: " + cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Channel has became inactive!");
    }

//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) {
//        System.out.println("Handler.channelUnregistered");
//        System.out.println(ctx.channel());
//    }
}