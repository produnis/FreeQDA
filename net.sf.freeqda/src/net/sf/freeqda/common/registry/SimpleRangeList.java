/*******************************************************************************
 * FreeQDA,  a software for professional qualitative research data 
 * analysis, such as interviews, manuscripts, journal articles, memos
 * and field notes.
 *
 * Copyright (C) 2011 Dirk Kitscha, Jörg große Schlarmann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.sf.freeqda.common.registry;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleRangeList extends ArrayList<SimpleRange> {

	private static final long serialVersionUID = 9170482954263799525L;

	@Override
	public boolean addAll(Collection<? extends SimpleRange> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends SimpleRange> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, SimpleRange element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(SimpleRange rangeToAdd) {
		if (rangeToAdd == null) return false;
		
		/*
		 * Insert the range 
		 */
		boolean needToInsert = true;
		for (int n=0; n < this.size(); n++) {
			SimpleRange myRange = get(n);
			if (myRange.compareTo(rangeToAdd) > 0) {
				super.add(n, rangeToAdd);
				needToInsert = false;;
				break;
			}
		}
		if (needToInsert) super.add(rangeToAdd);

		/*
		 * 	Merge ranges
		 */
		for (int n=0; n < this.size()-1; n++) {
			SimpleRange myRange = get(n);
			SimpleRange nextRange = get(n+1);
			if (myRange.isMergeable(nextRange)) {
				/*
				 * Merge ranges and remove the merged range.
				 * Start over with the current range (possibly there are more ranges to merge now)
				 */
				myRange.mergeWith(nextRange);
				remove(n+1);
				n--;
			}
		}
		return true;
	}

	@Override
	public SimpleRange remove(int index) {
		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		boolean res = false;
		if ((o == null) || (!(o instanceof SimpleRange))) return res;
		
		SimpleRange rangeToRemove = (SimpleRange) o;
		int n=0;
		while (n < size()) {
			SimpleRange range = get(n);
			if (range.isSubtractable(rangeToRemove)) {
				if ((range.start >= rangeToRemove.start) && (range.stop <= rangeToRemove.stop)) {
					super.remove(n);
					n--;
				}
				else if (range.stop < rangeToRemove.stop) {
					/*
					 * first range
					 */
					range.stop = rangeToRemove.start-1;
				}
				else if (range.start > rangeToRemove.start) {
					/*
					 * last range
					 */
					range.start = rangeToRemove.stop+1;
				}
				else {
					/*
					 * need to split this range
					 */
					SimpleRange newRange = new SimpleRange(rangeToRemove.stop+1, range.stop - rangeToRemove.stop - 1);
					range.stop = rangeToRemove.start-1;
					super.add(n+1, newRange);
				}
				res=true;
			}
			n++;
		}
		return res;
	}
	
	public SimpleRange getRangeAt(int charOffset) {
		for (SimpleRange range: this) {
			if (range.start <= charOffset && range.stop >= charOffset) return range;
		}
		return null;
	}
}
