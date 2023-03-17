package org.millburn.mhs.remote_logging.client;

/**
 * Describes types of message a server/client can receive/send
 *
 * @author Keming Fei, Alex Kolodkin
 */
public final class MessageType {
    /**
     * format: SPECIFY_NAME [string]
     * Must be sent as the first message from the client for every start of a connection
     */
    public static final byte SPECIFY_NAME = 0;
    /**
     * format: LOG [string]
     * Sent everytime a logging statement is generated at the client side
     */
    public static final byte LOG = 1;
    /**
     * format: KEY [string]
     * Must be sent as the second message to verify with the server that the client is safe
     */
    public static final byte KEY = 2;

    private MessageType() {
        throw new AssertionError();
    }
}
