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

package org.symphonyoss.client.ai.utils;


import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.ai.AiCommand;
import org.symphonyoss.client.ai.AiLastCommand;
import org.symphonyoss.client.common.MLTypes;
import org.symphonyoss.client.util.MlMessageParser;

import java.util.ArrayList;

/**
 * A static class used to determine if a command suggestion can be made.
 *
 * @author Nicholas Tarsillo
 */
public class AiSpellParser {
    /**
     * Determines if a given input matches another command closely enough,
     * in order to suggest a command
     *
     * @param commands        the list of commands to compare
     * @param chunks          the text chunks
     * @param closenessFactor the minimum closeness value needed to be considered a match
     * @return if a suggestion can be made
     */
    public static boolean canParse(ArrayList<AiCommand> commands, String[] chunks, double closenessFactor) {
        for (AiCommand response : commands) {


            if (chunks.length > response.getNumArguments()) {

                int likeness = 0;

                String[] checkCommand = response.getCommand().split("\\s+");
                for (int commandIndex = 0; commandIndex < checkCommand.length && commandIndex < chunks.length; commandIndex++) {

                    if (isCloseTo(chunks[commandIndex].trim(), checkCommand[commandIndex].trim(), closenessFactor))
                        likeness++;

                }

                int possibleArguments = chunks.length - likeness;
                if (possibleArguments >= response.getNumArguments()) {

                    if (closenessFactor <= (((double) likeness) / checkCommand.length)) {
                        return true;
                    }

                }

            }


        }

        return false;
    }

    /**
     * Find a close matching command.
     *
     * @param commands        the list of commands to compare
     * @param chunks          the text chunks
     * @param symClient       the org.org.symphonyoss.ai sym client
     * @param closenessFactor the minimum closeness value needed to be considered a match
     * @return command suggestion
     */
    public static AiLastCommand parse(ArrayList<AiCommand> commands, String[] chunks, SymphonyClient symClient, double closenessFactor) {
        for (AiCommand response : commands) {


            if (chunks.length >= response.getNumArguments()) {


                int likeness = 0;

                String[] checkCommand = response.getCommand().split("\\s+");
                for (int commandIndex = 0; commandIndex < checkCommand.length && commandIndex < chunks.length; commandIndex++) {

                    if (isCloseTo(chunks[commandIndex].trim(), checkCommand[commandIndex].trim(), closenessFactor))
                        likeness++;

                }

                int possibleArguments = chunks.length - likeness;
                if (possibleArguments >= response.getNumArguments()) {

                    if (closenessFactor <= (((double) likeness) / checkCommand.length)) {
                        String[] arguments = new String[response.getNumArguments()];

                        for (int index = 0; index < response.getNumArguments(); index++) {
                            arguments[index] = chunks[(chunks.length - 1) - index];
                        }

                        String fullCommand = response.getCommand() + " ";
                        for (int index = arguments.length - 1; index >= 0; index--) {
                            fullCommand += response.getPrefixRequirement((arguments.length - 1) - index) + arguments[index] + " ";
                        }

                        MlMessageParser mlMessageParser = new MlMessageParser(symClient);

                        try {

                            mlMessageParser.parseMessage(MLTypes.START_ML + fullCommand + MLTypes.END_ML);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return new AiLastCommand(mlMessageParser, response);
                    }

                }


            }


        }
        return null;
    }

    /**
     * Determines if one string is closely matched to another
     *
     * @param input1          the first string
     * @param input2          the second string
     * @param closenessFactor the minimum closeness value needed to be considered a match
     * @return if the two strings are close
     */
    private static boolean isCloseTo(String input1, String input2, double closenessFactor) {
        int likeness = 0;
        String larger;
        String smaller;

        if (input1.length() > input2.length()) {
            larger = input1;
            smaller = input2;
        } else {
            larger = input2;
            smaller = input1;
        }

        for (int index = 0; index < larger.length(); index++)
            if (smaller.contains(larger.substring(index, index + 1)))
                likeness++;

        return closenessFactor < (((double) likeness) / larger.length());
    }

}
