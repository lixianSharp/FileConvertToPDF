package com.fjminbao.util.spire;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @ClassName Merge2
 * @Description: TODO 合并PDF文档，不改变原PDF文件的字体
 * @Author lxy
 * @Date 2020/6/23
 * @Version V1.0
 **/
public class Merge2 {
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
