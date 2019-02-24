package org.femtoframework.net.gmpp;

import org.femtoframework.net.message.MessageListener;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.MessageRegistry;
import org.femtoframework.net.message.MessageRegistryUtil;
import org.femtoframework.net.message.ext.SimpleMessageMetadata;
import org.femtoframework.net.message.packet.MessagePacket;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fengyun
 * @version 1.00 2005-6-1 22:11:01
 */
public class GmppCommClientTest
    implements MessageListener
{

    /**
     * The class is for debug
     *
     * @author fengyun
     * @version 2.0
     */
    public class TimeWorker implements java.io.Serializable
    {
        public static final int INITIAL_SIZE = 5;

        private List<String> description = null;
        private List<Long> points = null;

        public TimeWorker()
        {
            this(INITIAL_SIZE);
        }

        public TimeWorker(int initialSize)
        {
            description = new ArrayList<>(initialSize);
            points = new ArrayList<>(initialSize);
        }

        public void addTimePoint()
        {
            points.add(System.currentTimeMillis());
            description.add(String.valueOf(points.size()));
        }

        public void addTimePoint(String desc)
        {
            points.add(System.currentTimeMillis());
            description.add(desc);
        }

        public int getSize()
        {
            return points.size();
        }

        public long getTimeBetween(int startindex, int endindex)
        {
            if (startindex < 0 || startindex > endindex
                    || endindex > points.size()) {
                return 0L;
            }
            Long start = points.get(startindex);
            Long end = points.get(endindex);
            return end - start;
        }

        public long[] getTimes()
        {
            if (points.size() <= 1) {
                return new long[0];
            }
            Long prev = points.get(0);
            long[] times = new long[points.size() - 1];
            for (int i = 1; i < points.size(); i++) {
                Long next = points.get(i);
                times[i - 1] = next - prev;
                prev = next;
            }
            return times;
        }

        public void clearTimePoints()
        {
            points.clear();
            description.clear();
        }

        public void printResult()
        {
            long[] times = getTimes();
            System.out.println("---------------------------------------------------");
            System.out.println(description.get(0));
            System.out.println("---------------------------------------------------");
            for (int i = 0; i < times.length; i++) {
                System.out.println(description.get(i + 1) + " :" + times[i]);
            }
            System.out.println("---------------------------------------------------");
        }
    }


    public static final int THREAD_COUNT = 10;
    public static final int COUNT = 500;
    public static final int SLEEP_TIME = 0;

    @Test
    public void testGmppCommClient() throws Exception
    {
        System.setProperty("cube.system.type", "gmpp");

        MessageRegistry registry = MessageRegistryUtil.getRegistry();
        SimpleMessageMetadata messageMetadata = new SimpleMessageMetadata();
        messageMetadata.setMessageClass("org.femtoframework.net.gmpp.SimpleMessage");
        messageMetadata.setListener("simple");
        messageMetadata.setType(240);
        registry.addMetadata(messageMetadata);

        SimpleServer server = new SimpleServer();
        server.setDaemon(true);
        server.setName("simple");
        server.setPort(9776);
        server.initialize();
        server.start();

        final GmppCommClient client = new GmppCommClient();
        client.setMessageListener(this);
        client.setAutoConnect(true);
        client.setHost("127.0.0.1");
        client.setPort(9776);
        client.setLogger(LoggerFactory.getLogger("gmpp:client"));
        client.connect();

        TimeWorker timer = new TimeWorker();
        timer.addTimePoint("Gmpp communication");
        SimpleClientThread[] threads = new SimpleClientThread[THREAD_COUNT];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new SimpleClientThread(client);
            threads[i].setCount(COUNT);
            threads[i].setSleepTime(SLEEP_TIME);
            threads[i].setDaemon(false);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        timer.addTimePoint("Started");
        for (int i = 0; i < threads.length; i++) {
            threads[i].getThread().join();
        }
        timer.addTimePoint("Stopped");
        timer.printResult();
        client.close();
        server.stop();
        server.destroy();
    }

    public void onMessage(MessageMetadata metadata, Object obj)
    {
        if (obj instanceof MessagePacket) {
            MessagePacket mp = (MessagePacket) obj;
            mp.destroy();
        }
    }
}