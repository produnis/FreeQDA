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
package net.sf.freeqda.common;

import java.util.Iterator;
import java.util.LinkedList;

public class StringTools {

	private static char LINEFEED_CHAR = '\n';
	public static String LINEFEED = Character.toString(LINEFEED_CHAR); 
	public static String EMPTY = ""; //$NON-NLS-1$
	public static String LINENUMBER_SEPERATOR = " || "; //$NON-NLS-1$
	

	public static int[] createLinefeedPositionArray(char[] text) {
		LinkedList<Integer> tmp = new LinkedList<Integer>();
		for (int index=0; index < text.length; index++) {
			if (text[index] == LINEFEED_CHAR) tmp.add(index);
		}
		
		int index = 0;
		Iterator<Integer> i = tmp.iterator();
		int[] res = new int[tmp.size()];
		while (i.hasNext()) {
			res[index] = i.next();
			index++;
		}
		return res;
	}
}
