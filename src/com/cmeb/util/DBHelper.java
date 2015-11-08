package com.cmeb.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.cmeb.ConstantBean;
import com.cmeb.MmsSend;

public class DBHelper {

	private static final int STATUS_SUCCESS = 0;
	
    /*
     * 获取待发送短信列表
     */
    public ArrayList<MmsSend> getMmsSendList(HashMap<String, Object> map) {

        ArrayList<MmsSend> list = new ArrayList<MmsSend>();
        try {

            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            String sql = "select * from mms_send where flag = " + map.get("flag");
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                MmsSend send = new MmsSend();
                send.setContent(rs.getString("content"));
                send.setMobile(rs.getString("mobile"));
                send.setId(rs.getInt("id"));

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
     * 更新短信发送表的记录状态
     */
    public long updateMmsSendFlag(HashMap<String, Object> map) {

        int result = -1;
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            String sql = "update mms_send set flag = " + map.get("flag") + " where id = " + map.get("id");
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
            String sql =
                    "select count(1) from " + mmsOrGprs + "_info where date= " + dateStr + " and status = "
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
    public boolean addMmsGprsByDate(String mmsOrGprs, String dateStr, List<LinkedHashMap<String, Object>> map) {

        boolean result = true;

        if (map == null || map.size() <= 0) {
            return false;
        }

        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            for (int i = 0; i < map.size(); i++) {
                LinkedHashMap<String, Object> record = map.get(i);
                String sql =
                        "delete from " + mmsOrGprs + "_history where mobile='" + record.get("msisdn") + "' and date='"
                                + dateStr + "'";
                if (!statement.execute(sql)) {
                    result = false;
                    break;
                }

                sql =
                        "insert into " + mmsOrGprs + "_history(mobile, date, amount) values('" + record.get("msisdn")
                                + "', '" + dateStr + "'," + record.get(mmsOrGprs);
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
    public boolean updateMmsGprsInfoByDate(String mmsOrGprs, String msisdn, String dateStr, int status) {

        int cnt = 0;

        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            String sql =
                    "update " + mmsOrGprs + "_info set status = " + status + " where date= " + dateStr
                            + " and mobile='" + msisdn + "'";
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
            conn = DriverManager.getConnection(ConstantBean.url, ConstantBean.user, ConstantBean.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

}
