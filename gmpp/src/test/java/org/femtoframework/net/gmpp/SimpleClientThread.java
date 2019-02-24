package org.femtoframework.net.gmpp;

import org.femtoframework.util.nutlet.NutletUtil;
import org.femtoframework.util.thread.LifecycleThread;

public class SimpleClientThread extends LifecycleThread {
    private GmppCommClient client;

    private int count = 10000;

    private int index = 0;

    private int sleepTime = 100;

    public SimpleClientThread(GmppCommClient client) {
        this.client = client;
    }

    protected void doRun() throws Exception {
        SimpleMessage message = new SimpleMessage();
        message.setBytes(NutletUtil.getBytes(8192));
        try {
            client.send(message);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        index++;
        if (sleepTime > 0) {
            Thread.sleep(sleepTime);
        }
        if (index >= count) {
            //Break
            throw new Exception();
        }
    }

    protected boolean handleException(Exception e) {
        return true;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
