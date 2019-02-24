package org.femtoframework.net.comm.packet;

import org.femtoframework.util.thread.ExecutorUtil;
import org.femtoframework.util.thread.ScheduleService;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Future;
/**
 * 自动连接器
 *
 * @author fengyun
 * @version 1.00 2005-5-5 16:56:56
 */
public class AutoConnector
{
    /**
     * 客户端到Future的映射
     */
    private Map<PacketCommClient, Future> clients = new HashMap<PacketCommClient, Future>();

    private ScheduleService scheduler = ExecutorUtil.newSingleThreadScheduler();

    AutoConnector()
    {
        scheduler.start();
    }

    /**
     * 是否有相应的客户端
     *
     * @param client 客户端
     * @return 是否有相应的客户端
     */
    public boolean hasClient(PacketCommClient client)
    {
        return clients.containsKey(client);
    }

    /**
     * 添加通讯客户端
     *
     * @param client 客户端
     */
    public void addClient(PacketCommClient client)
    {
        Future future = clients.get(client);
        if (future != null) {
            future.cancel(false);
        }
        future = scheduler.scheduleAtFixedRate(client, 100, client.getConnectPeriod());
        clients.put(client, future);
    }

    /**
     * 删除通讯客户端
     */
    public boolean removeClient(PacketCommClient client)
    {
        Future future = clients.remove(client);
        if (future != null) {
            future.cancel(false);
            return true;
        }
        return false;
    }
}
