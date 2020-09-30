package com.fjminbao.controller;

import com.fjminbao.dto.ResponseDTO;
import com.fjminbao.task.test.TestMain;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/testWordConvertPDF", method = RequestMethod.POST)
    public ResponseDTO testWordConvertPDF(@RequestBody RequestTestDto requestTestDto) {
        int count = atomicInteger.incrementAndGet();
        ResponseDTO responseDTO = new ResponseDTO();
        logger.info("第" + count + "个文件开始转换");
//        String targetFileFullPath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\20200619\\P\\1.docx";
//        String convertFileFullPath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\20200619\\P\\1.pdf";
//        TestMain.word2PDF(targetFileFullPath, convertFileFullPath);
        String targetFileFullPath = requestTestDto.getOriginFullPath();
        String targetFileName = targetFileFullPath.substring(targetFileFullPath.lastIndexOf("\\")+1, targetFileFullPath.length());

        String convertFileFullPath = requestTestDto.getConvertFullPath();
        String convertFileName = convertFileFullPath.substring(convertFileFullPath.lastIndexOf("\\")+1, convertFileFullPath.length());
        logger.info("文件"+targetFileName+"开始转换:......");
        TestMain.word2PDF(targetFileFullPath, convertFileFullPath);

//        System.out.println("数据=" + requestTestDto);
        logger.info("第" + count + "个文件转换完成");
        return responseDTO;
    }

    public static void main(String[] args) {
        String targetFileFullPath = "K:\\apache-tomcat-8.5.38-8085-file\\webapps\\ROOT\\upload\\20200619\\P\\1.docx";
        String targetFileName = targetFileFullPath.substring(targetFileFullPath.lastIndexOf("\\")+1, targetFileFullPath.length());
        System.out.println(targetFileName);

    }

}
