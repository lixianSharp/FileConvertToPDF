package com.fjminbao.util.spire;

import com.spire.pdf.PdfDocument;

/**
 * @ClassName SplitPDF1
 * @Description: TODO  按每一页单独拆分
 * @Author lxy
 * @Date 2020/6/23
 * @Version V1.0
 **/
public class SplitPDF1 {
    public static void main(String[] args) {
        //加载需要拆分的PDF文档
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile("26.pdf");

        //调用方法split()将PDF文档按每一页拆分为单独的文档 (下面这行代码的意思是:将26.pdf的第1页拆分成一个pdf文件，拆分后的pdf文件名为splitDocument-0.pdf，拆分后的pdf文件内容就是26.pdf的第一页的内容)
        doc.split("D://splitDocument-{0}.pdf", 0);//将26.pdf的第1页单独拆分成一个名字为splitDocument-0.pdf的pdf文件。对源文件26.pdf没影响.(splitDocument-0.pdf是保存在D盘中)
        doc.close();
    }

    public void example1() throws Exception{
        //加载需要拆分的PDF文档
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile("test.pdf");

        //调用方法split()将PDF文档按每一页拆分为单独的文档
        doc.split("output/splitDocument-{0}.pdf", 0);
        doc.close();
    }
}
