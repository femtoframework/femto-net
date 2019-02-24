package org.femtoframework.net.socket.bifurcation;

/**
 * Allow one port binds multiple protocols to simplify the port management
 */
public interface Bifurcated {

    /**
     * Bifurcation number indicates a protocol
     *
     * @return Bifurcation number
     */
    int getBifurcation();
}
