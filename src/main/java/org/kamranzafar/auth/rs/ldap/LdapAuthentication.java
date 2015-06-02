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

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Kamran Zafar
 *
 */
public class LdapAuthentication {
    private String ldapHost;
    private int ldapPort;
    private String searchBase;
    private String searchFilter;
    private String[] lookupAttributes;

    private static Logger logger = Logger.getLogger(LdapAuthentication.class.getName());

    public LdapAuthentication(String host) {
        this.ldapHost = host;
        this.ldapPort = 389;
    }

    public LdapAuthentication(String host, int ldapPort) {
        this(host);
        this.ldapPort = ldapPort;
    }

    public Map<String, List<String>> authenticate(String user, String pass) throws NamingException {
        if (searchFilter == null) {
            searchFilter = LdapConfig.DEFAULT_LDAP_SFILTER;
        }

        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(lookupAttributes);
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchCtls.setCountLimit(0);

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, pass);

        DirContext ctxGC = new InitialLdapContext(env, null);

        logger.fine("User authenticated.");

        NamingEnumeration<SearchResult> answer = ctxGC.search(searchBase, searchFilter, searchCtls);
        Map<String, List<String>> retMap = new HashMap<String, List<String>>();
        if (answer.hasMoreElements()) {
            Attributes attrs = answer.next().getAttributes();

            if (attrs != null) {
                NamingEnumeration<? extends Attribute> ne = attrs.getAll();

                while (ne.hasMore()) {
                    Attribute attr = ne.next();

                    List<String> valList = retMap.get(attr.getID());

                    if (valList == null) {
                        valList = new ArrayList<String>();
                        retMap.put(attr.getID(), valList);
                    }

                    NamingEnumeration<?> vals = attr.getAll();

                    while (vals.hasMoreElements()) {
                        valList.add(vals.next().toString());
                    }
                }
                ne.close();
            }

            return retMap;
        } else {
            throw new RuntimeException("Invalid User");
        }
    }

    public int getLdapPort() {
        return ldapPort;
    }

    public void setLdapPort(int ldapPort) {
        this.ldapPort = ldapPort;
    }

    public String getLdapHost() {
        return ldapHost;
    }

    public void setLdapHost(String ldapHost) {
        this.ldapHost = ldapHost;
    }

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String[] getLookupAttributes() {
        return lookupAttributes;
    }

    public void setLookupAttributes(String[] lookupAttributes) {
        this.lookupAttributes = lookupAttributes;
    }
}
