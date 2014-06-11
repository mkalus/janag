/**
 * Copyright (C) 2008-2014 Maximilian Kalus.  All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package de.beimax.janag;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @author mkalus Starter Class to start a Namegenerator HTTP/JSON server.
 */
public class NamegenHTTPServer {
    /**
     * static reference to name generator - used by all threads (through getInstance)
     */
    private static NameGenerator ng = new NameGenerator("languages.txt",
            "semantics.txt");

    /**
     * @param args
     *            expects up to one arguement (socket number)
     */
    public static void main(String[] args) {
        int port = 12023;

        // Ask command line
        if (args.length == 1) {// Port as argument
            port = Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            System.err
                    .println("Too many arguements - only considering the first"); //$NON-NLS-1$
        }

        System.out.println("Server running on port " + port + "..."); //$NON-NLS-1$

        // create class
        NanoHTTPDInstance server = new NanoHTTPDInstance(port);
        ServerRunner.executeInstance(server);
    }


    private static class NanoHTTPDInstance extends NanoHTTPD {
        /**
         * Constructor
         * @param port
         */
        public NanoHTTPDInstance(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            // only GET accepted
            if (!String.valueOf(session.getMethod()).equalsIgnoreCase("GET"))
                return error();

            // get uri
            String uri = String.valueOf(session.getUri());

            // check uri queries
            if (uri.equals("/") || uri.equals("/help"))
                return help();
            if (uri.equals("/langs"))
                return langs();
            if (uri.startsWith("/patterns/"))
                return patterns(uri.substring(10));
            // fallback
            if (uri.startsWith("/patterns"))
                return patterns("");
            if (uri.startsWith("/genders/"))
                return genders(uri.substring(9).split("/"));
            if (uri.startsWith("/get/"))
                return get(uri.substring(5).split("/"));

            return error404();
        }

        /**
         * Create response and add CORS headers
         * @param status
         * @param mimeType
         * @param txt
         * @return
         */
        private Response getResponse(Response.IStatus status, String mimeType, String txt) {
            Response response = new Response(status, mimeType, txt);
            // allow CORS
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET");
            response.addHeader("Access-Control-Max-Age", "3600");
            return response;
        }

        /**
         * print error
         * @return
         */
        private Response error() {
            return getResponse(Response.Status.INTERNAL_ERROR, "text/plain", "ERROR");
        }

        /**
         * print 404 error
         * @return
         */
        private Response error404() {
            return getResponse(Response.Status.NOT_FOUND, "text/plain", "NOT FOUND");
        }

        /**
         * Print help
         * @return
         */
        private Response help() {
            return getResponse(Response.Status.OK, "text/plain", "JaNaG Java Name Generator - HTTP/JSON Server\n\nUse the following uris to work with JaNaG:\n\n" +
                    "/help - print this help\n" +
                    "/langs - get JSON list of languages\n" +
                    "/patterns/[lang] - get JSON list of patterns\n" +
                    "/genders/[lang]/[pattern] - get JSON list of possible genders for a certain pattern\n" +
                    "/get/[lang]/[pattern]/[gender]/[count] - retrieves a number of random names");
        }

        /**
         * Print languages - fixed for now
         * @return
         */
        private Response langs() {
            return getResponse(Response.Status.OK, "application/json", "[\"en\",\"de\"]");
        }

        /**
         * Print patterns as JSON
         * @param lang
         * @return
         */
        private Response patterns(String lang) {
            try {
                // translate language
                lang = parseLanguage(lang);
            } catch (Exception e) {
                return error();
            }

            return getResponse(Response.Status.OK, "application/json", arrayToJSON(ng.getPatterns(lang)));
        }

        /**
         * Print genders
         * @param parameters
         * @return
         */
        private Response genders(String[] parameters) {
            if (parameters.length != 2) return error();

            String lang;
            try {
                // translate language
                lang = parseLanguage(parameters[0]);
            } catch (Exception e) {
                return error();
            }

            try {
                return getResponse(Response.Status.OK, "application/json", arrayToJSON(ng.getGenders(parameters[1], lang)));
            } catch (Exception e) {
                return error();
            }
        }

        /**
         * Print random names
         * @param parameters
         * @return
         */
        private Response get(String[] parameters) {
            if (parameters.length < 3 || parameters.length > 4) return error();

            String lang;
            try {
                // translate language
                lang = parseLanguage(parameters[0]);
            } catch (Exception e) {
                return error();
            }

            int count = parameters.length==3?1:Integer.parseInt(parameters[3]);

            try {
                return getResponse(Response.Status.OK, "application/json", arrayToJSON(ng.getRandomName(parameters[1], parameters[2], count, lang)));
            } catch (Exception e) {
                return error();
            }
        }

        /**
         * helper method to parse language string
         *
         * @param lang
         * @return
         * @throws java.io.IOException
         */
        private String parseLanguage(String lang)
                throws IOException {
            if (lang.equals("")) return "en"; // default
            if (lang.startsWith("de")) return "de";
            if (lang.startsWith("DE")) return "de";
            if (lang.startsWith("en")) return "en";
            if (lang.startsWith("EN")) return "en";

            throw new IOException();
        }

        /**
         * render JSON array
         * @param list
         * @return
         */
        private String arrayToJSON(String[] list) {
            boolean started = true;
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (String pattern : list) {
                if (started) started = false;
                else sb.append(',');
                sb.append('"').append(pattern.replace("\"", "\\\"")).append('"');
            }
            sb.append(']');

            return sb.toString();
        }
    }
}
