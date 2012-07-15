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



public class TagNodeCreatedEvent extends EventObject {

	private static final long serialVersionUID = -5055605425684151155L;
	
	private TagNode createdTagNode;
	private TagNode parentTagNode;
	
	public TagNodeCreatedEvent(Object source, TagNode createdTagNode, TagNode parentTagNode) {
		super(source);
		this.createdTagNode = createdTagNode;
		this.parentTagNode = parentTagNode;
	}
	
	public TagNode getCreatedTagNode() {
		return createdTagNode;
	}
	
	public TagNode getParentTagNode() {
		return parentTagNode;
	}
}
