package com.cmeb.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmeb.ConstantBean;
import com.cmeb.MmsSend;
import com.cmeb.SmsPocessorBean;
import com.cmpp.client.CmppClientIoHandler;

public class DBHelper {

	private static final int STATUS_SUCCESS = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(DBHelper.class);

	/*
	 * 获取待发送短信
	 */
	public SmsPocessorBean findSmsById(String id) {

		ArrayList<MmsSend> list = new ArrayList<MmsSend>();

		SmsPocessorBean result = null;
		try {

			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			String sql = "select * from tb_smspocessor where smspocessor_id = '"
					+ id + "'";
			ResultSet rs = statement.executeQuery(sql);

			if (rs.next()) {
				result = new SmsPocessorBean();
				result.setSMSPOCESSOR_ID(rs.getString("SMSPOCESSOR_ID"));
				result.setMSISDN(rs.getString("MSISDN"));
				result.setTYPE(rs.getString("TYPE"));
				result.setCONTENT(rs.getString("CONTENT"));
				result.setUSERID(rs.getString("USERID"));
				result.setCREATETIME(rs.getDate("CREATETIME"));
				result.setSTATUS(rs.getString("STATUS"));
				result.setSEQ_ID(rs.getInt("SEQ_ID"));
			}
			rs.close();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * 获取待发送短信列表
	 */
	public ArrayList<SmsPocessorBean> getMmsSendList(HashMap<String, Object> map) {

		ArrayList<SmsPocessorBean> list = new ArrayList<SmsPocessorBean>();
		try {

			String strSMT = ConfigUtil.getProp("send.max.time");
			int sendMaxTime = 600;
			if (strSMT == null || "".equals(strSMT)) {
				sendMaxTime = Integer.valueOf(sendMaxTime);
			}
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			//String sql = "select * from tb_smspocessor where status = '" 	+ map.get("status") + "' and (now() - " + sendMaxTime + " > 5 * 60)";
			String sql = "select * from tb_smspocessor where status = '" 	+ map.get("status") + "'";
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				SmsPocessorBean send = new SmsPocessorBean();
				send.setSMSPOCESSOR_ID(rs.getString("SMSPOCESSOR_ID"));
				send.setMSISDN(rs.getString("MSISDN"));
				send.setTYPE(rs.getString("TYPE"));
				send.setCONTENT(rs.getString("CONTENT"));
				send.setUSERID(rs.getString("USERID"));
				send.setCREATETIME(rs.getDate("CREATETIME"));
				send.setSTATUS(rs.getString("STATUS"));
				send.setSEQ_ID(rs.getInt("SEQ_ID"));
				list.add(send);
			}
			rs.close();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/*
	 * 写入短信发送记录
	 */
	public long insertMmsSendMsg(HashMap<String, Object> map) {

		int result = -1;
		try {
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			String sql = "insert into tb_smspocessor(SMSPOCESSOR_ID, MSISDN, TYPE, CONTENT, USERID, STATUS, CREATETIME) values('"
					+ map.get("SMSPOCESSOR_ID")
					+ "','"
					+ map.get("MSISDN")
					+ "','"
					+ map.get("TYPE")
					+ "','"
					+ map.get("CONTENT")
					+ "','"
					+ map.get("USERID")
					+ "','"
					+ map.get("STATUS")
					+ "','" + map.get("CREATETIME") + "')";
			logger.debug("sql:" + sql);
			result = statement.executeUpdate(sql);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 查询OWNER
	public String getUserNameByMsisdn(String msisdn) {
		String result = null;
		try {

			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			String sql = "select owner from tb_cardinfo where msisdn = '"
					+ msisdn + "'";

			ResultSet rs = statement.executeQuery(sql);

			if (rs.next()) {
				result = rs.getString("owner");
			}
			rs.close();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * 更新短信发送表的记录状态
	 */
	public long updateMmsSendFlag(HashMap<String, Object> map) {

		int result = -1;
		try {
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			String sql = "update tb_smspocessor set status = '"
					+ map.get("STATUS") + "' where SEQ_ID = "
					+ map.get("SEQ_ID");
			result = statement.executeUpdate(sql);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 判断某日的收据是否已经抓取成功
	 */
	public boolean haveGetedMmsGprsByDate(String mmsOrGprs, String dateStr) {
		int cnt = 0;
		try {
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			String sql = "select count(1) from " + mmsOrGprs
					+ "_info where date= " + dateStr + " and status = "
					+ STATUS_SUCCESS;
			ResultSet rs = statement.executeQuery(sql);

			if (rs.next()) {
				cnt = rs.getInt(1);
			}
			rs.close();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cnt > 0 ? true : false;
	}

	/*
	 * 按日期插入短信或者流量数据
	 */
	public boolean addMmsGprsByDate(String mmsOrGprs, String dateStr,
			List<LinkedHashMap<String, Object>> map) {

		boolean result = true;

		if (map == null || map.size() <= 0) {
			return false;
		}

		try {
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			for (int i = 0; i < map.size(); i++) {
				LinkedHashMap<String, Object> record = map.get(i);
				String sql = "delete from " + mmsOrGprs
						+ "_history where mobile='" + record.get("msisdn")
						+ "' and date='" + dateStr + "'";
				if (!statement.execute(sql)) {
					result = false;
					break;
				}

				sql = "insert into " + mmsOrGprs
						+ "_history(mobile, date, amount) values('"
						+ record.get("msisdn") + "', '" + dateStr + "',"
						+ record.get(mmsOrGprs);
				if (!statement.execute(sql)) {
					result = false;
					break;
				}
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 更新状态
	 */
	public boolean updateMmsGprsInfoByDate(String mmsOrGprs, String msisdn,
			String dateStr, int status) {

		int cnt = 0;

		try {
			Connection conn = getConnection();
			Statement statement = conn.createStatement();
			String sql = "update " + mmsOrGprs + "_info set status = " + status
					+ " where date= " + dateStr + " and mobile='" + msisdn
					+ "'";
			ResultSet rs = statement.executeQuery(sql);

			if (rs.next()) {
				cnt = rs.getInt(1);
			}
			rs.close();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cnt > 0 ? true : false;
	}

	/*
	 * 获取连接
	 */
	private Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(ConstantBean.driver);
			conn = DriverManager.getConnection(ConstantBean.url,
					ConstantBean.user, ConstantBean.password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

}
