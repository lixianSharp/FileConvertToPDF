package com.fjminbao.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fjminbao.dto.ResponseDTO;
import com.fjminbao.log.LogLevenCode;
import com.fjminbao.log.RemoteLogUtil;
import com.fjminbao.task.service.AsyncDealWithPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    private AsyncDealWithPdfService asyncDealWithPdfService;

    /**
     * 处理PDF文件的拆分与合并
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/splitPdfAndMergePdf")
    public ResponseDTO splitPdfAndMergePdf(HttpServletRequest request)throws Exception{
        ResponseDTO responseDTO = new ResponseDTO();
        // listMapJSON的值是一个 List<Map>格式的JSON数组,例如：[{"pageRange":"1,6","fileName":"支付宝和微信的转账详情.docx","flag":true,"previewFilePath":"convertToPdfDir/20200710/P/Pdocx20200710181407591.pdf","perInf_Code":"10000100","num":"1","printColor":"1","printOddEven":"1","PerUser_Code":"P1000194","pages":"6","printPaper":"A4","price":"0.06","pageNumStar":"1","fileType":"/static/img/word.png","fileId":5605,"pageNumEnd":"6"},{"pageRange":"1,50","fileName":"1.pdf","flag":true,"previewFilePath":"20200710/P/Ppdf20200710181301755.pdf","perInf_Code":"10000100","num":"1","printColor":"1","printOddEven":"1","PerUser_Code":"P1000194","pages":"50","printPaper":"A4","price":"0.5","pageNumStar":"1","fileType":"/static/img/pdf.png","fileId":5604,"pageNumEnd":"50"}]
        String listMapJSON = request.getParameter("listMapJSON");
        String mergePdfFilePath = request.getParameter("mergePdfFilePath");
        logger.info("listMapJSON="+listMapJSON);
        logger.info("mergePdfFilePath="+mergePdfFilePath);
        if(StringUtils.isEmpty(listMapJSON)){
            responseDTO.setResultMsg("listMapJSON IS NULL");
            responseDTO.setResultCode(-1);
            RemoteLogUtil.writeRemoteLog("PDF拆分与合并中,请求参数:listMapJSON IS NULL,APIS->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            return responseDTO;
        }
        if(StringUtils.isEmpty(mergePdfFilePath)){
            responseDTO.setResultMsg("mergePdfFilePath IS NULL");
            responseDTO.setResultCode(-1);
            RemoteLogUtil.writeRemoteLog("PDF拆分与合并中,请求参数:mergePdfFilePath IS NULL,APIS->converServer",RemoteLogUtil.getClassEtcMsg(new Throwable().getStackTrace()[0]), LogLevenCode.ERROR);
            return responseDTO;
        }
        JSONArray jsonArray = JSON.parseArray(listMapJSON);
        List<Map> listMap = jsonArray.toJavaList(Map.class);
        asyncDealWithPdfService.dealWithPdfSplitAndMerge(request,listMap);

        responseDTO.setResultCode(1);
        responseDTO.setResultMsg("SUCCESS");
        return responseDTO;
    }


}
