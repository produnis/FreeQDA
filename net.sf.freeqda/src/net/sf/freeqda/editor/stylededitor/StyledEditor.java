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
package net.sf.freeqda.editor.stylededitor;

import java.io.IOException;

import net.sf.freeqda.common.JAXBUtils;
import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.printing.TaggableStyledTextPrintDataContainer;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.widget.ComponentDirtyNotificationListener;
import net.sf.freeqda.common.widget.ITaggableStyledTextProvider;
import net.sf.freeqda.common.widget.TaggableStyledText;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


public class StyledEditor extends EditorPart implements ComponentDirtyNotificationListener, ITaggableStyledTextProvider { 

	public static final String ID = "net.sf.freeqda.stylededitor.StyledEditor"; //$NON-NLS-1$
	
	private static final DocumentRegistry DOCUMENT_REGISTRY = DocumentRegistry.getInstance();
	private static final TagManager TAG_MANAGER = TagManager.getInstance();
		
	private FQDADocumentEditorInput editorInput;
	
	private TaggableStyledText styledText;
	
	private boolean isDirty;
	
	public StyledEditor() {
	}

	
	@Override
	public void dispose() {
//		REGISTRY.removeDocumentSelectionManipulateTagsListener(editorInput.getTextNode(), styledText);
		styledText.deregisterComponentDirtyNotificationListener(this);
		super.dispose();
	}	
	
	@Override
	public void createPartControl(Composite parent) {
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);
		
		styledText = new TaggableStyledText(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setData(editorInput.getTextNode());
		styledText.setText(editorInput.getTextNode().getNEWText());
		
		styledText.setStyleRanges(editorInput.getTextNode().getNEWStyleRanges());
		styledText.registerComponentDirtyNotificationListener(this);
		styledText.addExtendedModifyListener(new ExtendedModifyListener() {
			
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				if (! isDirty) setDirty(true);
			}
		});
	
		/*
		 * Reset the dirty flag because the registration of tags changed it.
		 */
		setDirty(false);
		
		IWorkbenchPartSite site = this.getSite();
		
		IPartService partService = (IPartService) site.getService(IPartService.class);
		partService.addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				DOCUMENT_REGISTRY.registerDocumentSelectionManipulateTagsListener(editorInput.getTextNode(), getActiveStyledText());			
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				/* ignore */
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
//				IWorkbenchPart thisPart = (IWorkbenchPart) StyledEditor.this;
				DOCUMENT_REGISTRY.removeDocumentSelectionManipulateTagsListener(editorInput.getTextNode(), styledText);

				
				getActiveTextNode().reset();
				DOCUMENT_REGISTRY.resetDocumentData(getActiveTextNode());
				
				DOCUMENT_REGISTRY.updateCodeStats();
				TAG_MANAGER.updateCodeStats();
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				/* ignore */
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				/* ignore */
			}
		});

	}

	@Override
	public void setFocus() {
		styledText.setFocus();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			TextNode textNode = editorInput.getTextNode();

			/*
			 * Try to save the document data
			 */
			JAXBUtils.saveDocument(styledText.getStyleRanges(), styledText.getText(), JAXBUtils.getFileForTextNode(textNode));
			setDirty(false);

			/*
			 * Update the TextNode if the save was successful
			 */
			textNode.setTextContent(styledText.getText());
			textNode.setStyleRanges(styledText.getStyleRanges());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		
		if (input instanceof FQDADocumentEditorInput) {
			FQDADocumentEditorInput textInput = (FQDADocumentEditorInput) input;
			editorInput = textInput;
			updateTitle();
		}
		setDirty(false);
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public void setDirty(boolean isDirty) {
		if (this.isDirty != isDirty) {
			this.isDirty = isDirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
	
	private void updateTitle() {
		setPartName(editorInput.getName());
		setTitleToolTip(editorInput.getToolTipText());
	}

	@Override
	public TaggableStyledText getActiveStyledText() {
		return styledText;
	}

	public TaggableStyledTextPrintDataContainer getPrintableStyledText() {
		TaggableStyledTextPrintDataContainer res = new TaggableStyledTextPrintDataContainer();
		res.styledText = styledText;
		
		String[] lineNumberStrings = new String[styledText.getLineCount()];
		
		/*
		 * create line numbers
		 */
		for (int lineCtr = 1; lineCtr <= styledText.getLineCount(); lineCtr++) {
			lineNumberStrings[lineCtr-1] = String.valueOf(lineCtr)+StringTools.LINENUMBER_SEPERATOR;
		}
		
		res.lineNumberStrings = lineNumberStrings;
		return res;
	}

	@Override
	public TextNode getActiveTextNode() {
		return editorInput.getTextNode();
	}
}
