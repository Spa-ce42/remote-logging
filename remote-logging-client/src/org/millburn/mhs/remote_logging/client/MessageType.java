package org.millburn.mhs.remote_logging.client;

public final class MessageType {
    public static final byte SPECIFY_NAME = 0;
    public static final byte LOG = 1;

    private MessageType() {
        throw new AssertionError();
    }
}
