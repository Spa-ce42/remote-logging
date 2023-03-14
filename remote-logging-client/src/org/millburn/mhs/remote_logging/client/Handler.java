package org.millburn.mhs.remote_logging.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Netty framework requires the construction of a Handler, it is currently useless in the Client side.
 *
 * @author Keming Fei
 */
public class Handler extends ChannelInboundHandlerAdapter {
    private final RemoteLogger rl;

    public Handler(RemoteLogger rl) {
        this.rl = rl;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("The following error has occured: " + cause.getClass());
        System.out.println("The following was the error message: " + cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.rl.attemptToReconnect();
        System.out.println("Channel inactive!");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("Unregistering channel");
    }
}
