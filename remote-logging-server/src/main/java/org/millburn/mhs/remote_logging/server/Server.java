package org.millburn.mhs.remote_logging.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;

/**
 * Individual servers created to handle each incoming connection
 *
 * @author Keming Fei, Alex Kolodkin
 */
public class Server extends ChannelInboundHandlerAdapter {
    private final FileAppenderFactory faf;
    private final String desiredKey;
    private String loggerName;
    private boolean accepted = false;
    private FileAppender fa;

    /**
     * @param faf helps creates the FileAppender for the logger to log to
     * @param desiredKey the key that is needed for every incoming connection
     */
    public Server(FileAppenderFactory faf, String desiredKey) {
        this.faf = faf;
        this.desiredKey = desiredKey;
    }

    /**
     * Handles incoming messages from Client
     * The very first message sent by Client should always specify the name of the Client to be identified in Files
     * The second message sent by Client should always contain the correct the key to prevent any attacks
     * If the said message with the said requirement was not met, the connection to the Client will stop
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server.channelRead");
        System.out.println("NEW CHANNEL READ");
        ByteBuf in = (ByteBuf)msg;
        if(this.loggerName == null) {
            byte messageType = in.readByte();
            System.out.println(messageType);
            if(messageType != MessageType.SPECIFY_NAME) {
                System.err.println("The client did not send in a name! Abort!");
                ctx.channel().close();
                return;
            }

            int stringLength = in.readInt();
            byte[] b = new byte[stringLength];
            in.readBytes(b);
            this.loggerName = new String(b);
            this.fa = this.faf.createFileAppender(this.loggerName);
        }

        if(!in.isReadable()) {
            return;
        }

        if(!this.accepted) {
            byte messageType = in.readByte();
            System.out.println(messageType);
            if(messageType != MessageType.KEY) {
                System.err.println("The client did not send in the key! Abort!");
                ctx.channel().close();
                return;
            }

            int stringLength = in.readInt();
            byte[] b = new byte[stringLength];
            in.readBytes(b);
            if(!this.desiredKey.equals(new String(b))) {
                System.err.println("The client did not send in the correct key! Abort!");
                ctx.channel().close();
                return;
            } else {
                this.accepted = true;
            }
            System.out.println("Connection established with: " + this.loggerName);
        }

/*        System.out.println("STARTING THE READING");
//        ArrayList<Character> b = new ArrayList<>();
        StringBuilder b = new StringBuilder();
        int i = 0;
        while (in.isReadable()) {
            char character = (char) in.readByte();
            if (i > 4) {
                b.append(character);
            }
            i++;
        }
        System.out.println(b.toString());*/
        if(!in.isReadable()) {
            return;
        }

            byte messageType = in.readByte();
            System.out.println(messageType);

            if (messageType == MessageType.SPECIFY_NAME) {
                int stringLength = in.readInt();
                byte[] b = new byte[stringLength];
                in.readBytes(b);
                this.loggerName = new String(b);
                System.out.println(this.loggerName + ": # The client has changed its name to: " + this.loggerName);
                this.fa.appendLine("# The client has changed its name to: " + this.loggerName);
                this.fa.close();
                this.fa = this.faf.createFileAppender(this.loggerName);
            } else {
                int stringLength = in.readInt();
                System.out.println("Capacity: " + in.capacity());
                System.out.println("LENGTH: " + stringLength);
                System.out.println("Reader: " + in.readerIndex());
                System.out.println("Writer: " + in.writerIndex());
                byte[] b = new byte[100];
                in.readBytes(b);
                String message = new String(b);
                System.out.println(this.loggerName + ": " + message);
                this.fa.appendLine(message);
                System.out.println("APPENDED");
                this.fa.flush();
                System.out.println("Flushed");
            }
    }

    /**
     * Prints out any exceptions caught
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

    /**
     * Closes the FileAppender when the channel becomes inactive
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(this.fa != null) {
            this.fa.close();
        }

        System.out.println("Channel closed");
    }
}
