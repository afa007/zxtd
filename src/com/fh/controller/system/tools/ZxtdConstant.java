package com.fh.controller.system.tools;

public class ZxtdConstant {

	// 短信发送状态
	public static final String SMS_STATUS_TOSEND = "0";
	public static final String SMS_STATUS_TOSEND_DESC = "待处理";

	public static final String SMS_STATUS_SENDING = "9";
	public static final String SMS_STATUS_SENDING_DESC = "正在发送";

	public static final String SMS_STATUS_SENDED = "1";
	public static final String SMS_STATUS_SENDED_DESC = "发送成功";

	public static final String SMS_STATUS_FEEDBACK = "2";
	public static final String SMS_STATUS_FEEDBACK_DESC = "发送成功，收到回报";

	public static final String SMS_STATUS_UNKNOW_DESC = "未知";

	// 短信类型
	public static final String SMS_TYPE_SEND = "1";
	public static final String SMS_TYPE_SEND_DESC = "发送"; 
	public static final String SMS_TYPE_RECV = "2";
	public static final String SMS_TYPE_RECV_DESC = "接收";
	
	
	public static String getSmsStatusDesc(String status) {

		if (SMS_STATUS_TOSEND.equals(status)) {
			return SMS_STATUS_TOSEND_DESC;
		} else if (SMS_STATUS_SENDING.equals(status)) {
			return SMS_STATUS_SENDING_DESC;
		} else if (SMS_STATUS_SENDED.equals(status)) {
			return SMS_STATUS_SENDED_DESC;
		} else if (SMS_STATUS_FEEDBACK.equals(status)) {
			return SMS_STATUS_FEEDBACK_DESC;
		} else {
			return SMS_STATUS_UNKNOW_DESC;
		}
	}
}
