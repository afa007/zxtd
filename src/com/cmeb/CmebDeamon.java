package com.cmeb;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cmeb.util.CmebUtil;
import com.cmeb.util.DBHelper;
import com.cmeb.util.DateUtil;
import com.fh.service.system.cardinfo.CardInfoService;
import com.fh.service.system.smsgprsbalance.SmsgprsbalanceService;
import com.fh.util.PageData;

public class CmebDeamon {

	private static final int pagesize = 100;
	private static final int STATUS_SUCCESS = 0;
	private static final int STATUS_FAIL = -1;

	private static final int GRAB_INTERVAL = 1000;

	private final Log logger = LogFactory.getLog(getClass());

	@Resource(name = "cardinfoService")
	private CardInfoService cardInfoService;

	@Resource(name = "smsgprsbalanceService")
	private SmsgprsbalanceService smsgprsbalanceService;

	/*
	 * 按天查询流量和短信情况，并写入数据库
	 */
	public boolean queryUsedByDate() {

		CmebUtil cmeb = new CmebUtil();

		// 卡列表
		logger.info("开始写入流量短信余额信息");
		boolean result = false;
		try {
			PageData pd = new PageData();
			List<PageData> list = cardInfoService.listAll(pd);
			if (list == null) {

				logger.info("写入流量短信余额信息，未找到卡信息");
				return false;
			}

			Date now = new Date();
			for (int i = 0; i < list.size(); i++) {
				pd = list.get(i);
				String MSISDN = (String) pd.get("MSISDN");

				PageData insertPD = new PageData();
				insertPD.put("MSISDN", MSISDN);


				// 查询余额
				double balance = cmeb.balancerealsingle(MSISDN);
				if (balance >= 0) {
					insertPD.put("balance", balance);
				}
				else{
					logger.info("写入流量短信余额信息，查询余额失败");
					return false;
				}
				
				// 查询GPRS使用量
				Date queryDate = DateUtil.addDay(now, -1);
				String dateStr = DateUtil.DateToString(queryDate, "yyyyMMdd");
				long page_size = pagesize;
				long page_num = 1;
				List<LinkedHashMap<String, Object>> mapList = cmeb
						.batchgprsusedbydate(MSISDN, dateStr, page_size,
								page_num);

				if (mapList != null) {
					LinkedHashMap<String, Object> map = mapList.get(0);
					String msisdnStr = (String) map.get("Msisdn");
					String gprsStr = (String) map.get("gprs");

					insertPD.put("gprs", gprsStr);
				} else {
					logger.info("写入流量短信余额信息，查询GPRS使用量失败");
					return false;
				}

				// 查询短信使用量
				mapList = cmeb.batchsmsusedbydate(MSISDN, dateStr, page_size,
						page_num);
				if (mapList != null) {
					LinkedHashMap<String, Object> map = mapList.get(0);
					String msisdnStr = (String) map.get("Msisdn");
					String smsStr = (String) map.get("sms");

					insertPD.put("sms", smsStr);
				} else {
					logger.info("写入流量短信余额信息，查询短信使用量失败");
					return false;
				}

				insertPD.put("date", dateStr);

				smsgprsbalanceService.save(insertPD);

				Thread.sleep(GRAB_INTERVAL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 更新cardinfo表，调用接口补全数据。
	public boolean queryCardInfo() {

		CmebUtil cmeb = new CmebUtil();

		logger.info("开始更新卡列表信息");
		// 卡列表

		boolean result = false;
		try {
			PageData pd = new PageData();
			List<PageData> list = cardInfoService.listAllWithEmpty(pd);
			if (list == null) {

				logger.info("未找到需要更新的卡");
				return false;
			}

			logger.info("需要更新的卡, list.size():" + list.size());
			for (int i = 0; i < list.size(); i++) {
				pd = list.get(i);
				String MSISDN = (String) pd.get("MSISDN");

				PageData updatePD = new PageData();
				updatePD.put("MSISDN", MSISDN);

				// 查询码号
				HashMap<String, Object> map = cmeb.cardinfo(MSISDN, 0);

				if (map != null) {
					updatePD.put("imsi", map.get("imsi"));
					updatePD.put("iccid", map.get("iccid"));

					logger.info("更新卡信息: " + updatePD.getString("MSISDN") + ", "
							+ updatePD.getString("imsi") + ", "
							+ updatePD.getString("iccid"));
					cardInfoService.edit(updatePD);
				} else {
					logger.info("查询卡信息失败");
					return false;
				}

				Thread.sleep(GRAB_INTERVAL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 按天查询流量和短信情况，并写入数据库
	 */
	public boolean queryByDate(String mmsOrGprs, String msisdn, int ldate) {

		CmebUtil cmeb = new CmebUtil();
		DBHelper dbHelper = new DBHelper();
		Date now = new Date();

		boolean result = false;

		for (int i = 1; i <= ldate; i++) {
			Date endDate = DateUtil.addMonth(now, ldate * -1);
			String dateStr = DateUtil.DateToString(endDate, "yyyyMMdd");

			if (dbHelper.haveGetedMmsGprsByDate(mmsOrGprs, dateStr)) {
				continue;
			} else {

				long page_size = pagesize;
				long page_num = 1;

				List<LinkedHashMap<String, Object>> map = null;

				do {
					map = cmeb.batchgprsusedbydate(msisdn, dateStr, page_size,
							page_num++);

					if (dbHelper.addMmsGprsByDate(mmsOrGprs, dateStr, map)) {
						logger.info("抓取信息成功，mmsOrGprs = [" + mmsOrGprs
								+ "], dateStr = [" + dateStr + "]");
						dbHelper.updateMmsGprsInfoByDate(mmsOrGprs, msisdn,
								dateStr, STATUS_SUCCESS);
					} else {
						logger.info("抓取信息失败，mmsOrGprs = [" + mmsOrGprs
								+ "], dateStr = [" + dateStr + "]");
						dbHelper.updateMmsGprsInfoByDate(mmsOrGprs, msisdn,
								dateStr, STATUS_FAIL);
						result = false;
					}
				} while (map != null && map.size() > 0);
			}
		}

		return result;
	}
}
