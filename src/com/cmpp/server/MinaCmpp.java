package   com.cmpp.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.integration.jmx.IoServiceMBean;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * TODO : Add documentation
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * 
 */
public class MinaCmpp extends IoHandlerAdapter implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(MinaCmpp.class);

    public static final int MSG_SIZE = 5000;
    public static final int MSG_COUNT = 10;
    private static final int PORT = 7890;
    private static final int BUFFER_SIZE = 81920;

    public static final String OPEN = "open";

    public SocketAcceptor acceptor;
    public SocketConnector connector;

    private final Object LOCK = new Object();

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        public Thread newThread(final Runnable r) {
            return new Thread(null, r, "MinaThread", 64 * 1024 * 1024);
        }
    };

    private OrderedThreadPoolExecutor executor;

    public static AtomicInteger sent = new AtomicInteger(0);

    public MinaCmpp() throws IOException {

        executor = new OrderedThreadPoolExecutor(0, 1000, 60, TimeUnit.SECONDS,
                THREAD_FACTORY);

        acceptor = new NioSocketAcceptor(Runtime.getRuntime()
                .availableProcessors() + 1);

        acceptor.setReuseAddress(true);

        acceptor.getSessionConfig().setReceiveBufferSize(BUFFER_SIZE);

        acceptor.getFilterChain().addLast("threadPool",
                new ExecutorFilter(executor));

        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new CmppProtocolCodecFactory()));

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        IoServiceMBean acceptorMBean = new IoServiceMBean(acceptor);
        ObjectName acceptorName;

        try {
            acceptorName = new ObjectName(acceptor.getClass().getPackage()
                    .getName()
                    + ":type=acceptor,name="
                    + acceptor.getClass().getSimpleName());
            mBeanServer.registerMBean(acceptorMBean, acceptorName);

            // MBeanServer mBeanServer =
            // ManagementFactory.getPlatformMBeanServer();
            // mBeanServer.registerMBean(new IoSessionMBean(session),
            // new ObjectName(session.getClass()
            // .getPackage().getName()
            // + ":type=session,name=" + session.getClass().getSimpleName()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*public void start() throws Exception {

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }
*/
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {

        if (!(cause instanceof IOException)) {
            logger.error("Exception: ", cause);
        } else {
            logger.info("I/O error: " + cause.getMessage());
        }
        session.close(true);
    }

    public static void main(String[] args) throws Exception {
        new MinaCmpp().run();
    }

    @Override
    public void run() {
        try {

            final InetSocketAddress socketAddress = new InetSocketAddress(
                    "127.0.0.1", PORT);
            acceptor.setHandler(new CmppIoHandler(LOCK));
            acceptor.bind(socketAddress);

            logger.info("MinaCmpp启动成功，端口号为：" + PORT);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
