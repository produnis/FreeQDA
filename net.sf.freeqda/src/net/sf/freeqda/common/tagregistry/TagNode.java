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
import java.util.Collection;
import java.util.HashMap;

import net.sf.freeqda.common.GenericTreeNode;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentData;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.registry.SimpleRangeList;

import org.eclipse.swt.graphics.RGB;


public class TagNode extends GenericTreeNode<TagNode> {

//	private static final long serialVersionUID = 1751286374001763158L;
	private static final DocumentRegistry DOCUMENT_REGISTRY = DocumentRegistry.getInstance();
	private static final String EXCEPTION_EMPTY_NAME = Messages.TagNode_ExceptionEmptyName;
	
	private String name;

	private RGB rgbValue;
	
	private int uid;

	private int occuranceCtr;
	
	public TagNode(String name, RGB rgbValue, int uid) {
		if ((name == null) || (name.length() == 0)) {
			throw new IllegalArgumentException(EXCEPTION_EMPTY_NAME);
		}
		this.uid = uid;
		this.name = name;
		this.occuranceCtr = 0;
		if (rgbValue == null)
			rgbValue = new RGB(0, 0, 0);
		else
			this.rgbValue = rgbValue;
	}

	public int getUID() {
		return uid;
	}
	
	public String getName() {
		return name;
	}

	public int getOccuranceCounter() {
		return occuranceCtr;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public RGB getRGB() {
		return rgbValue;
	}
	
	public void setRGB(RGB rgbValue) {
		this.rgbValue = rgbValue;
	}
	
	public String toString() {
		return MessageFormat.format(" (%1)", new Object[] {getUID()}); //$NON-NLS-1$
	}
	
	public int hashCode() {
		return uid;
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o instanceof TagNode) {
			return (((TagNode) o).uid == uid);
		}
		return false;
	}
	
	public void updateCodeStats() {

		occuranceCtr = 0;
		HashMap<TextNode, DocumentData> documentData = DOCUMENT_REGISTRY.getDocumentDataMap();
				
		Collection<TextNode> textNodes = null;
		
		if (documentData.size() == 0) {
			textNodes = ProjectManager.getInstance().getLookupMapText().values();
		}
		else {
			textNodes = documentData.keySet();
		}
		
		for (TextNode textNode: textNodes) {
			
			DocumentData docData = documentData.get(textNode);
			
			HashMap<TagNode, SimpleRangeList> codedRanges = null;
			if (docData != null) {
				/*
				 * Document is currently being edited
				 */
				codedRanges = docData.getCodedRanges();
			}
			else {
				/*
				 * Count stats in stored document
				 */
				codedRanges = textNode.getCodedRangesList();
			}
			
			if (codedRanges != null && codedRanges.containsKey(this)) {
				SimpleRangeList codedRangeList = codedRanges.get(this);
				if (codedRangeList != null) {					
					occuranceCtr += codedRangeList.size();
				}
				else {
					// ignore
				}
			}
			else {
				// ignore
			}
		}
	}
}
