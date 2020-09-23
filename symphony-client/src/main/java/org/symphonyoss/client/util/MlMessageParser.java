/*
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.client.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;
import org.symphonyoss.client.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.SymException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides simple utility to parse MlMessage formatted text
 *
 * @author Frank Tarsillo
 */
public class MlMessageParser extends DefaultHandler {


    private String messageMl;
    private Element elementMessageML;
    private Document doc;
    private Document originalDoc;
    private StringBuilder textDoc = new StringBuilder();
    private String[] textChunks;

    private SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(MlMessageParser.class);


    public MlMessageParser(SymphonyClient symClient) {
        this.symClient = symClient;

    }

    public MlMessageParser() {
    }

    public void parseMessage(String message) throws SymException {

        Document doc = Jsoup.parse(message);
        originalDoc = doc.clone();
        Element elementErrors = doc.body().getElementsByTag("errors").first();


        if (elementErrors != null) {
            if (elementErrors.outerHtml() != null)
                logger.debug("Errors found in message: {}", elementErrors.outerHtml());
        }
        //Lets remove the errors elements
        doc.select("errors").remove();

        elementMessageML = doc.select("messageML").first();

        if(elementMessageML==null)
            elementMessageML = doc.select("div").first();


        if (elementMessageML != null) {
            if (elementMessageML.outerHtml() != null)
                logger.debug("Doc parsed: {}", elementMessageML.outerHtml());
        } else {

            logger.error("Could not parse document for message {}", message);
            throw new SymException("Malformed message");
        }

        textDoc = new StringBuilder();
        stripTags(textDoc, elementMessageML.childNodes());

        textChunks = textDoc.toString().split("\\s+");


    }

    public String[] getTextChunks() {
        return textChunks;
    }

    @SuppressWarnings("unused")
    public void setTextChunks(String[] textChunks) {
        this.textChunks = textChunks;
    }

    private void stripTags(StringBuilder builder, List<Node> nodesList) {


        for (Node node : nodesList) {
            String nodeName = node.nodeName();

            if (nodeName.equalsIgnoreCase("#text")) {

                builder.append(node.toString().trim()).append(" ");


            } else {

                if (nodeName.equalsIgnoreCase(NodeTypes.ANCHOR.toString())) {

                    if (node.attributes().hasKey(AttribTypes.HREF.toString()))
                        builder.append(node.attr(AttribTypes.HREF.toString()));


                } else if (nodeName.equalsIgnoreCase(NodeTypes.HASHTAG.toString())) {

                    if (node.attributes().hasKey(AttribTypes.TAG.toString()))
                        builder.append("#").append(node.attr(AttribTypes.TAG.toString())).append(" ");

                } else if (nodeName.equalsIgnoreCase(NodeTypes.MENTION.toString())) {
                    SymUser user = new SymUser();
                    user.setEmailAddress("UID:" + node.attr(AttribTypes.UID.toString()));
                    user.setId(Long.valueOf(node.attr(AttribTypes.UID.toString())));

                    if (node.attributes().hasKey(AttribTypes.UID.toString())) {

                        if (symClient != null)
                            try {
                                user = symClient.getUsersClient().getUserFromId(Long.valueOf(node.attr(AttribTypes.UID.toString())));
                            } catch (UsersClientException e) {
                                logger.error("Could not identify user from userID", e);
                            }


                    } else if (node.attributes().hasKey(AttribTypes.EMAIL.toString())) {

                        user.setEmailAddress(node.attr(AttribTypes.EMAIL.toString()));
                    }
                    builder.append(user.getEmailAddress());
                } else if (nodeName.equalsIgnoreCase(NodeTypes.CASHTAG.toString())) {

                    if (node.attributes().hasKey(AttribTypes.TAG.toString()))
                        builder.append("$").append(node.attr(AttribTypes.TAG.toString())).append(" ");


                } else {

                    // recurse
                    stripTags(builder, node.childNodes());
                }
            }
        }
    }


    public String getText() {
        return textDoc.toString();
    }

    @SuppressWarnings("unused")
    public String getOuterHtml() {
        return originalDoc.outerHtml();
    }


    public String getHtmlStartingFromText(String text) {


        StringBuilder stringBuilder = new StringBuilder();
        getHtmlStartingFromText(text, stringBuilder, elementMessageML.childNodes(), false);
        return stringBuilder.toString();
    }

