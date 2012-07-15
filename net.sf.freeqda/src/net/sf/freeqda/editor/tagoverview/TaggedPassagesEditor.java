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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sf.freeqda.common.JAXBUtils;
import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.printing.TaggableStyledTextPrintDataContainer;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentData;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.registry.SimpleRange;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.ComponentDirtyNotificationListener;
import net.sf.freeqda.common.widget.ComponentFocusGainedListener;
import net.sf.freeqda.common.widget.ITaggableStyledTextProvider;
import net.sf.freeqda.common.widget.TagableStyleRange;
import net.sf.freeqda.common.widget.TaggableStyledText;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


public class TaggedPassagesEditor extends EditorPart implements ComponentDirtyNotificationListener, ComponentFocusGainedListener, ITaggableStyledTextProvider {

	public static String ID = "net.sf.freeqda.stylededitor.TagOverviewEditor"; //$NON-NLS-1$

	private static final DocumentRegistry REGISTRY = DocumentRegistry.getInstance();

	private static final ProjectManager PROJECT_MANAGER = ProjectManager.getInstance();

	private static final DocumentRegistry DOCUMENT_REGISTRY = DocumentRegistry.getInstance();

	private TagNode selectedTag;

	private TaggedPassageComposite focusedComponent;

	private HashMap<TextNode, TaggedPassageComposite[]> documentCompositesMap;

	private boolean isDirty;

	private Composite parentComposite;

	public TaggedPassagesEditor() {
		documentCompositesMap = new HashMap<TextNode, TaggedPassageComposite[]>();
	}

