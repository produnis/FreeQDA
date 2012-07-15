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
package net.sf.freeqda.common.handler;

import java.io.IOException;
import java.text.MessageFormat;

import net.sf.freeqda.common.JAXBUtils;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.editor.stylededitor.StyledEditor;
import net.sf.freeqda.editor.tagoverview.TaggedPassagesEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;


public class SaveProjectHandler extends AbstractHandler implements IHandler {

	private static final ProjectManager PM = ProjectManager.getInstance();

	private static final String SAVE_PROJECT_UNHANDLED_TITLE = Messages.SaveProjectHandler_SaveProjectUnhandled_Title;
	private static final String SAVE_PROJECT_UNHANDLED_MESSAGE = Messages.SaveProjectHandler_SaveProjectUnhandled_Message;
	private static final String SAVE_PROJECT_UNHANDLED_BUTTON_CONFIRM= Messages.SaveProjectHandler_SaveProjectUnhandled_ButtonConfirm;
	private static final String SAVE_PROJECT_SUCCESS_TITLE = Messages.SaveProjectHandler_SaveProjectSuccess_Title;
	private static final String SAVE_PROJECT_SUCCESS_MESSAGE = Messages.SaveProjectHandler_SaveProjectSuccess_Message;
	private static final String SAVE_PROJECT_SUCCESS_BUTTON_CONFIRM= Messages.SaveProjectHandler_SaveProjectSuccess_ButtonConfirm;
	private static final String SAVE_PROJECT_FAILED_TITLE = Messages.SaveProjectHandler_SaveProjectFailed_Title;
	private static final String SAVE_PROJECT_FAILED_BUTTON_CONFIRM= Messages.SaveProjectHandler_SaveProjectFailed_ButtonConfirm;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

	    /*
	     * Load selected project file
	     */
	    try {
	    	/*
	    	 * Save project data
	    	 */
		    JAXBUtils.saveProject(PM.getProjectFile());
	
			ProgressMonitorDialog progressMonitor = new ProgressMonitorDialog(shell);
		    /*
		     * Save dirty editors
		     */
		    for (IEditorPart editor: PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDirtyEditors()) {
				if (editor instanceof StyledEditor) {
					StyledEditor styledEditor = (StyledEditor) editor;
					styledEditor.doSave(progressMonitor.getProgressMonitor());
				}
				else if (editor instanceof TaggedPassagesEditor) {
					TaggedPassagesEditor taggedPassagesEditor = (TaggedPassagesEditor) editor;
					taggedPassagesEditor.doSave(progressMonitor.getProgressMonitor());
				}
				else {
					new MessageDialog(shell, SAVE_PROJECT_UNHANDLED_TITLE, null, SAVE_PROJECT_UNHANDLED_MESSAGE, MessageDialog.ERROR, new String[] { SAVE_PROJECT_UNHANDLED_BUTTON_CONFIRM }, 0).open();
				}
		    }
			new MessageDialog(shell, SAVE_PROJECT_SUCCESS_TITLE, null, SAVE_PROJECT_SUCCESS_MESSAGE, MessageDialog.INFORMATION, new String[] { SAVE_PROJECT_SUCCESS_BUTTON_CONFIRM }, 0).open();

	    }
	    catch (IOException e) {
			new MessageDialog(shell, SAVE_PROJECT_FAILED_TITLE, null, MessageFormat.format(Messages.SaveProjectHandler_SaveProjectFailed_Message,new Object[] {PM.getProjectFile().getAbsolutePath()}), MessageDialog.ERROR, new String[] { SAVE_PROJECT_FAILED_BUTTON_CONFIRM }, 0).open();
			e.printStackTrace();
	    }
    	return null;
	}
}
