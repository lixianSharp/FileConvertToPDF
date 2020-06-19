package com.fjminbao.task.service;

import com.fjminbao.util.CaculatPDFPagesUtil;
import com.fjminbao.util.MergePdfUtils;
import com.fjminbao.util.OfficeConvertPDF;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @ClassName AsyncMergePdfService
 * @Description: TODO
 * @Author lxy
 * @Date 2020/6/19
 * @Version V1.0
 **/
@Service
public class AsyncMergePdfService {
    private static final Logger logger = Logger.getLogger("AsyncService");

    // 异步的方式执行
    @Async
    public void mergePdfByPdfNames(String mergePdfFilePath, String[] pdfFilePathNames) {
        try {
            logger.info("进入异步合并方法中进行PDF文件合并");
            MergePdfUtils.mergePdfByPdfNames(mergePdfFilePath,pdfFilePathNames);
            logger.info("异步PDF合并完成");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }catch (Exception e){

        }
    }
}
