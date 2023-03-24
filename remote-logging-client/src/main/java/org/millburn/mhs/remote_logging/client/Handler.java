package org.millburn.mhs.remote_logging.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 *
 * @author Keming Fei, Alex Kolodkin
 */
public class Handler extends ChannelInboundHandlerAdapter {
    private final RemoteLogger rl;

    public Handler(RemoteLogger rl) {
        this.rl = rl;
    }

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
            ctx.channel().flush();
            System.out.println("Sending name: " + this.rl.getName());
        }

        {
            ByteBuf bc = UnpooledByteBufAllocator.DEFAULT.directBuffer();
            bc.writeByte(MessageType.KEY);
            byte[] c = "1234".getBytes();
            bc.writeInt(c.length);
            bc.writeBytes(c);
            ctx.channel().write(bc);
            ctx.channel().flush();
            System.out.println("Sending key: 1234");
        }


        for(OnConnectedListener ocl : this.rl.onConnectedListeners) {
            ocl.onConnect(this.rl);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Channel has became inactive!");
    }
}