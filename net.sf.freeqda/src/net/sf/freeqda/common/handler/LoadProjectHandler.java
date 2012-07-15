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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import net.sf.freeqda.common.projectmanager.ProjectManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class LoadProjectHandler extends AbstractHandler {

//	private static final ProjectManager PROJECT_MANAGER = ProjectManager.getInstance();
	
	private static final String DIALOG_TEXT = Messages.LoadProjectHandler_DialogText;
	private static final String SUFFIX_FQDA_PROJECTS = "*.fqd"; //$NON-NLS-1$
	private static final String SUFFIX_ALL_FILES = "*.*"; //$NON-NLS-1$

	private static final String DIALOG_LOAD_CANCELED_TITLE = Messages.LoadProjectHandler_DialogLoadCanceled_Title;
	private static final String DIALOG_LOAD_CANCELED_MESSAGE = Messages.LoadProjectHandler_DialogLoadCanceled_Message;
	private static final String DIALOG_LOAD_CANCELED_BUTTONS = Messages.LoadProjectHandler_DialogLoadCanceled_ButtonConfirm;

	private static final String DIALOG_PROJECT_LOADED_TITLE = Messages.LoadProjectHandler_DialogProjectLoaded_Title;
	private static final String DIALOG_PROJECT_LOADED_BUTTONS = Messages.LoadProjectHandler_DialogProjectLoaded_ButtonConfirm;

	private static final String DIALOG_LOADING_FAILED_TITLE = Messages.LoadProjectHandler_DialogProjectLoadingFailed_Title;
	private static final String DIALOG_LOADING_FAILED_BUTTONS = Messages.LoadProjectHandler_DialogProjectLoadingFailed_ButtonConfirm;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

		//TODO check for active project and close if exists
		
		// File standard dialog
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		
		// Initialise the dialog
		fileDialog.setText(DIALOG_TEXT);
	    fileDialog.setFilterNames(new String[] { 
	    		MessageFormat.format(Messages.LoadProjectHandler_Filter_FQDAProjects, new Object[] {SUFFIX_FQDA_PROJECTS}), 
	    		MessageFormat.format(Messages.LoadProjectHandler_Filter_AllFiles, new Object[] {SUFFIX_ALL_FILES}) 
	    });
	    fileDialog.setFilterExtensions(new String[] { SUFFIX_FQDA_PROJECTS, SUFFIX_ALL_FILES }); //TODO create platform dependent wildcards!
	    
		// Open Dialog and save result of selection
		String selected = fileDialog.open();

		/*
	     * Check if dialog has been canceled
	     */
	    if (selected == null) {
			new MessageDialog(shell, DIALOG_LOAD_CANCELED_TITLE, null, DIALOG_LOAD_CANCELED_MESSAGE, MessageDialog.WARNING, new String[] { DIALOG_LOAD_CANCELED_BUTTONS }, 0).open();
	    	return null;
	    }
	    
	    /*
	     * Load selected project file
	     */
	    try {
	    	ProjectManager.getInstance().load(new File(selected));
			new MessageDialog(shell, DIALOG_PROJECT_LOADED_TITLE, null, MessageFormat.format(Messages.LoadProjectHandler_DialogProjectLoaded_Message, new Object[] {selected}), MessageDialog.INFORMATION, new String[] { DIALOG_PROJECT_LOADED_BUTTONS }, 0).open();
	    }
	    catch (IOException e) {
			new MessageDialog(shell, DIALOG_LOADING_FAILED_TITLE, null, MessageFormat.format(Messages.LoadProjectHandler_DialogProjectLoadingFailed_Message, new Object[] {selected}), MessageDialog.ERROR, new String[] { DIALOG_LOADING_FAILED_BUTTONS }, 0).open();
			e.printStackTrace();
	    }
    	return null;
	}

	
	
}
