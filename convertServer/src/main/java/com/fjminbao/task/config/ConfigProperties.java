package com.fjminbao.task.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author: xianyuanLi
 * @Date: created in 13:39 2019/12/20
 * Descrpition:
 */
@Component
@Data
@PropertySource("classpath:config.properties")
public class ConfigProperties {

    /**
     * 源文件存放路径
     */
    @Value("${static.originfile.path}")
    private String originFilePath;

    /**
     * 目标文件临时存放地址
     */
    @Value("${static.targetfile.path}")
    private String targetFilePath;

    /**
     * 转换后的文件存放地址
     */
    @Value("${static.convertfile.path}")
    private String convertFilePath;

}