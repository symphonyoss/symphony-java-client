[![Maven Central](https://img.shields.io/maven-central/v/org.symphonyoss.symphony/symphony-client.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.symphonyoss.symphony%20a%3Asymphony-client)
[![Build Status](https://travis-ci.org/symphonyoss/symphony-java-client.svg)](https://travis-ci.org/symphonyoss/symphony-java-client)
[![Validation Status](https://scan.coverity.com/projects/9112/badge.svg?flat=1)](https://scan.coverity.com/projects/symphonyoss-symphony-java-client)
[![Symphony Software Foundation - Active](https://cdn.rawgit.com/symphonyoss/contrib-toolbox/master/images/ssf-badge-active.svg)](https://symphonyoss.atlassian.net/wiki/display/FM/Active)
[![SonarCloud Quality gate](https://sonarcloud.io/api/badges/gate?key=org.symphonyoss.symphony%3Asymphony-java-client)](https://sonarcloud.io/dashboard?id=org.symphonyoss.symphony%3Asymphony-java-client)

Symphony Java Client
====================

The Symphony java client provides a real-time wrapper around the Symphony REST API's to simplify the creation of chat sessions, room access, presence, messaging and more...  The client provides a set of logical services representing supported features of the Symphony platform.  Services support real-time events through feature based listeners and communication objects.  Access is not limited to the services as all underlying Symphony client implementations are exposed for advanced use or creation of your own service.

## Features
* Basic client:
    * Authentication management for all variants (bot, Extension Apps, Apps, OBO)
    * Implements and exposes functional services and underlying clients.
    * Support for OBO workflow
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
* Presence Service
    * Real-time notification of presence events and client functions
* Connections request handling including auto-accept.
* Attachment Support
* Publish formatted articles (news) using ShareApi
* MessageML utilities
    * Support for command line processing
    * Conversion from MessageML to Text
* Exposure of underlying Symphony bindings:
    * Authentication, Users, Presence, Streams, Datafeed, RoomMembership, Connections, Attachments
* Lazy cache for user data.  Can also be extended to custom cache solutions. 
**Certain administration features are currently not supported in the library. (future work)**



## Change log and notes

### V1.1.2 (SNAPSHOT)
* Focus on testing and implementing more APIs

### V1.1.1 
* Support for OBO workflow AuthenticationClient and MessagesClient modified
* SymphonyClientConfig can now support general application properties
* Fixed issue with room mappings
* Firehose initial build


### V1.1.0 
* REMOVED MOST V2 Support and deprecated methods/classes
* Support for for 1.46.3 or higher. 1.48.1 added as dependency
* PresenceService reinstated - Utilizes new feed calls
* Using latest endpoints for presence functions.  Also replaced Presence objects with SymPresence.  
* SymphonyClient now support custom http clients for both Pod and Agent clients 
* SymphonyClientConfigID TRUSTSTORE_FILE and TRUSTSTORE_PASSWORD set to optional now.
* SymphonyClient can be initialized without defining truststore, taking defaults. 
* Added session logout to AuthenticationClient
* Added special attribute (ApiVersion) to SymMessage to support sending MessageMLv1 messages over V2 endpoints. This gets around some of the limitations of PresentationML/MessageMLv2
* SymStream object replacing all use of legacy Stream objects.
* SymphonyClient automatically identifies the Agent and Pod versions to select the latest versions.
* SymMessage.setMessageText(..) will automatically escapeXml
* Added new methods in MessageService to send message by SymUser and SymStream (Convenience)
* SymphonyClient instances default to V4 API
* ConnectionsClient now supports removal of connections.
* SymphonyClient has new internal name identifier for Thread handling bound to email address. 
* Added a convenience class SymphonyApis accessible through SymphonyClient supporting retrieval of all language binding APIs (Not recommended for use)
* SymMessage no longer has option to set Format as everything is PresentationML based moving forward.  Use SymMessage.setMessageText(TEXT) if you want to set simple text value.
* SymMessage will default setting text message to PresentationML
* New SymphonyConfigID for disabling services (DISABLE_SERVICES). SendMessage example added.
* Updated all examples to reflect major changes.
* Focus on implementing all REST API capabilities
* Interfaces defined for external frameworks and example implementations
* Spring support verification
* Implement Agent Server health-check under new AgentSystemClient
* Updated ShareClient to use V3 endpoint
* Added getUserBySession(SymAuth) to UsersClient
* Some new examples on use of AWS Lex, Copy data between chat rooms, and reporting




### V1.0.3
* Updated for 1.47.0 REST API
* Updated SymUser to support FeatureList, roles..etc
* New support for attachment thumbnails
* Fixed issue with V4 Room keywords
* Unit and integration test coverage
* Added AuthenticationClient to replace AuthorizationClient (deprecated)
* Added ability to set custom http clients to both session and keystore during authentication


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


#### Required System Properties or define through SymphonyClientConfig:

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
       

#### Other key requirements: 

* In addition to the above, **Java 8** must be installed.

* **V4** implementations require AgentServer 1.47+


## Examples

The latest [examples](examples/) are part of this SJC project and are continually updated with new versions released. 

In addition, there is an external project providing samples of use: [see Examples Project](https://github.com/symphonyoss/symphony-java-sample-bots)


## API Docs
[API Documentation](http://symphonyoss.github.io/symphony-java-client/index.html)

## Contribute
This project was initiated at [IHS Markit](https://www.ihsmarkit.com) and has been developed as open-source from the very beginning.

Contributions are accepted via GitHub pull requests. All contributors must be covered by contributor license agreements to comply with the [Code Contribution Process](https://symphonyoss.atlassian.net/wiki/display/FM/Code+Contribution+Process).
