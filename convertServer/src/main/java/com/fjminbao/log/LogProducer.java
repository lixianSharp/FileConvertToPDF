package com.fjminbao.log;


import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;


/**
 * @author lxy
 * @Date 2020/7/2
 * @Descript  通过log4j-1.2.17 将日志发送到日志服务器中集中管理
 **/
public class LogProducer {
    /**
     * 远程Log4j服务器的地址
     */
    static  String remoteHost="119.3.150.136";
    /**
     * 远程Log4j服务器的访问端口
     */
    static  int port = 4712;
    static int ReconnectionDelay = 1000;
    static boolean locationInfo = true;

    public static Logger getRemoteLogClass(String clazzName){
        SocketAppender sa = new SocketAppender(remoteHost,port);
        sa.setThreshold(Level.INFO);
        sa.setReconnectionDelay(ReconnectionDelay);
        sa.setReconnectionDelay(ReconnectionDelay);
        sa.setLocationInfo(locationInfo);

        Logger remoteLogger = LogManager.getLogger(clazzName);
        remoteLogger.addAppender(sa);
        return remoteLogger;
    }


}
