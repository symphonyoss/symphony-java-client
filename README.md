[![Dependencies](https://www.versioneye.com/user/projects/5770f47919424d000f2e0095/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5770f47919424d000f2e0095)
[![Build Status](https://travis-ci.org/symphonyoss/symphony-java-client.svg)](https://travis-ci.org/symphonyoss/symphony-java-client)
[![Validation Status](https://scan.coverity.com/projects/9112/badge.svg?flat=1)](https://scan.coverity.com/projects/symphonyoss-symphony-java-client)
[![Symphony Software Foundation - Active](https://cdn.rawgit.com/symphonyoss/contrib-toolbox/master/images/ssf-badge-active.svg)](https://symphonyoss.atlassian.net/wiki/display/FM/Active)
<a href="https://sonarqube.com/overview?id=org.symphonyoss.symphony%3Asymphony-java-client"><img src="https://www.sonarqube.org/assets/logo-31ad3115b1b4b120f3d1efd63e6b13ac9f1f89437f0cf6881cc4d8b5603a52b4.svg" title="SonarQube" width="80"/></a>

Symphony Java Client
====================

The Symphony java client provides a real-time wrapper around the Symphony REST API's to simplify the creation of chat sessions, room access, presence, messaging and more...  The client provides a set of logical services representing supported features of the Symphony platform.  Services support real-time events through feature based listeners and communication objects.  Access is not limited to the services as all underlying Symphony client implementations are exposed for advanced use or creation of your own service.

##Features
* Basic client:
    * Authentication management
    * Implements and exposes functional services and underlying clients.
* Chat Service:
    * Support for chat session creation and eventing
    * Filters only on chat messages
    * Real-time listeners on chat events such as callbacks on new chat session
    * Enriches chat objects with user attributes
    * X-Pod support
* Room Service
    * Support for room session creation and all associated room events
    * Filters only on room messages
    * Real-time listeners on all room events
    * Enriches Room objects with associated system attributes
    * X-Pod Support
* Presence Service (Disabled by default and NOT recommended for use)
    * Maintains an active cache of endpoint presence associated with single POD.
    * Real-time listeners on all presence changes
    * Request user presence
* Connections request handling including auto-accept.
* Attachment Support
* Publish formatted articles (news) using ShareApi
* MessageML utilities
    * Support for command line processing
    * Conversion from MessageML to Text
* Exposure of underlying Symphony clients:
    * Authentication, Users, Presence, Streams, Datafeed, RoomMembership, Connections, Attachments

**Administration features are currently not supported in the library. (future work)**



##Change log and notes
### V1.0.1 (SNAPSHOT)
* Focus on performance
* Moving symphony-apis to external project


### V1.0.0 (Stable)
* Compatible with 1.40.1 API, V2 and Agent 1.39+
* Presence service is now disabled by default
* ShareAPI supporting article (news) posts implemented
* Support for custom HTTP Clients when initiating SymphonyClient.
* Can support multiple SymphonyClient instances running in a single JVM representing different users.
* Automatic Auth refresh in client
* ChatService enhancements including remote user identification on Chat creation.
* Added ability to retrieve users by stream
* New admin features, creating users, updating user details, modify room membership
* Added room search under streams client
* REMOVED Experimental firehose service as per LLC
* Performance Updates
* All incoming SymMessages from MessageService are now set to MessageML format
* Command Framework (AI Package) supporting command parsing and actions
* CI integration testing (mvn goal -Pintegration-testing)
* Bug fixes, documentation, new examples

### V0.9.1 

* Compatible with 1.40.1 API, V2 and Agent 1.39+
* New Connections Service for managing connection requests.  This includes auto-accept.
* Support for attachments through new SymMessage and Attachments Client
* Message and User objects have been replaced with SymMessage and SymUser in order to handle future underlying versioning changes
* Listeners have been upgraded to support new object models
* Room Service is now linked into Datafeed for real-time processing and dynamic room formation
* New UserV2 lookups have been implemented that should alleviate the need for elevated privileges
* New exceptions handling implemented with detailed message outputs
* Lazy cache for room identification
* Additional examples added for new features
* Examples updated to reflect core changes
* Many..many..underlying code updates resolving sonar issues

### V0.9.0 

* Compatible with 1.38 API, V1 and Agent
* Bot user requires elevated privileges to acquire
* Inititial release includes the basic client, chat service, presence service and basic room functions.
* Exposes clients for Symphoni API including Authorization, Streams, Presence, RoomMembership, User, Users,
* Utilizes generated API models from Symphony



##Requirements

####POM:

        <dependency>
            <groupId>org.symphonyoss.symphony</groupId>
            <artifactId>symphony-client</artifactId>
            <version>(Version)</version>
        </dependency>

####Certificates:

        Please contact your Symphony local administrator to obtain the necessary certificates
        for the user/service account being used to access the POD.

        Server Truststore = Contains server certs
        User Keystore = Symphony user client certificate

        Note: The latest version of the SymphonyClient object supports the ability to create custom HTTP Clients, which
        means you can bind different .p12 certs representing different BOT users.


####Required System Properties:

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

##Examples
[see Examples Project](https://github.com/symphonyoss/symphony-java-sample-bots)


##API Docs
[API Documentation](http://symphonyoss.github.io/symphony-java-client/index.html)

## Contribute
This project was initiated at [IHS Markit](https://www.ihsmarkit.com) and has been developed as open-source from the very beginning.

Contributions are accepted via GitHub pull requests. All contributors must be covered by contributor license agreements to comply with the [Code Contribution Process](https://symphonyoss.atlassian.net/wiki/display/FM/Code+Contribution+Process).