	@Override
	public void createPartControl(Composite parent) {

		parentComposite = parent;
		
		FillLayout parentLayout = new FillLayout(SWT.HORIZONTAL);
		parent.setLayout(parentLayout);

		ScrolledComposite scrolledComposite1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite1.setExpandHorizontal(true);
		Composite contentComposite = new Composite(scrolledComposite1, SWT.BORDER);
 
		GridLayout thisLayout = new GridLayout(1, true);
		contentComposite.setLayout(thisLayout);

		boolean isFirstPassage = true;
		List<TextNode> activatedTexts = PROJECT_MANAGER.getActiveTextNodes();
		REGISTRY.init(activatedTexts);
		
		for (TextNode textNode : activatedTexts) {

			LinkedList<TaggedPassageComposite> textComponents = new LinkedList<TaggedPassageComposite>();

			for (SimpleRange range : REGISTRY.getPassagesFor(textNode, selectedTag)) {

				GridData documentTagComposite1LData = new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1);
				
				TaggedPassageComposite documentTagComposite = new TaggedPassageComposite(contentComposite, SWT.BORDER);
				documentTagComposite.setData(textNode, range.start, range.stop);
				documentTagComposite.setLayoutData(documentTagComposite1LData);
				documentTagComposite.registerComponentFocusGainedListener(this);
				documentTagComposite.getTaggableStyledText().registerComponentDirtyNotificationListener(this);
				documentTagComposite.layout();

				if (isFirstPassage) {
					documentTagComposite.setFocus(); // TODO obsolete?
					focusedComponent = documentTagComposite;
					isFirstPassage = false;
				}
			
				textComponents.add(documentTagComposite);
			}
			
			documentCompositesMap.put(textNode, textComponents.toArray(new TaggedPassageComposite[0]));
		}
		contentComposite.pack();
		scrolledComposite1.setContent(contentComposite);
		setDirty(false);
	}

	@Override
	public void dispose() {
		/*
		 * Dispose all composites
		 */
		for (TextNode textNode: documentCompositesMap.keySet()) {
			TaggedPassageComposite[] compositeArray = documentCompositesMap.get(textNode);
			for (int n = 0; n < compositeArray.length; n++) {
				compositeArray[n].dispose();
				compositeArray[n] = null;
			}
		}
		documentCompositesMap.clear();
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		for (TextNode textNode: documentCompositesMap.keySet()) {

			TaggedPassageComposite[] compositeArray = documentCompositesMap.get(textNode);

			boolean isDirty = false;
			
			for (int n = 0; n < compositeArray.length; n++) {
				if (compositeArray[n].getTaggableStyledText().isDirty()) {
					isDirty = true;
					break;
				}
			}
			
			if (isDirty) {

				/*
				 * Save the complete document
				 */
				try {
					DocumentData docData = DOCUMENT_REGISTRY.getDocumentData(textNode);
					TagableStyleRange[] styleRanges = (TagableStyleRange[]) docData.getWorkingStyleRanges().toArray(new TagableStyleRange[0]);
					String text = docData.getText();
					
					JAXBUtils.saveDocument(styleRanges, text, JAXBUtils.getFileForTextNode(textNode));
					
					/*
					 * Update the TextNode
					 */
					textNode.setTextContent(text);
					textNode.setStyleRanges(styleRanges);
					
					DOCUMENT_REGISTRY.resetDocumentData(textNode);
					
					/* 
					 * Clear the dirty flag(s) for the saved document
					 */
					for (int n = 0; n < compositeArray.length; n++) {
						compositeArray[n].getTaggableStyledText().setComponentDirty(false);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}


			}
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);

		if (input instanceof TaggedPassagesEditorInput) {
			TaggedPassagesEditorInput taggedPassagesInput = (TaggedPassagesEditorInput) input;
			setInput(taggedPassagesInput);
			selectedTag = taggedPassagesInput.getSelectedTag();
		}
	}

	@Override
	public IEditorInput getEditorInput() {
		return super.getEditorInput();
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void setActiveTaggedPassageComposite(TaggedPassageComposite component) {
		focusedComponent = component;
	}

	public TextNode getActiveTextNode() {
		if (focusedComponent != null) {
			return focusedComponent.getTextNode();
		}
		return null;
	}

	@Override
	public TaggableStyledText getActiveStyledText() {
		if (focusedComponent != null) {
			return focusedComponent.getTaggableStyledText();
		}
		return null;
	}

	@Override
	public void setDirty(boolean isDirty) {
		if (this.isDirty != isDirty) {
			this.isDirty = isDirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public TaggableStyledTextPrintDataContainer getPrintableStyledText() {
		
		TaggableStyledTextPrintDataContainer res = new TaggableStyledTextPrintDataContainer();
		
		TaggableStyledText toPrint = new TaggableStyledText(this.parentComposite, SWT.NONE);
		LinkedList<TagableStyleRange> toPrintStyleRanges = new LinkedList<TagableStyleRange>();
		
		LinkedList<String> lineNumberStrings = new LinkedList<String>();
		StringBuilder assembledText = new StringBuilder();
		int currentOffset = 0;

		for (TextNode textNode: documentCompositesMap.keySet()) {
			TaggedPassageComposite[] taggedCompositeArray = documentCompositesMap.get(textNode);
			for (TaggedPassageComposite composite: taggedCompositeArray) {

				/*
				 * Create header
				 */
				String headerText = composite.getLabelHeaderText();
				assembledText.append(headerText+StringTools.LINEFEED);
				lineNumberStrings.add(StringTools.EMPTY);

				currentOffset = assembledText.length();
				
				/*
				 * create text
				 */
				String passageText = composite.getTaggableStyledText().getText();
				assembledText.append(passageText);

				/*
				 * transpose style ranges
				 */
				StyleRange[] passageStyleRanges = composite.getTaggableStyledText().getStyleRanges();
				for (StyleRange sr: passageStyleRanges) {
					TagableStyleRange range = (TagableStyleRange) sr.clone();
					range.start += currentOffset;
					toPrintStyleRanges.add(range);
				}
				
				/*
				 * create line numbers
				 */
				int lineStart = composite.getDisplayedLineStart();
				int lineStop = lineStart + composite.getTaggableStyledText().getLineAtOffset(composite.getTaggableStyledText().getCharCount());
				
				for (int lineCtr = lineStart; lineCtr <= lineStop; lineCtr++) {
					lineNumberStrings.add(String.valueOf(lineCtr + 1)+StringTools.LINENUMBER_SEPERATOR);
				}

				/*
				 * create a footer - a clean transition to the next part
				 */
				if (! passageText.endsWith(StringTools.LINEFEED)) {
					assembledText.append(StringTools.LINEFEED);
					lineNumberStrings.add(StringTools.EMPTY);
				}
				assembledText.append(StringTools.LINEFEED);
			}
		}
		
		toPrint.setText(assembledText.toString());
		toPrint.setStyleRanges(toPrintStyleRanges.toArray(new TagableStyleRange[0]));

		/*
		 * wrap text and line numbers in result container
		 */
		res.styledText = toPrint;
		res.lineNumberStrings = lineNumberStrings.toArray(new String[0]);
		
		return res;
	}
}
