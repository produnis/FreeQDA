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

import java.text.MessageFormat;

import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.view.projectview.ProjectViewer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class DeleteCategoryHandler extends AbstractHandler {

	private static final ProjectManager PROJECT_MANAGER = ProjectManager.getInstance();

	private static final String DIALOG_TEXT =  Messages.DeleteCategoryHandler_DialogText;	
	private static final String DIALOG_MESSAGE_ADDITION = Messages.DeleteCategoryHandler_DialogMessageAddition;
	private static final String DIALOG_MESSAGE = Messages.DeleteCategoryHandler_DialogMessage;

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
			if ((obj != null) && (obj instanceof TextCategoryNode)) {
				TextCategoryNode toRemove = (TextCategoryNode) obj;
				TextCategoryNode parentNode = (TextCategoryNode) toRemove.getParentCategory();

				String deleteChildren = toRemove.getChildren().size() > 0 ? DIALOG_MESSAGE_ADDITION : StringTools.EMPTY;
				MessageBox messageBox = new MessageBox(window.getShell(), SWT.CANCEL|SWT.OK|SWT.ICON_QUESTION);
				messageBox.setText(DIALOG_TEXT);
				messageBox.setMessage(MessageFormat.format(DIALOG_MESSAGE, new Object[] {deleteChildren}));
				if (messageBox.open() == SWT.OK) {
					PROJECT_MANAGER.remove(toRemove);
						
					/*
					 * Update the view
					 */
					view.refresh(parentNode);

				}
			}
		}
		return null;
	}
}
