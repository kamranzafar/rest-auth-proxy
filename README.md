Using rest-auth-proxy server
============================

rest-auth-proxy is a Java based restful ldap-authentication microservice that can be used to authenticate users against ldap and
active directory. It serves as an authentication proxy between the calling application and the ldap servers. 
With the use of a restful architecture & microservice pattern it can be used by any application developed in any technology for user authentication. 
It is very easy to setup and simple to use and also supports *base64* username and password encoding.

Building from source
--------------------

Follow the steps below in order to build rest-auth-proxy from source. This requires git and maven:

1. Checkout the source from git
<pre><code> git clone git&#64;github.com:kamranzafar/rest-auth-proxy.git
</code></pre>

2. Compile and package using maven
<pre><code> mvn clean compile package
</code></pre>

This will compile the source and create a *rest-auth-proxy-dist.zip* file in the target directory, which can be used for installation.

Installation
------------

rest-auth-proxy microservice requires Java 6. Follow the below steps to install the server

1. Make sure JAVA_HOME environment variable is set and pointing to the home directory of JDK/JRE
2. Extract the contents of the dist-zip file to any location.
3. Edit the *conf/auth.conf* file and set ldap server preferences. 

Configuration
-------------

Below is an example configuration, this needs to be in the *conf/auth.conf* file:
<pre><code> ################## Server config

 # Server port
 server.port=9998

 # Server bind address
 server.bind=localhost

 ################## Ldap config
 
 ldap.host=localhost
 ldap.port=389 # optional, default is 389

 # ldap search base
 ldap.sbase=ou=People,dc=ldap,dc=local

 # ldap search filter (optional)
 ldap.sfilter=(objectclass=*)

 # ldap comma-separated lookup attributes
 ldap.lookup=cn,homeDirectory,loginShell

 # base64 encoding
 ldap.base64=true

 ################## Active directory specific config

 # Optional, but must be set to true for AD
 ldap.ad=false

 # AD domain (optional)
 ldap.ad.domain=MYDOMAIN
</code></pre>

It is also possible to configure multiple ldap servers, below is an example.
<pre><code> # Specify ldap server config names
auth.servers=ldap1,ldap2

# configuration for ldap1 auth server
ldap1.ldap.host=ldapserver1.hostname
ldap1.ldap.sbase=ou=People,dc=ldap,dc=local
ldap1.ldap.lookup=cn,homeDirectory,loginShell
ldap1.ldap.base64=false

# configuration for ldap2 auth server
ldap2.ldap.host=ldapserver2.hostname
ldap2.ldap.sbase=ou=People,dc=ldap,dc=local
ldap2.ldap.lookup=cn,homeDirectory,loginShell
ldap2.ldap.base64=true
</code></pre>

*Note: Multiple server configuration is available from version 0.2*

Running the server
------------------

The auth-proxy microservice can run on both windows and linux. Simply execute the *run.bat* file on windows or *run.sh* file on linux
in order to run the auth proxy microservice. You can also install the batch file as a windows service using any Java service wrapper
tools. The service by default logs INFO messages to console and captures a detailed log in *ras.log* file. The default logging 
preferences can be changed by updating the *conf/logging.properties* file.

Authentication
--------------

In order to authenticate the user, the application can pass username and password as a HTTP GET or POST request like below:

### Passing username and password as a GET request
Below is how to make GET request
<pre><code> http://[server-ip]:9998/auth/ldap/username/password
</code></pre>

Below is an example to authenticate against a named ldap server *(see multiple server config in the previous section)*
<pre><code> http://[server-ip]:9998/auth/ldap/ldap1/username/password
</code></pre>

### Passing username and password as a POST request
The username and password can be passed as a POST request to the following URL.
<pre><code> http://[server-ip]:9998/auth/ldap
</code></pre>

It is also possible to specify a named ldap server, by passing the *server* parameter in the request.

### Testing
The GET requests can be tested from a web browser. On linux you can also test authentication using curl like:
<pre><code> curl http://[server-ip]:9998/auth/ldap/testuser/testpass
 curl -d "server=ldap1&username=testuser&password=testpass" http://[server-ip]:9998/auth/ldap
</code></pre>

#### Performance
The auth-proxy microservice can easily be performance tested using any load testing tool, below is an example on linux using *httperf*.
<pre><code> httperf --server 127.0.1.1 --uri /auth/ldap/testuser/testpass --port 9998 --rate 10 --num-conns 500
</code></pre>

__The auth service has been tested against Active Directory and Open LDAP server__

### Service response
The microservice response is in json format, and returns the following on successful authentication
<pre><code> {"status":"SUCCESS","lookup":{"cn":"testuser","homeDirectory":"/home/testuser","loginShell":"/bin/bash"}}
</code></pre>

In case of error, the service returns an error response with a HTTP status code of 400, 401 or 500, depending on the error, below is 
an example of a HTTP 401 (Un-authorized) error:
<pre><code> {"status":"ERROR", "errorMessage":"[LDAP: error code 49 - Invalid Credentials]"}
</code></pre>

The auth-proxy service also supports base64 encoded username and password, which can easily be turned on/off by configuring the *ldap.base64* property
in the *conf/auth.conf* configuration file. If base64 encoding is enabled, the username and password must be encoded before passing to the server.

License
-------

The rest-auth-proxy microservice is open source and is free to use for both personal and commercial purposes under the terms and
conditions of [Apache Software License](http://www.apache.org/licenses/LICENSE-2.0.html "ASL 2.0").
