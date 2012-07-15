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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ProjectNameWizardPage extends WizardPage {
	
	private static final String WIZARD_PAGE_NAME = Messages.ProjectNameWizardPage_WizardPageName;
	private static final String WIZARD_PAGE_TITLE = Messages.ProjectNameWizardPage_WizardPageTitle;
	private static final String WIZARD_DESCRIPTION = Messages.ProjectNameWizardPage_WizardPageDescription;
	private static final String LABEL_PROJECT_NAME = Messages.ProjectNameWizardPage_LabelProjectName;
	private static final String ERROR_PROJECT_NAME_EMPTY = Messages.ProjectNameWizardPage_ErrorProjectNameEmpty;
	
	private Text textProjectName;
	private Label labelProjectName;
	private Composite container;

	protected ProjectNameWizardPage() {
		super(WIZARD_PAGE_NAME);
		setTitle(WIZARD_PAGE_TITLE);
		setDescription(WIZARD_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		
		container = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		
		layout.numColumns = 2;
			labelProjectName = new Label(container, SWT.NONE);
			GridData label2LData = new GridData();
			labelProjectName.setLayoutData(label2LData);
			labelProjectName.setText(LABEL_PROJECT_NAME);

			textProjectName = new Text(container, SWT.BORDER);
			GridData text1LData = new GridData();
			text1LData.grabExcessHorizontalSpace = true;
			text1LData.horizontalAlignment = GridData.FILL;
			textProjectName.setLayoutData(text1LData);
			textProjectName.addKeyListener(new KeyListener() {
				
				@Override
				public void keyReleased(KeyEvent e) {
					if (textProjectName.getText().isEmpty()) {
						setErrorMessage(ERROR_PROJECT_NAME_EMPTY);
					}
					else {
						setPageComplete(true);
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {}
			});
		setControl(container);
		setPageComplete(false);
	}

	public String getProjectName() {
		return textProjectName.getText();
	}
}
