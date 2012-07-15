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
package net.sf.freeqda.editor.stylededitor.commands;

import java.util.LinkedList;

import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.editor.stylededitor.FQDADocumentEditorInput;
import net.sf.freeqda.editor.stylededitor.StyledEditor;
import net.sf.freeqda.editor.tagoverview.TaggedPassagesEditor;
import net.sf.freeqda.view.projectview.ProjectViewer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class CallEditorHandler extends AbstractHandler implements IHandler { 

	private static final String CLOSE_TITLE = Messages.CallEditorHandler_CloseDialog_Title;
	private static final String CLOSE_MESSAGE = Messages.CallEditorHandler_CloseDialog_Message;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/* 
		 * Get the view
		 */
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		ProjectViewer view = (ProjectViewer) page.findView(ProjectViewer.ID);

		/*
		 *  Get the selection
		 */
		ISelection selection = view.getSite().getSelectionProvider()
				.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			
			IEditorReference[] editorReferences = page.getEditorReferences();
			LinkedList<IEditorReference> closeList = new LinkedList<IEditorReference>();
			for (IEditorReference editorReference: editorReferences) {
				if (editorReference.getEditor(false) instanceof TaggedPassagesEditor) {
					closeList.add(editorReference);
				}
			}
			if (closeList.size() > 0) {
				MessageDialog.openInformation(window.getShell(), CLOSE_TITLE, CLOSE_MESSAGE);
				page.closeEditors(closeList.toArray(new IEditorReference[0]), true);
			}
			
			// If we had a selection lets open the editor
			if ((obj != null) && (obj instanceof TextNode)) {
				
				TextNode textNode = (TextNode) obj;
				openTextInEditor(textNode, page);
			}
		}
		return null;
	}
	
	public static void openTextInEditor(TextNode textNode, IWorkbenchPage page) {
		FQDADocumentEditorInput input = new FQDADocumentEditorInput(textNode);
		DocumentRegistry.getInstance().resetDocumentData(textNode);
		try {
			/* StyledEditor editorPart = (StyledEditor) */ page.openEditor(input, StyledEditor.ID);
		} catch (PartInitException e) {
			e.getStackTrace();
		}
	}
}
