package org.millburn.mhs.remote_logging.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.SocketException;
import java.time.Instant;

/**
 * A class required by Netty, created for each connection to the super server
 *
 * @author Keming Fei
 */
public class Server extends ChannelInboundHandlerAdapter {
    private String loggerName;
    private boolean accepted = false;
    private final String key = "1234";
    private FileAppender fa;

    /**
     * Handles incoming messages from Client
     * The very first message sent by Client should always specify the name of the Client to be identified in Files
     * If the said message with the said requirement was not met, the connection to the Client will stop
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (this.loggerName == null) {
            ByteBuf in = (ByteBuf) msg;
            byte messageType = in.readByte();
            if (messageType != MessageType.SPECIFY_NAME) {
                System.err.println("The client did not send in a name! Abort!");
                ctx.channel().close();
                return;
            }

            int stringLength = in.readInt();
            byte[] b = new byte[stringLength];
            in.readBytes(b);
            this.loggerName = new String(b);
            this.fa = new FileAppender((this.loggerName + "-" + Instant.now() + ".log").replace(':', '-'));
        }

        if (!accepted) {
            ByteBuf in = (ByteBuf) msg;
            byte messageType = in.readByte();
            if (messageType != MessageType.KEY) {
                System.err.println("The client did not send in the key! Abort!");
                ctx.channel().close();
                return;
            }

            int stringLength = in.readInt();
            byte[] b = new byte[stringLength];
            in.readBytes(b);
            if (!key.equals(new String(b))) {
                System.err.println("The client did not send in the correct key! Abort!");
                ctx.channel().close();
                return;
            } else {
                accepted = true;
            }
        }

        System.out.println("Connection established with: " + this.loggerName);

        ByteBuf in = (ByteBuf) msg;

        while(in.isReadable()) {
            byte messageType = in.readByte();

            switch (messageType) {
                case MessageType.SPECIFY_NAME -> {

                }

                case MessageType.LOG -> {
                    int stringLength = in.readInt();
                    byte[] b = new byte[stringLength];
                    in.readBytes(b);
                    String message = new String(b);
                    System.out.println(this.loggerName + ": " + message);
                    this.fa.appendLine(message);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            System.out.println("Client connection closed!");
        } else {
            cause.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.fa.close();
    }
}
