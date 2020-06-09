package com.fjminbao.mapper;

import com.fjminbao.entity.FileConvertMsg;

import java.util.List;
import java.util.Map;

/**
 * @Author: xianyuanLi
 * @Date: created in 22:12 2019/12/19
 * Descrpition:
 */
public interface FileConvertToPDFCustomerMapper {
    /**
     * 新增
     * @param fileConvertMsg
     * @return 用户公钥的bean
     */
    public int addFileConvertMsg(FileConvertMsg fileConvertMsg);


    /**
     * 修改
     * @param userPubKey 用户公钥的bean
     * @return 新增结果
     */
    public int updateFileConvertMsg(FileConvertMsg userPubKey);


    /**
     * 删除
     * @param userPubKey
     * @return
     */
    public int delFileConvertMsg(FileConvertMsg userPubKey);

    /**
     * 查询
     * @param map
     * @return
     */
    public List<FileConvertMsg> queryFileConvertMsg(Map map);
}
