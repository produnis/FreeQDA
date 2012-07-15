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
package net.sf.freeqda.view.projectview.wizard;

import net.sf.freeqda.common.GenericTreeNode;
import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.projectmanager.TextNodeCreatedEvent;

import org.eclipse.jface.wizard.Wizard;


public class NewTextNodeWizard extends Wizard {

	public static final boolean CREATE_MODE = true;
	public static final boolean EDIT_MODE = false;

	private static final String WIZARD_TITLE_CREATE = Messages.NewTextNodeWizard_Title_Create;
	private static final String WIZARD_TITLE_EDIT = Messages.NewTextNodeWizard_Title_Edit;
	
	private static final String WIZARD_PAGE_ENTER_NAME_NAME = Messages.NewTextNodeWizard_TextNamePage_Name;
	private static final String WIZARD_PAGE_ENTER_NAME_TITLE = Messages.NewTextNodeWizard_TextNamePage_Title;
	private static final String WIZARD_PAGE_ENTER_NAME_DESCRIPTION = Messages.NewTextNodeWizard_TextNamePage_Description;
	
	private static final String WIZARD_PAGE_PARENT_CATEGORY_NAME = Messages.NewTextNodeWizard_ParentCategoryPage_Name;
	private static final String WIZARD_PAGE_PARENT_CATEGORY_TITLE = Messages.NewTextNodeWizard_ParentCategoryPage_Title;
	private static final String WIZARD_PAGE_PARENT_CATEGORY_DESCRIPTION = Messages.NewTextNodeWizard_ParentCategoryPage_Description;
	
	
	private EnterNameWizardPage namePage;
	private SelectParentCategoryWizardPage categoryPage;
	
	private String defaultName;
	private GenericTreeNode<TextCategoryNode> parentCategory;

	private TextNode editNode;
	
	public NewTextNodeWizard(String defaultName, GenericTreeNode<TextCategoryNode> parentCategory) {

		super();
		
		if (defaultName != null) this.defaultName = defaultName;
		else this.defaultName = StringTools.EMPTY;
		
		if (parentCategory != null) {
			this.parentCategory = parentCategory;
		}
		else {
			this.parentCategory = ProjectManager.getInstance().getRootCategory();
		}

		setNeedsProgressMonitor(true);
	}

	public NewTextNodeWizard(TextNode editNode) {
		this(editNode.getName(), editNode.getCategory());
		this.editNode = editNode;
	}

	@Override
	public boolean performFinish() {
		
		boolean isEditMode = editNode != null;
		if (isEditMode) {
			editNode.setName(namePage.getCategoryName());
			editNode.setCategory(categoryPage.getParentCategory());

			ProjectManager.getInstance().fireProjectDataModifiedEvent();
		}
		else {
			TextNode newTextNode = ProjectManager.getInstance().createTextNode();
			newTextNode.setName(namePage.getCategoryName());
			newTextNode.setCategory(categoryPage.getParentCategory());

			ProjectManager.getInstance().NodeCreated(new TextNodeCreatedEvent(this, newTextNode, categoryPage.getParentCategory()));
		}
		return true;
	}

	@Override
	public void addPages() {

		boolean isCreateMode = editNode == null;
		
		setWindowTitle(isCreateMode ? WIZARD_TITLE_CREATE : WIZARD_TITLE_EDIT);
	
		namePage = new EnterNameWizardPage(WIZARD_PAGE_ENTER_NAME_NAME, WIZARD_PAGE_ENTER_NAME_TITLE, WIZARD_PAGE_ENTER_NAME_DESCRIPTION, defaultName);
		addPage(namePage);
		
		categoryPage = new SelectParentCategoryWizardPage(WIZARD_PAGE_PARENT_CATEGORY_NAME, WIZARD_PAGE_PARENT_CATEGORY_TITLE, WIZARD_PAGE_PARENT_CATEGORY_DESCRIPTION, parentCategory, isCreateMode);
		addPage(categoryPage);
	}
}
