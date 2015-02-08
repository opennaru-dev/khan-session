# KHAN [session manager]

 KHAN [session manager] is the scalable Web Session Manager for Infinispan(JBoss Data Grid) or Redis.

 * Opennau, Inc. 
  * http://www.opennaru.com/
 * Blog 
  * http://opennaru.tistory.com/
  * http://opennaru.blog.me/

 * Presentation
  * http://opennaru.tistory.com/53

 * **KHAN [session manager]**
 
 KHAN [session manager] is module for session clustering for WAS(Web Application Server) such as JBoss, Tomcat, WebLogic and so on. KHAN [session-manager] could replace session clustering feature of WAS with In Memory Data Grid(IMDG) product like *JBoss Data Grid(Infinispan)* or *Redis*.
 Without any changes of an application, adding servlet filter to *web.xml* and some libraries are enough to apply KHAN [session-manager] .
 
 
 * **How to Apply**
  
  KHAN [session manager] provides 2 ways to cluster session for WAS. Moreover, it could be mixed up at the same time.
  
  * **Using WAS Heap Memory**
  * **Apart from WAS, using JBoss Data Grid(Infinispan) Server (Store session to Memory Grid)**
  * **Apart from WASm using Redis Server (Store session to Redis)**

  
 * **KHAN [session manager] Features**
  * Using Java EE StandardServlet Filter, it can be available to WAS that use beyond Servlet 2.5  (Tested WAS : JBoss EAP 6.x, Tomcat 7.x, WebLogic 11g, Still testing...)
  * Sharing session data among several Web applications.
  * Preventing duplication login for Web application.
  * Monitoring memory usage of session
    * MBean Monitoring - Active session count, Create/Destroy session count, Duplication login count, session create/destroy/duplicate_login count per second.
  * Filter Static Contents(avoid to create session)
  * Provide high scalability and stability with JBoss Data Grid(Infinispan).
  * Store session data to Redis.
  * SessionStore Type
   * Infinispan Library Mode
   * Infinispan HotRod Mode
   * Redis

 * Build
 
 ```
 $ mvn install
 ```

 * **How to Use**

 * Infinispan Library Mode
 ```xml
<dependency>
    <groupId>com.opennaru.khan</groupId>
    <artifactId>khan-session-infinispan</artifactId>
    <version>1.3.0</version>
</dependency>
 ```
 * Filter Class : com.opennaru.khan.session.filter.InfinispanLibSessionFilter

 * Infinispan HotRod Mode
 ```xml
<dependency>
    <groupId>com.opennaru.khan</groupId>
    <artifactId>khan-session-hotrod</artifactId>
    <version>1.3.0</version>
</dependency>
 ```
 * Filter Class : com.opennaru.khan.session.filter.InfinispanHotRodSessionFilter

 * Redis Mode
 ```xml
<dependency>
    <groupId>com.opennaru.khan</groupId>
    <artifactId>khan-session-redis</artifactId>
    <version>1.3.0</version>
</dependency>
 ```
 * Filter Class : com.opennaru.khan.session.filter.RedisSessionFilter

 * **web.xml 설정**
 
  * FilterClass의 종류
   * com.opennaru.khan.session.filter.InfinispanLibSessionFilter
   * com.opennaru.khan.session.filter.InfinispanHotRodSessionFilter
   * com.opennaru.khan.session.filter.RedisSessionFilter

 ```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~  Opennaru, Inc. http://www.opennaru.com/
  ~
  ~  Copyright (C) 2014 Opennaru, Inc. and/or its affiliates.
  ~  All rights reserved by Opennaru, Inc.
  ~
  ~  This is free software; you can redistribute it and/or modify it
  ~  under the terms of the GNU Lesser General Public License as
  ~  published by the Free Software Foundation; either version 2.1 of
  ~  the License, or (at your option) any later version.
  ~
  ~  This software is distributed in the hope that it will be useful,
  ~  but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~  Lesser General Public License for more details.
  ~
  ~  You should have received a copy of the GNU Lesser General Public
  ~  License along with this software; if not, write to the Free
  ~  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~  02110-1301 USA, or see the FSF site: http://www.fsf.org.
  -->
<web-app
        xmlns="http://java.sun.com/xml/ns/javaee"
        xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
        id="session1" version="2.5">

    <display-name>Test</display-name>
    <description>Test App</description>
    <!-- <distributable/> -->
    <filter>
        <filter-name>KhanSessionFilter</filter-name>
        <filter-class>com.opennaru.khan.session.filter.InfinispanLibSessionFilter</filter-class>
        <!-- Infinispan Library Mode -->
        <init-param>
            <param-name>configFile</param-name>
            <param-value>khan-session.xml</param-value>
        </init-param>

        <init-param>
            <param-name>infinispanCache</param-name>
            <param-value>KHAN_SESSION</param-value>
        </init-param>
        <init-param>
            <param-name>infinispanLoginCache</param-name>
            <param-value>KHAN_SESSION_LOGIN</param-value>
        </init-param>

        <init-param>
            <param-name>sessionId</param-name>
            <param-value>__KSMSID__</param-value>
        </init-param>
        <init-param>
            <param-name>domain</param-name>
            <param-value></param-value>
        </init-param>
        <init-param>
            <param-name>path</param-name>
            <param-value>/test1</param-value> <!-- 서로 다른 WebApp간 세션 공유하려면 '/' 으로 설정 -->
        </init-param>
        <init-param>
            <param-name>secure</param-name>
            <param-value>false</param-value>
        </init-param>
        <init-param>
            <param-name>httpOnly</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <param-name>sessionTimeout</param-name>
            <param-value>30</param-value>
        </init-param>
        <init-param>
            <param-name>excludeRegExp</param-name>
            <param-value>/.+\.(html|jpg|jpeg|png|gif|js|css|swf)</param-value>
        </init-param>
        <init-param>
            <param-name>allowDuplicateLogin</param-name> <!-- 중복 로그인을 허용하려면 true로 설정 -->
            <param-value>false</param-value>
        </init-param>
        <init-param>
            <param-name>logoutUrl</param-name> <!-- 중복 로그인시 logout URL 설정 -->
            <param-value>/logout.jsp</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>KhanSessionFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>ERROR</dispatcher>
        <dispatcher>INCLUDE</dispatcher>
        <dispatcher>FORWARD</dispatcher>
        <dispatcher>REQUEST</dispatcher>
    </filter-mapping>
    <listener>
        <listener-class>com.opennaru.khan.session.listener.SessionListener</listener-class>
    </listener>

</web-app>
```
```

 * **Library 모드를 사용할 때 설정**
  * khan-session.xml 파일에서 설정

```xml
<infinispan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="urn:infinispan:config:6.0 http://www.infinispan.org/schemas/infinispan-config-6.0.xsd"
	xmlns="urn:infinispan:config:6.0">

	<global>
		<transport clusterName="opennaru">
			<properties>
				<property name="configurationFile" value="jgroups.xml" />
			</properties>
		</transport>
	</global>

	<default>
		<locking concurrencyLevel="1000" useLockStriping="false" />

		<unsafe unreliableReturnValues="true" />

		<clustering mode="distribution">
			<sync replTimeout="200000" />
			<!-- <async/> -->
			<hash numOwners="2" numSegments="40" />
			<l1 enabled="true" lifespan="60000" />
		</clustering>

		<invocationBatching enabled="false" />
		<transaction transactionMode="NON_TRANSACTIONAL" />
	</default>

	<namedCache name="KHAN_SESSION">
		<jmxStatistics enabled="true" />
	</namedCache>

	<namedCache name="KHAN_SESSION_LOGIN">
		<jmxStatistics enabled="true" />
	</namedCache>

