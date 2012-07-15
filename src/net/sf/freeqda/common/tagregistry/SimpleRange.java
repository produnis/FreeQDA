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
package net.sf.freeqda.common.tagregistry;

import java.text.MessageFormat;

public class SimpleRange implements Comparable<SimpleRange> {

	public int start;
	public int stop;
	
	public SimpleRange(int start, int length) {
		this.start = start;
		this.stop = start+length;
	}
	
	public boolean equals(Object o) {
		if ((o == null) || (! (o instanceof SimpleRange))) return false;
		SimpleRange range = (SimpleRange) o;
		return start == range.start && stop == range.stop;
	}
	
	public boolean isMergeable(SimpleRange range) {
		if (start >= range.start) {
			return start <= range.stop;
		}
		else {
			return stop >= range.start - 1;
		}
	}
	
	public boolean isSubtractable(SimpleRange range) {
		if (start >= range.start) {
			return start <= range.stop;
		}
		else {
			return stop >= range.start;
		}
	}
	
	public void mergeWith(SimpleRange range) {
		start = start < range.start ? start : range.start;
		stop  = stop  > range.stop  ? stop  : range.stop;
	}

	@Override
	public int compareTo(SimpleRange o) {
		return start - o.start;
	}
	
	public String toString() {
		return MessageFormat.format("(%1, %2)", new Object[] {start, stop}); //$NON-NLS-1$
	}
}
