package com.cmpp.client.thread;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmeb.MmsSend;
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

	private static final long send_Interval = 30000;

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

				doSubmitFromMQ();
				try {
					Thread.sleep(send_Interval);
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

		SmsPocessorService smspocessorService = new SmsPocessorService();
		while (isRunning) {
			try {
				String SMSPOCESSOR_ID = null;
				PageData pd = new PageData();
				if ((SMSPOCESSOR_ID = (String) MsgQUtil.quene.poll(1,
						TimeUnit.SECONDS)) != null) {
					pd.put("SMSPOCESSOR_ID", SMSPOCESSOR_ID);

					pd = smspocessorService.findById(pd);
					if (pd != null) {
						Submit submit = new Submit();

						// Messege ID
						byte[] msgid = Tools.GetMsgid();
						submit.setMsgId(msgid);
						// Service ID
						submit.setServiceId(pu
								.getValue("CmppGw.client.serviceID"));
						// 源号码
						submit.setSrcId(pu.getValue("CmppGw.client.srcid"));
						// SP_ID
						submit.setMsgSrc(pu.getValue("CmppGw.server.clientId"));
						// 目的号码个数
						submit.setDestTermIdCount((byte) 1);

						// 接收消息的MSISDN号码
						submit.setDestTermId(new String[] { pd
								.getString("PHONE") });

						// 发送消息的序列号，根据这个序列号，可以从应答报文获取区分出来这条消息
						submit.setSequenceNumber((Integer) pd.get("SEQ_ID"));

						// 短信内容
						ShortMessage sm = new ShortMessage();
						String msg = pd.getString("CONTENT");
						// 中文GB编码
						sm.setMessage(msg.getBytes(), (byte) 15);

						logger.info("submit req发送短信，SEQ_ID:" + pd.get("SEQ_ID")
								+ "内容为: " + submit.getData().getHexDump());

						submit.setSm(sm);
						session.write(submit);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * 从数据库读取消息，循环发送
	 */
	private void doSubmitFromDB() {

		HashMap<String, Object> getMap = new HashMap<String, Object>();
		getMap.put("flag", 0);

		DBHelper dbHelper = new DBHelper();
		List<MmsSend> list = dbHelper.getMmsSendList(getMap);

		logger.info("list : " + list);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				MmsSend mmsSend = list.get(i);

				Submit submit = new Submit();

				byte[] msgid = Tools.GetMsgid();

				submit.setMsgId(msgid);
				// pktotal, pknumber 默认都是1

				submit.setServiceId("abc");

				// 源号码
				submit.setSrcId(pu.getValue("CmppGw.client.src"));
				submit.setMsgSrc("1016");
				submit.setDestTermIdCount((byte) 1);

				// 接收消息的MSISDN号码
				submit.setDestTermId(new String[] { mmsSend.getMobile() });
				submit.setFeeTermId("");
				submit.assignSequenceNumber();

				// 短信内容
				ShortMessage sm = new ShortMessage();
				String msg = mmsSend.getContent();
				sm.setMessage(msg.getBytes(), (byte) 15);
				submit.setSm(sm);

				submit.setLinkId("12345678901234567890");

				session.write(submit);

				/*
				 * 更新短信发送表的状态为1已发送
				 */
				HashMap<String, Object> updateMap = new HashMap<String, Object>();
				updateMap.put("id", mmsSend.getId());
				updateMap.put("flag", 1);
				if (dbHelper.updateMmsSendFlag(updateMap) < 0) {
					logger.error("更新短信发送状态失败，mmsSend id: " + mmsSend.getId());
				}
			}
		} else {

			logger.info("no pending msg to send");
		}
	}
}
