[![Dependencies](https://www.versioneye.com/user/projects/5770f47919424d000f2e0095/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5770f47919424d000f2e0095)
[![Build Status](https://travis-ci.org/symphonyoss/symphony-java-client.svg)](https://travis-ci.org/symphonyoss/symphony-java-client)
[![Validation Status](https://scan.coverity.com/projects/9112/badge.svg?flat=1)](https://scan.coverity.com/projects/symphonyoss-symphony-java-client)
[![Symphony Software Foundation - Active](https://cdn.rawgit.com/symphonyoss/contrib-toolbox/master/images/ssf-badge-active.svg)](https://symphonyoss.atlassian.net/wiki/display/FM/Active)
<a href="https://sonarqube.com/overview?id=org.symphonyoss.symphony%3Asymphony-java-client"><img src="https://www.sonarqube.org/assets/logo-31ad3115b1b4b120f3d1efd63e6b13ac9f1f89437f0cf6881cc4d8b5603a52b4.svg" title="SonarQube" width="80"/></a>

Symphony Java Client
====================

The Symphony java client provides a real-time wrapper around the Symphony REST API's to simplify the creation of chat sessions, room access, presence, messaging and more...  The client provides a set of logical services representing supported features of the Symphony platform.  Services support real-time events through feature based listeners and communication objects.  Access is not limited to the services as all underlying Symphony client implementations are exposed for advanced use or creation of your own service.

## Features
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
* Connections request handling including auto-accept.
* Attachment Support
* Publish formatted articles (news) using ShareApi
* MessageML utilities
    * Support for command line processing
    * Conversion from MessageML to Text
* Exposure of underlying Symphony bindings:
    * Authentication, Users, Presence, Streams, Datafeed, RoomMembership, Connections, Attachments
* Lazy cache for user data.  Can also be extended to custom cache solutions. 
**Administration features are currently not supported in the library. (future work)**



## Change log and notes

### V1.0.3 (SNAPSHOT)
* Unit and integration test coverage
* Attempt Room Cache again..
* AI framework enhancements & interfaces


### V1.0.2
* Support for REST API 1.46.0+
* Removed generated symphony-apis module and replaced with [symphony-java-api](https://github.com/symphonyoss/symphony-java-api) released modules
* Services and clients have support for V4 endpoints.  Use SymphonyClientFactory.TYPE.V4 when instantiating SymphonyClient
* Added new models and listeners to support V4 event based messaging.  This primarily impacts "Room" services if running V4 mode. 
* SymMessage support for PresentationML inclusive of EntityData
* SymMessage V4 support for attaching files directly to messages vs prior two step process.
* Examples added back to project, but are not part of distribution
* Example added for PresentationML (incl EntityData) and RoomExampleV4 showing event messaging
* Removed PresenceService and associated listeners as it is no longer supported
* Better exception handling exposing underlying root causes from API calls.
* Moved away from using system properties in favor of internal configuration properties with native support for system configuration and environmental properties.
* Increase in unit test coverage
* Added ability to obtain stream attributes in StreamsClient.getStreams(...).  Admin and user level.
* Added the ability to list all streams known streams for a given user StreamClient
* Updated MessageService to take advantage of new stream attributes functions
* Added additional integration testing



### V1.0.1 (Stable)
* Added Users local cache (Lazy) for services
* SymphonyClient now supports cache plugin setCache(CacheType). Extension sample provided.
* Updated REST API Spec to 1.45
* Simplification of CustomHttpClient
* Focus on performance
* Deferred: Although progress was made on moving symphony-apis module, more testing required 


### V1.0.0 
* Compatible with 1.45 API, V2 and Agent 1.39+
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

## Branch Strategy

**develop** - All active development on latest SNAPSHOT

**master**  - Periodic merged and tested features from develop branch



## Requirements

#### POM:

        <dependency>
            <groupId>org.symphonyoss.symphony</groupId>
            <artifactId>symphony-client</artifactId>
            <version>(Version)</version>
        </dependency>

#### Certificates:

        Please contact your Symphony local administrator to obtain the necessary certificates
        for the user/service account being used to access the POD.

        Server Truststore = Contains server certs
        User Keystore = Symphony user client certificate

        Note: The latest version of the SymphonyClient object supports the ability to create custom HTTP Clients, which
        means you can bind different .p12 certs representing different BOT users.


#### Required System Properties:

        -Dtruststore.file=
        -Dtruststore.password=password
        -Dsessionauth.url=https://(hostname)/sessionauth
        -Dkeyauth.url=https://(hostname)/keyauth
        -Duser.call.home=frank.tarsillo@markit.com
        -Duser.cert.password=password
        -Duser.cert.file=bot.user2.p12
        -Dpod.url=https://(pod host)/pod
        -Dagent.url=https://(agent server host)/agent
        -Duser.email=bot.user2@markit.com or bot user email
       


In addition to the above, **Java 8** must be installed.


## Examples

The latest **examples** are part of the SJC project and are continually updated with new versions released. 

In addition, there is an external project providing samples of use: [see Examples Project](https://github.com/symphonyoss/symphony-java-sample-bots)


## API Docs
[API Documentation](http://symphonyoss.github.io/symphony-java-client/index.html)

## Contribute
This project was initiated at [IHS Markit](https://www.ihsmarkit.com) and has been developed as open-source from the very beginning.

Contributions are accepted via GitHub pull requests. All contributors must be covered by contributor license agreements to comply with the [Code Contribution Process](https://symphonyoss.atlassian.net/wiki/display/FM/Code+Contribution+Process).
