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
import java.util.HashMap;

import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.registry.SimpleRangeList;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.TagableStyleRange;

import org.eclipse.swt.custom.StyleRange;


public class TextNode {

	private static final String EXCEPTION_EMPTY_NAME = Messages.TextNode_ExceptionEmptyName;
	private static final String EXCEPTION_ILLEGAL_OBJECT = Messages.TextNode_ExceptionIllegalObject;
	
	private String name;

	private TextCategoryNode category;
	
	private int uid;

	private boolean isActivated;
	
	private String text;
	
	private TagableStyleRange[] styleRanges;
	
	private HashMap<TagNode, SimpleRangeList> codedRangesList;

	private int codeCtr;

	public TextNode(String name, int uid, boolean isActivated) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException(EXCEPTION_EMPTY_NAME);
		}
		this.uid = uid;
		this.name = name;
		this.text = StringTools.EMPTY;
		this.codedRangesList = new HashMap<TagNode, SimpleRangeList>();
		this.isActivated = isActivated;
		codeCtr = 0;
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof TextNode) {
			return (((TextNode) o).uid == uid);
		}
		return false;
	}

	public TextCategoryNode getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public TagableStyleRange[] getNEWStyleRanges() {
		if (styleRanges == null) {
			styleRanges = new TagableStyleRange[0];
		}
		return styleRanges;
	}

	public String getNEWText() {
		return text;
	}

	public int getUID() {
		return uid;
	}

	public int hashCode() {
		return uid;	
	}
	
	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public void setCategory(TextCategoryNode category) {
		this.category = category;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public HashMap<TagNode, SimpleRangeList> getCodedRangesList() {
		return codedRangesList;
	}

	public void reset() {
		HashMap<TagNode, SimpleRangeList> codedRanges = DocumentRegistry.createTagRangeList(this); 
		setCodedRangesList(codedRanges);
		updateCodeStats();
	}
	
	private void setCodedRangesList(HashMap<TagNode, SimpleRangeList> codedRangesList) {
		this.codedRangesList = codedRangesList;
		updateCodeStats();
	}

	public void setStyleRanges(StyleRange[] nEWStyleRanges) {
		styleRanges = new TagableStyleRange[nEWStyleRanges.length];
		for (int n=0; n<nEWStyleRanges.length; n++) {
			if (nEWStyleRanges[n] instanceof TagableStyleRange) {
				styleRanges[n] = (TagableStyleRange) nEWStyleRanges[n];
			}
			else {
				throw new IllegalArgumentException(EXCEPTION_ILLEGAL_OBJECT);
			}
		}
		updateCodeStats();
	}

	public void setTextContent(String nEWText) {
		text = nEWText;
	}

	public String toString() {
		//TODO (12:16:12) produnis: und hinter den Textnamen (in der linken Leiste) müsste in Klammern stehen, wie oft innerhalb dieses Textes insgesamt Tags vergeben wurden
		return MessageFormat.format("ID #%1 (%2)", new Object[] {uid, getCategory() != null ? "Category ID #"+getCategory().getUID() : "root"}) ; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public int getCodeCounter() {
		return codeCtr;
	}
	
	public void updateCodeStats() {
		codeCtr = 0;
		for (SimpleRangeList rangeList: codedRangesList.values()) {
			codeCtr += rangeList.size();
		}
	}
}
