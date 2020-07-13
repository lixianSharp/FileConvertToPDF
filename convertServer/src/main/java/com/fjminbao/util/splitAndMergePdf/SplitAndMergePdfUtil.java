package com.fjminbao.util.splitAndMergePdf;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName SplitAndMergePdfUtil
 * @Description: TODO 文件的拆分与合并原始工具类
 * @Author lxy
 * @Date 2020/7/10
 * @Version V1.0 对PDF文件的拆分与合并，使用itextpdf-5.5.0.jar 和itext-asian.jar 5.2.0版本的
 **/
public class SplitAndMergePdfUtil {
//    public static void main(String args[])  throws IOException, DocumentException {
//        try {
//            //抽取页面
//
////            splitPDF("D:\\50.pdf", "D:\\output1.pdf",
////                    1,2);
////            splitPDF("D:\\桌面\\翻译任务\\IEC-61158-2-2003.pdf", "D:\\桌面\\翻译任务\\0-1.pdf",
////                    260,261);
//            //合并页面
//            String[] files = {"D:\\invoice.pdf","D:\\fapiao.pdf"};
//            String savepath = "D:\\mergePdf.pdf";
//            mergePdfFiles(Arrays.asList(files), savepath);
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * 合并原pdf为新文件
     *
     * @param files   pdf绝对路径集
     * @param newfile 新pdf绝对路径
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static void mergePdfFiles(List<String> files, String newfile) throws IOException, DocumentException {
        Document document = new Document(new PdfReader(files.get(0)).getPageSize(1));
        PdfCopy copy = new PdfCopy(document, new FileOutputStream(newfile));
        document.open();
        for (int i = 0; i < files.size(); i++) {
            PdfReader reader = new PdfReader(files.get(i));
            int n = reader.getNumberOfPages();
            for (int j = 1; j <= n; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
        }
        document.close();
    }

    /**
     * 拆分PDF
     * @param pdfFilePath
     * @param newFile
     * @param start 拆分的起始页，起始页是从1开始的，如果你要拆分源文件的 第1页，应该 start=1,end=1 如果你要缠粉源文件的第1、2页，应该 start=1,end=2
     * @param end 拆分结束页
     */
    public static void splitPDF(String pdfFilePath, String newFile, int start, int end) {
        Document document = null;
        PdfCopy copy = null;
        try {
            PdfReader reader = new PdfReader(pdfFilePath);
            //获取pdf页数
            int n = reader.getNumberOfPages();
            if (end == 0) {
                end = n;
            }
            document = new Document(reader.getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(newFile));
            document.open();
            for (int j = start; j <= end; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("split pdf file error:" + e.getMessage());
        }
    }

}
