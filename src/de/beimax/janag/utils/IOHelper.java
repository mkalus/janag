/**
 * $Id$
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

package de.beimax.janag.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.beimax.janag.i18n.I18N;
import de.beimax.janag.i18n.Messages;

/**
 * @author mkalus
 *
 * Input/Output helper class
 */
public class IOHelper {
	/**
	 * @param prompt string
	 * Prints a simple prompt string to stdout
	 */
	public static void printPrompt(String prompt)
	{
		if (prompt == "") return;
        System.out.print(prompt + " ");
		System.out.flush();
	}

	/**
	 * @param prompt string
	 * @return string from keyboard
	 * Reads a string from keyboard
	 */
	public static String readString(String prompt)
	{
		String input = "";

		printPrompt(prompt);

        try {
			byte buffer[] = new byte[80];
			int read = 0;
			read = System.in.read(buffer, 0, 80);
			input = new String(buffer, 0, read-2);
		}
		catch(java.io.IOException e) { System.err.println(I18N.geti18nString(Messages.getString("MyUtils.InputError"))); } //$NON-NLS-1$

		System.out.println();
		return input;
	}

	/**
	 * @param prompt string
	 * @return integer from keyboard
	 * Reads an integer from keyboard
	 */
	public static int readInt(String prompt)
	{
		boolean success = false;
		int intInput = 0;
		String userInput = null;

		printPrompt(prompt);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do {
			try {
				userInput = br.readLine();
			}
			catch (IOException ioe) { System.out.println(I18N.geti18nString(Messages.getString("MyUtils.IOError"))); } //$NON-NLS-1$

			try {
				intInput = Integer.parseInt(userInput);
				success = true;
			}
			catch(NumberFormatException e) { System.err.println(I18N.geti18nString(Messages.getString("MyUtils.NoInteger"))); success = false; } //$NON-NLS-1$
		} while(success == false);

		System.out.println();
		return intInput;
	}

	/**
	 * @param prompt string
	 * @return double from keyboard
	 * Reads a double value from keyboard
	 */
	public static double readDouble(String prompt)
	{
		boolean success = false;
		double zahl = -1;

		printPrompt(prompt);

		do {
			try {
				String input = readString("");
				zahl = Double.parseDouble(input);
				success = true;
			}
			catch(NumberFormatException e) { System.err.println(I18N.geti18nString(Messages.getString("MyUtils.NoFloat"))); } //$NON-NLS-1$
		} while(success == false);

		System.out.println();
		return zahl;
	}

	/**
	 * @param prompt string
	 * @return single char
	 * Reads a single char from keyboard
	 */
	public static char readChar(String prompt)
	{
		char c = ' ';

		printPrompt(prompt);

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String eingabe = in.readLine();
			c = eingabe.charAt(0);
		}
		catch(IOException x) { c = ' '; }
		catch(IndexOutOfBoundsException x) { c = ' '; }

		System.out.println();
		return c;
	}
}