    public String getHtmlStartingFromNode(String nodeType, String attrib, String attribValue) {


        StringBuilder stringBuilder = new StringBuilder();
        getHtmlStartingFromNode(nodeType, attrib, attribValue, stringBuilder, elementMessageML.childNodes(), false);
        return stringBuilder.toString();
    }


    public void getHtmlStartingFromText(String text, StringBuilder builder, List<Node> nodesList, boolean append) {


        for (Node node : nodesList) {
            String nodeName = node.nodeName();

            if (append) {
                builder.append(node.outerHtml());
                continue;
            }


            if (nodeName.equalsIgnoreCase("#text")) {

                if (node.toString().trim().equalsIgnoreCase(text))
                    append = true;

            }
            getHtmlStartingFromText(text, builder, node.childNodes(), append);
        }
    }


    private void getHtmlStartingFromNode(String nodeType, String attrib, String attribValue, StringBuilder builder, List<Node> nodesList, boolean append) {


        for (Node node : nodesList) {
            String nodeName = node.nodeName();

            if (append) {

                if (node.nodeName().equalsIgnoreCase("#text") && node.outerHtml().charAt(0) != ' ')
                    builder.append(" ");

                builder.append(node.outerHtml());

                if (!node.nodeName().equalsIgnoreCase("#text"))
                    builder.append(" ");

                continue;
            }


            if (nodeName.equalsIgnoreCase(nodeType)) {

                if (node.attributes().hasKey(attrib) && node.attr(attrib).equalsIgnoreCase(attribValue))
                    append = true;

            }
            getHtmlStartingFromNode(nodeType, attrib, attribValue, builder, node.childNodes(), append);
        }
    }


    public void updateMentionUidToEmail(SymphonyClient symClient) {

        updateMentionUidToEmail(symClient, elementMessageML.childNodes());
    }

    //Terrible that Symphony publishes UID on mention but only allows EMAIL on message submission.
    private void updateMentionUidToEmail(SymphonyClient symClient, List<Node> nodesList) {


        for (Node node : nodesList) {
            String nodeName = node.nodeName();


            if (nodeName.equalsIgnoreCase(NodeTypes.MENTION.toString())) {

                if (node.attributes().hasKey(AttribTypes.UID.toString())) {

                    String uid = node.attr(AttribTypes.UID.toString());

                    SymUser user = null;
                    try {
                        user = symClient.getUsersClient().getUserFromId(Long.parseLong(uid));

                        logger.info("Translated mention uid {} to email {}", uid, user.getEmailAddress());
                    } catch (UsersClientException e) {
                        logger.error("Could not identify user email from id", e);
                    }

                    if (user != null && user.getEmailAddress() != null) {
                        uid = user.getEmailAddress();
                    }

                    Attribute emailAttribute = new Attribute(AttribTypes.EMAIL.toString(), uid);

                    node.attributes().put(emailAttribute);
                    node.removeAttr(AttribTypes.UID.toString());

                }

            }

            if(node.childNodeSize() > 0)
                updateMentionUidToEmail(symClient, node.childNodes());
        }


    }

    @SuppressWarnings("unused")
    public Elements getAllElements() {
        return elementMessageML.getAllElements();
    }

    @SuppressWarnings("unused")
    public List<Node> getChildNodes() {

        return elementMessageML.childNodes();
    }


    /**
     * Experimental - will attempt to escape all text within xml elements
     * @param xml full xml
     * @return escaped xml string
     */
    public static String escapeAllXml(String xml)
    {
        // Match the pattern <something>text</something>
        Pattern xmlCleanerPattern = Pattern.compile("(<[^/<>]*>)([^<>]*)(</[^<>]*>)");

        StringBuilder xmlStringBuilder = new StringBuilder();

        Matcher matcher = xmlCleanerPattern.matcher(xml);
        int lastEnd = 0;
        while (matcher.find())
        {
            // Include any non-matching text between this result and the previous result
            if (matcher.start() > lastEnd) {
                xmlStringBuilder.append(xml.substring(lastEnd, matcher.start()));
            }
            lastEnd = matcher.end();

            // Sanitise the characters inside the tags and append the sanitised version
            String cleanText = StringEscapeUtils.escapeXml(matcher.group(2));
            xmlStringBuilder.append(matcher.group(1)).append(cleanText).append(matcher.group(3));
        }
        // Include any leftover text after the last result
        xmlStringBuilder.append(xml.substring(lastEnd));

        return xmlStringBuilder.toString();
    }
}

