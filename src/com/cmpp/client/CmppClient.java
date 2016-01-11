package com.cmpp.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmpp.util.PropertyUtil;

public class CmppClient implements Runnable {

	private final static Object LOCK = new Object();

	public static PropertyUtil pu = new PropertyUtil("ServerIPAddress");

	public static Boolean IsConnected = false;

	private static final Logger logger = LoggerFactory
			.getLogger(CmppClient.class);

	public void startup() {
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void run() {
		while (!CmppClient.IsConnected) {
			if (!CmppClient.IsConnected) {
				try {
					// create tcp/ip connector
					IoConnector connector = new NioSocketConnector();
					connector.getFilterChain().addLast(
							"codec",
							new ProtocolCodecFilter(
									new CmppProtocolCodecFactory()));
					connector.setHandler(new CmppClientIoHandler(LOCK));
					// set connect timeout
					connector.setConnectTimeoutMillis(30000);

					ConnectFuture cf = connector
							.connect(new InetSocketAddress(pu
									.getValue("CmppGw.server.ip"),
									Integer.parseInt(pu
											.getValue("CmppGw.server.port"))));
					logger.info("CmppGw.server.ip:"
							+ pu.getValue("CmppGw.server.ip")
							+ ","
							+ Integer.parseInt(pu
									.getValue("CmppGw.server.port")));
					// wait for the connection attem to be finished
					cf.awaitUninterruptibly();
					cf.getSession().getCloseFuture().awaitUninterruptibly();
					connector.dispose();
				} catch (Exception e) {
					logger.info(e.toString());
					e.printStackTrace();

					synchronized (CmppClient.IsConnected) {
						CmppClient.IsConnected = false;
					}
				}
			}

			long awaitInterval = pu.getValue("reconnect.interval") == null
					|| "".equals(pu.getValue("reconnect.interval")) ? 1000 * 60 * 10
					: Long.valueOf(pu.getValue("reconnect.interval"));
			try {
				Thread.sleep(awaitInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/*
	 * 
	 * 用收回进程的方式执行程序
	 */
	public boolean runAsDeamon() {
		CmppClient client = new CmppClient();

		// while (true) {
		client.startup();
		logger.info("client cmpp reconnector");

		try {
			logger.info("Thread.sleep(30*1000)");
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

		return true;
	}

	public static void main(String args[]) {
		CmppClient client = new CmppClient();
		// while (true) {
		client.startup();
		logger.info("client cmpp reconnector");
		try {
			logger.info("Thread.sleep(30*1000)");
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
	}
}
