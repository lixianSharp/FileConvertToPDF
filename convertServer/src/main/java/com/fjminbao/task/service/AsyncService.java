package com.fjminbao.task.service;

import com.alibaba.fastjson.JSON;
import com.fjminbao.dao.FileConvertMapperDao;
import com.fjminbao.dto.ResponseDTO;
import com.fjminbao.entity.FileConvertMsg;
import com.fjminbao.util.CaculatPDFPagesUtil;
import com.fjminbao.util.HttpUtil;
import com.fjminbao.util.OfficeConvertPDF;
import com.google.gson.JsonParser;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 进行异步处理的service
 *
 * @Author: xianyuanLi
 * @Date: created in 14:57 2019/12/19
 * Descrpition:
 */
@Service
public class AsyncService {

    private static final Logger logger = Logger.getLogger("AsyncService");


    @Autowired
    private FileConvertMapperDao fileConvertMapperDao;

    private static final int wdFormatPDF = 17;
    private static final int xlsFormatPDF = 0;
    private static final int pptFormatPDF = 32;

    private static int count = 0;

    //告诉Spring，这是一个异步方法(非阻塞的)。每调用一次，Spring都会使用一个线程去执行，线程由Spring的线程池维护。
    @Async
    public void handleFileConvert2Pdf( String targetFileFullPath, String convertFileFullPath, String originFileName) {
        try {
            logger.info("文件转换任务处理中....." + (++count));
            convertToPdf(targetFileFullPath, convertFileFullPath, originFileName);
            logger.info("文件转换任务处理完成" + (count));
        } catch (Exception e) {
            logger.info("文件" + originFileName + "转换失败");
            e.printStackTrace();
        }
    }

    @Async
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
        //文件转换并获取该文件转成PDF的文件的总页数后，保存转换文件的信息
        handleFileConvertMsg(targetFileFullPath,
                convertFileFullPath, originFileName, convertStatus, String.valueOf(pages));

    }


    //异步保存文件信息
    @Async
    public void handleFileConvertMsg(String targetFileFullPath,
                                     String convertFileFullPath, String originFileName,String  convertStatus, String pages) {
        //目标文件存放路径
        String targetFilePath = targetFileFullPath;//"C:\\upload\\";//configProperties.getTargetFilePath();
        //转换后的文件存放地址
        String convertFilePath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\convertToPdfDir\\";//configProperties.getConvertFilePath();

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
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("pages",pages);//总页数
        paramMap.put("fileName",convertFileName);//文件名
        String fileDefaultPrice = "0";
        try {
            String result = HttpUtil.post(url, paramMap);
            logger.info("返回的数据:result="+result);
            //TODO 解析json格式的result，将里面的默认价格解析出来
            Map<String, Object> data = JSON.parseObject(result, Map.class);
            fileDefaultPrice = (String)data.get("filePrice");
            logger.info("价格="+fileDefaultPrice);
        } catch (Exception e) {
            logger.info("获取默认价格失败");
            e.printStackTrace();
        }
        //默认价格 黑白、单面、A4、1份的价格
        fileConvertMsg.setReserve3(fileDefaultPrice);

        //保存
        fileConvertMapperDao.addFileConvertMsg(fileConvertMsg);
    }

//    public static void main(String[] args) {
//        String url = "http://localhost:8088/queryFilePriceByFileNameAndPages";
//        Map<String,Object> paramMap = new HashMap<String,Object>();
//        paramMap.put("pages",3);//总页数
//        paramMap.put("fileName","Ppdf20200609173432024.pdf");//文件名
//        String fileDefaultPrice = "0";
//        try {
//            String result = HttpUtil.post(url, paramMap);
//            logger.info("返回的数据:result="+result);
//            //TODO 解析json格式的result，将里面的默认价格解析出来
//            Map<String, Object> data = JSON.parseObject(result, Map.class);
//            String filePrice = (String)data.get("filePrice");
//            logger.info("价格="+filePrice);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /////////////////////////////////////////////////////////////////////////////////
    ////////////////【【【【【【【【【【以下是文件转换代码】】】】】】】】】】】】】】】】】
    /////////////////////////////////////////////////////////////////////////////////
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
            //return false;
        }
        if (suffix.equals("pdf")) {
            System.out.println("PDF not need to convert!");
            return -1;
            //return true;
        }
        if (suffix.equals("doc") || suffix.equals("docx")) {
            return word2PDF(inputFile, pdfFile);
        } else if (suffix.equals("ppt") || suffix.equals("pptx")) {
            return ppt2PDF(inputFile, pdfFile);
        } else if (suffix.equals("xls") || suffix.equals("xlsx")) {
            return excel2PDF(inputFile, pdfFile);
        } else {
            System.out.println("文件格式不支持转换!");
            return -1;
            //return false;
        }
    }

    /***
     * Word(doc、docx)转PDF
     * @param inputFile
     * @param pdfFile
     * @return 转换成功返回对应的页码数。转换失败返回-1
     */
    public int word2PDF(String inputFile, String pdfFile) {
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

            //释放资源
//            Dispatch.call(docs, "Close", false);
            //文件转换成功之后，获取PDF文件页码
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
            //  return true;
        } catch (Exception e) {
            return -1;
            // return false;
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

            //释放资源
//            Dispatch.call(excels, "Close", false);
            //文件转换成功之后，获取文件总页码
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
            //return true;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
            //  return false;
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
            ActiveXComponent app = new ActiveXComponent(
                    "PowerPoint.Application");
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            Dispatch ppt = Dispatch.call(ppts, "Open", inputFile, true,// ReadOnly
                    true,// Untitled指定文件是否有标题
                    false// WithWindow指定文件是否可见
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

            //释放资源
//            Dispatch.call(ppts, "Close");

            //文件转换成功之后，获取文件总页码
            return CaculatPDFPagesUtil.getPDFPage(pdfFile);
            //  return true;
        } catch (Exception e) {
            e.printStackTrace();
            //文件转换失败，返回-1
            return -1;
            // return false;
        }
    }


}
