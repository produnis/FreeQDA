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
package net.sf.freeqda.common.projectmanager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.freeqda.common.GenericTreeNode;



public class TextCategoryNode extends GenericTreeNode<TextCategoryNode> {
	
	private static final String EXCEPTION_EMPTY_NAME = Messages.TextNode_ExceptionEmptyName;

	private String name;

	private int uid;
	
	private ArrayList<TextNode> fileList;

	public TextCategoryNode(String name, int uid) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException(EXCEPTION_EMPTY_NAME);
		}
		this.uid = uid;
		this.name = name;
		this.fileList = new ArrayList<TextNode>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUID() {
		return uid;
	}

	public void setUID(int uid) {
		this.uid = uid;
	}

	public void addTextFile(TextNode file) {
		fileList.add(file);
	}
	
	public List<TextNode> getTextFileDescriptors() {
		return fileList;
	}
	
	public String toString() {
		return MessageFormat.format("Name: %1 UID: %2", new Object[] {name, uid}); //$NON-NLS-1$
	}
	
	public int hashCode() {
		return uid;
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof TextCategoryNode) {
			return (((TextCategoryNode) o).uid == uid);
		}
		return false;
	}

}
