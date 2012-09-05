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
package org.kamranzafar.auth.rs.ldap;

import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kamranzafar.auth.rs.AuthException;
import org.kamranzafar.auth.rs.AuthResponse;
import org.kamranzafar.auth.rs.AuthService;
import org.kamranzafar.auth.rs.Configuration;

import com.sun.jersey.core.util.Base64;

/**
 * @author Kamran Zafar
 * 
 */
@Path("/auth")
public class LdapAuthService implements AuthService {
	private final Properties config = Configuration.getConfig();

	@GET
	@Path("/ldap/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public AuthResponse authenticateGet(@PathParam("username") String username, @PathParam("password") String password) {
		return authenticate(username, password);
	}

	@POST
	@Path("/ldap")
	@Produces(MediaType.APPLICATION_JSON)
	public AuthResponse authenticatePost(@FormParam("username") String username, @FormParam("password") String password) {
		return authenticate(username, password);
	}

	@Override
	public AuthResponse authenticate(String username, String password) {
		LdapAuthentication ldap = new LdapAuthentication(config.getProperty("ldap.host"), Integer.parseInt(config
				.getProperty("ldap.port")));
		ldap.setSearchBase(config.getProperty("ldap.sbase"));

		String la = config.getProperty("ldap.lookup");

		if (la != null && !la.trim().equals("")) {
			ldap.setLookupAttributes(la.split(","));
		}

		try {
			if ("true".equalsIgnoreCase(config.getProperty("ldap.base64"))) {
				username = new String(Base64.decode(username));
				password = new String(Base64.decode(password));
			}

			// authenticate
			Map<String, String> lookupMap = ldap.authenticate(username, password);

			AuthResponse response = new AuthResponse();

			if (lookupMap != null && !lookupMap.isEmpty()) {
				response.setLookup(lookupMap);
			}

			return response;
		} catch (NamingException e) {
			throw new AuthException(e.getMessage());
		} catch (Exception e) {
			throw new AuthException(e.getMessage());
		}
	}
}
