#!/bin/sh

# The address to the Symphony Agent API and Key Manager endpoints prefix (see -Dsessionauth.url, -Dkeyauth.url, -Dpod.url and -Dagent.url below)
FOUNDATION_API_URL=https://foundation-dev-api.symphony.com

# The address to the Symphony Pod endpoint prefix (see -Dpod.url below)
FOUNDATION_POD_URL=https://foundation-dev.symphony.com

# This is specific for foundation-dev.symphony.com endpoint configuration
# Your pod setup may be different (in ports, hosts and URL paths)
export SESSIONAUTH_URL=$FOUNDATION_API_URL/sessionauth
export KEYAUTH_URL=$FOUNDATION_API_URL/keyauth
export POD_URL=$FOUNDATION_POD_URL/pod
export AGENT_URL=$FOUNDATION_API_URL/agent

#####
# Sensible data
# Please use encryption to use these environment variables on CI environments
# Start
#####

# Trusts all *.symphony.com Symphony Pods
export TRUSTSTORE_FILE=./certs/server.truststore
export TRUSTSTORE_PASSWORD=changeit

# Used by all bots as Symphony Service Account
export BOT_USER_EMAIL=java-client@symphony.foundation
export BOT_USER_CERT_FILE=./certs/symphony-java-client.p12
export BOT_USER_CERT_PASSWORD=...

# Used by HelloWorldBot to identify Symphony user that receives the message
export RECEIVER_USER_EMAIL=receiver@mycompany.com

# Used by EchoBotIT (src/test/java/..)
export SENDER_USER_EMAIL=testbot@symphony.foundation
export SENDER_USER_CERT_FILE=./certs/testbot.p12
export SENDER_USER_CERT_PASSWORD=...


export TEST_ROOM_STREAM=276YAAI9Kzvl3wRA+z2hln___qV6zxh9dA


#####
# Sensible data
# End
#####

# Used by RssBot to identify RSS feed source
export RSS_URL=https://twitrss.me/twitter_user_to_rss/?user=symphonyoss
export RSS_LIMIT=3

if [ -f $BOT_USER_CERT_PATH ]; then
  echo "Found bot cert file $BOT_USER_CERT_PATH"
else
  echo "Bot cert file is missing: $BOT_USER_CERT_PATH"
fi

if [ -f $TRUSTSTORE_FILE ]; then
  echo "Found truststore file $TRUSTSTORE_FILE"
else
  echo "truststore file is missing: $TRUSTSTORE_FILE"
fi