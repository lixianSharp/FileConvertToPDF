package com.fjminbao.log;

/**
 * @author lxy
 * @Date 2020/7/2
 * @Descript
 **/
public class RemoteLogUtil {

    static org.apache.log4j.Logger remoteLogge = LogProducer.getRemoteLogClass("RemoteLogUtil");
    public static void main(String[] args) {
//        org.apache.log4j.Logger remoteLogge = LogProducer.getRemoteLogClass("TTGXQ");
//        RemoteLogUtil remoteLogUtil = new RemoteLogUtil();
//        for(int i=0;i<=2;i++){
////            remoteLogge.trace("这是trace");
////            remoteLogge.debug("这是debug");
//            remoteLogUtil.writeRemoteLog("这是info",15,"RemoteLogUtil","info");
//            remoteLogUtil.writeRemoteLog("这是info",16,"RemoteLogUtil","warn");
//            remoteLogUtil.writeRemoteLog("这是info",17,"RemoteLogUtil","error");
//        }
//        RemoteLogUtil.writeRemoteLog("这是PrintServer,info",15,"RemoteLogUtil","info");
//        RemoteLogUtil.writeRemoteLog("这是info",16,"RemoteLogUtil","warn");
//        RemoteLogUtil.writeRemoteLog("这是info",17,"RemoteLogUtil","error");
        String s = getClassEtcMsg(new Throwable().getStackTrace()[0]);
        System.out.println(s);
    }

    /**
     * 获取当前类名、方法名、代码所在行号
     * @param stackTraceElement
     * @return
     */
    public static String getClassEtcMsg(StackTraceElement stackTraceElement) {
//        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        return stackTraceElement.getFileName() +" "+ stackTraceElement.getMethodName()+ ":Line " + stackTraceElement.getLineNumber();
    }


    /**
     *
     * @param logMsg  日志信息
     * @param clazzMsg  当前类信息(类名 方法名 代码所在行数)
     * @param logLeven 日志级别
     */
    public static   void writeRemoteLog(String logMsg,String clazzMsg,String logLeven){
        //类名，代码所在行数，
        switch (logLeven){
            case "INFO":
                remoteLogge.info("[INFO-APIS] "+clazzMsg+" "+logMsg);
                break;
            case "WARN":
                remoteLogge.warn("[WARN-APIS] "+clazzMsg+" "+logMsg);
                break;
            case "ERROR":
                remoteLogge.error("[ERROR-APIS] "+clazzMsg+" "+logMsg);
                break;
            default:
                break;
        }

    }

}
