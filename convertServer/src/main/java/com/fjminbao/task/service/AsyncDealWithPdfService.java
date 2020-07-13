package com.fjminbao.task.service;

import com.fjminbao.util.splitAndMergePdf.SplitAndMergePdfOpenUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @ClassName AsyncDealWithPdfService
 * @Description: TODO 进行一步处理的 文件的拆分 与 合并
 * @Author lxy
 * @Date 2020/6/23
 * @Version V1.0
 **/
@Service
public class AsyncDealWithPdfService {
    private static final Logger logger = Logger.getLogger("AsyncDealWithPdfService");


    @Async
    public void dealWithPdfSplitAndMerge(HttpServletRequest request,List<Map> listMap) throws Exception {
        logger.info("开始分割处理pdf....");
        //合并后的pdf文件的路径
        String mergePdfFilePath = request.getParameter("mergePdfFilePath");
        String mergePdfSavePath = "K:/apache-tomcat-8.5.38-8085-file/webapps/ROOT/upload/"+mergePdfFilePath;
//        String mergePdfSavePath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+mergePdfFilePath;
//        String mergePdfSavePath = "D:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+mergePdfFilePath;
        //从小程序端上送到后台，后台再传过来的要打印的所有PDF文件及其参数
//        String listMapJSON = request.getParameter("listMapJSON");
//        JSONArray jsonArray = JSON.parseArray(listMapJSON);
//        List<Map> listMap = jsonArray.toJavaList(Map.class);
//        for(Map map:listMap){
//            //要打印的文件的路径
//            String previewFilePath = (String)map.get("previewFilePath");
//            // 文件ID
//            Object fileId1 =  map.get("fileId");
//            //打印的页码范围
//            String pageRange2 = (String)map.get("pageRange");
//            // 文件名
//            String fileName = (String)map.get("fileName");
//            Object flag =  map.get("flag");
//            //个人用户号
//            String perInf_code = (String) map.get("perInf_Code");//10000206
//            //打印的份数
//            String num = (String) map.get("num");
//            //打印的颜色
//            String printColor1 = (String) map.get("printColor");
//            //单面或双面打印
//            String printOddEven = (String) map.get("printOddEven");
//            //个人用户号
//            String perUser_code = (String) map.get("PerUser_Code");//P1000300
//            //要打印的总页数
//            String pages = (String) map.get("pages");
//            //打印的格式
//            String printPaper = (String) map.get("printPaper");//A4 这里要转译一下
//            //价格
//            String price = (String) map.get("price");
//            //文件类型
//            String fileType = (String) map.get("fileType");
//
//        }

        // TODO 1、将要打印的文件按照指定页码进行拆分
        List<String> filesPath = SplitAndMergePdfOpenUtil.splitPdf(listMap);
        logger.info("PDf文件拆分之后，filesPath="+filesPath);
        // TODO 2、合并拆分后的PDF文件
        SplitAndMergePdfOpenUtil.merPdfBySpirePdfUtils(mergePdfSavePath,  filesPath);
        // TODO 3、删除拆分后的PDF文件
//        delPdfFile(filesPath);
    }

    public void delPdfFile(List<String> filesPath){
        for(String filePath:filesPath){
            File file= new File(filePath);
            file.delete();
        }

    }
}
