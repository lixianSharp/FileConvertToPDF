package com.fjminbao.dto;

import lombok.Data;

/**
 * @Author: xianyuanLi
 * @Date: created in 17:40 2019/12/19
 * Descrpition:
 */
@Data
public class RequestDTO {
    /**
     * 源文件名
     */
    private String fileName;
    /**
     *  源文件存储路径
     */
    private String filePath;

    /**
     * 要合并的PDF文件的文件全路径名
     */
    private String[] pdfFilePathNames;

    /**
     * 合并后的PDF的全路径
     */
    private String mergePdfFilePath;
}
