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

import java.util.EventObject;

import net.sf.freeqda.common.tagregistry.TagNode;



public class TagNodeDeletedEvent extends EventObject {

	private static final long serialVersionUID = 9134961329273630406L;
	
	private TagNode deletedTagNode;
	
	public TagNodeDeletedEvent(Object source, TagNode deletedTagNode) {
		super(source);
		this.deletedTagNode = deletedTagNode;
	}
	
	public TagNode getDeletedTagNode() {
		return deletedTagNode;
	}
}
