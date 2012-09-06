Using rest-auth-proxy server
============================

rest-auth-proxy is a Java based restful ldap-authentication http server that can be used to authenticate users against ldap and
active directory. It serves as an authentication proxy server between the authenticating application and the ldap server. 
With the use of a restful architecture it can be used by any application developed in any technology for user authentication. 
It is very easy to setup and simple to use and also supports *base64* username and password encoding.

Building from source
--------------------

Follow the steps below in order to build rest-auth-proxy from source. This requires git and maven:

1. Checkout the source from git
> git clone git&#64;github.com:kamranzafar/rest-auth-proxy.git

2. Compile and package using maven
> mvn clean compile package

This will compile the source and create a *rest-auth-proxy-dist.zip* file in the target directory, which can be used for installation.

Installation
------------

rest-auth-proxy server requires Java 6. Follow the below steps to install the server

1. Make sure JAVA_HOME environment variable is set and pointing to the bin directory of JDK/JRE
2. Extract the contents of the dist-zip file to any location.
3. Edit the *conf/auth.conf* file and set ldap server preferences. 

Configuration
-------------

Below is an example configuration, this needs to be in the *conf/auth.conf* file:

> \# Server port<br />
 server.port=9998<br /><br />
 \# Ldap config<br />
 ldap.host=localhost<br />
 ldap.port=389 \# optional<br /><br />
 \# ldap search base<br />
 ldap.sbase=ou=People,dc=ldap,dc=local<br /><br />
 \# ldap search filter (optional)<br />
 ldap.sfilter=(objectclass=*)<br /><br />
 \# ldap comma-separated lookup attributes<br />
 ldap.lookup=cn,homeDirectory,loginShell<br /><br />
 \# base64 encoding<br />
 ldap.base64=true<br /><br />
 \# Active directory specific configuration<br />
 \# Active directory (optional, but must be set to true for AD)<br />
 ldap.ad=false<br />
 \# AD domain (optional)<br />
 ldap.ad.domain=MYDOMAIN

Running the server
------------------

The auth server can run on both windows and linux. Simply execute the *run.bat* file on windows or *run.sh* file on linux
in order to run the auth proxy server. You can also install the batch file as a windows service using any Java service wrapper
tools.

Authentication
--------------

In order to authenticate the user, the application can pass username and password as a HTTP GET or POST request like below:

### Passing username and password as a GET request
Below is how to make GET request
> http://\[server-ip\]:9998/auth/ldap/username/password

### Passing username and password as a POST request
The username and password can be passed as a POST request to the following URL.
> http://\[server-ip\]:9998/auth/ldap

### Testing
The GET requests can be tested from a web browser. On linux you can also test authentication using curl like:

> curl http://\[server-ip\]:9998/auth/ldap/testuser/testpass<br />
> curl -d "username=testuser&password=testpass" http://\[server-ip\]:9998/auth/ldap

*The auth server has been tested against Active Directory and Open LDAP server*

### Server response
The server response is in json format, and returns the following on successful authentication
> {"status":"SUCCESS","lookup":{"cn":"testuser","homeDirectory":"/home/testuser","loginShell":"/bin/bash"}}

The auth server also supports base64 encoded username and password, which can easily be turned on/off by configuring the *ldap.base64* property
in the *conf/auth.conf* configuration file. If base64 encoding is enabled, the username and password must be encoded before passing to the server.

License
-------

The rest-auth-proxy server is open source and is free to use for both personal and commercial purposes under the terms and
conditions of [Apache Software License](http://www.apache.org/licenses/LICENSE-2.0.html "ASL 2.0").

__Note: This document is in [markdown](http://daringfireball.net/projects/markdown "Markdown") format__
