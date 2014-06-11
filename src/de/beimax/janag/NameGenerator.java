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

package de.beimax.janag;

import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import de.beimax.janag.lists.*;
import de.beimax.janag.i18n.*;

/**
 * @author mkalus
 * 
 *         The name generator - core of the program
 */
public class NameGenerator {
	/**
	 * hashset of languages
	 */
	private Hashtable<String, JaNaGList> languages = null;

	private Hashtable<String, Hashtable<String, JaNaGWeightedList>> namePatterns = null;

	/**
	 * @param languageFile
	 *            filename of language file
	 * @param semanticsFile
	 *            filename of semantics file Constructor
	 */
	public NameGenerator(String languageFile, String semanticsFile) {
		I18N.getLangList(); // set language
		readSemantics(semanticsFile);
		readLanguage(languageFile);
	}

	/**
	 * @param languageFile
	 *            filename of language file
	 * @param semanticsFile
	 *            filename of semantics file
	 * @param lang
	 *            locale entry, e.g. "de", "nl", ... Constructor
	 */
	public NameGenerator(String languageFile, String semanticsFile, String lang) {
		I18N.getLangList(lang); // set specific language
		readSemantics(semanticsFile);
		readLanguage(languageFile);
	}

	/**
	 * private copy constructor
	 * 
	 * @param languages
	 * @param namePatterns
	 */
	private NameGenerator(Hashtable<String, JaNaGList> languages,
			Hashtable<String, Hashtable<String, JaNaGWeightedList>> namePatterns) {
		this.languages = languages;
		this.namePatterns = namePatterns;
	}

	/**
	 * returns new instance of name generator - useful for multithreaded
	 * applications
	 * 
	 * @return new instance of name generator with implemented languages and
	 *         name patterns (you must only read these, not write anything to
	 *         them!!!)
	 */
	public NameGenerator getInstance() {
		return new NameGenerator(this.languages, this.namePatterns);
	}

