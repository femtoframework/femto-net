package org.femtoframework.net.gmpp;

import org.femtoframework.net.message.packet.MessagePacket;
import org.femtoframework.util.queue.Queue;
import org.femtoframework.util.thread.LifecycleThread;

/**
 * @author fengyun
 * @version 1.00 2005-6-2 19:22:02
 */
public class SimpleServerThread extends LifecycleThread {
    private Queue queue;

    private GmppCommClient client;

    public SimpleServerThread(GmppCommClient client, Queue queue) {
        this.client = client;
        this.queue = queue;
    }

    protected void doRun() throws Exception {
        Object obj = queue.poll();
        if (obj != null) {
            if (obj instanceof MessagePacket) {
                MessagePacket mp = (MessagePacket)obj;
                obj = mp.getMessage();
                mp.destroy();
            }
        }

        try {
            client.send(obj);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
