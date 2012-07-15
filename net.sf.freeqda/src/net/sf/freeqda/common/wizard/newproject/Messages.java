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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.freeqda.common.wizard.newproject.messages"; //$NON-NLS-1$
	public static String NewProjectWizard_ButtonLabelOk;
	public static String NewProjectWizard_ErrorCannotWrite;
	public static String NewProjectWizard_ErrorProjectCreationFailed;
	public static String NewProjectWizard_ErrorProjectExists;
	public static String NewProjectWizard_MsgCreationSucceeded_Text;
	public static String NewProjectWizard_MsgCreationSucceeded_Title;
	public static String ProjectDirectorySelectionWizardPage_ButtonBrowse;
	public static String ProjectDirectorySelectionWizardPage_ErrorDirectoryNotEmpty;
	public static String ProjectDirectorySelectionWizardPage_LabelDirectoryPath;
	public static String ProjectDirectorySelectionWizardPage_WizardPageDescription;
	public static String ProjectDirectorySelectionWizardPage_WizardPageName;
	public static String ProjectDirectorySelectionWizardPage_WizardPageTitle;
	public static String ProjectNameWizardPage_ErrorProjectNameEmpty;
	public static String ProjectNameWizardPage_LabelProjectName;
	public static String ProjectNameWizardPage_WizardPageDescription;
	public static String ProjectNameWizardPage_WizardPageName;
	public static String ProjectNameWizardPage_WizardPageTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
