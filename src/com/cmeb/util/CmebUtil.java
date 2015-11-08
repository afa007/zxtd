package com.cmeb.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cmeb.ConstantBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CmebUtil {

	private HttpUtil http = new HttpUtil();

	private final Log logger = LogFactory.getLog(getClass());

	private static long serialNo = 1;

	/*
	 * 
	 * CMIOT_API2012－用户当月短信查询
	 */
	public long smsusedinfosingle(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000036";
		String uri = getPath() + "smsusedinfosingle?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					long sms = result.path("sms").asLong();

					return sms;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API2005－用户当月 GPRS 查询
	 */
	public long gprsusedinfosingle(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000012";
		String uri = getPath() + "gprsusedinfosingle?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					long gprs = result.path("gprs").asLong();

					return gprs;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API2009－短信使用信息批量查询，7日内
	 */
	public List<LinkedHashMap<String, Object>> batchsmsusedbydate(
			String msisdn, String queryDate, long page_size, long page_num) {
		String transId = getTransId();
		String ebid = "0001000000026";
		String uri = getPath() + "batchsmsusedbydate?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdns=" + msisdn
				+ "&query_date=" + queryDate + "&page_size=" + page_size
				+ "&page_num=" + page_num;
		logger.info(uri);

		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					String result = rootNode.path("result").asText();

					List<LinkedHashMap<String, Object>> list = mapper
							.readValue(result, List.class);

					return list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * 
	 * CMIOT_API2010－流量使用信息批量查询，7日内
	 */
	public List<LinkedHashMap<String, Object>> batchgprsusedbydate(
			String msisdn, String queryDate, long page_size, long page_num) {
		String transId = getTransId();
		String ebid = "0001000000027";
		String uri = getPath() + "batchgprsusedbydate?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdns=" + msisdn
				+ "&query_date=" + queryDate + "&page_size=" + page_size
				+ "&page_num=" + page_num;
		logger.info(uri);

		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					String result = rootNode.path("result").asText();

					List<LinkedHashMap<String, Object>> list = mapper
							.readValue(result, List.class);

					return list;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * 
	 * CMIOT_API2011－用户余额信息实时查询
	 */
	public double balancerealsingle(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000035";
		String uri = getPath() + "balancerealsingle?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					double balance = result.path("balance").asDouble();

					return balance;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API2013－集团用户数查询, 截至某一天
	 */
	public long groupuserinfo(String msisdn, String queryDate) {
		String transId = getTransId();
		String ebid = "0001000000039";
		String uri = getPath() + "groupuserinfo?appid=" + ConstantBean.APPID
				+ "&transid=" + transId + "&ebid=" + ebid + "&token="
				+ getToken(transId) + "&msisdn=" + msisdn + "&query_date="
				+ queryDate;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					long total = result.path("total").asLong();

					return total;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API2014－用户短信使用查询, 某一天, 一般来说实时性会晚一天
	 */
	public long smsusedbydate(String msisdn, String queryDate) {
		String transId = getTransId();
		String ebid = "0001000000040";
		String uri = getPath() + "smsusedbydate?appid=" + ConstantBean.APPID
				+ "&transid=" + transId + "&ebid=" + ebid + "&token="
				+ getToken(transId) + "&msisdn=" + msisdn + "&query_date="
				+ queryDate;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					long sms = result.path("sms").asLong();

					return sms;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API2001－在线信息实时查询,提供单个MSISDN号卡的GPRS在线状态、IP地址、APN、RAT实时查询功能
	 */
	public HashMap<String, Object> gprsrealsingle(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000008";
		String uri = getPath() + "gprsrealsingle?appid=" + ConstantBean.APPID
				+ "&transid=" + transId + "&ebid=" + ebid + "&token="
				+ getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");

					HashMap<String, Object> resultMap = new HashMap<String, Object>();

					int gprsstatus = result.path("gprsstatus").asInt();
					resultMap.put("gprsstatus", gprsstatus);
					String ip = result.path("ip").asText();
					resultMap.put("ip", ip);
					String apn = result.path("apn").asText();
					resultMap.put("apn", apn);
					String rat = result.path("rat").asText();
					resultMap.put("rat", rat);

					return resultMap;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * 
	 * CMIOT_API2002－用户状态信息实时查询 返回值： 1在线， 0其他
	 */
	public int userstatusrealsingle(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000009";
		String uri = getPath() + "userstatusrealsingle?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					String statusStr = result.path("status").asText();
					if ("00".equals(statusStr)) {
						return 1;
					} else {
						return 0;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API2003－码号信息查询,根据 ICCID、IMSI、MSISDN 任意 1 个查询剩余 2 个的能力
	 */
	public HashMap<String, Object> cardinfo(String cardInfo, int type) {
		String transId = getTransId();
		String ebid = "0001000000010";
		String uri = getPath() + "cardinfo?appid=" + ConstantBean.APPID
				+ "&transid=" + transId + "&ebid=" + ebid + "&token="
				+ getToken(transId) + "&card_info=" + cardInfo + "&type="
				+ type;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					HashMap<String, Object> resultMap = new HashMap<String, Object>();

					String msisdn = result.path("msisdn").asText();
					resultMap.put("msisdn", msisdn);
					String imsi = result.path("imsi").asText();
					resultMap.put("imsi", imsi);
					String iccid = result.path("iccid").asText();
					resultMap.put("iccid", iccid);

					return resultMap;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * 
	 * CMIOT_API2008－开关机状态实时查询
	 */
	public int onandoffrealsingle(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000025";
		String uri = getPath() + "onandoffrealsingle?appid="
				+ ConstantBean.APPID + "&transid=" + transId + "&ebid=" + ebid
				+ "&token=" + getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					JsonNode result = rootNode.path("result");
					int statusVal = result.path("status").asInt();
					if (1 == statusVal) {
						return 1;
					} else {
						return 0;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*
	 * 
	 * CMIOT_API4001－短信状态重置
	 */
	public int smsstatusreset(String msisdn) {
		String transId = getTransId();
		String ebid = "0001000000034";
		String uri = getPath() + "smsstatusreset?appid=" + ConstantBean.APPID
				+ "&transid=" + transId + "&ebid=" + ebid + "&token="
				+ getToken(transId) + "&msisdn=" + msisdn;
		logger.info(uri);
		try {
			String respMsg = http.excute(0, uri);
			logger.info(respMsg);
			if (respMsg != null && !"".equals(respMsg)) {
				respMsg = respMsg.toLowerCase();
				// 读取Json
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(respMsg);

				logger.info("status:" + rootNode.path("status") + ", message:"
						+ rootNode.path("message"));
				int status = rootNode.path("status").asInt();
				if (status == 0) {
					return 0;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	private String getPath() {
		return "http://" + ConstantBean.IP + ":" + ConstantBean.PORT + "/"
				+ ConstantBean.VERSION + "/";
	}

	private String getToken(String transId) {
		return DigestUtil.sha256(ConstantBean.APPID + ConstantBean.PASSWD
				+ transId);
	}

	private String getTransId() {
		return ConstantBean.APPID
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ String.format("%08d", serialNo++);
	}

	private String getErrMsg(int errCode) {
		switch (errCode) {
		case 0:
			return "正确";
		case 1:
			return "数据库错误";
		case 2:
			return "数据库无记录";
		case 3:
			return "订购关系鉴权不通过";
		case 4:
			return "应用状态不正常";
		case 6:
			return "IP地址鉴权不通过";
		case 7:
			return "Token鉴权不通过";
		case 8:
			return "白名单鉴权不通过";
		case 10:
			return "请求参数个数不正确";
		case 11:
			return "号码没有归属省份";
		case 12:
			return "MSISDN号不是所查询的集团下的用户";
		case 13:
			return "请求参数命名不规范";
		case 14:
			return "日期参数格式不正确";
		case 18:
			return "卡号数量超出限制范围";
		case 19:
			return "查询时间超出限制范围";
		case 20:
			return "数据文件还未生成";
		case 21:
			return "查询号码全部非法";
		case 71:
			return "已超出 API 最大流控限制";
		case 72:
			return "已超出订购关系最大流控限制";
		case 99:
			return "APIname 调用，系统错误";
		case 100:
			return "短信重置刷新失败";
		case 101:
			return "通信失败";
		case 102:
			return "通信获取系统参数失败";
		case 200:
			return "号卡余额查询通信异常";
		case 118040195:
			return "其他错误";
		case 118040196:
			return "业务权限校验失败";
		case 118030260:
			return "用户{0}不存在";
		case 118032232:
			return "IP{0}格式不正确";
		case 118030478:
			return "系统错误，{0}";
		default:
			return "未知返回码";
		}
	}

	public boolean testAPIs() {
		String msisdn = "1064805103117";

		// 在线信息实时查询,提供单个MSISDN号卡的GPRS在线状态、IP地址、APN、RAT实时查询功能
		HashMap<String, Object> result = this.gprsrealsingle(msisdn);
		if (result != null) {
			logger.info("gprsrealsingle:" + result.get("gprsrealsingle")
					+ ",IP:" + result.get("IP") + ",APN:" + result.get("APN")
					+ ",RAT:" + result.get("RAT"));
		}
		// 用户状态信息实时查询
		logger.info("在线状态： " + this.userstatusrealsingle(msisdn));

		// 码号信息查询,根据 ICCID、IMSI、MSISDN 任意 1 个查询剩余 2 个的能力
		result = this.cardinfo(msisdn, 0);
		if (result != null) {
			logger.info("msisdn:" + result.get("msisdn") + ",imsi:"
					+ result.get("imsi") + ",iccid:" + result.get("iccid"));
		}
		logger.info("当月GPRS使用量： " + this.gprsusedinfosingle(msisdn));

		logger.info("开关机状态实时查询： " + this.onandoffrealsingle(msisdn));

		// 短信使用信息批量查询，7日内
		List<LinkedHashMap<String, Object>> list = this.batchsmsusedbydate(
				msisdn, "20151030", 100, 1);
		if (list != null) {
			logger.info("短信使用批量查询： " + list.size());
		}
		// 流量使用信息批量查询，7日内
		list = this.batchgprsusedbydate(msisdn, "20151030", 100, 1);
		if (list != null) {
			logger.info("流量使用批量查询： " + list.size());
		}
		logger.info("用户余额： " + this.balancerealsingle(msisdn));

		logger.info("用户当月短信查询： " + this.smsusedinfosingle(msisdn));

		logger.info("集团用户数查询： " + this.groupuserinfo(msisdn, "20151030"));

		logger.info("用户短信使用查询： " + this.smsusedbydate(msisdn, "20151030"));

		// 短信状态重置
		// this.smsstatusreset(msisdn);

		return true;
	}
	

	public static void main(String args[]) {
		CmebUtil util = new CmebUtil();
		util.testAPIs();
	}
}
