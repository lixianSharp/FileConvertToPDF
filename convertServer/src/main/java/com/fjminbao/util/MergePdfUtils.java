package com.fjminbao.util;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @ClassName MergeMultiPDF
 * @Description: TODO 将多个PDF文件合并成一个PDF文件 的工具类.(合并出来的文档的顺序是按文件名数组元素顺序来的)
 * @Author lxy
 * @Date 2020/6/18
 * @Version V1.0
 **/
public class MergePdfUtils {
    private static final Logger logger = Logger.getLogger("MergePdfUtils");



    public static void main(String[] args) throws Exception {

        logger.info("开始合并....");
        String folder = "D:\\qrcode\\merge\\";
        String destinationFileName = "D:\\qrcode\\merge\\mergedTest.pdf";
//        String[] filesInFolder = getFiles(folder);

        String[] filePathNames = {"D:\\qrcode\\merge\\4.pdf","D:\\qrcode\\merge\\24.pdf","D:\\qrcode\\merge\\pingfang.pdf"};
        mergePdfByPdfNames(destinationFileName, filePathNames);
        logger.info("合并完成....");
        return;
    }

    /**
     * TODO 将多个PDF文件合并成一个PDF文件，并且不改变原格式样式
     * @param destinationFileName 合并后的PDF的文件全路径(例如: D:\qrcode\merge\mergedTest.pdf)
     * @param filePathNames  要合并的所有PDF的文件全路径(例如:String[] filePathNames = {"D:\\qrcode\\merge\\24.pdf","D:\\qrcode\\merge\\pingfang.pdf"};)
     * @throws IOException
     * @throws COSVisitorException
     */
    public static void mergePdfByPdfNames(String destinationFileName, String[] filePathNames) throws IOException, COSVisitorException {
        logger.info("开始将多个合并成一个pdf....");
        //pdf合并工具类
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        //合并某个目录下的所有PDF文件
        for(int i = 0; i < filePathNames.length; i++){
            //循环添加要合并的pdf存放的路径
            mergePdf.addSource(filePathNames[i]);
        }

        //设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(destinationFileName);
        //合并pdf
        mergePdf.mergeDocuments();
        logger.info("合并后的PDF文件名为="+destinationFileName);
    }

    /**
     * @param folder              要合并的所有PDF 所在的目录(全路径)(例如D:\qrcode\merge\)
     * @param destinationFileName 合并后的PDF的文件名(例如:mergedTest.pdf)
     * @throws IOException
     * @throws COSVisitorException
     */
    public static void mergeFolderPDF(String folder, String destinationFileName) throws IOException, COSVisitorException {
        logger.info("开始将多个合并成一个pdf....");
        String[] filesInFolder = getFiles(folder);

        //pdf合并工具类
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        //合并某个目录下的所有PDF文件
        for (int i = 0; i < filesInFolder.length; i++) {
            //循环添加要合并的pdf存放的路径
            mergePdf.addSource(folder + File.separator + filesInFolder[i]);
        }

        //设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(folder + File.separator + destinationFileName);
        //合并pdf
        mergePdf.mergeDocuments();
        logger.info("合并完成，合并后的PDF文件名为="+destinationFileName);
    }

    private static String[] getFiles(String folder) throws IOException {
        File _folder = new File(folder);
        String[] filesInFolder;

        if(_folder.isDirectory()){
            filesInFolder = _folder.list();
            return filesInFolder;
        } else {
            throw new IOException("Path is not a directory");
        }
    }



}
