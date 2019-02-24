package org.femtoframework.net.socket;

/**
 * Socket Actions
 */
public interface SocketActions {
    /**
     * Socket Connected
     */
    int ACTION_CONNECTED = 1;

    /**
     * Socket Handling
     */
    int ACTION_HANDLING = 2;

    /**
     * Socket Timeout
     */
    int ACTION_TIMEOUT = 4;

    /**
     * START_TLS
     */
    int ACTION_START_TLS = 5;

    /**
     * HANDSHAKE
     */
    int ACTION_HANDSHAKE = 6;

    /**
     * Socket Context FINISH
     */
    int ACTION_FINISH = 8;

    /**
     * Socket FINISHED
     */
    int ACTION_FINISHED = 9;

    /**
     * SASL
     */
    int ACTION_SASL = 11;

    /**
     * SASL completed
     */
    int ACTION_SASL_COMPLETED = 12;

    /**
     * SASL FAILED
     */
    int ACTION_SASL_FAILED = 13;

    /**
     * Socket Context CLOSE
     */
    int ACTION_CLOSE = 16;

    /**
     * Socket Context Closed
     */
    int ACTION_CLOSED = 17;

    /**
     * Socket Context blocked
     */
    int ACTION_BLOCKED = 18;

    /**
     * Socket Context Closing
     */
    int ACTION_CLOSING = 19;

    /**
     * Event
     */
    int ACTION_EVENT = 40;

    /**
     * Waiting for event
     */
    int ACTION_WAITING_EVENT = 41;

}
