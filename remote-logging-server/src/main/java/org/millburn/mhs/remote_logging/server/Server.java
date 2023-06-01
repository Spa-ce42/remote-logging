package org.millburn.mhs.remote_logging.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Individual servers created to handle each incoming connection
 *
 * @author Keming Fei, Alex Kolodkin
 */
public class Server extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    private final FileAppender fa;
    private final String desiredKey;
    private String loggerName;
    private boolean accepted = false;

    /**
     * @param fa         The file appender
     * @param desiredKey the key that is needed for every incoming connection
     */
    public Server(FileAppender fa, String desiredKey) {
        this.fa = fa;
        this.desiredKey = desiredKey;
    }

    /**
     * @return should continue
     */
    private boolean ensureLoggerNameSpecified(ChannelHandlerContext ctx, ByteBuf in) {
        if(this.loggerName == null) {
            byte messageType = in.readByte();
            if(messageType != MessageType.SPECIFY_NAME) {
                LOG.warn("The client did not send in a name! Abort!");
                ctx.channel().close();
                return false;
            }

            int stringLength = in.readInt();
            byte[] b = new byte[stringLength];
            in.readBytes(b);
            this.loggerName = new String(b);
            return false;
        }

        return true;
    }

    /**
     * @return should continue
     */
    private boolean ensureLoggerKeySpecified(ChannelHandlerContext ctx, ByteBuf in) {
        if(!this.accepted) {
            byte messageType = in.readByte();

            if(messageType != MessageType.KEY) {
                LOG.warn("The client did not send in the key! Abort!");
                ctx.channel().close();
                return false;
            }

            int stringLength = in.readInt();
            byte[] b = new byte[stringLength];
            in.readBytes(b);

            if(!this.desiredKey.equals(new String(b))) {
                LOG.warn("The client did not send in the correct key! Abort!");
                ctx.channel().close();
                return false;
            }

            this.accepted = true;
            LOG.info("Connection established with: " + this.loggerName);
            return false;
        }

        return true;
    }

    /**
     * Handles incoming messages from Client
     * The very first message sent by Client should always specify the name of the Client to be identified in Files
     * The second message sent by Client should always contain the correct the key to prevent any attacks
     * If the said message with the said requirement was not met, the connection to the Client will stop
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;

        if(!this.ensureLoggerNameSpecified(ctx, in)) {
            in.release();
            return;
        }

        if(!this.ensureLoggerKeySpecified(ctx, in)) {
            in.release();
            return;
        }

        byte messageType = in.readByte();

        switch(messageType) {
            case MessageType.SPECIFY_NAME -> {
                int stringLength = in.readInt();
                byte[] b = new byte[stringLength];
                in.readBytes(b);
                this.loggerName = new String(b);
                LOG.info(this.loggerName + ": # The client has changed its name to: " + this.loggerName);
                this.fa.appendLine("# The client has changed its name to: " + this.loggerName);
            }

            case MessageType.LOG -> {
                int stringLength = in.readInt();
                byte[] b = new byte[stringLength];
                in.readBytes(b);
                String s = new String(b);
                this.fa.append(s);
                LOG.info(this.loggerName + ": " + s);
            }
        }

        in.release();
    }

    /**
     * Prints out any exceptions caught
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.warn(cause.getMessage(), cause);
    }

    /**
     * Closes the FileAppender when the channel becomes inactive
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOG.info("Channel closed");
    }
}
