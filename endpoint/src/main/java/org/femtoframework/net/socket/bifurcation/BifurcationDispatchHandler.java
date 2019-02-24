package org.femtoframework.net.socket.bifurcation;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.lang.Binary;
import org.femtoframework.net.socket.SocketHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 根据Bifurcation分发到不同处理器的装置
 *
 * @author fengyun
 * @version 1.00 2005-3-10 11:13:42
 */
public class BifurcationDispatchHandler
    implements SocketHandler
{
    private BifurcationSocketHandlerFactory factory;

    /**
     * 设置SocketHandler工厂
     *
     * @param factory Factory工厂
     */
    public void setSocketHandlerFactory(BifurcationSocketHandlerFactory factory)
    {
        this.factory = factory;
    }

    /**
     * 返回SocketHandlerFactory工厂
     *
     * @return
     */
    public BifurcationSocketHandlerFactory getSocketHandlerFactory()
    {
        return factory;
    }

    /**
     * 为了适应早期的Framework和Journal的端口MagicNumber
     * 和方便将来的扩展。对于第一字节是0X80的，
     * 继续读取三个字节，然后组装成一个Integer
     */
    public static final int ADAPTER = 0x80;

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        try {
            InputStream input = socket.getInputStream();
            int magic = input.read();
            if (magic == ADAPTER) {
                //为了将来的空间不够做的扩展
                byte[] bytes = new byte[4];
                bytes[0] = (byte)magic;
                bytes[1] = (byte)input.read();
                bytes[2] = (byte)input.read();
                bytes[3] = (byte)input.read();
                magic = Binary.toInt(bytes);
            }

            SocketHandler handler = factory.getHandler(magic);
            if (handler == null) {
                IOUtil.close(socket);
                return;
            }

            OutputStream output = socket.getOutputStream();
            if (magic < 0x81000000) {
                CodecUtil.writeInt(output, magic);
            }
            else {
                output.write(magic);
            }
            output.flush();

            handler.handle(socket);
        }
        catch (Throwable t) {
            //Ignore
        }
    }
}
