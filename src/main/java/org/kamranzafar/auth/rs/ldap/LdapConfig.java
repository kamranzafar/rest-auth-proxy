package org.kamranzafar.auth.rs.ldap;

public class LdapConfig {
	public static final int DEFAULT_LDAP_PORT = 389;
	public static final String DEFAULT_AD_SFILTER = "(&(objectClass=user)(sAMAccountName={username}))";
	public static final String DEFAULT_LDAP_SFILTER = "(objectclass=*)";
	public static final String[] DEFAULT_AD_LOOKUP_ATTR = new String[] { "sn", "givenName", "mail" };
	public static final String[] DEFAULT_LDAP_LOOKUP_ATTR = new String[] { "cn", "homeDirectory", "loginShell" };

	public static enum Keys {
		LDAP_HOST("ldap.host"), LDAP_PORT("ldap.port"), LDAP_SBASE("ldap.sbase"), LDAP_SFILTER("ldap.sfilter"), LDAP_LOOKUP(
				"ldap.lookup"), LDAP_BASE64("ldap.base64"), LDAP_AD("ldap.ad"), LDAP_AD_DOMAIN("ldap.ad.domain");

		private final String key;

		Keys(String key) {
			this.key = key;
		}

		public String key() {
			return key;
		}
	}
}
