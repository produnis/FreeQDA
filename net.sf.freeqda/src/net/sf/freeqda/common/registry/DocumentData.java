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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.TagableStyleRange;


public class DocumentData {

	private String text;
	private TagableStyleRange[] styles;
	private ArrayList<TagableStyleRange> workStyles;
	private HashMap<TagNode, SimpleRangeList> codedRanges;
	
	public DocumentData(TextNode textNode) {
		this.text = textNode.getNEWText();
		this.styles = textNode.getNEWStyleRanges();
		this.codedRanges = textNode.getCodedRangesList();
		this.workStyles = new ArrayList<TagableStyleRange>();
		for (TagableStyleRange range: styles) {
			workStyles.add(range);
		}
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public TagableStyleRange[] getStyles() {
		return styles;
	}
	
	public void setStyles(TagableStyleRange[] styles) {
		this.styles = styles;
	}
	
	public HashMap<TagNode, SimpleRangeList> getCodedRanges() {
		return codedRanges;
	}
	
	public void setCodedRanges(HashMap<TagNode, SimpleRangeList> codedRanges) {
		this.codedRanges = codedRanges;
	}
	
	public ArrayList<TagableStyleRange> getWorkingStyleRanges() {
		return workStyles;
	}

	public void setWorkingStyleRanges(ArrayList<TagableStyleRange> workingStyles) {
		workStyles = workingStyles;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(MessageFormat.format("Text: %1\nWorkStyles: %2\nStyles: ", new Object[] {text, workStyles})); //$NON-NLS-1$
		for (TagableStyleRange range: styles) {
			sb.append("> "+range); //$NON-NLS-1$
		}
		return sb.toString();
	}
}
