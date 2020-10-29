package com.fjminbao.task.service;

import com.alibaba.fastjson.JSON;
import com.fjminbao.dao.FileConvertMapperDao;
import com.fjminbao.entity.FileConvertMsg;
import com.fjminbao.log.LogLevenCode;
import com.fjminbao.log.RemoteLogUtil;
import com.fjminbao.util.CaculatPDFPagesUtil;
import com.fjminbao.util.HttpUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * 进行异步处理文件转为PDF的service
 *
 * @Author: xianyuanLi
 * @Date: created in 14:57 2019/12/19
 * Descrpition:
 */
@Service
public class TestMainService {

    private static final Logger logger = Logger.getLogger("TestMain");
    @Autowired
    private FileConvertMapperDao fileConvertMapperDao;

    static {
        //初始化com的线程
        ComThread.InitSTA();
    }

    private static final int wdFormatPDF = 17;
    private static final int xlsFormatPDF = 0;
    private static final int pptFormatPDF = 32;

    public static AtomicInteger atomicInteger = new AtomicInteger(0);


    Lock lock = new ReentrantLock();
    @Async
    public void officeFileConvertPDF(String targetFileFullPath, String targetFileName , String pages,String minConvertServerName ,String fileConvertRequest ,String convertFileFullPath) {
//        String targetFileFullPath = request.getParameter("originFullPath");
//        String targetFileName = targetFileFullPath.substring(targetFileFullPath.lastIndexOf("\\") + 1, targetFileFullPath.length());
//        String pages = request.getParameter("pages");//
//        String minConvertServerName = request.getParameter("minConvertServerName");//文件转换的服务名
//        String fileConvertRequest = request.getParameter("fileConvertRequest");//文件转换请求任务
            lock.lock();
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("minConvertServerName", minConvertServerName);//转换服务名
                map.put("fileConvertRequest", fileConvertRequest);//文件转换请求
                map.put("pages", pages);

                // todo 1.查看该文件转换请求任务是否还未完成，如果未完成，则继续执行。若之前已经完成了。则不继续执行
                try {
                    String res = HttpUtil.post("http://localhost:8915/pullDataToRedis", map);
                    if (!res.contains("SUCCESS")) {
                        //之前就已经转换完成了,不进行后续操作，直接结束方法
                        return;
                    }
                } catch (Exception e) {

                }

//        String convertFileFullPath = request.getParameter("convertFullPath");
//        String convertFileName = convertFileFullPath.substring(convertFileFullPath.lastIndexOf("\\") + 1, convertFileFullPath.length());
                logger.info("文件" + targetFileName + "开始转换:......");
                logger.info("参数targetFileFullPath=" + targetFileFullPath + ",targetFileName=" + targetFileName + ",convertFileFullPath=" + convertFileFullPath);
                /////// todo 2.#########文件转换 start
                handleConverTask(targetFileFullPath, targetFileName, convertFileFullPath);

                ///////#########文件转换 end
                //todo 4.文件转换完成之后，往调度中心taskdispatch中发送一个通知，通知该文件转换请求已经转换完成了，让其去更新对应文件转换服务的积压总页数和删除对应的文件转换请求
                try {
                    HttpUtil.post("http://localhost:8915/notifyMsg", map);
                } catch (Exception e) {

                }
            }finally {
                lock.unlock();
            }


    }


    @Async
    public void handleConverTask(String targetFileFullPath, String targetFileName, String convertFileFullPath) {
        int pdfFilePages = -1;
        if (targetFileName.toLowerCase().contains("doc") || targetFileName.toLowerCase().contains("docx")) {
            //word转pdf
            pdfFilePages = word2PDF(targetFileFullPath, convertFileFullPath);
            logger.info("文件" + targetFileName + "转换完成:......");
            //关闭com的线程
            ComThread.Release();
        } else if (targetFileName.toLowerCase().contains("xls") || targetFileName.toLowerCase().contains("xlsx")) {
            //excel转pdf
            pdfFilePages = excel2PDF(targetFileFullPath, convertFileFullPath);
            logger.info("文件" + targetFileName + "转换完成:......");
            //关闭com的线程
            ComThread.Release();
        } else if (targetFileName.toLowerCase().toLowerCase().contains("ppt") || targetFileName.toLowerCase().contains("pptx")) {
            //ppt转pdf
            pdfFilePages = ppt2PDF(targetFileFullPath, convertFileFullPath);
            logger.info("文件" + targetFileName + "转换完成:......");
            //关闭com的线程
            ComThread.Release();
        } else if (targetFileName.toLowerCase().contains("pdf")) {
            pdfFilePages = CaculatPDFPagesUtil.getPDFPage(targetFileFullPath);
        }
        //转换任务完成后，往APIS中保存相关数据
        String convertStatus = "-1";
        if (pdfFilePages == -1) {
            //文件转换失败
            convertStatus = String.valueOf(-1);
        } else {
            convertStatus = "1";
        }
        //文件转换并获取该文件转成PDF的文件的总页数后，保存转换文件的信息
        handleFileConvertMsg(targetFileFullPath,
                convertFileFullPath, targetFileName, convertStatus, String.valueOf(pdfFilePages));
    }


    /**
     * 异步保存文件信息
     *
     * @param targetFileFullPath
     * @param convertFileFullPath
     * @param originFileName
     * @param convertStatus
     * @param pages
     */
    @Async
    public void handleFileConvertMsg(String targetFileFullPath,
                                     String convertFileFullPath, String originFileName, String convertStatus, String pages) {
        //目标文件存放路径
        String targetFilePath = targetFileFullPath;
        //转换后的文件存放地址
        String convertFilePath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\";

        //获取目标文件名(不带目录)
        String targetFileName = originFileName;
        //获取转换后的PDF文件名(不带目录)
        String convertFileName = convertFileFullPath.substring(convertFileFullPath.lastIndexOf("\\") + 1, convertFileFullPath.length());

        FileConvertMsg fileConvertMsg = new FileConvertMsg();
        //源文件名
        fileConvertMsg.setOriginFileName(originFileName);
        //源文件存储路径
        fileConvertMsg.setOriginFileSavePath("http://www.fjminbaoscp.com:8085/upload/");
        //目标文件名
        fileConvertMsg.setTargetFileName(targetFileName);
        //目标文件路径
        fileConvertMsg.setTargetFileSavePath(targetFilePath);
        //转换后的PDF文件名
        fileConvertMsg.setReserve1(convertFileName);
        //转换后的PDF存储路径
        fileConvertMsg.setReserve2(convertFilePath);
        //转换状态
        fileConvertMsg.setConvertStatus(convertStatus);
        //应用名
        fileConvertMsg.setSourceSystem("SS_WX");
        //总页码
        fileConvertMsg.setPages(pages);


        //往8088API服务发起一个请求，获取默认价格
        String url = "http://www.fjminbaoscp.com:8088/queryFilePriceByFileNameAndPages";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        //总页数
        paramMap.put("pages", pages);
        //文件名
        paramMap.put("fileName", convertFileName);
        String fileDefaultPrice = "0";
        try {
            logger.info("发送的参数为:" + paramMap);
            String result = HttpUtil.post(url, paramMap);
            logger.info("返回的数据:result=" + result);
            //TODO 解析json格式的result，将里面的默认价格解析出来
            Map<String, Object> data = JSON.parseObject(result, Map.class);
            fileDefaultPrice = (String) data.get("filePrice");
            logger.info("价格=" + fileDefaultPrice);
        } catch (Exception e) {
            RemoteLogUtil.writeRemoteLog("文件转PDF成功后,获取默认价格失败,请求参数paramMap=" + paramMap + ",converServer->APIS", RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
//            logger.info("获取默认价格失败");
        }
        //默认价格 黑白、单面、A4、1份的价格
        fileConvertMsg.setReserve3(fileDefaultPrice);

        //保存
        try {
            fileConvertMapperDao.addFileConvertMsg(fileConvertMsg);
        } catch (Exception e) {
            RemoteLogUtil.writeRemoteLog("文件转换信息保存失败,fileConvertMsg=" + fileConvertMsg + ",converServer->APIS", RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            e.printStackTrace();
        }
    }

    ////////////////////////////////以下是文件转换的方法///////////////////////////////

    /***
     * Word(doc、docx)转PDF
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    @Async
    public int word2PDF(String inputFile, String pdfFile) {
        try {
            //初始化com的线程
            ComThread.InitMTA();
            ActiveXComponent app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", inputFile, false, true)
                    .toDispatch();
            File tofile = new File(pdfFile);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF
            );
            Dispatch.call(doc, "Close");
            app.invoke("Quit");

            String convertFileName = pdfFile.substring(pdfFile.lastIndexOf("\\") + 1, pdfFile.length());
            logger.info("文件" + convertFileName + "转换完成!!");

            //关闭com的线程
            ComThread.Release();
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            return -1;
        } finally {
            //关闭com的线程
            ComThread.Release();
        }
    }

    /**
     * excel(xls、xlsx)转PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    @Async
    public int excel2PDF(String inputFile, String pdfFile) {
        try {
            //初始化com的线程
            ComThread.InitMTA();
            ActiveXComponent app = new ActiveXComponent("Excel.Application");
            app.setProperty("Visible", false);
            Dispatch excels = app.getProperty("Workbooks").toDispatch();
            Dispatch excel = Dispatch.call(excels, "Open", inputFile, false,
                    true).toDispatch();
            File tofile = new File(pdfFile);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(excel, "ExportAsFixedFormat", xlsFormatPDF, pdfFile);
            Dispatch.call(excel, "Close");
            app.invoke("Quit");
            //关闭com的线程
            ComThread.Release();
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            //关闭com的线程
            ComThread.Release();
        }

    }

    /**
     * ppt文件(pptx、ppt)转PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    @Async
    public int ppt2PDF(String inputFile, String pdfFile) {
        try {
            //初始化com的线程
            ComThread.InitMTA();
            ActiveXComponent app = new ActiveXComponent(
                    "PowerPoint.Application");
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            //倒数第一个参数：WithWindow指定文件是否可见  倒数第二个参数：Untitled指定文件是否有标题 倒数第三个参数：ReadOnly
            Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true,
                    true,
                    false
            ).toDispatch();
            File tofile = new File(pdfFile);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(ppt, "SaveAs", pdfFile, pptFormatPDF);
            Dispatch.call(ppt, "Close");
            app.invoke("Quit");
            //关闭com的线程
            ComThread.Release();

            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            return -1;
        } finally {
            //关闭com的线程
            ComThread.Release();
        }
    }

    @Async
    public void testServerIsDie() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("***测试文件转换服务于是否Die..,时间" + now + "***");
    }


}
