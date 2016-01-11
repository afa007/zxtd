package com.cmpp.client.thread;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmeb.MmsSend;
import com.cmeb.SmsPocessorBean;
import com.cmeb.util.DBHelper;
import com.cmpp.client.CmppClientIoHandler;
import com.cmpp.pdu.Submit;
import com.cmpp.pdu.Tools;
import com.cmpp.sms.ShortMessage;
import com.cmpp.util.MsgQUtil;
import com.cmpp.util.PropertyUtil;
import com.fh.service.system.smspocessor.SmsPocessorService;
import com.fh.util.PageData;

public class MsgSendThread extends Thread {

	private IoSession session = null;
	private static final Logger logger = LoggerFactory
			.getLogger(MsgSendThread.class);

	public static PropertyUtil pu = new PropertyUtil("ServerIPAddress");

	private boolean isRunning = false;

	public MsgSendThread(IoSession s) {
		setDaemon(true);
		this.session = s;
	}

	public void run() {
		try {
			isRunning = true;
			while ((session.isConnected())
					&& (CmppClientIoHandler.Connect == true)) {

				// doSubmitFromMQ();
				doSubmitFromDB();
				try {

					// 毫秒
					String sSendInterval = pu.getValue("send.interval");
					if (sSendInterval != null && !"".equals(sSendInterval)) {
						
						long sendInterval = Long.valueOf(sSendInterval);
						Thread.sleep(sendInterval);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		isRunning = false;
	}

	protected void shutdown() {
		if (session != null)
			session.close(true);
		isRunning = false;
	}

	/*
	 * 
	 * 发送测试短信
	 */
	private void doSubmitAMsg() {

		HashMap<String, Object> getMap = new HashMap<String, Object>();
		getMap.put("flag", 0);

		Submit submit = new Submit();

		byte[] msgid = Tools.GetMsgid();

		submit.setMsgId(msgid);
		// pktotal, pknumber 默认都是1

		submit.setServiceId("ZXTD");

		// 源号码
		submit.setSrcId(pu.getValue("CmppGw.client.srcid"));
		// SP_ID
		submit.setMsgSrc(pu.getValue("CmppGw.server.clientId"));

		submit.setDestTermIdCount((byte) 1);

		// 接收消息的MSISDN号码
		submit.setDestTermId(new String[] { "1064805103117" });

		submit.assignSequenceNumber();

		// 短信内容
		ShortMessage sm = new ShortMessage();
		String msg = "this is just a test";

		sm.setMessage(msg.getBytes(), (byte) 15);

		logger.info("submit req: " + submit.getData().getHexDump());
		submit.setSm(sm);

		session.write(submit);
	}

	/*
	 * 
	 * 从消息队列获取短信，进行发送
	 */
	private void doSubmitFromMQ() {

		DBHelper dbHelper = new DBHelper();
		try {
			String SMSPOCESSOR_ID = null;
			Object objMsg = MsgQUtil.quene.poll(1, TimeUnit.SECONDS);
			if (objMsg != null && !"".equals(objMsg)) {
				SMSPOCESSOR_ID = (String) objMsg;

				logger.info("正在处理队列中的消息，SMSPOCESSOR_ID:" + SMSPOCESSOR_ID);

				SmsPocessorBean bean = dbHelper.findSmsById(SMSPOCESSOR_ID);

				if (bean != null) {
					Submit submit = new Submit();

					// Messege ID
					byte[] msgid = Tools.GetMsgid();
					submit.setMsgId(msgid);

					logger.info("消息队列，开始发送短信：msgid : " + msgid);

					// Service ID
					submit.setServiceId(pu.getValue("CmppGw.client.serviceID"));
					// 源号码
					submit.setSrcId(pu.getValue("CmppGw.client.srcid"));
					// SP_ID
					submit.setMsgSrc(pu.getValue("CmppGw.server.clientId"));
					// 目的号码个数
					submit.setDestTermIdCount((byte) 1);

					// 接收消息的MSISDN号码
					submit.setDestTermId(new String[] { bean.getMSISDN() });

					// 发送消息的序列号，根据这个序列号，可以从应答报文获取区分出来这条消息
					submit.setSequenceNumber(bean.getSEQ_ID());

					// 是否需要短信汇报
					submit.setNeedReport((byte) 0);

					// 短信内容
					ShortMessage sm = new ShortMessage();
					String msg = bean.getCONTENT();

					sm.setMessage(
							new String(msg.getBytes("UTF-8"), "US-ASCII"),
							(byte) 0);
					sm.setMsgFormat((byte) 1);

					logger.info("消息队列，submit req发送短信，SEQ_ID:"
							+ bean.getSEQ_ID() + "内容为: "
							+ submit.getData().getHexDump());

					submit.setSm(sm);

					logger.info("消息队列，submit req发送短信:" + submit.dump());

					session.write(submit);

					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("SEQ_ID", bean.getSEQ_ID());
					// 更新短信状态为发送成功
					map.put("STATUS", "1");
					logger.info("更新短信发送状态为正在发送，SEQ_ID:" + map.get("SEQ_ID"));
					dbHelper.updateMmsSendFlag(map);

				} else {
					logger.info("消息队列，读取短信失败，将要重试！");
				}
			} else {
				logger.info("消息队列，没有需要发送的短信");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// 毫秒
			String sSendInterval = pu.getValue("send.interval");
			if (sSendInterval != null && !"".equals(sSendInterval)) {
				
				long sendInterval = Long.valueOf(sSendInterval);
				Thread.sleep(sendInterval);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * 从数据库读取消息，循环发送
	 */
	private void doSubmitFromDB() {

		HashMap<String, Object> getMap = new HashMap<String, Object>();
		getMap.put("status", 0);

		DBHelper dbHelper = new DBHelper();
		ArrayList<SmsPocessorBean> list = dbHelper.getMmsSendList(getMap);

		logger.info("数据库，list.size : " + list.size());
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				SmsPocessorBean bean = list.get(i);

				Submit submit = new Submit();

				// Messege ID
				byte[] msgid = Tools.GetMsgid();
				submit.setMsgId(msgid);

				logger.info("数据库，开始发送短信：msgid : " + msgid);

				// Service ID
				submit.setServiceId(pu.getValue("CmppGw.client.serviceID"));
				// 源号码
				submit.setSrcId(pu.getValue("CmppGw.client.srcid"));
				// SP_ID
				submit.setMsgSrc(pu.getValue("CmppGw.server.clientId"));
				// 目的号码个数
				submit.setDestTermIdCount((byte) 1);

				// 接收消息的MSISDN号码
				submit.setDestTermId(new String[] { bean.getMSISDN() });

				// 发送消息的序列号，根据这个序列号，可以从应答报文获取区分出来这条消息
				submit.setSequenceNumber(bean.getSEQ_ID());

				// 是否需要短信汇报
				submit.setNeedReport((byte) 1);

				// 短信内容
				ShortMessage sm = new ShortMessage();
				String msg = bean.getCONTENT();

				try {
					sm.setMessage(
							new String(msg.getBytes("UTF-8"), "US-ASCII"),
							(byte) 0);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sm.setMsgFormat((byte) 0);

				/*
				 * try { sm.setMessage(msg.getBytes("UTF-8"), (byte) 4); } catch
				 * (UnsupportedEncodingException e) { e.printStackTrace(); }
				 * sm.setMsgFormat((byte) 4);
				 */
				logger.info("数据库，submit req发送短信，SEQ_ID:" + bean.getSEQ_ID()
						+ "内容为: " + submit.getData().getHexDump());

				submit.setSm(sm);
				session.write(submit);
			}
		} else {
			logger.info("数据库，没有待发送的短信！");
		}
	}
}
