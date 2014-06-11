/**
 * $Id: I18N.java 4 2008-12-23 14:52:29Z ronix $
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
package de.beimax.janag.i18n;

import java.util.Locale;

/**
 * @author mkalus Internationalizer for strings and data entries...
 */
public class I18N {
	/**
	 * flag for initialization
	 */
	private static boolean initialized = false;
	
	/**
	 * Current language (i18n/l10n)
	 */
	private static int LangIdx;
	/**
	 * List of languages (i18n/l10n)
	 */
	private static String[] LangList;

	/**
	 * initializer
	 */
	private static void init() {
		getLangList(); //call lang list
		initialized = true;
	}
	
	/**
	 * @param line
	 *            input line
	 * @return part of the line that was internationalized
	 * 
	 *         Reads an internationalized version of a string
	 */
	public static String geti18nString(String line) {
		if (!initialized) init(); //initialize, of needed
		
		String elements[] = line.split("\\|"); //$NON-NLS-1$

		if (elements.length <= LangIdx)
			return elements[0]; // in case none is set
		else {
			String output = elements[LangIdx];
			if (output.equals(""))
				output = elements[0]; // Default Name

			return output;
		}
	}

	/**
	 * @param line
	 *            input line
	 * @param lang
	 *            language, e.g. "de"
	 * @return part of the line that was internationalized
	 * 
	 *         Reads an internationalized version of a string
	 */
	public static String geti18nString(String line, String lang) {
		if (!initialized) init(); //initialize, of needed
		
		String elements[] = line.split("\\|"); //$NON-NLS-1$

		int id = findLangId(lang);

		// default language in cases where there is no translation
		if (id == 0 || elements.length <= id)
			return elements[0];

		// otherwise, return localized string
		return elements[id];
	}

	/**
	 * @param line
	 *            input line
	 * @param langId
	 *            language-Id
	 * @return part of the line that was internationalized
	 * 
	 *         Reads an internationalized version of a string
	 */
	public static String geti18nString(String line, int langId) {
		if (!initialized) init(); //initialize, of needed
		
		String elements[] = line.split("\\|"); //$NON-NLS-1$

		// default language in cases where there is no translation
		if (langId == 0 || elements.length <= langId)
			return elements[0];

		// otherwise, return localized string
		return elements[langId];
	}

	/**
	 * searches for a language in the index and returns its it
	 * 
	 * @param lang
	 * @return id of language, or 0 for not found/default
	 */
	public static int findLangId(String lang) {
		if (!initialized) init(); //initialize, of needed
		
		for (int i = 0; i < LangList.length; i++) {
			if (LangList[i].equalsIgnoreCase(lang))
				return i + 1;
		}

		return 0; // not found
	}
	
	/**
	 * 
	 * @return locale lang index
	 */
	public static int getLangId() {
		if (!initialized) init(); //initialize, of needed
		
		return LangIdx;
	}

	/**
	 * @param lang
	 *            language setting set language to lang (e.g. "en", "de", etc.)
	 */
	public static void getLangList(String lang) {
		// Get Language List
		LangList = Messages.getString("Namegenerator.langs").split("\\|"); //$NON-NLS-1$

		LangIdx = 0;
		// Find Language
		for (int i = 0; i < LangList.length; i++)
			if (LangList[i].equals(lang)) {
				LangIdx = i + 1;
				break;
			}
	}

	/**
	 * automatically detect language
	 */
	public static void getLangList() {
		String lang = Locale.getDefault().getLanguage();
		getLangList(lang);
	}
}
