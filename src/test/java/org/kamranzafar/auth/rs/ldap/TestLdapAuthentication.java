package org.kamranzafar.auth.rs.ldap;

import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestLdapAuthentication {
	@Test
	public void testOpenLdap() throws NamingException {
		LdapAuthentication auth = new LdapAuthentication("localhost");
		auth.setSearchBase("ou=People,dc=ldap,dc=local");
		auth.setLookupAttributes(new String[] { "cn", "homeDirectory", "loginShell" });

		System.out.println(auth.authenticate("testuser", "testpass"));
	}
}
