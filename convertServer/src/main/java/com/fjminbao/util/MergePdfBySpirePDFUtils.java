package com.fjminbao.util;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @ClassName Merge2
 * @Description: TODO 合并PDF文档，不改变原PDF文件的字体
 * @Author lxy
 * @Date 2020/6/23
 * @Version V1.0
 **/
public class MergePdfBySpirePDFUtils {

    private static final Logger logger = Logger.getLogger("MergePdfBySpirePDFUtils");
    public static void main(String[] args) throws Exception{
        //合并后的PDF文件的名字
        String outputFile = "D:\\mergeFilesByStream.pdf";
        //待合并的3个PDF文件
        FileInputStream stream1 = new FileInputStream(new File("D:\\25.pdf"));
        FileInputStream stream2 = new FileInputStream(new File("D:\\26.pdf"));
        FileInputStream stream3 = new FileInputStream(new File("D:\\sample3.pdf"));
        //加载PDF示例文档
        InputStream[] streams = new FileInputStream[]{stream1, stream2,stream3};

        //合并PDF文档
        PdfDocumentBase doc = PdfDocument.mergeFiles(streams);

        //保存文档
        doc.save(outputFile);
        doc.close();
    }


    /**
     * 合并PDf文档
     * @param mergePdfFilePath
     * @param filesPath
     * @throws Exception
     */
    public static void merPdfBySpirePdfUtils(String mergePdfFilePath, List<String> filesPath) throws Exception {
        logger.info("进行合并PDf,mergePdfFilePath="+mergePdfFilePath+",filesPath="+filesPath);
        //加载PDF示例文档
        InputStream[] streams = new FileInputStream[filesPath.size()];
        for(int i=0;i<filesPath.size();i++){
            String filePath = filesPath.get(i);
            FileInputStream stream1 = new FileInputStream(new File(filePath));
            streams[i] = stream1;
        }
        //合并PDF文档
        PdfDocumentBase doc = PdfDocument.mergeFiles(streams);

        //保存文档
        doc.save(mergePdfFilePath);
        doc.close();
        logger.info("pdf合并完成");
    }


    public void example1() throws Exception{
        String outputFile = "output/mergeFilesByStream.pdf";
        FileInputStream stream1 = new FileInputStream(new File("sample1.pdf"));
        FileInputStream stream2 = new FileInputStream(new File("sample2.pdf"));
        FileInputStream stream3 = new FileInputStream(new File("sample3.pdf"));
        //加载PDF示例文档
        InputStream[] streams = new FileInputStream[]{stream1, stream2, stream3};

        //合并PDF文档
        PdfDocumentBase doc = PdfDocument.mergeFiles(streams);

        //保存文档
        doc.save(outputFile);
        doc.close();
    }
}
