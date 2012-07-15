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
package net.sf.freeqda.common.tagregistry.events;

import java.util.ArrayList;
import java.util.EventObject;

import net.sf.freeqda.common.widget.TagableStyleRange;


public class DocumentSelectionAddTagEvent extends EventObject  {

	private static final long serialVersionUID = 2507114034226158759L;

//	private TagableStyleRange[] affectedStyleRanges;
	private ArrayList<TagableStyleRange> affectedStyleRanges;
	private int start;
	private int length;
	
	public DocumentSelectionAddTagEvent(Object source, ArrayList<TagableStyleRange> affectedStyleRanges, int start, int length) {
		super(source);
		this.affectedStyleRanges = affectedStyleRanges;
		this.start = start;
		this.length = length;
	}

	public ArrayList<TagableStyleRange>  getAffectedRanges() {
		return affectedStyleRanges;
	}

	public int getStart() {
		return start;
	}

	public int getLength() {
		return length;
	}
}
