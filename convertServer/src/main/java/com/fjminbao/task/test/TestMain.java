package com.fjminbao.task.test;

import com.alibaba.fastjson.JSON;
import com.fjminbao.dao.FileConvertMapperDao;
import com.fjminbao.entity.FileConvertMsg;
import com.fjminbao.log.LogLevenCode;
import com.fjminbao.log.RemoteLogUtil;
import com.fjminbao.util.CaculatPDFPagesUtil;
import com.fjminbao.util.HttpUtil;
import com.fjminbao.util.OfficeConvertPDF;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * 进行异步处理文件转为PDF的service
 *
 * @Author: xianyuanLi
 * @Date: created in 14:57 2019/12/19
 * Descrpition:
 */
public class TestMain {

    private static final Logger logger = Logger.getLogger("AsyncService");



    private static final int wdFormatPDF = 17;
    private static final int xlsFormatPDF = 0;
    private static final int pptFormatPDF = 32;

    public static AtomicInteger atomicInteger = new AtomicInteger(0);



    /**
     * 告诉Spring，这是一个异步方法(非阻塞的)。每调用一次，Spring都会使用一个线程去执行，线程由Spring的线程池维护。
     * @param targetFileFullPath
     * @param convertFileFullPath
     * @param originFileName
     *
     *         String convertFileFullPath = convertFilePath  + originFileName.substring(0,originFileName.lastIndexOf(".")+1)+"pdf";
     *         String convertFilePath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\"+currentTime+"\\P\\";
     *                 String targetFilePath =  "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+fileName;
     *                  //获取文件名
     *         String originFileName = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());;
     */
    public void handleFileConvert2Pdf( String targetFileFullPath, String convertFileFullPath, String originFileName) {
        try {
            int count = atomicInteger.getAndIncrement();
            logger.info("文件转换任务处理中....." + count);
            RemoteLogUtil.writeRemoteLog("开始处理Office文件转为PDF,参数信息originFileName"+originFileName+",targetFileFullPath="+targetFileFullPath+",convertFileFullPath"+convertFileFullPath+",fileserver->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.INFO);
            convertToPdf(targetFileFullPath, convertFileFullPath, originFileName);
            logger.info("文件转换任务处理完成" + count);
        } catch (Exception e) {
            logger.info("文件" + originFileName + "转换失败");
            RemoteLogUtil.writeRemoteLog("Office文件转PDF失败,参数信息originFileName"+originFileName+",targetFileFullPath="+targetFileFullPath+",convertFileFullPath"+convertFileFullPath+",fileserver->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String targetFileFullPath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\20200619\\P\\1.docx";
        String convertFileFullPath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\20200619\\P\\1.pdf";
        word2PDF(targetFileFullPath, convertFileFullPath);

    }

    /**
     * Office文档转为 PDF
     * @param targetFileFullPath
     * @param convertFileFullPath
     * @param originFileName
     */
    public void convertToPdf(String targetFileFullPath, String convertFileFullPath, String originFileName) {
        String convertStatus = "0";
        int pages = -1;
        logger.info("目标文件全路径=>fileFullPath=" + targetFileFullPath);
        //获取目标文件扩展名
        String extensionName = originFileName.substring(originFileName.lastIndexOf(".") + 1, originFileName.length());
        logger.info("源文件扩展名extensionName=" + extensionName);

        //进行文件转换,将目标文件转换成PDF文件
        if ("xls".equals(extensionName) || "xlsx".equals(extensionName)) {
            //文件转换失败返回-1，文件转换成功返回对应的总页码数
            pages =  OfficeConvertPDF.excel2PDF(targetFileFullPath,convertFileFullPath);
        } else if ("doc".equals(extensionName) || "docx".equals(extensionName)) {
            pages = word2PDF(targetFileFullPath, convertFileFullPath);
        } else if ("ppt".equals(extensionName) || "pptx".equals(extensionName)) {
            pages =  OfficeConvertPDF.ppt2PDF(targetFileFullPath,convertFileFullPath);
        }else if("pdf".equals(extensionName)){
            pages = CaculatPDFPagesUtil.getFilePages(targetFileFullPath);
        }

        logger.info("转换后文件路径=>convertFileFullPath=" + convertFileFullPath);
        if (pages == -1) {
            //文件转换失败
            convertStatus = String.valueOf(-1);
        } else {
            convertStatus = "1";
        }

    }


    /**
     * 获取文件后缀名
     * @param fileName
     * @return
     */
    public String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    /***
     * 判断需要转化文件的类型（Excel、Word、ppt）
     *
     * @param inputFile  传入文件
     * @param pdfFile    转化文件
     */
    public int convert2PDF(String inputFile, String pdfFile) {
        String suffix = getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            System.out.println("文件不存在！");
            return -1;
        }
        if ("pdf".equals(suffix)) {
            System.out.println("PDF not need to convert!");
            return -1;
            //return true;
        }
        if ("doc".equals(suffix) || "docx".equals(suffix)) {
            return word2PDF(inputFile, pdfFile);
        } else if ("ppt".equals(suffix)|| "pptx".equals(suffix)) {
            return ppt2PDF(inputFile, pdfFile);
        } else if ("xls".equals(suffix) || "xlsx".equals(suffix)) {
            return excel2PDF(inputFile, pdfFile);
        } else {
            System.out.println("文件格式不支持转换!");
            return -1;
        }
    }

    /***
     * Word(doc、docx)转PDF
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    public static int word2PDF(String inputFile, String pdfFile) {
        try {
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
            Dispatch.call(doc, "Close", false);
            app.invoke("Quit", 0);
            File fromfile = new File(inputFile);
            if (fromfile.exists()) {
                fromfile.delete();
            }

            String convertFileName = pdfFile.substring(pdfFile.lastIndexOf("\\")+1, pdfFile.length());
            logger.info("文件"+convertFileName+"转换完成!!");

            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * excel(xls、xlsx)转PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    public int excel2PDF(String inputFile, String pdfFile) {
        try {
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
            Dispatch.call(excel, "Close", false);
            app.invoke("Quit");
            File fromfile = new File(inputFile);
            if (fromfile.exists()) {
                fromfile.delete();
            }
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * ppt文件(pptx、ppt)转PDF
     *
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    public int ppt2PDF(String inputFile, String pdfFile) {
        try {
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
            File fromfile = new File(inputFile);
            if (fromfile.exists()) {
                fromfile.delete();
            }

            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


}
