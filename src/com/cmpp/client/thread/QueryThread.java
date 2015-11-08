package com.cmpp.client.thread;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmpp.client.CmppClientIoHandler;
import com.cmpp.pdu.Query;
import com.cmpp.pdu.Submit;
import com.cmpp.pdu.Tools;
import com.cmpp.sms.ShortMessage;
import com.cmpp.util.CmppConstant;

public class QueryThread extends Thread {

	private IoSession session = null;
	private static final Logger logger = LoggerFactory
			.getLogger(QueryThread.class);

	private static final long query_Interval = 10000;

	public QueryThread(IoSession s) {
		setDaemon(true);
		this.session = s;
	}

	public void run() {
		try {
			while ((session.isConnected())
					&& (CmppClientIoHandler.Connect == true)) {
				doQuery();
				try {
					Thread.sleep(query_Interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void shutdown() {
		if (session != null)
			session.close(true);
	}

	private void doQuery() {

		Query query = new Query();
		// 命令字
		query.setCommandId(CmppConstant.CMD_QUERY);

		// 日期
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		query.setTime(df.format(new Date()));

		// 查询类型 0：总数查询，1：按业务类型查询
		query.setQueryType((byte) 0);

		// Query_Type为1时，此项填写业务类型Service_Id
		query.setQueryCode(CmppConstant.SERVICE_ID_MMS);

		// 保留字段
		query.setReserve("00000000");

		session.write(query);

	}
}
