/**
 * $Id: NamegenServer.java 14 2010-08-11 09:29:41Z ronix $
 *
 * Copyright (C) 2008-2010 Maximilian Kalus. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package de.beimax.janag;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;

/**
 * @author mkalus Starter Class to start a Namegenerator server.
 */
public class NamegenServer {

    private static int port = 12022;
    /**
     * static reference to name generator - used by all threads (through
     * getInstance)
     */
    private static final NameGenerator ng = new NameGenerator("languages.txt",
            "semantics.txt");

    /**
     * @param args expects up to one argument (socket number)
     */
    public static void main(String[] args) {

        // Ask command line
        if (args.length == 1) {// Port as argument
            port = Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            System.err.println("Too many arguements - only considering the first"); //$NON-NLS-1$
        }

        try {
            ServerSocket seso = new ServerSocket(port);
            port = seso.getLocalPort();
            System.out.println("JaNaG Server version: " + getVersion());
            System.out.println("Starting server on port " + port + "..."); //$NON-NLS-1$
            System.out.println("Expected request form: GET \"PATTERN\" \"GENDER\" COUNT \"LANGUAGE\""); //$NON-NLS-1$

            while (true) {
                Socket so = seso.accept();
                Thread worker = new NamegenServerThread(so, ng.getInstance());
                worker.start();
            }
        } catch (IOException e) {
            System.err.println("Server IO Error"); //$NON-NLS-1$
        }
        System.out.println("Stopping Server..."); //$NON-NLS-1$
    }

    public static synchronized String getVersion() {
        String version = null, date = null;

        // try to load from maven properties first
        try {
            Properties p = new Properties();
            InputStream is = NameGenerator.class.getResourceAsStream("/"
                    + NamegenServer.class.getPackage().getName().replaceAll("\\.", "/")
                    + "/version.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", "");
                date = p.getProperty("build.date", new Date().toString());
            }
        } catch (Exception e) {
            // ignore
        }

        // fallback to using Java API
        if (version == null) {
            Package aPackage = NameGenerator.class.getPackage();
            if (aPackage != null) {
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    version = aPackage.getSpecificationVersion();
                }
            }
        }

        if (version == null) {
            // we could not compute the version so use a blank
            version = "";
        }

        return date == null ? version : version + "\nBuild: " + date;
    }

    public static int getPort() {
        return port;
    }
}
