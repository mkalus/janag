/**
 * $Id: NamegenServerThread.java 14 2010-08-11 09:29:41Z ronix $
 *
 * Copyright (C) 2008-2010 Maximilian Kalus.  All rights reserved.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.Socket;
import java.util.Locale;

import de.beimax.janag.i18n.I18N;
import de.beimax.janag.i18n.Messages;

/**
 * @author mkalus Working thread of ng server
 */
public class NamegenServerThread extends Thread {
	/**
	 * Namegenerator instance of worker
	 */
	private NameGenerator ng;

	/**
	 * Socket of worker
	 */
	private Socket so;

	/**
	 * @param cs
	 *            socket of worker Constructor
	 * @param ng
	 *            name generator instance
	 */
	public NamegenServerThread(Socket cs, NameGenerator ng) {
		this.so = cs;
		this.ng = ng;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		String command = "";

		System.out.println(so.getInetAddress().getHostAddress() + ": Open.");

		try {
			// Input stream from client
			BufferedReader receive = new BufferedReader(new InputStreamReader(
					so.getInputStream()));

			// Output stream to client
			BufferedWriter send = new BufferedWriter(new OutputStreamWriter(
					so.getOutputStream()));

			try {
				command = receive.readLine();
				System.out.println(so.getInetAddress().getHostAddress()
						+ ": Command: " + command);

				// parse command
				String[] echo = parseCommand(command);

				for (int i = 0; i < echo.length; i++) {
					System.out.println(so.getInetAddress().getHostAddress()
							+ ": Answer: " + echo[i]);
					send.write(echo[i] + "\n");
				}
			} catch (Exception e) {
				new PrintWriter(send).println("Error!");
				System.err.println("Error in command: " + command);
			}
			send.flush();
			so.close();
		} catch (IOException e) {
			ng = null;
			System.err.println("Socket-Error!");
			return;
		}

		ng = null;
		System.out.println(so.getInetAddress().getHostAddress() + ": Close.");
	}

	/**
	 * @param command
	 *            http-command
	 * @return list of randomly generated names or other information - or error
	 *         Parses command given to a thread
	 */
	private String[] parseCommand(String command) {
		// create tokenizer to parse command
		StreamTokenizer st = new StreamTokenizer(new StringReader(command));

		// get first command
		try {
			// First arguement has to be GET, PATTERNS or GENDERS
			int type = st.nextToken();
			if (type != StreamTokenizer.TT_WORD
					|| st.sval.equalsIgnoreCase("HELP"))
				return help(); // return help/usage
			if (st.sval.equals("GET"))
				return getRandomNames(st);
			if (st.sval.equals("PATTERNS"))
				return getPatterns(st);
			if (st.sval.equals("GENDERS"))
				return getGenders(st);
			return help(); // return help/usage
		} catch (IOException e) {
			System.err.println(I18N.geti18nString(Messages
					.getString("NamegenServerThread.CommandError"))); //$NON-NLS-1$
			return new String[] { I18N.geti18nString(Messages
					.getString("NamegenServerThread.CommandErrorStart")) + e.getMessage() + I18N.geti18nString(Messages.getString("NamegenServerThread.CommandErrorEnd")) }; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * returns help
	 * 
	 * @return
	 */
	private String[] help() {
		return new String[] {
				"Use the following commands to work with JaNaG:",
				"GET \"PATTERN\" \"GENDER\" COUNT \"LANG\" retrieves a number of random names.",
				"PATTERNS \"LANG\" returns a list of possible patterns in a certain language.",
				"GENDERS \"PATTERN\" \"LANG\" returns a list of possible genders for a certain pattern." };
	}

	/**
	 * Handle stream for "GET" command
	 * 
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private String[] getRandomNames(StreamTokenizer st) throws IOException {
		// now pattern
		st.nextToken();
		if (st.sval == null || st.sval.equals(""))throw new IOException("Argument 2 must be a pattern name enclosed by \""); //$NON-NLS-2$
		String pattern = st.sval;

		// now gender
		st.nextToken();
		if (st.sval == null || st.sval.equals(""))throw new IOException("Argument 3 must be a gender name enclosed by \""); //$NON-NLS-2$
		String gender = st.sval;

		// at last the number of arguements
		int type = st.nextToken();
		if (type != StreamTokenizer.TT_NUMBER)
			throw new IOException("Argument 4 must be an integer number"); //$NON-NLS-1$
		int count = (int) st.nval;

		// get language
		String lang = getLanguageFromTokenizer(st);

		// more?
		if (st.nextToken() != StreamTokenizer.TT_EOF)
			throw new IOException("Expected request form: GET \"PATTERN\" \"GENDER\" COUNT \"LANGUAGE\"");

		// ok, everything fine - now get names
		try {
			return ng.getRandomName(pattern, gender, count, lang);
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { "###ERROR###" }; //$NON-NLS-1$
		}
	}

	/**
	 * Handle stream for "PATTERNS" command
	 * 
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private String[] getPatterns(StreamTokenizer st) throws IOException {
		// get language
		String lang = getLanguageFromTokenizer(st);

		if (st.nextToken() != StreamTokenizer.TT_EOF)
			throw new IOException(
					"PATTERNS takes exactly one (language) or no arguments (default language)!");

		return ng.getPatterns(lang);
	}

	/**
	 * Handle stream for "GENDERS" command
	 * 
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private String[] getGenders(StreamTokenizer st) throws IOException {
		// find pattern
		st.nextToken();
		if (st.sval == null || st.sval.equals(""))throw new IOException("Argument 2 must be a pattern name enclosed by \""); //$NON-NLS-2$
		String pattern = st.sval;

		// also, get language
		String lang = getLanguageFromTokenizer(st);

		// more?
		if (st.nextToken() != StreamTokenizer.TT_EOF)
			throw new IOException(
					"GENDERS takes exactly two (pattern, language) or one arguments (pattern using default language)!");

		// ok, everything fine - now get names
		return ng.getGenders(pattern, lang);
	}

	/**
	 * helper method to parse language string from tokenizer
	 * 
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private String getLanguageFromTokenizer(StreamTokenizer st)
			throws IOException {
		st.nextToken();
		// if not defined -> define default language of server...
		if (st.sval == null || st.sval.equals(""))
			return Locale.getDefault().getLanguage();
		// else return language string
		return st.sval;
	}
}
