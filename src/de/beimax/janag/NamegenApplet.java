/**
 * $Id: NamegenApplet.java 22 2010-10-20 20:31:32Z ronix $
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

import javax.swing.JApplet;

/**
 * @author mkalus
 *
 */
public class NamegenApplet extends JApplet {
	private static final long serialVersionUID = -87858429809433277L;

	/**
	 * initializes applet
	 */
	@Override
	public void init() {
		//get language parameter
		String mylang = getParameter("language");
		
		//create panel and add to applet
		NamegenPanel panel = new NamegenPanel();
		if (mylang != null)
			panel.setLang(mylang);
		panel.init();
		this.add(panel);

		this.resize(230,380);
	}
}
