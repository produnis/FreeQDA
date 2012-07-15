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
package net.sf.freeqda.editor.tagoverview;

import java.text.MessageFormat;

import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.widget.ComponentFocusGainedListener;
import net.sf.freeqda.common.widget.TagableStyleRange;
import net.sf.freeqda.common.widget.TaggableStyledText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;


public class TaggedPassageComposite extends org.eclipse.swt.widgets.Composite {

	private static final DocumentRegistry REGISTRY = DocumentRegistry.getInstance();
	
	private static int ADDITIONAL_LINES = 1; //TODO Preferences!
	
	private Label labelHeader;

	private TaggableStyledText styledText;

	private TextNode textNode;

	private String labelHeaderText;
	
	/**
	 * marks the first character of the selected tagged passage that is
	 * displayed in the styled text control
	 */
	private int taggedPassageStart;

	/**
	 * marks the last character of the selected tagged passage that is displayed
	 * in the styled text control
	 */
	private int taggedPassageStop;

	private int taggedPassageLineStart;
	private int taggedPassageLineStop;

	private int displayedLineStart;
	private int displayedLineStop;
	
	public TaggedPassageComposite(org.eclipse.swt.widgets.Composite parent,
			int style) {

		super(parent, style);

		GridLayout thisLayout = new GridLayout(1, true);
		setLayout(thisLayout);
		this.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		labelHeader = new Label(this, SWT.BORDER);
		GridData labelHeaderLData = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
		labelHeader.setLayoutData(labelHeaderLData);
		labelHeader.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		labelHeader.setText("**********************"); //$NON-NLS-1$

		styledText = new TaggableStyledText(this, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		GridData textLData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		styledText.setLayoutData(textLData);
		styledText.setWordWrap(true);
		styledText.addVerifyKeyListener(new VerifyKeyListener() {

			@Override
			public void verifyKey(VerifyEvent event) {
				/*
				 * ignore all key strokes
				 */
				event.doit = false;
			}
		});

		styledText.setText(StringTools.EMPTY);
		this.layout();
	}

	public void setData(TextNode textNode, int rangeStart, int rangeStop) {

		this.textNode = textNode;

		String completeText = textNode.getNEWText();
		char[] textChars = completeText.toCharArray();

		/*
		 * create an array containing the positions of linefeed characters inside the text
		 */
		int[] linefeedPositionArray = StringTools.createLinefeedPositionArray(textChars); 

		/*
		 * determine the coded passage's start/stop lines 
		 */

		/*
		 * default values
		 */
		taggedPassageLineStart = -1;
		taggedPassageLineStop = -1;

		/*
		 * search the first linebreak inside the coded passage 
		 */
		int lastIndex = -1;
		boolean isBreak = false;
		
		for (int index=0; index < linefeedPositionArray.length; index++) {
			lastIndex = index;
			if (linefeedPositionArray[index] >= rangeStart) {
				isBreak = true;
				break;
			}
		}
		
		/*
		 * Now determine the start/stop characters and first/last linenumber of the passage
		 * linenumbers start with 0 here
		 */
		if (lastIndex < 0) {
			/*
			 * just one line without linebreak - start and stop on line 0
			 */
			taggedPassageLineStart = 0;
			taggedPassageLineStop = 0;
		}
		else {
			/*
			 * the tag does not start in the last line if isBreak was set above 
			 */
			if (isBreak) {
				/*
				 * tag does not start in the last line 
				 */
				taggedPassageLineStart = lastIndex; // Attention: Lines start with 0 here, not with 1!
				
				/*
				 * now search the first linebreak outside the coded passage
				 * //TODO substitute this with a faster method (without creating the linefeed array)
				 */
				isBreak = false;
				for (int index=lastIndex; index < linefeedPositionArray.length; index++) {
					lastIndex = index;
					if (linefeedPositionArray[index] >= rangeStop) {
						isBreak = true;
						break;
					}
				}
				taggedPassageLineStop = lastIndex;
			}
			else {
				/*
				 * tag is contained in the last line
				 */
				taggedPassageLineStart = lastIndex;
				taggedPassageLineStop = lastIndex;
			}
		}
		
		displayedLineStart = taggedPassageLineStart - ADDITIONAL_LINES;
		displayedLineStop = taggedPassageLineStop + ADDITIONAL_LINES + 1;

		/*
		 * Adjust line start/stop values uf they exceed borders
		 */
		if (displayedLineStart < 0) {
			displayedLineStart = 0;
		}
		if (displayedLineStop >= linefeedPositionArray.length) {
			displayedLineStop = linefeedPositionArray.length;
		}

		if (displayedLineStart <= 1) { // Assuption: ADDITIONAL_LINES > 1 
			this.taggedPassageStart = 0;
		}
		else {
			this.taggedPassageStart = linefeedPositionArray[displayedLineStart - 1] + 1; //FIXME what if the last line ends with a linefeed?
		}

		if (displayedLineStop == linefeedPositionArray.length + 1) {
			this.taggedPassageStop = textChars.length; // - 1;
		}
		else {
			this.taggedPassageStop = linefeedPositionArray[displayedLineStop - 1] + 1;
		}
		
		String displayedText = completeText.substring(this.taggedPassageStart, this.taggedPassageStop);
		
		styledText.setText(displayedText);
		styledText.setOffsets(this.taggedPassageStart, taggedPassageLineStart);

		/*
		 * Update header line
		 */
		if (taggedPassageLineStart == taggedPassageLineStop) labelHeaderText=MessageFormat.format(Messages.TaggedPassageComposite_Headline_SingleLine, new Object[] {textNode.getName(), (taggedPassageLineStart+1)});
		else labelHeaderText=MessageFormat.format(Messages.TaggedPassageComposite_Headline_MultipleLines, new Object[] {textNode.getName(), (taggedPassageLineStart+1), (taggedPassageLineStop+1)});
		labelHeader.setText(labelHeaderText);
		
		REGISTRY.registerDocumentSelectionManipulateTagsListener(textNode, styledText);

		TagableStyleRange[] transposedStyleRanges = DocumentRegistry.getTransposedStyleRanges(textNode, this.taggedPassageStart, this.taggedPassageStop /*, taggedPassageStart */);
		styledText.setStyleRanges(transposedStyleRanges);
		styledText.setComponentDirty(false);

		styledText.setData(textNode);
	}

	public int getDisplayedLineStart() {
		return displayedLineStart;
	}

	public void registerComponentFocusGainedListener(
			ComponentFocusGainedListener listener) {
		styledText.registerComponentFocusGainedListener(listener);
	}

	public void deregisterComponentFocusGainedListener(
			ComponentFocusGainedListener listener) {
		styledText.deregisterComponentFocusGainedListener(listener);
	}

	public TaggableStyledText getTaggableStyledText() {
		return styledText;
	}

	public TextNode getTextNode() {
		return textNode;
	}

	@Override
	public void dispose() {
		styledText.dispose();
		REGISTRY.removeDocumentSelectionManipulateTagsListener(textNode, styledText);
		super.dispose();
	}

	public String getLabelHeaderText() {
		return labelHeaderText;
	}

}
