/*
 *
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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

package org.symphonyoss.client.model;

import org.symphonyoss.symphony.agent.model.ShareArticle;

/**
 *
 * An abstraction object representing a Share Article entity.  This is a pre-built component that is populated by the
 * client and published into Symphony.  In simple terms the output looks like a standard news headline.
 *
 * @author Frank Tarsillo on 10/22/2016.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SymShareArticle {

    private String articleId = null;

    private String title = null;

    private String subTitle = null;

    private String message = null;

    private String publisher = null;

    private String thumbnailUrl = null;

    private String author = null;

    private String articleUrl = null;

    private String summary = null;

    private String appId = null;

    private String appName = null;

    private String appIconUrl = null;

    private Long publishDate = null;




    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIconUrl() {
        return appIconUrl;
    }

    public void setAppIconUrl(String appIconUrl) {
        this.appIconUrl = appIconUrl;
    }

    public Long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Long publishDate) {
        this.publishDate = publishDate;
    }

    public static ShareArticle toShareArticle(SymShareArticle symShareArticle){

        ShareArticle shareArticle = new ShareArticle();
        shareArticle.setAppIconUrl(symShareArticle.getAppIconUrl());
        shareArticle.setAppId(symShareArticle.getAppId());
        shareArticle.setAppIconUrl(symShareArticle.getAppIconUrl());
        shareArticle.setAppName(symShareArticle.getAppName());
        shareArticle.setArticleId(symShareArticle.getArticleId());
        shareArticle.setArticleUrl(symShareArticle.getArticleUrl());
        shareArticle.setAuthor(symShareArticle.getAuthor());
        shareArticle.setMessage(symShareArticle.getMessage());
        shareArticle.setPublisher(symShareArticle.getPublisher());
        shareArticle.setSubTitle(symShareArticle.getSubTitle());
        shareArticle.setSummary(symShareArticle.getSummary());
        shareArticle.setThumbnailUrl(symShareArticle.getThumbnailUrl());
        shareArticle.setTitle(symShareArticle.getTitle());
        shareArticle.setPublishDate(symShareArticle.getPublishDate());

        return shareArticle;

    }


}
