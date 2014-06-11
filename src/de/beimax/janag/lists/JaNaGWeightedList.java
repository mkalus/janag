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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import de.beimax.janag.entities.WeightedEntity;

/**
 * @author mkalus
 * 
 *         list of weighed entities - does not implement calculation of total
 *         weight of removed entities!!
 */
public class JaNaGWeightedList extends LinkedList<WeightedEntity> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 437413939564756915L;
	
	/**
	 * total Weights
	 */
	private int totalWeight;
	
	/**
	 * random number generator
	 */
	private static Random r = null;

	/**
	 * Add a string with weight 1 as a new name
	 * 
	 * @param name
	 * @return
	 */
	public boolean add(String name) {
		return add(new WeightedEntity(name));
	}

	/**
	 * Add a string and a weight as a new name
	 * 
	 * @param name
	 * @return
	 */
	public boolean add(String name, int weight) {
		return add(new WeightedEntity(name, weight));
	}

	/**
	 * add entity and add to total weight
	 */
	@Override
	public boolean add(WeightedEntity e) {
		totalWeight += e.getWeight();
		return super.add(e);
	}

	/**
	 * @return the totalWeight
	 */
	public int getTotalWeight() {
		return totalWeight;
	}

	/**
	 * 
	 * @return random string, based on entity weight
	 */
	public String getRandom() {
		// variable calculator variable
		if (r == null) r = new Random();
		int max = r.nextInt(getTotalWeight()) + 1;

		// create an iterator and traverse it
		Iterator<WeightedEntity> it = iterator();
		while (it.hasNext()) {
			WeightedEntity entity = it.next();
			// lower calculator variable
			max -= entity.getWeight();
			// if it is lower/equal 0, return node's name
			if (max <= 0)
				return entity.getName();
		}

		return "###ERROR###"; // application should never reach this point!
	}
}
