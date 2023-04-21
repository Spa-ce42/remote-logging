package org.millburn.mhs.remote_logging.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Keming Fei, Alex Kolodkin
 */
public class Handler extends ChannelInboundHandlerAdapter {
    private final RemoteLogger rl;
    private ProtocolWriter pw;

    public Handler(RemoteLogger rl) {
        this.rl = rl;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.pw = new ProtocolWriter(ctx.channel());
        System.out.println("Reconnected!");
        {
            this.pw.beginMessage(MessageType.SPECIFY_NAME);
            this.pw.writeString(this.rl.getName());
            this.pw.endMessage();
            System.out.println("Sending name: " + this.rl.getName());
        }

        {
            this.pw.beginMessage(MessageType.KEY);
            this.pw.writeString("1234");
            this.pw.endMessage();
            System.out.println("Sending key: 1234");
        }


        for(OnConnectedListener ocl : this.rl.onConnectedListeners) {
            ocl.onConnect(this.rl);
        }

        this.pw.flush();
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