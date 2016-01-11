package   com.cmpp.client.thread;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import   com.cmpp.pdu.ActiveTest;

public class ActiveThread extends Thread {
    private IoSession session = null;
    private static final Logger logger = LoggerFactory
            .getLogger(ActiveThread.class);

    private static final long heartbeatInterval = 10000;
    private static final long heartbeatRetry = 3;
    private static final long reconnectInterval = 10000;
    public static long lastActiveTime = 0;
    private long lastCheckTime = 0;

    public ActiveThread(IoSession s) {
        setDaemon(true);
        this.session = s;
        lastCheckTime = System.currentTimeMillis();
        lastActiveTime = System.currentTimeMillis();
    }

    public void run() {
        try {
            while (session.isConnected()) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime - lastCheckTime) > heartbeatInterval) {
                    logger.info("CmppSession.checkConnection");

                    if ((currentTime - lastActiveTime) < (heartbeatInterval * heartbeatRetry)) {
                        logger.info("send ActiveTest");
                        lastCheckTime = currentTime;
                        
                        ActiveTest activeTest = new ActiveTest();
                        activeTest.assignSequenceNumber();

                		Date date = new Date();
                		Format formatter = new SimpleDateFormat("MMddHHmmss");
                		int timeStamp = Integer.valueOf(formatter.format(date));
                        activeTest.timeStamp = timeStamp;
                        session.write(activeTest);
                        
                    } else {
                        logger.info("connection lost!");
                        session.close(true);
                        break;
                    }
                }
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException e) {
                    //
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
