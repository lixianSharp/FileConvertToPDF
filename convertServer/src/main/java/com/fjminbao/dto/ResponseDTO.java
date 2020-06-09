package com.fjminbao.dto;


import com.alibaba.fastjson.JSON;
import lombok.Data;


import java.util.Map;

/**
 *  结果返回信息
 * @author llliao
 * 2019-03-01
 **/
@Data
public class ResponseDTO {

	private static final long serialVersionUID = 1L;
	private int resultCode = ResultCode.FAIL;
	private String resultMsg = "FAIL";
	private Map<String,Object> data;

	/**
	 * resultCode
	 * 
	 * 通过状态码返回响应对象
	 * @param resultCode
	 * @return
	 */
	public static ResponseDTO createByResultCode(int resultCode) {
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setResultCode(resultCode);
		String resultMsg = "FAIL";

		switch (resultCode) {
			case ResultCode.SUCCESS:
				resultMsg = "SUCCESS";
				break;
			case ResultCode.FAIL:
				resultMsg = "FAIL";
				break;
			case ResultCode.SYSTEM_ERROR:
				resultMsg = "SYSTEM_ERROR";
				break;
			case ResultCode.PARAM_ERROR:
				resultMsg = "PARAM_ERROR";
				break;
			default:
				break;
		}
		responseDTO.setResultMsg(resultMsg);

		return responseDTO;
	}

	/**
	 * 转换API服务返回的结果
	 * @param result
	 * @return
	 */
	public static ResponseDTO transformAPIResult(String result) {
		return JSON.parseObject(result, ResponseDTO.class);
	}
}
