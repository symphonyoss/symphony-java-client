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

import org.jsoup.select.Elements;
import org.symphonyoss.client.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.symphony.pod.model.User;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class MlMessageParser extends DefaultHandler {

    private String messageMl;
    private Element elementMessageML;
    private Document doc;
    private Document originalDoc;
    private StringBuilder textDoc = new StringBuilder();
    private String [] textChunks;

    private SymphonyClient symClient;
    private final Logger logger = LoggerFactory.getLogger(MlMessageParser.class);


    public MlMessageParser(SymphonyClient symClient){
        this.symClient = symClient;

    }

    public MlMessageParser() {
    }

    public void parseMessage(String message) throws Exception {

        Document doc = Jsoup.parse(message);
        originalDoc = doc.clone();
        Element elementErrors = doc.body().getElementsByTag("errors").first();

        if (elementErrors != null)
            logger.debug("Errors found in message: {}", elementErrors.outerHtml());

        //Lets remove the errors elements
        doc.select("errors").remove();

        elementMessageML = doc.select("messageML").first();

        if (elementMessageML != null) {

            logger.debug("Doc parsed: {}", elementMessageML.outerHtml());
        } else {

            logger.error("Could not parse document for message {}", message);
            throw new Exception("Malformed message");
        }

        textDoc = new StringBuilder();
        stripTags(textDoc, elementMessageML.childNodes());

        textChunks = textDoc.toString().split("\\s+");



    }

    public String[] getTextChunks() {
        return textChunks;
    }

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
                    User user = new User();
                    user.setEmailAddress("UID:" + node.attr(AttribTypes.UID.toString()));
                    user.setId(Long.valueOf(node.attr(AttribTypes.UID.toString())));

                    if (node.attributes().hasKey(AttribTypes.UID.toString())) {

                        if (symClient != null)
                            try {
                                user = symClient.getUsersClient().getUserFromId(Long.valueOf(node.attr(AttribTypes.UID.toString())));
                            } catch (Exception e) {
                                e.printStackTrace();
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


    public static void main(String[] args) {
        MlMessageParser m2 = new MlMessageParser();

        try {
            m2.parseMessage("<messageML>add <hash TAG=\"test\"/><a HREF=\"https://bits.mdevlab.com\"><b>ANCHOR <i>TEXT</i></b></a> some other stuff <cash tag=\"mrkt\"/> is there <mention uid=\"221232232\"/> <b>test of </b><a HREF=\"https://bits.mdevlab.com\"/>something<errors><error>Invalid control characters in message</error></errors></messageML>");
            System.out.println(m2.getText());
            System.out.println(m2.getHtmlStartingFromText("add"));
            System.out.println(m2.getHtmlStartingFromNode(NodeTypes.CASHTAG.toString(), AttribTypes.TAG.toString(), "mrkt"));
            System.out.println(m2.getHtmlStartingFromNode(NodeTypes.HASHTAG.toString(), AttribTypes.TAG.toString(), "test"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getText() {
        return textDoc.toString();
    }

    public String getOuterHtml() {
        return originalDoc.outerHtml();
    }


    private String getHtmlStartingFromText(String text) {


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

                if(node.nodeName().equalsIgnoreCase("#text") && node.outerHtml().charAt(0) != ' ')
                    builder.append(" ");

                builder.append(node.outerHtml());

                if(!node.nodeName().equalsIgnoreCase("#text"))
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

    public Elements getAllElements(){
        return elementMessageML.getAllElements();
    }

    public List<Node> getChildNodes(){

        return elementMessageML.childNodes();
    }
}

