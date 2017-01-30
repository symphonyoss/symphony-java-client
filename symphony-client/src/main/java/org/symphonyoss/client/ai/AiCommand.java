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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.client.ai;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.symphony.clients.model.SymMessage;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A model for ai commands.
 * Used to compare input and command, and check for a match.
 *
 * @author Nicholas Tarsillo
 */
@SuppressWarnings("WeakerAccess")
public class AiCommand {
    private final Logger logger = LoggerFactory.getLogger(AiCommand.class);

    private String command;
    private int numArguments;
    private String[] prefixRequirements = {""};
    private String[] arguments = new String[0];
    private String usage;

    private Set<AiAction> actions = new LinkedHashSet<>();
    private Set<AiPermission> permissions = new HashSet<>();

    public AiCommand(String command, int numArguments, String usage) {
        setCommand(command);
        setNumArguments(numArguments);
        setUsage(usage);
    }

    /**
     * Checks to see if the user's input fulfills the ai command requirements
     *
     * @param chunks the user's input in text chunks
     * @return if the user input fulfills the command requirements
     */
    public boolean isCommand(String[] chunks) {
        String[] checkCommand = command.split("\\s+");

        if ((chunks.length - checkCommand.length) + 1 <= numArguments) {
            return false;
        }

        for (int commandIndex = 0; commandIndex < checkCommand.length; commandIndex++) {

            if (!chunks[commandIndex].trim().equalsIgnoreCase(checkCommand[commandIndex].trim())) {
                return false;
            }

        }

        for (int chunkIndex = checkCommand.length; chunkIndex < numArguments + checkCommand.length; chunkIndex++) {

            if (!chunks[chunkIndex].startsWith(prefixRequirements[chunkIndex - checkCommand.length])) {
                return false;
            }

        }

        return true;
    }

    /**
     * Creates a HTML string, that can be used to instruct users how to use this
     * command.
     *
     * @return the usage string in HTML
     */

    public String toMLCommand() {
        StringBuilder toML = new StringBuilder();
        toML.append("    <b>");
        toML.append(command);
        toML.append("</b> ");

        for (int index = 0; index < numArguments; index++) {
            toML.append( prefixRequirements[index]);
            toML.append( arguments[index]);
            toML.append("     (");
            toML.append(usage);
            toML.append(")");
        }

        toML.append("<br/>");


        return toML.toString();
    }

    /**
     * Determines if a user is allowed to use this command
     *
     * @param userID the user's id
     * @return if the user is permitted to use this command
     */
    public boolean userIsPermitted(Long userID) {
        for (AiPermission permission : permissions) {

            if (!permission.userHasPermission(userID)) {
                return false;
            }

        }

        return true;
    }

    /**
     * Executes all the command's actions.
     * Receives back all the response sequences from the actions.
     *
     * @param mlMessageParser a parser that contains the input in ML
     * @param message         the received message
     * @return a set of responses, given by completing all the commands actions
     */
    public Set<AiResponseSequence> getResponses(MlMessageParser mlMessageParser, SymMessage message) {
        Set<AiResponseSequence> responses = new LinkedHashSet<>();

        for (AiAction action : getActions()) {
            responses.add(action.respond(mlMessageParser, message, this));
        }

        return responses;
    }

    //Private methods
    public void resizePrefixesArguments() {

        String[] resize = new String[numArguments];

        for (int index = 0; index < prefixRequirements.length && index < numArguments; index++) {

            resize[index] = prefixRequirements[index];
            if (resize[index] == null)
                resize[index] = "";

        }

        prefixRequirements = resize;

        resize = new String[numArguments];
        for (int index = 0; index < arguments.length && index < numArguments; index++) {

            resize[index] = arguments[index];
            if (resize[index] == null)
                resize[index] = "";

        }

        arguments = resize;

    }

    //Getters and Setters
    public int getNumArguments() {
        return numArguments;
    }

    public void setNumArguments(int numArguments) {

        this.numArguments = numArguments;
        resizePrefixesArguments();

    }


    public void setPrefixRequirement(int argumentIndex, String requirement) {

        if (argumentIndex > numArguments) {

            if (logger != null)
                logger.debug("Could not add prefix requirement {}," +
                        " not enough arguments.", requirement);

            return;
        }

        prefixRequirements[argumentIndex] = requirement;

    }


    public void setAllPrefixRequirements(String[] prefixRequirements) {
        this.prefixRequirements = prefixRequirements;
    }


    public String getPrefixRequirement(int argumentIndex) {

        if (prefixRequirements.length > argumentIndex) {
            return prefixRequirements[argumentIndex];
        } else {
            return null;
        }

    }


    public void setArgument(int argumentIndex, String holder) {

        if (argumentIndex > numArguments) {

            if (logger != null)
                logger.debug("Could not add place holder {}, " +
                        "not enough arguments.", holder);

            return;
        }

        arguments[argumentIndex] = holder;

    }


    public void setAllArguments(String[] placeHolders) {
        this.arguments = placeHolders;
    }


    public String getArguments(int argumentIndex) {

        if (arguments.length < argumentIndex) {
            return arguments[argumentIndex];
        } else {
            return null;
        }

    }


    public String getCommand() {
        return command;
    }


    public void setCommand(String command) {
        this.command = command;
    }


    public Set<AiPermission> getPermissions() {
        return permissions;
    }


    public void setPermissions(Set<AiPermission> permissions) {
        this.permissions = permissions;
    }


    public Set<AiAction> getActions() {
        return actions;
    }


    public void setActions(Set<AiAction> actions) {
        this.actions = actions;
    }


    public void addPermission(AiPermission permission) {
        permissions.add(permission);
    }


    public void addAction(AiAction action) {
        actions.add(action);
    }


    public void removePermission(AiPermission permission) {
        permissions.remove(permission);
    }


    public void removeAction(AiAction action) {
        actions.remove(action);
    }


    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}


