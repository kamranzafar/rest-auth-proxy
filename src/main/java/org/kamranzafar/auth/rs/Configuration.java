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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.kamranzafar.auth.rs.ldap.LdapConfig.Keys;

/**
 * @author Kamran Zafar
 * 
 */
public class Configuration {
	private final static Properties serverConfig = new Properties();
	private final static Map<String, Map<String, String>> authServerConfig = new HashMap<String, Map<String, String>>();
	private static Logger logger = Logger.getLogger(Configuration.class.getName());

	static {
		try {
			logger.info("Loading auth server configuration...");
			serverConfig
					.load(new FileInputStream(new File(new File(System.getProperty("user.dir")), "conf/auth.conf")));

			String ldapHosts = serverConfig.getProperty("auth.servers");

			if (ldapHosts != null) {
				String[] lha = ldapHosts.split("[\\|\\,;]");

				for (String lh : lha) {
					lh = lh.trim();

					if (!StringUtils.isBlank(lh)) {
						Map<String, String> map = new HashMap<String, String>();

						for (Keys k : Keys.values()) {
							map.put(k.key(), serverConfig.getProperty(lh + "." + k.key()));
						}

						authServerConfig.put(lh, map);
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isConfigured(String s) {
		return authServerConfig.get(s) != null;
	}

	public static String get(String server, Keys key) {
		return server == null ? serverConfig.getProperty(key.key()) : authServerConfig.get(server).get(key.key());
	}

	public static Properties getServerConfig() {
		return serverConfig;
	}

	public static Map<String, Map<String, String>> getAuthServerConfig() {
		return authServerConfig;
	}
}
