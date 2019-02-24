package org.femtoframework.net.comm.packet;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

import org.femtoframework.net.comm.CommException;
import org.femtoframework.pattern.Loggable;
import org.femtoframework.util.thread.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comm错误处理器
 *
 * @author fengyun
 * @version 1.00 2005-5-5 16:47:43
 */
public class CommErrorHandler implements ErrorHandler, Loggable
{

    private static final CommErrorHandler instance = new CommErrorHandler();

    /**
     * 构造
     */
    private CommErrorHandler()
    {
    }

    /**
     * 返回实例
     */
    public static ErrorHandler getInstance()
    {
        return instance;
    }

    private Logger logger = LoggerFactory.getLogger(CommErrorHandler.class);

    /**
     * 处理异常
     *
     * @param e 通讯异常
     * @return 是否继续任务 （返回 int 还是 boolean ？？)
     */
    public boolean handleException(Exception e)
    {
        if (e instanceof SocketException) {
            return true;
        }
        else if (e instanceof EOFException) {
            return true;
        }
        else if (e instanceof CommException) {
            CommException ce = (CommException) e;
            return handleException((Exception) ce.getCause());
        }
        else if (e instanceof IOException) {
            String message = e.getMessage();
            return message != null && message.startsWith("Socket closed");
        }
        else if (e instanceof RuntimeException) {
            logger.warn("RuntimeException: " + e.getMessage(), e);
            return true;
        }
        else if (logger.isDebugEnabled() && e != null) {
            logger.debug("Exception: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 处理错误
     *
     * @param t 错误
     * @return 是否继续任务
     */
    public boolean handleError(Throwable t)
    {
        logger.error("Error", t);
        return true;
    }

    /**
     * 设置日志处理器
     *
     * @param logger 日志处理器
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * 返回日志处理器
     *
     * @return 日志处理器
     */
    public Logger getLogger()
    {
        return logger;
    }
}
