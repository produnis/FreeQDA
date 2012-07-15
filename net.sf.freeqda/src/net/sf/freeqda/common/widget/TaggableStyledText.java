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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.registry.SimpleRange;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.tagregistry.events.DocumentSelectionAddTagEvent;
import net.sf.freeqda.common.tagregistry.events.DocumentSelectionManipulateTagsListener;
import net.sf.freeqda.common.tagregistry.events.DocumentSelectionRemoveAllTagsEvent;
import net.sf.freeqda.common.tagregistry.events.DocumentSelectionRemoveTagEvent;
import net.sf.freeqda.common.tagregistry.events.TagActivatedEvent;
import net.sf.freeqda.common.tagregistry.events.TagNodeCreatedEvent;
import net.sf.freeqda.common.tagregistry.events.TagNodeDeletedEvent;
import net.sf.freeqda.editor.tagoverview.TaggedPassageComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;


public class TaggableStyledText extends StyledText implements DocumentSelectionManipulateTagsListener, ITagModificationListener {

	private static final DocumentRegistry DOCUMENT_REGISTRY = DocumentRegistry.getInstance();
	
	private static final String NUM_APPLIED_CODES_SINGULAR = Messages.TaggableStyledText_NumOfAppliedCodes_Singular;
	private static final String NUM_APPLIED_CODES_PLURAL = Messages.TaggableStyledText_NumOfAppliedCodes_Plural;
	private static final String EXCEPTION_LISTENER_ALREADY_REGISTERED = Messages.TaggableStyledText_ExceptionListenerAlreadyRegistered;
	private static final String EXCEPTION_NO_LISTENER_REGISTERED = Messages.TaggableStyledText_ExceptionNoListenerRegistered;
	private static final String EXCEPTION_LISTENER_NOT_REGISTERED = Messages.TaggableStyledText_ExceptionListenerNotRegistered;

	private boolean isDirty;
 
	private int characterOffset;
	
	private int lineOffset;

	private TextNode textNode;
	
	private static int LINE_SPACING = 10; // TODO Preferences!

	private ComponentFocusGainedListener focusGainedListener;
	
