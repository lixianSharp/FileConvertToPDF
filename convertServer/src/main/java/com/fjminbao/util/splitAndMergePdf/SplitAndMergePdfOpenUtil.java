package com.fjminbao.util.splitAndMergePdf;

import com.fjminbao.log.LogLevenCode;
import com.fjminbao.log.RemoteLogUtil;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @ClassName SplitAndMergePdfOpenUtil
 * @Description: TODO 用于对外使用的文件的拆分与合并的工具类
 * @Author lxy
 * @Date 2020/7/10
 * @Version V1.0
 **/
public class SplitAndMergePdfOpenUtil {

    private static final Logger logger = Logger.getLogger("SplitAndMergePdfOpenUtil");


    /**
     * 拆分PDF文件
     * @param listMap  要进行拆分的PDF文件
     * @return List<String> 返回拆分后的PDF文件存放的目录
     * @throws Exception
     */
    public static List<String> splitPdf(List<Map> listMap)throws Exception{
        logger.info("进行拆分PDf,listMap="+listMap);
        //拆分后的PDF文件存放的目录
        String splitSaveDir = "K:/apache-tomcat-8.5.38-8085-file/webapps/ROOT/upload/splitPDFDir/";
//        String splitSaveDir = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\splitPDFDir\\";

        String spFilePath = "K:/apache-tomcat-8.5.38-8085-file/webapps/ROOT/upload/";
        //保存拆分后的PDF文件的全路径
        List<String> filesPath = new ArrayList<>();
        try {
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

                    //要拆分的PDf文档的全路径
                    String previewFileAllPath = spFilePath+previewFilePath;
                    logger.info("要拆分的pdf文档的全路径previewFileAllPath="+previewFileAllPath);

                    //将拆分后的PDF保存到指定目录(全路径)
                    String saveSplitPdfFilePath = splitSaveDir+cutm+j+".pdf";

                    SplitAndMergePdfUtil.splitPDF(previewFileAllPath, saveSplitPdfFilePath,start,end);

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

                        //要拆分的PDf文档的全路径
                        String previewFileAllPath = spFilePath+previewFilePath;
                        logger.info("要拆分的pdf文档的全路径previewFileAllPath="+previewFileAllPath);

                        //将拆分后的PDF保存到指定目录(全路径)
                        String saveSplitPdfFilePath = splitSaveDir+cutm+j+".pdf";

                        SplitAndMergePdfUtil.splitPDF(previewFileAllPath, saveSplitPdfFilePath,start,end);

                        filesPath.add(saveSplitPdfFilePath);
                    }
                }
            }
        } catch (NumberFormatException e) {
            RemoteLogUtil.writeRemoteLog("PDF文件拆分失败,参数listMap="+listMap+",APIS->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            e.printStackTrace();
        }

        logger.info("pdf拆分完成，filesPath="+filesPath);
        return filesPath;
    }



    /**
     * 合并PDf文档
     * @param mergePdfFilePath
     * @param filesPath
     * @throws Exception
     */
    public static void merPdfBySpirePdfUtils(String mergePdfFilePath, List<String> filesPath) throws Exception {
        logger.info("进行合并PDf,mergePdfFilePath="+mergePdfFilePath+",filesPath="+filesPath);

        try {
            SplitAndMergePdfUtil.mergePdfFiles(filesPath,mergePdfFilePath);
        } catch (IOException e) {
            RemoteLogUtil.writeRemoteLog("PDF合并失败,合并参数mergePdfFilePath="+mergePdfFilePath+",filesPath"+filesPath+",APIS->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            e.printStackTrace();
        } catch (DocumentException e) {
            RemoteLogUtil.writeRemoteLog("PDF合并失败,合并参数mergePdfFilePath="+mergePdfFilePath+",filesPath"+filesPath+",APIS->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            e.printStackTrace();
        }

    }

}
