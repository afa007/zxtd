package com.cmpp.client;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cmeb.DateStyle;
import com.cmeb.util.DBHelper;
import com.cmeb.util.DateUtil;
import com.cmpp.client.thread.ActiveThread;
import com.cmpp.client.thread.MsgSendThread;
import com.cmpp.pdu.ActiveTest;
import com.cmpp.pdu.ActiveTestResp;
import com.cmpp.pdu.CmppPDU;
import com.cmpp.pdu.Connect;
import com.cmpp.pdu.ConnectResp;
import com.cmpp.pdu.Deliver;
import com.cmpp.pdu.DeliverResp;
import com.cmpp.pdu.QueryResp;
import com.cmpp.pdu.SubmitResp;
import com.cmpp.server.MinaCmpp;
import com.cmpp.sms.ByteBuffer;
import com.cmpp.sms.StrUtil;
import com.cmpp.util.CmppConstant;
import com.fh.controller.system.tools.ZxtdConstant;
import com.fh.util.PageData;
import com.fh.util.UuidUtil;
import com.google.gson.Gson;

/**
 * TODO: Document me !
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class CmppClientIoHandler extends IoHandlerAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(CmppClientIoHandler.class);
	public static AtomicInteger received = new AtomicInteger(0);
	public static AtomicInteger closed = new AtomicInteger(0);
	private final Object LOCK;

	public static boolean Connect = false;
	public static boolean Firstmsg = true;

	private ExecutorService exec = Executors.newSingleThreadExecutor();

	private DBHelper dbHelper = new DBHelper();

	public CmppClientIoHandler(Object lock) {
		LOCK = lock;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		if (!(cause instanceof IOException)) {
			logger.error("Exception: ", cause);
		} else {
			logger.info("I/O error: " + cause.getMessage());
		}
		cause.printStackTrace();
		session.close(true);

		synchronized (CmppClient.IsConnected) {
			CmppClient.IsConnected = false;
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("Session " + session.getId() + " is opened");

		doConnect(session);
		session.resumeRead();
	}

	/*
	 * 
	 * 应答报文处理函数
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {

		CmppPDU pdu = (CmppPDU) message;
		Gson gson = new Gson();

		logger.info("接收到应答报文，session id : [" + session.getId() + "]"
				+ "pdu : [" + gson.toJson(pdu, CmppPDU.class) + "]");

		PageData pd = new PageData();

		final int recCnt = received.incrementAndGet();
		if (Firstmsg == true || Connect == true) {
			Firstmsg = false;
			switch (pdu.header.getCommandId()) {
			// 连接应答报文
			case CmppConstant.CMD_CONNECT_RESP:
				ConnectResp conrsp = (ConnectResp) pdu;

				logger.info("连接应答报文，session id : [" + session.getId() + "]"
						+ "conrsp : [" + gson.toJson(conrsp, ConnectResp.class)
						+ "]");

				if (conrsp.getStatus() == 0) {

					Connect = true;
					/* 连接成功，则启动守护进程发送信息 */
					startDeamonThreads(session);

					synchronized (CmppClient.IsConnected) {
						CmppClient.IsConnected = true;
					}

				} else {

					Connect = false;
					session.close(true);

					// 连接失败
					synchronized (CmppClient.IsConnected) {
						CmppClient.IsConnected = false;
					}
				}

				break;
			// 链路检测应答报文
			case CmppConstant.CMD_ACTIVE_TEST_RESP:
				ActiveTestResp activeTestRsp = (ActiveTestResp) pdu;

				logger.info("链路检测应答报文，session id : [" + session.getId() + "]"
						+ "activeTestRsp : ["
						+ gson.toJson(activeTestRsp, ActiveTestResp.class)
						+ "]");

				ActiveThread.lastActiveTime = System.currentTimeMillis();
				break;
			// 链路检测报文
			case CmppConstant.CMD_ACTIVE_TEST:
				ActiveTest activeTest = (ActiveTest) pdu;
				logger.info("链路检测请求报文，session id : [" + session.getId() + "]"
						+ "activeTest : ["
						+ gson.toJson(activeTest, ActiveTest.class) + "]");

				ActiveTestResp activeTestResp = (ActiveTestResp) activeTest
						.getResponse();
				session.write(activeTestResp);
				logger.info("链路检测请求的应答报文，session id : [" + session.getId()
						+ "]" + "activeTestResp : ["
						+ gson.toJson(activeTestResp, ActiveTestResp.class)
						+ "]");

				break;
			// 提交短信应答报文
			case CmppConstant.CMD_SUBMIT_RESP:
				SubmitResp subresp = (SubmitResp) pdu;

				logger.info("短信发送应答报文，session id : [" + session.getId()
						+ "], SEQ_ID:" + subresp.getSequenceNumber()
						+ "subresp : ["
						+ gson.toJson(subresp, SubmitResp.class) + "]");

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("SEQ_ID", subresp.getSequenceNumber());
				// 更新短信状态为正在发送
				map.put("STATUS", "2");
				logger.info("更新短信发送状态为成功，SEQ_ID:" + map.get("SEQ_ID"));
				dbHelper.updateMmsSendFlag(map);

				break;
			// ISMG向SP提交短信
			case CmppConstant.CMD_DELIVER:
				Deliver cmppDeliver = (Deliver) pdu;

				logger.info("接收短信请求报文，session id : [" + session.getId() + "]"
						+ "cmppDeliver : ["
						+ gson.toJson(cmppDeliver, Deliver.class) + "]");

				DeliverResp cmppDeliverResp = (DeliverResp) cmppDeliver
						.getResponse();
				session.write(cmppDeliverResp);
				logger.info("发送-接收短信应答报文，session id : [" + session.getId()
						+ "]" + "cmppDeliverResp : ["
						+ gson.toJson(cmppDeliverResp, DeliverResp.class) + "]");

				if (cmppDeliver.getIsReport() == 0) { // 接收到的短信写入库表
					logger.info("接收到消息：sms_mo");

					logger.info("将接收到的短信写入数据库库, MSISDN:"
							+ cmppDeliver.getSrcTermId() + ", CONTENT:"
							+ cmppDeliver.getMsgContent());

					HashMap<String, Object> insertMap = new HashMap<String, Object>();
					// 接收的消息写入数据库表
					insertMap.put("SMSPOCESSOR_ID", UuidUtil.get32UUID());
					insertMap.put("MSISDN", cmppDeliver.getSrcTermId());
					insertMap.put("TYPE", ZxtdConstant.SMS_TYPE_RECV);
					insertMap.put("CONTENT", cmppDeliver.getMsgContent());

					// 查询卡对应的USERID
					String userName = dbHelper.getUserNameByMsisdn(cmppDeliver
							.getSrcTermId());

					// TODO 可以根据卡号查询得到用户ID
					insertMap.put("USERID", userName);
					insertMap.put("STATUS", "1"); // 接收成功
					insertMap.put("CREATETIME", DateUtil.DateToString(
							new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS));
					dbHelper.insertMmsSendMsg(insertMap);

				} else { // 接收到的短信报告不写入库表
					logger.info("接收到的是短信报告：sms_stat");
					ByteBuffer buffer = cmppDeliver.getSm().getData();
					try {
						logger.info("buffer.length=" + buffer.length());
						logger.info("setMsgId:"
								+ (StrUtil.bytesToHex(buffer.removeBytes(8)
										.getBuffer())));
						logger.info("setStat:" + (buffer.removeStringEx(7)));
						logger.info("setSubmitTime:"
								+ (buffer.removeStringEx(10)));
						logger.info("setDoneTime:"
								+ (buffer.removeStringEx(10)));
						logger.info("setUserNumber:"
								+ (buffer.removeStringEx(32)));
						logger.info("setSmscSequence:" + (buffer.removeInt()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case CmppConstant.CMD_QUERY_RESP:
				QueryResp queryresp = (QueryResp) pdu;

				logger.info("session id : [" + session.getId() + "]"
						+ "queryresp : ["
						+ gson.toJson(queryresp, QueryResp.class) + "]");

				processQueryResp(queryresp);

				break;
			default:
				logger.warn("Unexpected PDU received! PDU Header: "
						+ pdu.header.getData().getHexDump());
				break;
			}
			logger.info("\n");
		}
		if (recCnt == MinaCmpp.MSG_COUNT) {
			synchronized (LOCK) {
				LOCK.notifyAll();
			}
		}

		// session.close(true);
	}

	/*
	 * 
	 * 守护进程，主要功能： 1、链路检测 2、发送短信 3、定时获取统计数据
	 */
	private boolean startDeamonThreads(IoSession session) {

		// 1、链路检测线程
		Thread active = new Thread(new ActiveThread(session));
		active.setDaemon(true);
		active.start();

		// 发送短信
		Thread send = new Thread(new MsgSendThread(session));
		send.setDaemon(true);
		send.start();

		// 查询统计信息
		// Thread query = new Thread(new QueryThread(session));
		// query.setDaemon(true);
		// query.start();

		return true;
	}

	/* 向服务器发送连接请求 */
	public void doConnect(IoSession session) {

		Connect request = new Connect(CmppConstant.TRANSMITTER);

		// Client ID
		request.setClientId(CmppClient.pu.getValue("CmppGw.server.clientId"));
		request.setSharedSecret(CmppClient.pu
				.getValue("CmppGw.server.password"));
		/*
		 * 权限验证，MD5（Source_Addr+9 字节的0 +shared secret+timestamp）
		 */
		request.setAuthClient(request.genAuthClient());
		// 协议版本号
		request.setVersion(CmppConstant.PROTOCALTYPE_VERSION_CMPP3);
		request.setTimeStamp(request.genTimeStamp());
		// 消息序列号
		request.assignSequenceNumber();

		logger.info("连接请求，Connect hex: " + request.getData().getHexDump());
		Gson gson = new Gson();
		logger.info("连接请求，session id: [" + session.getId()
				+ "], pdu.header : [" + gson.toJson(request, Connect.class)
				+ "]");
		logger.info("连接请求，Connect json: " + request.dump());
		logger.info("连接请求，Connect string: " + request.byteBufferToString());

		session.write(request);
	}

	private boolean processQueryResp(QueryResp queryResp) {
		boolean result = false;

		queryResp.getMo_scs();

		return result;
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("Creation of session " + session.getId());
		session.setAttribute(MinaCmpp.OPEN);
		session.suspendRead();

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		session.removeAttribute(MinaCmpp.OPEN);
		logger.info("Session closed, session.getId(): " + session.getId());
		final int clsd = closed.incrementAndGet();

		if (clsd == MinaCmpp.MSG_COUNT) {
			synchronized (LOCK) {
				LOCK.notifyAll();
			}
		}
	}
}
