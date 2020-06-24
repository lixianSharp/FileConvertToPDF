package com.fjminbao.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fjminbao.dto.ResponseDTO;
import com.fjminbao.task.service.AsyncDealWithPdfService;
import com.fjminbao.task.service.AsyncMergePdfService;
import com.fjminbao.util.MergePdfBySpirePDFUtils;
import com.fjminbao.util.MergePdfUtils;
import com.fjminbao.util.SplitPDFUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @ClassName MergePdfFilesController
 * @Description: TODO 合并多个PDF文件为一个PDF文件
 * @Author lxy
 * @Date 2020/6/18
 * @Version V1.0
 **/
@RestController
public class MergePdfFilesController {
    private static final Logger logger = Logger.getLogger("ConverFileToPDFController");


    @Autowired
    private AsyncMergePdfService asyncMergePdfService;

    @Autowired
    private AsyncDealWithPdfService asyncDealWithPdfService;

    @RequestMapping(value = "/mergePdfFiles",method = RequestMethod.POST)
    public ResponseDTO mergePdfFiles(HttpServletRequest request){
        ResponseDTO responseDTO = new ResponseDTO();


        String mergePdfFilePath = request.getParameter("mergePdfFilePath");
        mergePdfFilePath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+mergePdfFilePath;
        //这是JSON格式的List<String>字符串，需要将其从JSON数据转为List<String>数据
        String pdfFilePathNamesJSON = request.getParameter("pdfFilePathNames");

        logger.info("pdfFilePathNamesJSON="+pdfFilePathNamesJSON);
        List<String> list = JSON.parseArray(pdfFilePathNamesJSON, String.class);

        String[] pdfFilePathNames = new String[list.size()];
        for(int i=0;i<list.size();i++){
            String pdfFilePath = list.get(i);
            pdfFilePathNames[i] = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\"+pdfFilePath;
        }


        if (StringUtils.isEmpty(mergePdfFilePath) || StringUtils.isEmpty(pdfFilePathNames)) {
            responseDTO.setResultMsg("PARAMMETER_ERROR");
            return responseDTO;
        }
        try {
            //进行将多个PDF文件合并成一个PDF,用异步的方式
            asyncMergePdfService.mergePdfByPdfNames(mergePdfFilePath,pdfFilePathNames);
            responseDTO.setResultMsg("SUCCESS");
            responseDTO.setResultCode(1);
        } catch (Exception e) {
            logger.info("文件打开异常");
            e.printStackTrace();
        }

        return responseDTO;
    }


    /**
     * 处理文件的拆分与合并
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/splitPdfAndMergePdf")
    public ResponseDTO splitPdfAndMergePdf(HttpServletRequest request)throws Exception{
        ResponseDTO responseDTO = new ResponseDTO();
        asyncDealWithPdfService.dealWithPdfSplitAndMerge(request);

        responseDTO.setResultCode(1);
        responseDTO.setResultMsg("SUCCESS");
        return responseDTO;
    }


}
