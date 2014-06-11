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

package de.beimax.janag.lists;

import java.util.LinkedList;
import java.util.Random;

import de.beimax.janag.entities.NamedEntity;

/**
 * @author mkalus
 * 
 * Lists with named entities
 *
 */
public class JaNaGList extends LinkedList<NamedEntity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4503920656427382535L;

	/**
	 * random number generator
	 */
	private static Random r = null;

	/**
	 * Add a string as a new name
	 * 
	 * @param name
	 * @return
	 */
	public boolean add(String name) {
		return add(new NamedEntity(name));
	}
	
	/**
	 * 
	 * @return random string
	 */
	public String getRandom() {
		// variable calculator variable
		if (r == null) r = new Random();
		int index = r.nextInt(size());
		
		//return node at index x
		return get(index).getName();
	}
}
