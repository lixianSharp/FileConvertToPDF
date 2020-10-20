package com.fjminbao.task.service;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
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


   static {
       //初始化com的线程
       ComThread.InitSTA();
   }

    private static final int wdFormatPDF = 17;
    private static final int xlsFormatPDF = 0;
    private static final int pptFormatPDF = 32;

    public static AtomicInteger atomicInteger = new AtomicInteger(0);




    /***
     * Word(doc、docx)转PDF
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    @Async
    public  void word2PDF(String inputFile, String pdfFile) {
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

            String convertFileName = pdfFile.substring(pdfFile.lastIndexOf("\\")+1, pdfFile.length());
            logger.info("文件"+convertFileName+"转换完成!!");

            //关闭com的线程
            ComThread.Release();
//            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
//            return -1;
        }finally {
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
    public  void excel2PDF(String inputFile, String pdfFile) {
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
//            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
//            return -1;
        }finally {
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
    public  void ppt2PDF(String inputFile, String pdfFile) {
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

//            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
//            return -1;
        }finally {
            //关闭com的线程
            ComThread.Release();
        }
    }


}
