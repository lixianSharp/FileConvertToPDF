package com.fjminbao.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author: xianyuanLi
 * @Date: created in 1:02 2019/12/26
 * Descrpition:
 */
public class DownLoadFileUtil {

    /**
     * 从网络Url中下载文件
     * @param urlStr  源文件所咋IDE目录
     * @param fileName 源文件名
     * @param savePath  源文件要下载到本地的哪个目录(全路径)
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+ File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }


        System.out.println("info:"+url+" download success");

    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

//    public static void main(String[] args) {
//        try{
//            downLoadFromUrl("http://172.18.33.247:8081/files/5ad6a162b1727a35cc7dff9b",
//                    "test.png","/Users/zhuominjie/Desktop/1工作/4归一化平台/");
//        }catch (Exception e) {
//            // TODO: handle exception
//        }
//    }

    public static void main(String[] args) {
        try{
            downLoadFromUrl("http://www.fjminbaoscp.com:8085/upload/20191225/P/Ppdf20191225211918106.pdf",
                    "Ppdf20191225211918106.pdf","C:\\upload\\");
        }catch (Exception e) {
            // TODO: handle exception
        }
    }
}
