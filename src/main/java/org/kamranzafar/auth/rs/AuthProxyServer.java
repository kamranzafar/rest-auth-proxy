/**
 * Copyright 2012 Kamran Zafar 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package org.kamranzafar.auth.rs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * @author Kamran Zafar
 * 
 */
public class AuthProxyServer implements Runnable {
	private static final String LOCALHOST = "127.0.0.1";
	private static final String SERVER_BIND = "server.bind";
	private static final String SERVER_PORT = "server.port";

	private static Properties config = Configuration.getServerConfig();
	private static final URI BASE_URI = getBaseURI();
	private static Logger logger = Logger.getLogger(AuthProxyServer.class.getName());

	private HttpServer server = null;
	private final Object lock = new Object();

	protected HttpServer startServer() throws IOException {
		logger.info("Starting rest-auth-proxy server...");
		ResourceConfig rc = new PackagesResourceConfig("org.kamranzafar.auth.rs", "org.kamranzafar.auth.rs.ldap");
		rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
	}

	protected void stopServer() {
		if (server != null) {
			logger.info("Shutting down rest-auth-proxy server...");
			server.stop();
		}
	}

	@Override
	public void run() {
		try {
			server = startServer();
			logger.info("rest-auth-proxy server started, press ctrl-c to shutdown");

			while (server.isStarted()) {
				synchronized (lock) {
					try {
						lock.wait(10000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int getPort(int defaultPort) {
		String port = System.getProperty("auth.server.port");

		if (port == null) {
			port = config.getProperty(SERVER_PORT, "" + defaultPort);
		}

		return StringUtils.toInt(port, defaultPort);
	}

	private static URI getBaseURI() {
		String addr = LOCALHOST;
		String bindAddress = config.getProperty(SERVER_BIND);

		if (bindAddress == null) {
			try {
				InetAddress ia = InetAddress.getLocalHost();
				addr = ia.getHostAddress();
			} catch (UnknownHostException e) {
			}
		} else {
			addr = bindAddress;
		}

		return UriBuilder.fromUri("http://" + addr + "/").port(getPort(9998)).build();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		final AuthProxyServer authServer = new AuthProxyServer();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				authServer.stopServer();
				System.out.println("Shutdown complete.");
				System.out.flush();
			}
		});

		Thread authServerThread = new Thread(authServer);
		authServerThread.start();
		authServerThread.join();
	}
}
