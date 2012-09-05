package org.kamranzafar.auth.rs.ldap;

import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestLdapAuthentication {
	@Test
	public void testAuth() throws NamingException {
		LdapAuthentication auth = new LdapAuthentication("localhost");
		auth.setSearchBase("ou=People,dc=ldap,dc=local");

		auth.authenticate("testuser", "testpass");
	}
}
