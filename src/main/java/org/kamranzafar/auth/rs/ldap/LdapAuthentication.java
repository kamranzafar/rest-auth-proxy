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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

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

	public LdapAuthentication(String host) {
		this.ldapHost = host;
		lookupAttributes = new String[] { "sn", "givenName", "mail" };
		this.ldapPort = 389;
	}

	public LdapAuthentication(String host, int ldapPort) {
		this(host);
		this.ldapPort = ldapPort;
	}

	public Map<String, String> authenticate(String user, String pass) throws NamingException {
		if (searchFilter == null) {
			searchFilter = "(objectclass=*)";
		}

		// String dn = "uid=" + user + "," + searchBase;

		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(lookupAttributes);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, pass);

		DirContext ctxGC = new InitialLdapContext(env, null);
		NamingEnumeration<SearchResult> answer = ctxGC.search(searchBase, searchFilter, searchCtls);
		if (answer.hasMoreElements()) {
			Attributes attrs = answer.next().getAttributes();
			Map<String, String> amap = null;
			if (attrs != null) {
				amap = new HashMap<String, String>();
				NamingEnumeration<? extends Attribute> ne = attrs.getAll();

				while (ne.hasMore()) {
					Attribute attr = ne.next();
					amap.put(attr.getID(), attr.get().toString());
				}
				ne.close();
			}
			return amap;
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