	/**
	 * @param semanticsFile
	 *            filename of semantics file Reads the semantic file and parses
	 *            it into the semantic structure
	 */
	public void readSemantics(String semanticsFile) {
		String line;
		Hashtable<String, JaNaGWeightedList> pattern = null;
		JaNaGWeightedList group = null;

		namePatterns = new Hashtable<String, Hashtable<String, JaNaGWeightedList>>();

		// open file
		File file = new File(semanticsFile);
		// check file existence
		// if(!file.exists()) {
		//	System.err.println(Messages.getString("Namegenerator.FileDoesNotExist")); //$NON-NLS-1$
		// System.exit(0);
		// }

		try { // read file by line
				// either get the file as resource or as file stream
			InputStream fIS = getClass().getResourceAsStream(file.getName());
			if (fIS == null)
				fIS = new FileInputStream(file);
			// buffered input as UTF8
			BufferedReader lNR = new BufferedReader(new InputStreamReader(fIS,
					"UTF8"));

			// traverse the file
			while ((line = lNR.readLine()) != null) {

				if (line.trim().equals("[End]"))
					break; // if end is reached - break while

				// ignore empty lines and comments
				if (!line.startsWith("##") && !line.trim().equals("")) {
					if (pattern == null && (line.charAt(0) != '['))
						throw new IOException(
								I18N.geti18nString(Messages
										.getString("Namegenerator.SemanticsFileSyntaxError")) + line); //$NON-NLS-1$
					else if (pattern != null && group == null
							&& (line.charAt(0) != ':'))
						throw new IOException(I18N.geti18nString(Messages
								.getString("Namegenerator.NoGenderSelected"))); //$NON-NLS-1$
					if (line.charAt(0) == '['
							&& line.charAt(line.length() - 1) == ']') {
						line = line.substring(1, line.length() - 1);
						pattern = new Hashtable<String, JaNaGWeightedList>(); // new
																				// pattern
																				// group

						namePatterns.put(line, pattern); // add group to list
					} else if (line.charAt(0) == ':') {
						line = line.substring(1);
						group = new JaNaGWeightedList(); // add group to list

						pattern.put(line, group);
					} else {
						int myweight;
						String[] part = line.split(":");
						if (part.length != 3)
							throw new IOException(
									I18N.geti18nString(Messages
											.getString("Namegenerator.WrongNameStart")) + line + I18N.geti18nString(Messages.getString("Namegenerator.WrongNameEnd"))); //$NON-NLS-1$ //$NON-NLS-2$

						if (part[1].equals(""))
							myweight = 10;
						else
							try {
								myweight = Integer.parseInt(part[1]);
							} catch (NumberFormatException e) {
								e.printStackTrace();
								myweight = 10;
							}

						group.add(part[2], myweight); // add group element
					}
				}
			}

			fIS.close();
			fIS = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param languageFile
	 *            filename of language file Reads the language file and parses
	 *            it into the linguistic structure
	 */
	private void readLanguage(String languageFile) {
		String line;
		JaNaGList group = null;
		String key = null;

		// create new hashset
		languages = new Hashtable<String, JaNaGList>();

		// open file
		File file = new File(languageFile);
		// check file existence
		// if(!file.exists()) {
		//	System.err.println(Messages.getString("Namegenerator.FileDoesNotExist")); //$NON-NLS-1$
		// System.exit(0);
		// }

		try { // read file by line
				// either get the file as resource or as file stream
			InputStream fIS = getClass().getResourceAsStream(file.getName());
			if (fIS == null)
				fIS = new FileInputStream(file);
			// buffered input as UTF8
			BufferedReader lNR = new BufferedReader(new InputStreamReader(fIS,
					"UTF8"));

			// traverse the file
			while ((line = lNR.readLine()) != null) {

				if (line.trim().equals("[End]"))
					break; // if end is reached - break while

				// ignore empty lines and comments
				if (!line.startsWith("##") && !line.trim().equals("")) {
					if (group == null && line.charAt(0) != '[')
						throw new IOException(
								I18N.geti18nString(Messages
										.getString("Namegenerator.LanguageFileSyntaxError")) + line); //$NON-NLS-1$
					if (line.charAt(0) == '['
							&& line.charAt(line.length() - 1) == ']') {
						key = line.substring(1, line.length() - 1);
						group = new JaNaGList();

						// add new group to list
						languages.put(key, group);
					} else {
						if (line.charAt(0) == '$')
							line = line.substring(1);
						else
							line = line.trim();
						// add line to group
						group.add(line);
					}
				}
			}

			fIS.close();
			fIS = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param pattern
	 *            name of pattern (localized)
	 * @param gender
	 *            name of gender in that pattern (localized)
	 * @param count
	 *            number of random names to generate
	 * @param lang
	 *            short language
	 * @return array of random names Creates an array of randomly generated
	 *         names based on pattern/gender
	 */
	public String[] getRandomName(String pattern, String gender, int count,
			String lang) {
		return getRandomName(pattern, gender, count, I18N.findLangId(lang));
	}

	/**
	 * @param pattern
	 *            name of pattern (localized)
	 * @param gender
	 *            name of gender in that pattern (localized)
	 * @param count
	 *            number of random names to generate
	 * @return array of random names Creates an array of randomly generated
	 *         names based on pattern/gender
	 */
	public String[] getRandomName(String pattern, String gender, int count) {
		return getRandomName(pattern, gender, count, I18N.getLangId());
	}

	/**
	 * @param pattern
	 *            name of pattern (localized)
	 * @param gender
	 *            name of gender in that pattern (localized)
	 * @param count
	 *            number of random names to generate
	 * @param langId
	 *            language index
	 * @return array of random names Creates an array of randomly generated
	 *         names based on pattern/gender
	 */
	private String[] getRandomName(String pattern, String gender, int count,
			int langId) {
		// error handling - number lower than 1?
		if (count < 1) {
			String[] back = new String[1];
			back[0] = I18N.geti18nString(Messages
					.getString("Namegenerator.CountLessThanOne")); //$NON-NLS-1$
			return back;
		}

		// error handling - find pattern
		String internationalPattern = translateLocalizedPatternName(pattern,
				langId);
		if (internationalPattern == null) {
			String[] back = new String[1];
			back[0] = I18N.geti18nString(Messages
					.getString("Namegenerator.NoPatternFound"), langId) + pattern; //$NON-NLS-1$
			return back;
		}

		// error handling - find gender
		String internationalGender = translateLocalizedGenderName(
				internationalPattern, gender, langId);
		if (internationalGender == null) {
			String[] back = new String[1];
			back[0] = I18N.geti18nString(Messages
					.getString("Namegenerator.NoGenderFound"), langId) + gender; //$NON-NLS-1$
			return back;
		}

		// define back array
		String[] back = new String[count];
		// get list of possible generators
		JaNaGWeightedList randomNameGeneratorList = namePatterns.get(
				internationalPattern).get(internationalGender);

		// generate list!
		for (int i = 0; i < count; i++) {
			// get random generator
			String generator = randomNameGeneratorList.getRandom();

			// add parsed generator string
			back[i] = parseGeneratorString(generator, langId);
		}

		return back;
	}

	/**
	 * parses a generator string and creates a random name from it
	 * 
	 * @param generator
	 * @param langId
	 *            language index
	 * @return
	 */
	private String parseGeneratorString(String generator, int langId) {
		StringBuilder randomName = new StringBuilder();

		// split generator and add each element randomly
		String[] subpart = generator.split(",");
		for (int i = 0; i < subpart.length; i++) {
			// uppercase? part has to start with "^"
			boolean upperCase;
			if (subpart[i].charAt(0) == '^') {
				subpart[i] = subpart[i].substring(1);
				upperCase = true;
			} else
				upperCase = false;

			// find language entry - maybe localized
			JaNaGList langEntry = languages.get(I18N.geti18nString(subpart[i], langId));
			// found entry?
			if (langEntry == null) //error:
				randomName.append("###ERROR###").append(subpart[i]);
			else { //ok, found it, take random entry from it
				String randPart = I18N.geti18nString(langEntry.getRandom(), langId);
				if (upperCase) {
					char ch = randPart.charAt(0);
					if (Character.isLetter(ch))
						ch = Character.toUpperCase(ch);
					randomName.append(ch).append(randPart.substring(1));
				} else
					randomName.append(randPart);
			}
		}

		return randomName.toString();
	}

	/**
	 * returns a list of all patterns
	 * 
	 * @param lang
	 *            short language
	 * @return list of all possible patterns
	 * 
	 *         Returns an array of all possible patterns
	 */
	public String[] getPatterns(String lang) {
		// get language id
		return getPatterns(I18N.findLangId(lang));
	}

	/**
	 * returns a list of all patterns - takes default language
	 * 
	 * @return list of all possible patterns
	 * 
	 *         Returns an array of all possible patterns
	 */
	public String[] getPatterns() {
		// get default language
		return getPatterns(I18N.getLangId());
	}

	/**
	 * actual method
	 * 
	 * @param langId
	 *            language index
	 * @return
	 */
	private String[] getPatterns(int langId) {
		// get all keys
		Set<String> keys = namePatterns.keySet();
		// prepare array
		String[] patterns = new String[keys.size()];

		// copy keys to array
		int i = 0;
		for (String pattern : keys) {
			patterns[i++] = I18N.geti18nString(pattern, langId);
		}

		//sort array
		Arrays.sort(patterns);
		return patterns;
	}

	/**
	 * returns a list of all patterns
	 * 
	 * @param pattern
	 *            to be examined
	 * @param lang
	 *            short language
	 * @return list of all possible patterns
	 * 
	 *         Returns an array of all possible patterns
	 */
	public String[] getGenders(String pattern, String lang) {
		// get language id
		return getGenders(pattern, I18N.findLangId(lang));
	}

	/**
	 * returns a list of all patterns - takes default language
	 * 
	 * @param pattern
	 *            to be examined
	 * @return list of all possible patterns
	 * 
	 *         Returns an array of all possible patterns
	 */
	public String[] getGenders(String pattern) {
		// get default language
		return getGenders(pattern, I18N.getLangId());
	}

	/**
	 * actual method
	 * 
	 * @param pattern
	 *            to be examined
	 * @param langId
	 *            language index
	 * @return all possible genders belonging to a pattern Returns an array of
	 *         genders of pattern pattern or null, if not found
	 */
	private String[] getGenders(String pattern, int langId) {
		String internationalName = translateLocalizedPatternName(pattern,
				langId);

		if (internationalName == null)
			return null; // no entry found

		// get entry based on international (full) name of entry
		Hashtable<String, JaNaGWeightedList> genderList = namePatterns
				.get(internationalName);

		// get all keys
		Set<String> keys = genderList.keySet();
		// prepare array
		String[] genders = new String[keys.size()];

		// copy keys to array
		int i = 0;
		for (String gender : keys) {
			genders[i++] = I18N.geti18nString(gender, langId);
		}

		//sort array
		Arrays.sort(genders);
		return genders;
	}

	/**
	 * helper to find pattern names
	 * 
	 * @param pattern
	 * @param langId
	 * @return
	 */
	private String translateLocalizedPatternName(String pattern, int langId) {
		// search gender in pattern list - localized search, which makes it
		// harder...
		Set<String> keys = namePatterns.keySet();
		for (String internationalPattern : keys) {
			String patternName = I18N.geti18nString(internationalPattern,
					langId);
			if (patternName.equals(pattern))
				return internationalPattern;
		}

		return null;
	}

	/**
	 * helper to find pattern names
	 * 
	 * @param internationPattern
	 * @param gender
	 * @param langId
	 * @return
	 */
	private String translateLocalizedGenderName(String internationalPattern,
			String gender, int langId) {
		// get pattern
		Hashtable<String, JaNaGWeightedList> pattern = namePatterns
				.get(internationalPattern);

		if (pattern == null)
			return null;

		// search for gender entry

		// search gender in pattern list - localized search, which makes it
		// harder...
		Set<String> keys = pattern.keySet();
		for (String internationalGender : keys) {
			String genderName = I18N.geti18nString(internationalGender, langId);
			if (genderName.equals(gender))
				return internationalGender;
		}

		return null;
	}
}