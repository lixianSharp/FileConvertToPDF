package com.fjminbao.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

import java.io.File;
import java.util.logging.Logger;

/**
 * @Author: xianyuanLi
 * @Date: created in 14:02 2019/12/19
 * Descrpition:将office文档(ppt pptx doc docx xls xlsx)转为pdf，要在jdk的bin目录下放jacob-1.19-x64.dll文件
 */
public class OfficeConvertPDF {

    private static final Logger logger = Logger.getLogger("FileToPDF");
    private static final int wdFormatPDF = 17;
    private static final int xlsFormatPDF = 0;
    private static final int pptFormatPDF = 32;
    private static final int msoTrue = -1;
    private static final int msofalse = 0;

    static {
        //初始化com的线程
        ComThread.InitSTA();
    }

    /**
     * 调用转PDF的方法
     * @param inputFile 传入文件路径
     * @param ouputFile 生成文件路径
     * @return
     */
//    public static boolean PdfManager(String inputFile,String ouputFile) {
//      //  boolean flag = TransferToPDFUtil.convert2PDF(inputFile, ouputFile);
//     //   return flag;
//        return false;
//    }


    /***
     * 判断文件类型
     *
     * @param fileName
     * @return
     */
    public static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    /***
     * 判断需要转化文件的类型（Excel、Word、ppt）
     *
     * @param inputFile  传入文件
     * @param pdfFile    转化文件
     */
    public static int convert2PDF(String inputFile, String pdfFile) {
        String suffix = getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            System.out.println("文件不存在！");
            return -1;
        }
        if ("pdf".equals(suffix)) {
            System.out.println("PDF not need to convert!");
            return -1;
        }
        if ("doc".equals(suffix) || "docx".equals(suffix)) {
            return word2PDF(inputFile, pdfFile);
        } else if ("ppt".equals(suffix) || "pptx".equals(suffix)) {
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
    public synchronized   static int word2PDF(String inputFile, String pdfFile) {
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
            Dispatch.call(doc, "Close", false);
            app.invoke("Quit", 0);
            //关闭com的线程
            ComThread.Release();

            //文件转换成功之后，获取PDF文件页码
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            return -1;
        }finally {
            //关闭com的线程
            ComThread.Release();
        }
    }

    /**
     * excel(xls、xlsx)转PDF
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    public synchronized static int excel2PDF(String inputFile, String pdfFile) {
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
            Dispatch.call(excel, "Close", false);
            app.invoke("Quit");
            //关闭com的线程
            ComThread.Release();
            //文件转换成功之后，获取文件总页码
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }finally {
            //关闭com的线程
            ComThread.Release();
        }

    }

    /**
     * ppt文件(pptx、ppt)转PDF
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    public  synchronized static int  ppt2PDF(String inputFile, String pdfFile) {
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
            //文件转换成功之后，获取文件总页码
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            //文件转换失败，返回-1
            return -1;
        }finally {
            //关闭com的线程
            ComThread.Release();
        }
    }


}
