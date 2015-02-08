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
 
 KHAN [session manager]는 JBoss, Tomcat, WebLogic등 WAS의 세션 클러스터링을 위한 모듈입니다. WAS의 세션 클러스터링 기능을 In Memory Data Grid(IMDG) 제품인 JBoss Data Grid(Infinispan)이나 Redis를 사용하여 구축할 수 있도록 합니다.
 기존 애플리케이션의 변경없이 web.xml 파일에 서블릿 필터 설정만 추가하고 필요 라이브러리를 추가하면 됩니다.
 
 
 * **세션 구성 방법**
  
  KHAN [session manager]는 다음의 두가지 방법으로 WAS의 세션 클러스터링을 구축할 수 있습니다. 아래 두가지 방법을 혼용하여 사용할 수도 있습니다.
  
  * **WAS내의 메모리를 사용하는 방법**
  * **별도의 서버들에 JBoss Data Grid(Infinispan)를 구성하여 세션을 Memory Grid에 저장하는 방법**
  * **별도의 Redis Server를 구성하여 세션을 저장하는 방법**

  
 * **KHAN [session manager]의 주요 기능**
  * Java EE 표준 Servlet Filter를 사용하여 Servlet 2.5 이상를  지원하는 WAS 서버에서 사용할 수 있음(현재 테스트 서버 : JBoss EAP 6.x, Tomcat 7.x, WebLogic 11g, 계속 테스트 중)
  * 서로 다른 Web 애플리케이션 간의 세션 공유 기능
  * Web 애플리케이션의 중복 로그인 방지 기능
  * 세션에서 사용하는 메모리 사용량 모니터링
  * Active 세션 개수, 세션 생성/소멸 개수, 중복 로그인 횟수, 초당 세션 생성/소멸/중복로그인 횟수에 대한 MBean 모니터링
  * 주요 Static Contents에 대해 세션을 생성하지 않도록 필터링
  * IMDG인 JBoss Data Grid(Infinispan)을 사용하여 안정적이며 세션에 대한 확장성이 높음.
  * Redis에 세션 데이터를 저장할 수 있음.
  * SessionStore의 종류
   * Infinispan Library Mode
   * Infinispan HotRod Mode
   * Redis

 * 빌드 방법
 
 ```
 $ mvn install
 ```

 * **사용 방법**

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

 * **Hotrod 클라이언트/서버 모드를 사용할 때 설정**
  * hotrod.properties 파일에서 Infinispan 서버의 IP:PORT 설정
  * JBoss Data Grid(Infinispan) Server는 KHAN [provisioning]을 이용하여 자동 설치할 수 있습니다.

```java
infinispan.client.hotrod.server_list = 192.168.0.11:11222
```

 * **Redis 모드를 사용할 때 설정**
  * redis.properties 파일에서 Redis 서버의 URL 설정
```java
redis.client.server_url=redis://:@localhost:6379/1
```


 * 메모리 모니터링을 위한 설정
  * JBoss EAP 6.x는 아래 설정이 필요함(다른 WAS는 필요 없음)

```
export JAVA_OPTS=" $JAVA_OPTS -Djboss.modules.system.pkgs=org.jboss.byteman,org.github.jamm"
```

* 
  * javaagent 옵션 설정

```
export JAVA_OPTS=" $JAVA_OPTS -javaagent:/PATH_TO_JAMM_JAR/jamm-0.2.5.jar"
```

* 중복 로그인 방지 로그인 방법

중복로그인 방지 기능을 사용하려면 로그인, 로그아웃시 KHAN [session manager]가 제공하는 API를 사용하여 다음과 같이 로그인/로그아웃 코드를 추가하여야 합니다.

 * 로그인 성공시 아래 코드 사용

```java
SessionLoginManager.getInstance().login(request, "userId");
```
 * 로그아웃시 아래 코드 사용

```java
SessionLoginManager.getInstance().logout(request);
```
 * 로그인한 사용자 정보

```java
SessionLoginManager.getInstance().loggedInUserId(request));
```

#### KHAN [provisioning]
* JBoss Data Grid(Infinispan) Server는 KHAN [provisioning]을 이용하여 자동 설치할 수 있습니다.
 * 관련자료 : http://opennaru.tistory.com/category/KHAN

### Contacts
* Web / JBoss(WAS) 문의 및 KHAN [session manager] 문의
* **오픈나루 - Opennaru, Inc.**
 * 홈페이지 : http://www.opennaru.com/
 * 기술자료 : http://opennaru.tistory.com/
 * Mail : [service@opennaru.com](mailto:service@opennaru.com)

