package com.fjminbao.entity;

import lombok.Data;

/**
 * @Author: xianyuanLi
 * @Date: created in 17:41 2019/12/19
 * Descrpition: 文件转换的bean
 */
@Data
public class FileConvertMsg {
    private int id;
    //源文件名(不带http)
    private String originFileName;
    //源文件存储路径
    private String originFileSavePath;
    //目标文件名(不带http)
    private String  targetFileName;
    //目标文件路径(源文件转换后的路径)
    private String targetFileSavePath;
    //转换状态(0 未转换、1 转换成功)
    private String convertStatus;
    //应用名
    private String  sourceSystem;
    //总页码
    private String pages;
    //打印页码范围
    private String pageRange;
    //保留字段1
    private String reserve1;
    //保留字段2
    private String reserve2;
    //保留字段3
    private String reserve3;
    //保留字段4
    private String reserve4;
    //保留字段5
    private String reserve5;
}
