package com.fjminbao.dao;

/**
 * @Author: xianyuanLi
 * @Date: created in 18:44 2019/12/19
 * Descrpition:
 */

import com.fjminbao.entity.FileConvertMsg;
import com.fjminbao.mapper.FileConvertToPDFCustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Author: xianyuanLi
 * @Date: created in 22:22 2019/9/25
 * Descrpition:
 */
@Repository
public class FileConvertMapperDao {

    private static final Logger logger = Logger.getLogger("FileConvertMapperDao");

    @Autowired
    private FileConvertToPDFCustomerMapper fileConvertToPDFCustomerMapper;


    /**
     * 按条件分页查询
     * @param map
     * @return
     */
    public List<FileConvertMsg> queryFileConvertMsg(Map map){
        List<FileConvertMsg> fileConvertMsgsList = fileConvertToPDFCustomerMapper.queryFileConvertMsg(map);
        return fileConvertMsgsList;
    }


    /**
     * 新增
     * @param fileConvertMsg
     * @return
     */
    public int addFileConvertMsg(FileConvertMsg fileConvertMsg){
        int result = fileConvertToPDFCustomerMapper.addFileConvertMsg(fileConvertMsg);
        return result;
    }

    /**
     * 修改
     * @param fileConvertMsg
     * @return
     */
    public int updateFileConvertMsg(FileConvertMsg fileConvertMsg){
        int result = fileConvertToPDFCustomerMapper.updateFileConvertMsg(fileConvertMsg);
        return result;
    }

    /**
     * 删除
     * @param fileConvertMsg
     * @return
     */
    public int delFileConvertMsg(FileConvertMsg fileConvertMsg){
        int result = fileConvertToPDFCustomerMapper.delFileConvertMsg(fileConvertMsg);
        return result;
    }
}
