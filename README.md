[![Dependencies](https://www.versioneye.com/user/projects/5770f47919424d000f2e0095/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5770f47919424d000f2e0095)
[![Build Status](https://travis-ci.org/symphonyoss/symphony-java-client.svg)](https://travis-ci.org/symphonyoss/symphony-java-client)
[![Validation Status](https://scan.coverity.com/projects/9112/badge.svg?flat=1)](https://scan.coverity.com/projects/symphonyoss-symphony-java-client)
[![SonarQube](http://www.sonarqube.org/wp-content/themes/sonarsource.org/images/sonar.png =100x)](https://sonarqube.com/component_measures/?id=org.symphonyoss%3Asymphony-java-client)

The Symphony java client provides a wrapper around the Symphony REST API's to simplify the
creation of chat sessions, presence and general messaging.  It is event based with
associated services and listeners to capture updates and forward.

REST API Support:

        Version 1 API calls implemented and tested under Standalone Agent API 1.38
        -Authentication
        -Users
        -Presence
        -Streams
        -Messages
        -Datafeed
        -RoomMembership


Requirements:

POM:

        <dependency>
            <groupId>org.symphonyoss</groupId>
            <artifactId>symphony-client</artifactId>
            <version>(Version)</version>
        </dependency>

Certificates:

        Please contact your Symphony local administrator to obtain the necessary certificates
        for the user/service account being used to access the POD.

        Server Truststore = Contains server certs
        User Keystore = Symphony user client certificate


Required System Properties:

        -Dkeystore.password=(Pass)
        -Dtruststore.password=(Pass)
        -Dsessionauth.url=https://(pod-host).symphony.com:8444/sessionauth
        //Note: you may have local HSM vs pod
        -Dkeyauth.url=https://(pod-host).symphony.com:8444/keyauth
        -Dsymphony.agent.pod.url=https://(symagent-host).mdevlab.com:8446/pod
        -Dsymphony.agent.agent.url=https://(symagent-host).mdevlab.com:8446/agent
        -Dcerts.dir=/dev/certs/
        -Dtruststore.file=/dev/certs/server.truststore
        -Dbot.user=(user name)
