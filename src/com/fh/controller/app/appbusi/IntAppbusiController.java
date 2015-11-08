package com.fh.controller.app.appbusi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmeb.util.CmebUtil;
import com.fh.controller.base.BaseController;
import com.fh.service.system.appbusi.AppbusiService;
import com.fh.util.AppUtil;
import com.fh.util.PageData;

/**
 * 能力查询-接口类
 * 
 * 相关参数协议： 00 成功 01 失败
 */
@Controller
@RequestMapping(value = "/appbusi")
public class IntAppbusiController extends BaseController {

	@Resource(name = "appbusiService")
	private AppbusiService appbusiService;

	/**
	 * APP查询汇总信息：余额、当月短信使用情况、当月流量使用情况、开关机、卡的用户状态、GPRS在线信息
	 */
	@RequestMapping(value = "/queryinfo")
	@ResponseBody
	public Object queryInfo() {
		logBefore(logger, "APP查询汇总信息");
		
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		String result = "00";
		CmebUtil cmeb = new CmebUtil();

		try {

			String msisdn = pd.getString("MSISDN");

			// 余额
			pd.put("balance", cmeb.balancerealsingle(msisdn));

			// 当月sms使用量
			pd.put("sms", cmeb.smsusedinfosingle(msisdn));

			// 当月gprs使用量
			pd.put("gprs", cmeb.gprsusedinfosingle(msisdn));
			
			// 开关机状态
			pd.put("onoff", cmeb.onandoffrealsingle(msisdn));
			
			// 卡的用户状态
			pd.put("userstatus", cmeb.userstatusrealsingle(msisdn));
			
			// GPRS在线信息
			HashMap<String, Object> gprsMap = cmeb.gprsrealsingle(msisdn);
			if(gprsMap != null){
				pd.put("gprsstatus", gprsMap.get("gprsstatus"));
				pd.put("ip", gprsMap.get("ip"));
				pd.put("apn", gprsMap.get("apn"));
				pd.put("rat", gprsMap.get("rat"));
			}
			
			map.put("pd", pd);

		} catch (Exception e) {
			logger.error(e.toString(), e);
			result = "01"; // 交易失败
		} finally {
			map.put("result", result);
			logAfter(logger);
		}

		return AppUtil.returnObject(new PageData(), map);
	}

	/**
	 * APP查询SMS短信和GPRS历史使用情况
	 * 
	 * 参数： msisdn: 卡号， 非必输，不输则表示全部卡 type: sms or gprs，必输 date_s，非必输
	 * date_e，非必输，不输，默认30天
	 */
	@RequestMapping(value = "/qryusedbydate")
	@ResponseBody
	public Object qryusedbydate() {
		logBefore(logger, "APP查询SMS短信和GPRS历史使用情况");
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String result = "00";

		try {

			String MSISDN = pd.getString("MSISDN");
			String TYPE = pd.getString("TYPE");
			String DATE_S = pd.getString("DATE_S");
			String DATE_E = pd.getString("DATE_E");

			System.out.println("MSISDN:" + MSISDN + ",TYPE:" + TYPE
					+ ",DATE_S:" + DATE_S + ",DATE_E:" + DATE_E);

			List<PageData> list = appbusiService.qryusedbydate(pd);

			if (list != null && list.size() > 0) {

				map.put("pd", list);

			} else {
				result = "02"; // 记录不存在
			}

		} catch (Exception e) {
			logger.error(e.toString(), e);
			result = "01"; // 交易失败
		} finally {
			map.put("result", result);
			logAfter(logger);
		}

		return AppUtil.returnObject(new PageData(), map);
	}

	/**
	 * APP查询码号信息
	 * 
	 * 参数： card_info: 所查询卡的 msisdn、imsi 或 iccid type: 0—msisdn 1—imsi 2—iccid
	 */
	@RequestMapping(value = "/qrycardinfo")
	@ResponseBody
	public Object qrycardinfo() {
		logBefore(logger, "APP查询码号信息");
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String result = "00";

		try {
			String CARD_INFO = pd.getString("CARD_INFO");
			String TYPE = pd.getString("TYPE");

			System.out.println("CARD_INFO:" + CARD_INFO + ",TYPE:" + TYPE);

			pd = appbusiService.qrycardinfo(pd);
			if (pd != null) {

				map.put("pd", pd);
			} else {
				result = "01"; // 未找到信息
			}

		} catch (Exception e) {
			logger.error(e.toString(), e);
			result = "02"; // 交易失败
		} finally {
			map.put("result", result);
			logAfter(logger);
		}

		return AppUtil.returnObject(new PageData(), map);
	}

	// 集团用户数查询
	// 短信状态重置
}