	public TaggableStyledText(Composite parent, int style) {
		super(parent, style);

		setLineSpacing(LINE_SPACING);

//		toolTip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);

		
		addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
								
				if (e.x != e.y) {
//					commandStateService.setTextSelected(true);
				}
				else {
//					commandStateService.setTextSelected(false);
				}
				StyleRange styleRange = new StyleRange();
				styleRange.start = 0;
				styleRange.length = 0;
				styleRange.foreground = null;
				styleRange.background = null;
				styleRange.fontStyle = SWT.NORMAL;

				setStyleRange(styleRange);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (focusGainedListener != null) focusGainedListener.setActiveTaggedPassageComposite((TaggedPassageComposite) getParent());
			}
		});

		addExtendedModifyListener(new ExtendedModifyListener() {
			
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				if (! isDirty) {
					setComponentDirty(true);
				}
			}
		});

		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent event) {
				super.mouseHover(event);
				try {
					StringBuilder tooltiptext = new StringBuilder();
					
					int characterOffset = getOffsetAtLocation(new Point(event.x, event.y)) + TaggableStyledText.this.characterOffset;
					HashMap<TagNode, SimpleRange> codedRanges = DOCUMENT_REGISTRY.getPassagesAt(TaggableStyledText.this.textNode, characterOffset);
					
					if (codedRanges.size() > 1) {
						tooltiptext.append(NUM_APPLIED_CODES_PLURAL);
					}
					else if (codedRanges.size() > 0) {
						tooltiptext.append(NUM_APPLIED_CODES_SINGULAR);
					}
					for (TagNode code: codedRanges.keySet()) {
						
						SimpleRange range = codedRanges.get(code);
						
						int lineCalcOffsetStart = range.start - TaggableStyledText.this.characterOffset;
						if (lineCalcOffsetStart < 0) lineCalcOffsetStart = 0;
						int lineCalcOffsetStop = range.stop - TaggableStyledText.this.characterOffset;
						if (lineCalcOffsetStop > getCharCount()) lineCalcOffsetStop = getCharCount();
						
						int lineStart = getLineAtOffset(lineCalcOffsetStart) + lineOffset;
						int lineStop = getLineAtOffset(lineCalcOffsetStop) + lineOffset;
						
						if (lineStart == lineStop) {
							tooltiptext.append(MessageFormat.format(Messages.TaggableStyledText_TooltipLines_Singular, new Object[] {lineStart}));
						}
						else {
							tooltiptext.append(MessageFormat.format(Messages.TaggableStyledText_TooltipLines_Plural, new Object[] {lineStart, lineStop}));
						}
						tooltiptext.append(code.getName()+StringTools.LINEFEED);
					}
					if (tooltiptext.length() > 0) tooltiptext.deleteCharAt(tooltiptext.length()-1);
					setToolTipText(tooltiptext.toString());
				}
				catch (IllegalArgumentException e) {
					/* ignore */
					setToolTipText(null);
				}
			}
		});
		
		TagManager.getInstance().registerListener(this);
		setComponentDirty(false);
	}

	public void setData(TextNode textNode) {		
		this.textNode = textNode;
	}
	
	private void setActiveTag(TagNode activeTag) {
		
		if (activeTag == null) return;
		
		//FIXME currently not all TagActivation listeners are removed correctly, so there is this workaround
		if (isDisposed()) return;
		
		/*
		 * Iterate over all style ranges and activate the active tag if it is applied to this range
		 */
		for (StyleRange range: getStyleRanges()) {
			TagableStyleRange tagableRange = (TagableStyleRange) range;
			TagNode[] appliedTags = tagableRange.getTags();
			if (appliedTags != null) {
				for (TagNode tag: appliedTags) {
					if (tag.equals(activeTag)) {
						tagableRange.background = new Color(PlatformUI.getWorkbench().getDisplay(), tag.getRGB()); //FIXME colors are UI resources - use a color registry!
						setStyleRange(tagableRange);
						break;
					}
				}
			}
		}
	}	

	public TagNode[] getTagsInRange(int start, int length) {
		HashSet<TagNode> res = new HashSet<TagNode>();
		for (StyleRange range: getStyleRanges(start, length)) {
			TagableStyleRange tagRange = (TagableStyleRange) range;
			TagNode[] tagsInRange = tagRange.getTags(); 
			if (tagsInRange != null) {
				for (TagNode tag: tagsInRange) {
					res.add(tag);
				}
			}
		}
		return res.toArray(new TagNode[0]);
	}
	
	public boolean isRangeTaggedWith(int start, int length, TagNode tag) {
		for (StyleRange range: getStyleRanges(start, length)) {
			TagableStyleRange tagRange = (TagableStyleRange) range;
			if (tagRange.isTaggedWith(tag)) return true;
		}
		return false;
	}
	
	@Override
	public void DocumentSelectionTagAdded(DocumentSelectionAddTagEvent event) {
	
		if (isDisposed()) {
			return;
		}
		ArrayList<TagableStyleRange>  affectedRanges = event.getAffectedRanges();
		int start = event.getStart();
		int charCount = getCharCount();

		LinkedList<TagableStyleRange> appliedRanges = new LinkedList<TagableStyleRange>();
			
		for (TagableStyleRange range: affectedRanges) {

			TagableStyleRange cloneRange = (TagableStyleRange) range.clone();
			cloneRange.start -= characterOffset;
			
			int firstChar = cloneRange.start;
			int lastChar = firstChar + cloneRange.length - 1;

			if ((lastChar < 0) || (firstChar > charCount)) {
				// ignore
			}
			else {
				if (firstChar < 0) {
					cloneRange.start = 0;
				}
				if (lastChar > charCount) {
					cloneRange.length = charCount - cloneRange.start;
				}
				appliedRanges.add(cloneRange);
			}
		}
		
		affectedRanges = new ArrayList<TagableStyleRange>(appliedRanges);
		
		TagableStyleRange[] affectedRangesArray = new TagableStyleRange[affectedRanges.size()];
		for (int n=0; n<affectedRanges.size(); n++) {
			affectedRangesArray[n] = affectedRanges.get(n);
		}
		setStyleRanges(affectedRanges.toArray(new TagableStyleRange[0]));
		finishTaggingAction(start);
	}

	@Override
	public void DocumentSelectionRemoveAllTags(
			DocumentSelectionRemoveAllTagsEvent event) {
		
		int start = event.getStart() - characterOffset;
		int length = event.getLength();

		if ((start < 0) || (length < 1)) return;
		
		StyleRange[] ranges = getStyleRanges(start, length);
		
		for (int n=0; n<ranges.length; n++) {
			TagableStyleRange tagableRange = (TagableStyleRange) ranges [n];
			tagableRange.removeAllTags();
		}
		
		replaceStyleRanges(start, length, ranges);

		/*
		 * remove the selection and place the caret at the start of the former selection
		 */
		setSelectionRange(start, 0);
		setCaretOffset(start);

		finishTaggingAction(start);
	}

	@Override
	public void DocumentSelectionRemoveTag(DocumentSelectionRemoveTagEvent event) {
		
		TagNode tag = event.getTag();
		int start = event.getStart() - characterOffset;
		int length = event.getLength();

		if ((tag == null) || (start < 0) || (length < 1) || (start+length > getCharCount())) return;
		
		StyleRange[] ranges = getStyleRanges(start, length);
		
		for (int n=0; n<ranges.length; n++) {
			TagableStyleRange tagableRange = (TagableStyleRange) ranges [n];
			tagableRange.removeTag(tag);
			//TODO remove range if unstyled
		}
		
		replaceStyleRanges(start, length, ranges);

		finishTaggingAction(start);
	}
	
	private void finishTaggingAction(int caretOffset) {
		/*
		 * remove the selection and place the caret at the start of the former selection
		 */
		setSelectionRange(caretOffset, 0);
		setCaretOffset(caretOffset);

		setComponentDirty(true);
	}
	
	public void setComponentDirty(boolean isDirty) {
		this.isDirty = isDirty;
		if (dirtyNotificationListener != null) {
			dirtyNotificationListener.setDirty(isDirty);
		}
	}
	
	private ComponentDirtyNotificationListener dirtyNotificationListener;
	
	public void registerComponentDirtyNotificationListener(ComponentDirtyNotificationListener listener) {

		if (dirtyNotificationListener != null) throw new IllegalStateException(EXCEPTION_LISTENER_ALREADY_REGISTERED);
		dirtyNotificationListener = listener;
		if (isDirty) listener.setDirty(true);
	}
	
	public void deregisterComponentDirtyNotificationListener(ComponentDirtyNotificationListener listener) {

		if (listener == null)  throw new IllegalStateException(EXCEPTION_NO_LISTENER_REGISTERED);
		if (listener.equals(dirtyNotificationListener)) {
			dirtyNotificationListener = null;
		}
		else {
			 throw new IllegalStateException(EXCEPTION_LISTENER_NOT_REGISTERED);
		}
	}
	
	public void registerComponentFocusGainedListener(ComponentFocusGainedListener listener) {

		if (focusGainedListener != null) throw new IllegalStateException(EXCEPTION_LISTENER_ALREADY_REGISTERED);
		focusGainedListener = listener;
	}
	
	public void deregisterComponentFocusGainedListener(ComponentFocusGainedListener listener) {

		if (listener == null)  throw new IllegalStateException(EXCEPTION_NO_LISTENER_REGISTERED);
		if (listener.equals(focusGainedListener)) {
			focusGainedListener = null;
		}
		else {
			 throw new IllegalStateException(EXCEPTION_LISTENER_NOT_REGISTERED);
		}
	}

	public void setOffsets(int characterOffset, int lineOffset) {
		this.characterOffset = characterOffset;
		this.lineOffset = lineOffset;
	}

	@Override
	public void dispose() {
		TagManager.getInstance().deregisterListener(this);
		super.dispose();
	}
	
	public void NodeDeleted(TagNodeDeletedEvent evt) {
		DocumentSelectionRemoveTag(new DocumentSelectionRemoveTagEvent(this, evt.getDeletedTagNode(), 0, getCharCount()));
	}

	public void TagActivated(TagActivatedEvent evt) {
		setActiveTag(evt.getActiveTag());
	}

	public int getCharacterOffset() {
		return characterOffset;
	}

	public int getLineOffset() {
		return lineOffset;
	}

	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void NodeCreated(TagNodeCreatedEvent evt) {
		// ignore
	}

}
