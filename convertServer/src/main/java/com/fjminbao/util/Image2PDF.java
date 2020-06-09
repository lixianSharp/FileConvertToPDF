package com.fjminbao.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * @Author: xianyuanLi
 * @Date: created in 14:30 2020/1/7
 * Descrpition:将原先横屏的图片 转成pdf后竖屏显示
 */
@Slf4j
public class Image2PDF {
    private static final Logger logger = Logger.getLogger("Image2PDF");


    /*** @param picturePath 图片地址*/
    private static void createPic(Document document, String picturePath) {
        try {
            Image image = Image.getInstance(picturePath);
//            float scalePercentage = (72 / 300f) * 100.0f;
//            image.scalePercent(scalePercentage, scalePercentage);
            float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float documentHeight = document.getPageSize().getHeight()-document.leftMargin() - document.rightMargin();;//documentWidth / 580 * 320;//重新设置宽高
            image.scaleAbsolute(documentWidth, documentHeight);//重新设置宽高

            //如果多个图片转为PDF的话，只需要将每个图片生成如下注释掉的4行就可以了，也就是再生成一个Image对象，每个图片对应一个Image对象，只需要将Image对象添加到document中就可以了。往document中添加多少个Image对象，生成的PDF中就会有多少张图片
//            Image image2 = Image.getInstance(picturePath);
//            float documentWidth2 = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
//            float documentHeight2 = document.getPageSize().getHeight()-document.leftMargin() - document.rightMargin();;//documentWidth / 580 * 320;//重新设置宽高
//            image2.scaleAbsolute(documentWidth2, documentHeight2);//重新设置宽高


            document.add(image);
//            document.add(image2);
        } catch (Exception ex) {
        }
    }
    public static void image2pdf(String text, String pdf) throws DocumentException, IOException {
        Document document = new Document();
        OutputStream os = new FileOutputStream(new File(pdf));
        PdfWriter.getInstance(document,os);
        document.open();
        createPic(document,text);
        document.close();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        //单张图片转PDF
        image2pdf("D:\\qrcode\\images\\33\\33_1.png","D:\\qrcode\\images\\33\\33.pdf");
    }



}