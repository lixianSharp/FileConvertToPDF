package com.fjminbao.util;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfMargins;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @ClassName SplitPDF2
 * @Description: TODO 按指定页数范围拆分PDF。拆分PDF的工具类
 * @Author lxy
 * @Date 2020/6/23
 * @Version V1.0
 **/
public class SplitPDFUtils {

    private static final Logger logger = Logger.getLogger("SplitPDFUtils");


    public static void main(String[] args) {
        //加载需要拆分的PDF文档
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile("28.pdf");

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


    /**
     * 拆分PDF文件
     * @param listMap  要进行拆分的PDF文件
     * @return List<String> 返回拆分后的PDF文件存放的目录
     * @throws Exception
     */
    public static List<String> splitPdf(List<Map> listMap)throws Exception{
        logger.info("进行拆分PDf,listMap="+listMap);
        //拆分后的PDF文件存放的目录
        String splitSaveDir = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\splitPDFDir\\";
//        String splitSaveDir = "D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\splitPDFDir\\";;
        //保存拆分后的PDF文件的全路径
        List<String> filesPath = new ArrayList<>();
        for(int j=0;j<listMap.size();j++){
            Map map = listMap.get(j);
            long cutm = System.currentTimeMillis();
            //要打印的文件的路径
            String previewFilePath = (String)map.get("previewFilePath");
            //打印的份数
            String num = (String) map.get("num");
            if("1".equals(num)){
                //只拆分一份
                //打印的页码范围
                String pageRange = (String)map.get("pageRange");
                String[] split = pageRange.split(",");
                //打印起始页
                int start = Integer.parseInt(split[0]);
                //打印结束页
                int end = Integer.parseInt(split[1]);

                //
                //加载需要拆分的PDF文档
                PdfDocument doc = new PdfDocument();
                doc.loadFromFile("K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+previewFilePath);
//            doc.loadFromFile("D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+previewFilePath);
                //新建第1个PDF文档1
                PdfDocument newpdf1 = new PdfDocument();
                PdfPageBase page;

                //分割起始页
                int  spStart = start-1;
                //分割结束页
                int spEnd = end;
                //将原PDF文档的第1、2页拆分，并保存到newpdf1
                for (int i = spStart; i < spEnd; i++) {
                    page = newpdf1.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
                    doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
                }
                //将拆分后的PDF保存到指定目录(全路径)
                String saveSplitPdfFilePath = splitSaveDir+cutm+j+".pdf";
                newpdf1.saveToFile(saveSplitPdfFilePath);
                filesPath.add(saveSplitPdfFilePath);
            }else{
                //打印的份数如果超过两份，就拆分几次
                int printNum = Integer.parseInt(num);
                for(int k=0;k<printNum;k++){
                    //打印的页码范围
                    String pageRange = (String)map.get("pageRange");
                    String[] split = pageRange.split(",");
                    //打印起始页
                    int start = Integer.parseInt(split[0]);
                    //打印结束页
                    int end = Integer.parseInt(split[1]);

                    //
                    //加载需要拆分的PDF文档
                    PdfDocument doc = new PdfDocument();
                    doc.loadFromFile("K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+previewFilePath);
//            doc.loadFromFile("D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+previewFilePath);
                    //新建第1个PDF文档1
                    PdfDocument newpdf1 = new PdfDocument();
                    PdfPageBase page;

                    //分割起始页
                    int  spStart = start-1;
                    //分割结束页
                    int spEnd = end;
                    //将原PDF文档的第1、2页拆分，并保存到newpdf1
                    for (int i = spStart; i < spEnd; i++) {
                        page = newpdf1.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
                        doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
                    }
                    //将拆分后的PDF保存到指定目录(全路径)
                    String saveSplitPdfFilePath = splitSaveDir+cutm+j+k+".pdf";
                    newpdf1.saveToFile(saveSplitPdfFilePath);
                    filesPath.add(saveSplitPdfFilePath);
                }
            }

//            //打印的页码范围
//            String pageRange = (String)map.get("pageRange");
//            String[] split = pageRange.split(",");
//            //打印起始页
//            int start = Integer.parseInt(split[0]);
//            //打印结束页
//            int end = Integer.parseInt(split[1]);
//
//            //
//            //加载需要拆分的PDF文档
//            PdfDocument doc = new PdfDocument();
//            doc.loadFromFile("K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+previewFilePath);
////            doc.loadFromFile("D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+previewFilePath);
//            //新建第1个PDF文档1
//            PdfDocument newpdf1 = new PdfDocument();
//            PdfPageBase page;
//
//            //分割起始页
//            int  spStart = start-1;
//            //分割结束页
//            int spEnd = end;
//            //将原PDF文档的第1、2页拆分，并保存到newpdf1
//            for (int i = spStart; i < spEnd; i++) {
//                page = newpdf1.getPages().add(doc.getPages().get(i).getSize(), new PdfMargins(0));
//                doc.getPages().get(i).createTemplate().draw(page, new Point2D.Float(0, 0));
//            }
//            //将拆分后的PDF保存到指定目录(全路径)
//            String saveSplitPdfFilePath = splitSaveDir+cutm+j+".pdf";
//            newpdf1.saveToFile(saveSplitPdfFilePath);
//            filesPath.add(saveSplitPdfFilePath);
        }

        logger.info("pdf拆分完成，filesPath="+filesPath);
        return filesPath;
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
