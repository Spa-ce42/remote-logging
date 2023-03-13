package org.millburn.mhs.remote_logging.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.SocketException;

/**
 * Netty framework requires the construction of a Handler, it is currently useless in the Client side.
 *
 * @author Keming Fei
 */
public class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause instanceof SocketException) {

        }

        System.out.println(cause.getClass());
        System.out.println("An error occurred, noooo!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws InterruptedException {

    }
}