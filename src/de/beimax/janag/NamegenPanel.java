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

import java.awt.Button;
import java.awt.Choice;
import java.awt.Container;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

import javax.swing.SpringLayout;

import de.beimax.janag.i18n.I18N;
import de.beimax.janag.i18n.Messages;

/**
 * @author mkalus
 * 
 */
public class NamegenPanel extends Container implements ActionListener,
		ItemListener {
	private static final long serialVersionUID = -3483228672117525774L;

	/**
	 * Private instance of name generator
	 */
	private NameGenerator ng;
	
	/**
	 * references to languages, semantics files and language (e.g. "de")
	 */
	private String languagesFile, semanticsFile, lang;

	/**
	 * Instances of form elements
	 */
	private Button butGenerate, butClear;
	private Choice chPattern, chGender, chCount;
	private TextArea txtOutput;

	/**
	 * current pattern
	 */
	private String strPattern;

	/**
	 * Constructor
	 */
	public NamegenPanel() {
		languagesFile = "languages.txt";
		semanticsFile = "semantics.txt";
		lang = Locale.getDefault().getLanguage();
	}
	
	/**
	 * initializes layout of JFrame
	 */
	public void init() {
		// create a name generator instance
		ng = new NameGenerator(languagesFile, semanticsFile); //$NON-NLS-1$ //$NON-NLS-2$

		// set layout
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);

		// patterns label
		Label label1 = new Label(I18N.geti18nString(Messages
				.getString("Label.Patterns"), lang));
		this.add(label1);

		// create patterns
		chPattern = new Choice();
		String[] list = ng.getPatterns(lang);

		for (int i = 0; i < list.length; i++)
			chPattern.addItem(list[i]);

		// react to changes...
		chPattern.addItemListener(this);
		this.add(chPattern);
		// current pattern
		strPattern = list[0];

		// genders label
		Label label2 = new Label(I18N.geti18nString(Messages
				.getString("Label.Genders"), lang));
		this.add(label2);

		// create genders
		chGender = new Choice();
		list = ng.getGenders(strPattern, lang);

		for (int i = 0; i < list.length; i++)
			chGender.addItem(list[i]);

		chGender.addItemListener(this);
		this.add(chGender);

		// create amount
		chCount = new Choice();
		String[] adder = { "1", "2", "3", "4", "5", "10", "20", "30", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
				"40", "50", "100" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		for (int i = 0; i < adder.length; i++)
			chCount.addItem(adder[i]);
		this.add(chCount);

		// create button to create stuff
		butGenerate = new Button(I18N.geti18nString(Messages
				.getString("Namegen0"), lang)); //$NON-NLS-1$
		butGenerate.addActionListener(this);
		this.add(butGenerate);

		// add text area
		txtOutput = new TextArea();
		this.add(txtOutput);

		// add clear button
		butClear = new Button(I18N.geti18nString(Messages.getString("Clear"), lang)); //$NON-NLS-1$
		butClear.addActionListener(this);
		this.add(butClear);

		// do layout...
		layout.putConstraint(SpringLayout.WEST, label1, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, label1, 5,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, label2, 5,
				SpringLayout.SOUTH, label1);
		layout.putConstraint(SpringLayout.EAST, label2, 0,
				SpringLayout.EAST, label1);
		
		layout.putConstraint(SpringLayout.WEST, chPattern, 5,
				SpringLayout.EAST, label1);
		layout.putConstraint(SpringLayout.EAST, chPattern, -5,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, chPattern, 5,
				SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.WEST, chGender, 5,
				SpringLayout.EAST, label1);
		layout.putConstraint(SpringLayout.EAST, chGender, -5,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, chGender, 5,
				SpringLayout.SOUTH, chPattern);
		
		layout.putConstraint(SpringLayout.WEST, chCount, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, chCount, 5,
				SpringLayout.SOUTH, chGender);
		layout.putConstraint(SpringLayout.EAST, chCount, 0,
				SpringLayout.EAST, label1);
		
		layout.putConstraint(SpringLayout.WEST, butGenerate, 5,
				SpringLayout.EAST, label1);
		layout.putConstraint(SpringLayout.NORTH, butGenerate, 5,
				SpringLayout.SOUTH, chGender);
		
		layout.putConstraint(SpringLayout.WEST, txtOutput, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, txtOutput, -5,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, txtOutput, 5,
				SpringLayout.SOUTH, butGenerate);
		layout.putConstraint(SpringLayout.SOUTH, txtOutput, -5,
				SpringLayout.NORTH, butClear);
		
		layout.putConstraint(SpringLayout.SOUTH, butClear, -5,
				SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, butClear, -5,
				SpringLayout.EAST, this);
	}

	/**
	 * changes the gender according to the pattern
	 */
	@Override
	public void itemStateChanged(ItemEvent ie) {
		if (ie.getSource() == chPattern) { // changed pattern
			chGender.removeAll();

			if (strPattern == chPattern.getSelectedItem())
				return;
			strPattern = chPattern.getSelectedItem();
			String[] list = ng.getGenders(strPattern, lang);

			for (int i = 0; i < list.length; i++)
				chGender.addItem(list[i]);
		}
	}

	/**
	 * reacts on clicking the buttons
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == butGenerate) {
			// generate names
			int zahl = Integer.parseInt(chCount.getSelectedItem());
			String[] mynames = ng.getRandomName(chPattern.getSelectedItem(),
					chGender.getSelectedItem(), zahl, lang);

			for (int i = 0; i < zahl; i++)
				txtOutput.append(mynames[i] + "\n"); //$NON-NLS-1$
		} else if (ae.getSource() == butClear) {
			txtOutput.setText(""); // clear the text area
		}
	}

	/**
	 * @return the languagesFile
	 */
	public String getLanguagesFile() {
		return languagesFile;
	}

	/**
	 * @param languagesFile the languagesFile to set
	 */
	public void setLanguagesFile(String languagesFile) {
		this.languagesFile = languagesFile;
	}

	/**
	 * @return the semanticsFile
	 */
	public String getSemanticsFile() {
		return semanticsFile;
	}

	/**
	 * @param semanticsFile the semanticsFile to set
	 */
	public void setSemanticsFile(String semanticsFile) {
		this.semanticsFile = semanticsFile;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

}
