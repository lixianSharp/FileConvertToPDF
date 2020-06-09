package com.fjminbao.dto;

/**
 * 状态码
 *      系统通用代码 1 成功 其他都以负值表示
 * @author llliao
 * 2019-03-01
 **/
public class ResultCode {

    public final static int SUCCESS = 1;
    public final static int FAIL = -1;
    public final static int PARAM_ERROR = -3;
    public final static int SYSTEM_ERROR = -1000;

}
