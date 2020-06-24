package com.fjminbao.util.spire;

import com.spire.pdf.*;
import com.spire.pdf.graphics.PdfMargins;

import java.awt.geom.Point2D;

/**
 * @ClassName SplitPDF2
 * @Description: TODO 按指定页数范围拆分PDF
 * @Author lxy
 * @Date 2020/6/23
 * @Version V1.0
 **/
public class SplitPDF2 {
    public static void main(String[] args) {
        //加载需要拆分的PDF文档
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile("test.pdf");

        //新建第1个PDF文档1
        PdfDocument newpdf1 = new PdfDocument();
        PdfPageBase page;

        //将原PDF文档的第1、2页拆分，并保存到newpdf1
        for (int i = 0; i < 2; i++) {
            page = newpdf1.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
            doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
        }
        newpdf1.saveToFile("split/result1.pdf");

        //新建第2个PDF文档
        PdfDocument newpdf2 = new PdfDocument();

        //将原PDF文档的第3、4页拆分，并保存到newpdf2
        for (int i = 2; i < 4; i++) {
            page = newpdf2.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
            doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
        }
        newpdf2.saveToFile("split/result2.pdf");
    }

    public void example()throws Exception{
        //加载需要拆分的PDF文档
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile("test.pdf");

        //新建第1个PDF文档1
        PdfDocument newpdf1 = new PdfDocument();
        PdfPageBase page;

        //将原PDF文档的第1、2页拆分，并保存到newpdf1
        for (int i = 0; i < 2; i++) {
            page = newpdf1.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
            doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
        }
        newpdf1.saveToFile("split/result1.pdf");

        //新建第2个PDF文档
        PdfDocument newpdf2 = new PdfDocument();

        //将原PDF文档的第3、4页拆分，并保存到newpdf2
        for (int i = 2; i < 4; i++) {
            page = newpdf2.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
            doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
        }
        newpdf2.saveToFile("split/result2.pdf");
    }
}
