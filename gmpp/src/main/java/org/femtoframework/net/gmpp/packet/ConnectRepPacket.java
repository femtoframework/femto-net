package org.femtoframework.net.gmpp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.net.gmpp.GmppConstants;

/**
 * 连接响应
 *
 * @author fengyun
 * @version 1.00 2004-7-1 20:50:39
 */
public class ConnectRepPacket extends ConnectPacket
{
    private int status = GmppConstants.SC_OK;

    public ConnectRepPacket()
    {
        super(GmppConstants.PACKET_CONNECT_REP, nextId());
    }

    public ConnectRepPacket(int id)
    {
        super(GmppConstants.PACKET_CONNECT_REP, id);
    }

    /**
     * 返回连接状态
     *
     * @return
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * 设置连接状态
     *
     * @param status 状态
     */
    public void setStatus(int status)
    {
        this.status = status;
    }

    /**
     * 状态是否正确
     */
    public boolean isStatusOK()
    {
        return status == GmppConstants.SC_OK;
    }

    /**
     * 输出
     *
     * @param pos 包输出流
     */
    public void writePacket(OutputStream pos)
        throws IOException
    {
        CodecUtil.writeUnsignedShort(pos, status);
        if (isStatusOK()) {
            super.writePacket(pos);
        }
    }

    /**
     * 输入
     *
     * @param pis 包输入流
     */
    public void readPacket(InputStream pis)
        throws IOException
    {
        status = CodecUtil.readUnsignedShort(pis);
        if (isStatusOK()) {
            super.readPacket(pis);
        }
    }
}
