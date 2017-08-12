package org.tio.http.server;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.threadpool.SynThreadPoolExecutor;
import org.tio.http.common.HttpUuid;
import org.tio.http.server.handler.DefaultHttpRequestHandler;
import org.tio.http.server.handler.IHttpRequestHandler;
import org.tio.http.server.listener.IHttpServerListener;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.session.IHttpSessionStore;
import org.tio.http.server.session.impl.GuavaHttpSessionStore;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;

/**
 * 
 * @author tanyaowu
 */
public class HttpServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HttpServerStarter.class);

	private HttpServerConfig httpServerConfig = null;

	private HttpServerAioHandler httpServerAioHandler = null;

	private HttpServerAioListener httpServerAioListener = null;

	//	private HttpGroupListener httpGroupListener = null;

	private ServerGroupContext serverGroupContext = null;

	private AioServer aioServer = null;

	private IHttpRequestHandler httpRequestHandler;

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public static void main(String[] args) throws IOException {
	}
	
	private void init(HttpServerConfig httpServerConfig, IHttpRequestHandler httpRequestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this.httpServerConfig = httpServerConfig;
		this.httpRequestHandler = httpRequestHandler;
		this.httpServerAioHandler = new HttpServerAioHandler(httpServerConfig, httpRequestHandler);
		httpServerAioListener = new HttpServerAioListener();
		serverGroupContext = new ServerGroupContext(httpServerAioHandler, httpServerAioListener, tioExecutor, groupExecutor);
		serverGroupContext.setHeartbeatTimeout(1000 * 10);
		serverGroupContext.setShortConnection(true);

		aioServer = new AioServer(serverGroupContext);

		HttpUuid imTioUuid = new HttpUuid();
		serverGroupContext.setTioUuid(imTioUuid);
	}

	public HttpServerStarter(HttpServerConfig httpServerConfig, IHttpRequestHandler httpRequestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		init(httpServerConfig, httpRequestHandler, tioExecutor, groupExecutor);
	}

	public HttpServerStarter(HttpServerConfig httpServerConfig, IHttpRequestHandler httpRequestHandler) {
		this(httpServerConfig, httpRequestHandler, null, null);
	}

	public HttpServerStarter(String pageRootDir, int serverPort, String[] scanPackages, IHttpServerListener httpServerListener) {
		this(pageRootDir, serverPort, scanPackages, httpServerListener, null, null, null);
	}
	
	public HttpServerStarter(String pageRootDir, int serverPort, String[] scanPackages, IHttpServerListener httpServerListener, IHttpSessionStore httpSessionStore) {
		this(pageRootDir, serverPort, scanPackages, httpServerListener, httpSessionStore, null, null);
	}
	
	public HttpServerStarter(String pageRootDir, int serverPort, String[] scanPackages, IHttpServerListener httpServerListener, IHttpSessionStore httpSessionStore, SynThreadPoolExecutor tioExecutor,
			ThreadPoolExecutor groupExecutor) {
		int port = serverPort;
		String pageRoot = pageRootDir;

		httpServerConfig = new HttpServerConfig(port, null);
		httpServerConfig.setRoot(pageRoot);
		if (httpSessionStore != null) {
			httpServerConfig.setHttpSessionStore(httpSessionStore);
		}
//		} else {
//			httpServerConfig.setHttpSessionStore(GuavaHttpSessionStore.getInstance(httpServerConfig.getSessionTimeout()));
//		}
		
		//		String[] scanPackages = new String[] { AppStarter.class.getPackage().getName() };
		Routes routes = new Routes(scanPackages);
		DefaultHttpRequestHandler httpRequestHandler = new DefaultHttpRequestHandler(httpServerConfig, routes);
		httpRequestHandler.setHttpServerListener(httpServerListener);
		
		init(httpServerConfig, httpRequestHandler, tioExecutor, groupExecutor);
	}
	
	public void start() throws IOException {
		if (httpServerConfig.getHttpSessionStore() == null) {
			httpServerConfig.setHttpSessionStore(GuavaHttpSessionStore.getInstance(httpServerConfig.getSessionTimeout()));
		}
		
		aioServer.start(this.httpServerConfig.getBindIp(), this.httpServerConfig.getBindPort());
	}
	
	public void stop() throws IOException {
		aioServer.stop();
	}

	/**
	 * @return the httpServerAioHandler
	 */
	public HttpServerAioHandler getHttpServerAioHandler() {
		return httpServerAioHandler;
	}

	/**
	 * @return the httpServerAioListener
	 */
	public HttpServerAioListener getHttpServerAioListener() {
		return httpServerAioListener;
	}

	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
	}

	/**
	 * @return the httpServerConfig
	 */
	public HttpServerConfig getHttpServerConfig() {
		return httpServerConfig;
	}

	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
}
