/**
 * Copyright 2012 Kamran Zafar
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kamranzafar.auth.rs.ldap;

import com.sun.jersey.core.util.Base64;
import org.kamranzafar.auth.rs.*;
import org.kamranzafar.auth.rs.ldap.LdapConfig.Keys;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Kamran Zafar
 *
 */
@Path("/auth")
public class LdapAuthService implements AuthService {
    private static Logger logger = Logger.getLogger(LdapAuthService.class.getName());

    @GET
    @Path("/ldap/{username}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthResponse authenticateGet(@PathParam("username") String username, @PathParam("password") String password) {
        return authenticate(null, username, password);
    }

    @GET
    @Path("/ldap/{server}/{username}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthResponse authenticateGet(@PathParam("server") String server, @PathParam("username") String username,
                                        @PathParam("password") String password) {
        return authenticate(server, username, password);
    }

    @POST
    @Path("/ldap")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthResponse authenticatePost(@FormParam("server") String server, @FormParam("username") String username,
                                         @FormParam("password") String password) {
        return authenticate(server, username, password);
    }

    @Override
    public AuthResponse authenticate(String server, String username, String password) {
        logger.fine("New request recieved");

        if (server != null && !Configuration.isConfigured(server)) {
            throw new AuthException("No configuration found for server [" + server + "]");
        }

        if (StringUtils.isEmpty(username)) {
            throw new AuthException("Username not supplied");
        }

        try {
            if ("true".equalsIgnoreCase(Configuration.get(server, Keys.LDAP_BASE64))) {
                username = new String(Base64.decode(username));
                password = new String(Base64.decode(password));
            }

            boolean ad = !StringUtils.isEmpty(Configuration.get(server, Keys.LDAP_AD))
                    && "true".equalsIgnoreCase(Configuration.get(server, Keys.LDAP_AD));
            String sbase = Configuration.get(server, Keys.LDAP_SBASE);
            String domain = Configuration.get(server, Keys.LDAP_AD_DOMAIN);
            String principle; // dn

            if (ad) {
                if (StringUtils.isEmpty(domain)) {
                    principle = username;
                } else {
                    principle = domain + "\\" + username;
                }
            } else {
                principle = "uid=" + username + "," + sbase;
            }

            LdapAuthentication ldap = getLdap(server, username);

            logger.fine("Authenticating request");
            // authenticate
            Map<String, List<String>> lookupMap = ldap.authenticate(principle, password);

            AuthResponse response = new AuthResponse();

            if (lookupMap != null && !lookupMap.isEmpty()) {
                response.setLookup(lookupMap);
            }

            return response;
        } catch (Exception e) {
            throw new AuthException(e.getMessage(), e);
        }
    }

    private LdapAuthentication getLdap(String server, String username) {
        String host = Configuration.get(server, Keys.LDAP_HOST);
        int port = StringUtils.toInt(Configuration.get(server, Keys.LDAP_PORT), LdapConfig.DEFAULT_LDAP_PORT);
        boolean ad = !StringUtils.isEmpty(Configuration.get(server, Keys.LDAP_AD))
                && "true".equalsIgnoreCase(Configuration.get(server, Keys.LDAP_AD));
        String sbase = Configuration.get(server, Keys.LDAP_SBASE);
        String lookup = Configuration.get(server, Keys.LDAP_LOOKUP);
        String sfilter = Configuration.get(server, Keys.LDAP_SFILTER);

        LdapAuthentication ldap = new LdapAuthentication(host, port);

        if (!StringUtils.isEmpty(sbase)) {
            if (!ad) {
                sbase = "uid=" + username + "," + sbase;
            }

            ldap.setSearchBase(sbase);
        }

        if (!StringUtils.isEmpty(lookup)) {
            ldap.setLookupAttributes(lookup.split(","));
        } else {
            if (ad) {
                ldap.setLookupAttributes(LdapConfig.DEFAULT_AD_LOOKUP_ATTR);
            } else {
                ldap.setLookupAttributes(LdapConfig.DEFAULT_LDAP_LOOKUP_ATTR);
            }
        }

        if (!StringUtils.isEmpty(sfilter)) {
            ldap.setSearchFilter(sfilter.replaceAll("\\{username\\}", username));
        } else {
            if (ad) {
                ldap.setSearchFilter(LdapConfig.DEFAULT_AD_SFILTER.replaceAll("\\{username\\}", username));
            } else {
                ldap.setSearchFilter(LdapConfig.DEFAULT_LDAP_SFILTER);
            }
        }

        return ldap;
    }
}
