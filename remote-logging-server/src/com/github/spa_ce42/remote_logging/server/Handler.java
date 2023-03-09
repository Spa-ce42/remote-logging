package com.github.spa_ce42.remote_logging.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * A class required by Netty, created for each connection to this server
 *
 * @author Keming Fei
 */
public class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf)msg;
        int stringLength = in.readInt();
        byte[] b = new byte[stringLength];
        in.readBytes(b);
        String s = new String(b);
        System.out.print(s);
        in.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
