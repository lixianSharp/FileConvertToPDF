package com.fjminbao.controller;

import com.fjminbao.dto.ResponseDTO;
import com.fjminbao.task.service.TestMainService;
import com.jacob.com.ComThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @ClassName TestController
 * @Description: TODO
 * @Author lxy
 * @Date 2020/9/21
 * @Version V1.0
 **/
@RestController
public class TestController {

    private static final Logger logger = Logger.getLogger("TestController");

    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    private TestMainService testMainService;

    @RequestMapping(value = "/testOfficeFileConvertPDF", method = RequestMethod.POST)
    public ResponseDTO testOfficeFileConvertPDF(HttpServletRequest request) {
        int count = atomicInteger.incrementAndGet();
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setResultCode(1);
        responseDTO.setResultMsg("success");
        logger.info("第" + count + "个文件开始转换");
        String targetFileFullPath = request.getParameter("originFullPath");
        String targetFileName = targetFileFullPath.substring(targetFileFullPath.lastIndexOf("\\")+1, targetFileFullPath.length());

        String convertFileFullPath = request.getParameter("convertFullPath");
        String convertFileName = convertFileFullPath.substring(convertFileFullPath.lastIndexOf("\\")+1, convertFileFullPath.length());
        logger.info("文件"+targetFileName+"开始转换:......");
        if(targetFileName.contains("doc")||targetFileName.contains("docx")){
            //word转pdf
            testMainService.word2PDF(targetFileFullPath, convertFileFullPath);
            logger.info("文件"+targetFileName+"转换完成:......");
            //关闭com的线程
            ComThread.Release();
        } else if (targetFileName.contains("xls") || targetFileName.contains("xlsx")) {
            //excel转pdf
            testMainService.excel2PDF(targetFileFullPath, convertFileFullPath);
            logger.info("文件"+targetFileName+"转换完成:......");
            //关闭com的线程
            ComThread.Release();
        } else if (targetFileName.contains("ppt") || targetFileName.contains("pptx")) {
            //ppt转pdf
            testMainService.ppt2PDF(targetFileFullPath, convertFileFullPath);
            logger.info("文件"+targetFileName+"转换完成:......");
            //关闭com的线程
            ComThread.Release();
        }

        return responseDTO;
    }


}
