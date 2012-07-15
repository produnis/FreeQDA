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
package net.sf.freeqda.common.widget;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sf.freeqda.common.tagregistry.TagNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;


public class TagableStyleRange extends StyleRange {

	public static List<TagableStyleRange> createTransposedStyleRanges(TagableStyleRange[] styleRanges, int characterOffset) {

		List<TagableStyleRange> styleList = new LinkedList<TagableStyleRange>();
		
		for (StyleRange sr: styleRanges) {
			TagableStyleRange range = (TagableStyleRange) sr.clone();
			range.start += characterOffset;
		}
		return styleList;
	}	


	private TagNode[] appliedTags;
	
	public TagableStyleRange() {
		super();
		
	}

	public TagableStyleRange(int start, int length, TagNode[] tags, int fontStyle) {
		super(start, length, null, null, fontStyle);
		for (TagNode tag: tags) {
			addTag(tag);
		}
	}

	public TagableStyleRange(int start, int length, TagNode[] tags) {
		this(start, length, tags, SWT.NORMAL);
	}
	
	public TagNode[] getTags() {
		return appliedTags;
	}

	public void addTag(TagNode tagNode) {

		/*
		 * TagNode must not be null
		 */
		if (tagNode == null) {
			return;
		}
		else {
			/*
			 * Lazy initialization - initialize the array together with the first TagNode to be added
			 */
			if (appliedTags == null) {
				appliedTags = new TagNode[] {tagNode};
			}
			else {
				/*
				 * check if the TagNode is already managed
				 */
				boolean tagExists = false;
				for (TagNode tag: appliedTags) {
					if (tag.equals(tagNode)) {
						tagExists = true;
						break;
					}
				}
				if (! tagExists) {
					appliedTags = Arrays.copyOf(appliedTags, appliedTags.length+1);
					appliedTags[appliedTags.length-1] = tagNode; //NOTE: appliedTags.length increased due to array copy!	
				}
			}
			/*
			 * update the background color of this range
			 */
			background = new Color(PlatformUI.getWorkbench().getDisplay(), tagNode.getRGB()); //FIXME colors are UI resources - use a color registry!
			
			/*
			 * Add a dotted border is there are more than one tags applied to this range 
			 */
			if (appliedTags.length > 1) {
				borderStyle = SWT.BORDER_DOT;
			}
		}
	}
	
	public void removeTag(TagNode tagNode) {
		if ((tagNode == null) || (appliedTags == null)) {
			return;
		}
		else {
			/*
			 * Search the tag that is to be removed
			 */
			int tagFoundIndex = -1;
			for (int n=0; n<appliedTags.length; n++) {
				if (appliedTags[n].equals(tagNode)) {
					tagFoundIndex = n;
					break;
				}
			}

			/*
			 * Remove tag and create a new (reduced) array if the tag was found before 
			 */
			if (tagFoundIndex >= 0) {
				TagNode[] newAppliedTags = new TagNode[appliedTags.length-1];
				int ctr = 0;
				for (int n=0; n<appliedTags.length; n++) {
					if (n != tagFoundIndex) {
						newAppliedTags[ctr] = appliedTags[n];
						ctr++;
					}
				}
				appliedTags = newAppliedTags;
				updateDisplay();
			}
		}
	}
	
	public void removeAllTags() {
		
		appliedTags = null;
		updateDisplay();
	}

	public boolean containsTag(TagNode code) {
		
		if ((code == null) || (appliedTags == null)) return false;
		
		boolean res = false;
		
		for (TagNode currentCode: appliedTags) {
			if (code.equals(currentCode)) {
				res = true;
				break;
			}
		}
		return res;
	}
	/**
	 * Updates the presentation of this styled range (background color, border)
	 */
	private void updateDisplay() {
		if (appliedTags != null && appliedTags.length > 0) {
			//TODO activate the TagRegistry.getActiveTag() if it is applied to this range 
			background = new Color(PlatformUI.getWorkbench().getDisplay(), appliedTags[0].getRGB()); //FIXME colors are UI resources - use a color registry!
			if (appliedTags.length < 2) {
				borderStyle = SWT.NONE;
			}
		}
		else {
			appliedTags = null;
			background = null;
			borderStyle = SWT.NONE;
		}
	}
	
	@Override
	public boolean similarTo(StyleRange style) {
		boolean res = super.similarTo(style);
		if ((res) && (style instanceof TagableStyleRange)) {
			TagableStyleRange tagableStyle = (TagableStyleRange) style;
			if ((tagableStyle.appliedTags == null) && (appliedTags == null)) {
				return res;
			}
			else if ((tagableStyle.appliedTags != null) ^ (appliedTags != null)) {
				return false;
			}
			else if (tagableStyle.appliedTags.length == appliedTags.length) {
				for (TagNode aTag: tagableStyle.appliedTags) {
					if (! isTaggedWith(aTag)) return false;
				}
				return true;
			}
			else return false;
		}
		else return res;
	}
	
	@Override
	public boolean isUnstyled() {
		if (super.isUnstyled()) {
			return ((appliedTags == null) || (appliedTags.length == 0));
		}
		return false;
	}

	/**
	 * Determines if the given tag is used in this range 
	 * @param tag a TagNode 
	 * @return true, if the given tag is used in this range
	 */
	public boolean isTaggedWith(TagNode tag) {
		if (appliedTags == null) return false;
		for (TagNode tagNode: appliedTags) {
			if (tagNode.equals(tag)) return true;
		}
		return false;
	}

	@Override
	public Object clone() {
		TagableStyleRange clone = (TagableStyleRange) super.clone();
		if (appliedTags != null) {
			clone.appliedTags = new TagNode[appliedTags.length];
			for (int n=0; n< appliedTags.length; n++) {
				clone.appliedTags[n] = appliedTags[n];
			}
		}
		return clone;
	}	
	
	public String toString() {
		return MessageFormat.format("(%1, %2)", new Object[] {start, length});  //$NON-NLS-1$
	}
}
