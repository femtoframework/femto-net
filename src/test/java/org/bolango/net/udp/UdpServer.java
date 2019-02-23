package org.bolango.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author fengyun
 * @version 1.00 2005-12-7 13:12:30
 */
public class UdpServer implements Runnable
{

    private DatagramSocket ds;

    private boolean running = true;

    public UdpServer(int port) throws SocketException
    {
        ds = new DatagramSocket(port);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
        byte[] bytes = new byte[64 * 1024];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        while (isRunning()) {
            try {
                ds.receive(packet);
                //收到之后，直接写回去
                System.out.println("Received packet size:" + packet.getLength());
                System.out.println("Remote address:" + packet.getSocketAddress());
                ds.send(packet);
            }
            catch (IOException e) {
            }
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public static void main(String[] args) throws Exception
    {
        int port = 9777;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        UdpServer server = new UdpServer(port);
        Thread thread = new Thread(server);
        thread.start();
    }
}
