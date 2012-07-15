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
package net.sf.freeqda.view.projectview.commands;

import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.view.projectview.ProjectViewer;
import net.sf.freeqda.view.projectview.wizard.NewTextNodeWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class EditTextHandler extends AbstractHandler {

	private static final String EXCEPTION_INVALID_OBJECT = Messages.EditTextHandler_InvalidObject;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		/* 
		 * Get the view
		 */
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ProjectViewer view = (ProjectViewer) page.findView(ProjectViewer.ID);

		/*
		 *  Get the selection
		 */
		ISelection selection = view.getSite().getSelectionProvider().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			/*
			 * If we had a selection lets open the editor
			 */
			if ((obj != null) && (obj instanceof TextNode)) {
				TextNode toEdit = (TextNode) obj;

				NewTextNodeWizard newTextNodeWizard = new NewTextNodeWizard(toEdit);

				WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), newTextNodeWizard);
				dialog.open();
				
				view.refresh(null);
				
//				TextEditorDialog textEditorDialog = new TextEditorDialog(window.getShell(), SWT.APPLICATION_MODAL);
//				textEditorDialog.presetEditValues(toEdit);
//				textEditorDialog.open(); // blocks until the dialog is closed
//
//				if (textEditorDialog.isConfirmed()) {
//					/*
//					 * Update the view
//					 */
//					view.refresh(toEdit.getCategory());
//				}
			}
			else {
				throw new RuntimeException(EXCEPTION_INVALID_OBJECT);
			}
		}
		return null;
	}
}
