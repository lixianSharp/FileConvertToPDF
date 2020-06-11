package com.fjminbao.util;

/**
 * @Author: xianyuanLi
 * @Date: created in 14:59 2019/12/19
 * Descrpition:
 */

import com.itextpdf.text.pdf.PdfReader;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * 读取pdf、ppt、pptx、xls、xlsx的页码
 */
public class CaculatPDFPagesUtil {


    private static final Logger logger = Logger.getLogger("GetFilePagesUtil");

    /**
     * 获取 pdf 、ppt 、pptx、 xls、 xlsx文件页数
     * @param path 文件全路基
     * @return  pdf ppt 、pptx、 xls、 xlsx文件总页数
     * @throws Exception
     */
    public static int getFilePages(String path){
        String extensionName = path.substring(path.lastIndexOf(".")+1,path.length()).toLowerCase();
        logger.info("后缀:+extensionName="+extensionName);
        if("ppt".equals(extensionName)){
            return getPptPage(path);
        }else if("pptx".equals(extensionName)){
            return getPptxPage(path);
        }else if("pdf".equals(extensionName)){
            return getPDFPage(path);
        }else if("xls".equals(extensionName)){
            return getXlsPage(path);
        }else if("xlsx".equals(extensionName)){
            return getXlsxPage(path);
        }
        return -1;
    }

    /** 这个没问题
     * 计算pdf格式文档的页数
     * @param filepath 文件路径
     * @return pagecount页数
     */
    public static int getPDFPage(String filepath){
        logger.info("准备获取pdf的页数.....");
        int pagecount = -1;
        PdfReader reader;
        try {
            reader = new PdfReader(filepath);
            pagecount= reader.getNumberOfPages();
            //释放资源
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("获取到pdf的页数为:"+pagecount+"页");
        return pagecount;
    }

    /**
     *
     * 计算Excel格式文档的页数 xlsx
     * @param filePath 文件路径
     * @return result 页数
     */
    public static int getXlsxPage(String filePath){
        logger.info("准备获取xlsx的页数......");
        int result = -1;
        try {
            InputStream myxls = new FileInputStream(filePath);
            XSSFWorkbook wb     = new XSSFWorkbook(myxls);
            result = wb.getNumberOfSheets() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("获取到xlsx的页数为:"+result+"页");
        return result ;
    }


    /**
     * 计算Excel格式文档的页数 xls
     * @param filePath 文件路径
     * @return result 页数
     */
    public static int getXlsPage(String filePath){
        logger.info("准备获取xls的页数.....");
        int result = -1;
        try {
            InputStream myxls = new FileInputStream(filePath);
            HSSFWorkbook wb     = new HSSFWorkbook(myxls);
            result = wb.getNumberOfSheets() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("获取到xls的页数为:"+result+"页");
        return result ;
    }


    /**
     * 计算PPT格式文档的页数  pptx
     * @param filePath 文件路径
     * @return pages 页数
     */
    public static int getPptxPage(String filePath)  {
        logger.info("准备获取pptx的页数");
        int pages = -1;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XMLSlideShow pptxfile = new XMLSlideShow(fis);
            pages = pptxfile.getSlides().size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("获取到pptx的页数为:"+pages+"页");
        return pages;
    }


    /**
     * 计算PPT格式文档的页数 ppt
     *
     * @param filePath 文件路径
     * @return pages 页数
     */
    public static int getPptPage(String filePath)  {
        logger.info("准备获取ppt的页数...");
        int pages = -1;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            HSLFSlideShow pptxfile = new HSLFSlideShow(fis);
            pages = pptxfile.getSlides().size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("获取到ppt的页数为:"+pages+"页");
        return pages;
    }


}