</infinispan>
```

 * **Hotrod client/server mode configuration**
  * Specify Infinispan server IP:PORT from hotrod.properties
  * JBoss Data Grid(Infinispan) Server can be installed by KHAN [provisioning].

```java
infinispan.client.hotrod.server_list = 192.168.0.11:11222
```

 * **Redis mode configuration**
  * Specify Redis server url from redis.properties.
```java
redis.client.server_url=redis://:@localhost:6379/1
```


 * Configuration for memory monitor
  * It is only for JBoss EAP 6.x(No need for other WAS)

```
export JAVA_OPTS=" $JAVA_OPTS -Djboss.modules.system.pkgs=org.jboss.byteman,org.github.jamm"
```

* 
  * javaagent option configuration

```
export JAVA_OPTS=" $JAVA_OPTS -javaagent:/PATH_TO_JAMM_JAR/jamm-0.2.5.jar"
```

* Preventing duplication login

If you enable duplicated login, following code which use KHAN [session manager] API has to be added at your login/logout source.

 * Successfully login

```java
SessionLoginManager.getInstance().login(request, "userId");
```
 * Successfully logout

```java
SessionLoginManager.getInstance().logout(request);
```
 * Login user information

```java
SessionLoginManager.getInstance().loggedInUserId(request));
```

#### KHAN [provisioning]
* JBoss Data Grid(Infinispan) Server can be installed by KHAN [provisioning] automatically.
* Reference : http://opennaru.tistory.com/category/KHAN

### Contacts
* Web / JBoss(WAS) Inquires or KHAN [session manager] Inquires
* **Opennaru, Inc.**
 * Home : http://www.opennaru.com/
 * Tech Reference : http://opennaru.tistory.com/
 * Mail : [service@opennaru.com](mailto:service@opennaru.com)

