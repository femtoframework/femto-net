package org.femtoframework.net.gmpp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import org.femtoframework.io.IOUtil;
import org.femtoframework.net.message.MessageListener;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.socket.SocketHandler;
import org.femtoframework.util.queue.LinkedQueue;
import org.femtoframework.util.queue.Queue;
import org.femtoframework.util.thread.LifecycleThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleServer
    extends LifecycleThread
    implements SocketHandler, MessageListener {

    private Logger log = LoggerFactory.getLogger("gmpp/simple/socket/handler");


    private byte[] supportedVersions = new byte[]{GmppConstants.VERSION};

    private HashSet<String> supportedCodecs = new HashSet<String>();

    private boolean daemon = false;

    private ServerSocket server;

    private int port = 9776;

    private Queue queue = new LinkedQueue();

    private int threads = 10;

    {
        supportedCodecs.add("jrmp");
        supportedCodecs.add("apsis");
        supportedCodecs.add("niep");
    }

    public SimpleServer() {
    }

    public void _doInitialize() {
        super._doInitialize();

        try {
            server = new ServerSocket(port);
        }
        catch (IOException e) {
        }
    }

    public void handleSocket(Socket socket) {
        GmppConnection conn = new GmppConnection();
        try {
            conn.setProtocol(new GmppPacketProtocol());
            conn.setSocket(socket);
            conn.accept(supportedVersions, supportedCodecs);
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Exception", t);
            }
            IOUtil.close(conn);
            return;
        }

        String remoteHost = conn.getRemoteHost();
        int remotePort = conn.getRemotePort();
        String remoteType = conn.getRemoteType();
        String codec = conn.getCodec();

        GmppCommClient client = new GmppCommClient();
        client.setAutoConnect(false);
        client.setHost(remoteHost);
        client.setPort(remotePort);
        client.setCodec(codec);
        client.setDaemon(daemon);
        client.addConnection(conn);
        client.setLogger(log);
        client.setMessageListener(this);

        if (log.isInfoEnabled()) {
            log.info("Add connection:" + conn);
        }

        for (int i = 0; i < threads; i++) {
            SimpleServerThread thread = new SimpleServerThread(client, queue);
            thread.setDaemon(daemon);
            thread.start();
        }
    }

    public void handle(Socket socket) {
        handleSocket(socket);
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    protected void doRun() throws Exception {
        Socket socket = server.accept();
        log.info("Socket Accepted:" + socket);
        handleSocket(socket);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void onMessage(MessageMetadata metadata, Object message) {
        queue.offer(message);
    }

    public int getThreadCount() {
        return threads;
    }

    public void setThreadCount(int threads) {
        this.threads = threads;
    }
}
