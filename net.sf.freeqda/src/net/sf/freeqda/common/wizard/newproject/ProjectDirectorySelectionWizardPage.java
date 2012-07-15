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

import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.projectmanager.ProjectManager;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ProjectDirectorySelectionWizardPage extends WizardPage {

	private static final String WIZARD_PAGE_NAME = Messages.ProjectDirectorySelectionWizardPage_WizardPageName;
	private static final String WIZARD_PAGE_TITLE = Messages.ProjectDirectorySelectionWizardPage_WizardPageTitle;
	private static final String WIZARD_PAGE_DESCRIPTION = Messages.ProjectDirectorySelectionWizardPage_WizardPageDescription;
	private static final String LABEL_DIRECTORY_PATH = Messages.ProjectDirectorySelectionWizardPage_LabelDirectoryPath;
	private static final String BUTTON_BROWSE = Messages.ProjectDirectorySelectionWizardPage_ButtonBrowse;
	private static final String ERROR_DIRECTORY_NOT_EMPTY = Messages.ProjectDirectorySelectionWizardPage_ErrorDirectoryNotEmpty;

	
	private Button buttonBrowse;
	private Text textProjectDirectory;
	private Label labelDirectoryPath;
	private Composite container;

	protected ProjectDirectorySelectionWizardPage() {
		super(WIZARD_PAGE_NAME);
		setTitle(WIZARD_PAGE_TITLE);
		setDescription(WIZARD_PAGE_DESCRIPTION);
		setControl(textProjectDirectory);
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		labelDirectoryPath = new Label(container, SWT.NONE);
		GridData label1LData = new GridData();
		
		labelDirectoryPath.setLayoutData(label1LData);
		labelDirectoryPath.setText(LABEL_DIRECTORY_PATH);
	
		textProjectDirectory = new Text(container, SWT.BORDER);
		
		GridData text2LData = new GridData();
		text2LData.horizontalAlignment = GridData.FILL;
		text2LData.grabExcessHorizontalSpace = true;
		text2LData.heightHint = 17;
		
		textProjectDirectory.setLayoutData(text2LData);
		textProjectDirectory.setText(StringTools.EMPTY);
//		textProjectDirectory.setBounds(0, 56, 315, 21);

		buttonBrowse = new Button(container, SWT.PUSH | SWT.CENTER);
		buttonBrowse.setText(BUTTON_BROWSE);
		buttonBrowse.setBounds(325, 56, 64, 35);
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
//				String platform = SWT.getPlatform();
//				dialog.setFilterPath(platform.equals("win32")
//						|| platform.equals("wpf") ? "c:\\" : "/");
//				ProjectNameWizardPage namePage = (ProjectNameWizardPage) getPreviousPage();
				String workspaceDirectory = ProjectManager.getInstance().getWorkspaceDirectory().getAbsolutePath();
				dialog.setFilterPath(workspaceDirectory);
//				dialog.setText(workspaceDirectory+File.separator+namePage.getName());
				String absolutePath = dialog.open();
				if (absolutePath != null && ( ! absolutePath.isEmpty())) {
					textProjectDirectory.setText(absolutePath);

					File directory = new File(absolutePath);

					/*
					 * Check if the directory is empty
					 */
					boolean isEmptyDirectory = directory.list().length == 0;
					if (isEmptyDirectory) {
						setErrorMessage(null);
					} else {
						setErrorMessage(ERROR_DIRECTORY_NOT_EMPTY);
					}
					setPageComplete(isEmptyDirectory);
				}
			}
		});
		setControl(container);
		setPageComplete(false);
	}

	public String getProjectDirectoryAbsolutePath() {
		return textProjectDirectory.getText();
	}
}
