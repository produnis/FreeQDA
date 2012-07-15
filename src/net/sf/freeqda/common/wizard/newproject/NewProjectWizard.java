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
package net.sf.freeqda.common.wizard.newproject;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import net.sf.freeqda.bindings.project.FQDAProjectType;
import net.sf.freeqda.bindings.project.ObjectFactory;
import net.sf.freeqda.common.JAXBUtils;
import net.sf.freeqda.common.projectmanager.ProjectManager;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;


public class NewProjectWizard extends Wizard {

	private static final String MSG_CREATION_SUCCEEDED_TITLE = Messages.NewProjectWizard_MsgCreationSucceeded_Title;
	private static final String MSG_CREATION_SUCCEEDED_TEXT = Messages.NewProjectWizard_MsgCreationSucceeded_Text;
	private static final String ERROR_CANNOT_WRITE = Messages.NewProjectWizard_ErrorCannotWrite; 
	private static final String BUTTON_OK_LABEL = Messages.NewProjectWizard_ButtonLabelOk;
	
	private ProjectNameWizardPage nameSelectionPage;
	
	
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		nameSelectionPage = new ProjectNameWizardPage();
		addPage(nameSelectionPage);
	}

	@Override
	public boolean performFinish() {

		File projectDirectory = new File(ProjectManager.getInstance().getWorkspaceDirectory()+File.separator+nameSelectionPage.getProjectName());
		projectDirectory.mkdirs();
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		if (projectDirectory.exists() && projectDirectory.canRead() && projectDirectory.canExecute()) {
			
			File projectFile = new File(projectDirectory, ProjectManager.FQDA_PROJECT_FILE_NAME);
			ObjectFactory of = new ObjectFactory();
			FQDAProjectType newProject = of.createFQDAProjectType();
			newProject.setName(nameSelectionPage.getProjectName());
			
			if (projectFile.exists()) {
				nameSelectionPage.setErrorMessage(MessageFormat.format(Messages.NewProjectWizard_ErrorProjectExists, new Object[] {projectFile.getAbsolutePath()}));
				return false;
			}

			try {
				JAXBUtils.saveProject(projectFile);
				ProjectManager.getInstance().load(projectFile);
				
				new MessageDialog(shell, MSG_CREATION_SUCCEEDED_TITLE, null, MSG_CREATION_SUCCEEDED_TEXT, MessageDialog.INFORMATION, new String[] { BUTTON_OK_LABEL }, 0).open();
				return true;
				
			} catch (IOException e) {
				e.printStackTrace();

				nameSelectionPage.setErrorMessage(MessageFormat.format(Messages.NewProjectWizard_ErrorProjectCreationFailed, new Object[] {e.getMessage()}));
				return false;
			}
		}
		else {
			nameSelectionPage.setErrorMessage(ERROR_CANNOT_WRITE);
			return false;
		}
	}
}
