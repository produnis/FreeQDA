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
package net.sf.freeqda.view.tagview.wizard;

import net.sf.freeqda.common.GenericTreeNode;
import net.sf.freeqda.common.StringTools;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.tagregistry.events.TagNodeCreatedEvent;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.RGB;


public class NewTagNodeWizard extends Wizard {

	private static final String TITLE_CREATE = Messages.NewTagNodeWizard_CreateNewTag;
	private static final String TITLE_EDIT = Messages.NewTagNodeWizard_EditTag;

	private static final String WIZARD_PAGE_NAME_NAME = Messages.NewTagNodeWizard_NameSelectionPage_Name;
	private static final String WIZARD_PAGE_NAME_TITLE = Messages.NewTagNodeWizard_NameSelectionPage_Title;
	private static final String WIZARD_PAGE_NAME_DESCRIPTION = Messages.NewTagNodeWizard_NameSelectionPage_Description;

	private static final String WIZARD_PAGE_PARENT_NAME = Messages.NewTagNodeWizard_ParentSelectionPage_Name;
	private static final String WIZARD_PAGE_PARENT_TITLE = Messages.NewTagNodeWizard_ParentSelectionPage_Title;
	private static final String WIZARD_PAGE_PARENT_DESCRIPTION = Messages.NewTagNodeWizard_ParentSelectionPage_Description;

	private EnterNameAndColorWizardPage namePage;
	private SelectParentTagWizardPage categoryPage;
	
	private String defaultName;
	private RGB defaultColor;
	private GenericTreeNode<TagNode> parentTag;
	
	private TagNode editNode;
	
	public NewTagNodeWizard(String defaultName, RGB defaultColor, GenericTreeNode<TagNode> parentTag) {

		super();
		
		if (defaultName != null) this.defaultName = defaultName;
		else this.defaultName = StringTools.EMPTY;
		
		if (this.defaultColor != null) this.defaultColor = defaultColor;
		else this.defaultColor = new RGB(255,255,255);
		
		if (parentTag != null) {
			this.parentTag = parentTag;
		}
		else {
			this.parentTag = TagManager.getInstance().getRootTag();
		}

		setNeedsProgressMonitor(true);
	}

	public NewTagNodeWizard(TagNode editNode) {
		this(editNode.getName(), editNode.getRGB(), editNode.getParentCategory());
		this.editNode = editNode;
	}

	@Override
	public boolean performFinish() {
		
		if (editNode == null) {
			/*
			 * Creating a new node
			 */
			TagNode newTagNode = TagManager.getInstance().createTagNode();
			newTagNode.setName(namePage.getTagName());
			newTagNode.setRGB(namePage.getTagColor());
			newTagNode.setParentNode(categoryPage.getParentTag());
			
			TagNodeCreatedEvent createdEvent = new TagNodeCreatedEvent(this, newTagNode, categoryPage.getParentTag());
			TagManager.getInstance().fireTagNodeCreatedEvent(createdEvent);
		}
		else {
			/*
			 * Update the provided node
			 */
			editNode.setName(namePage.getTagName());
			editNode.setRGB(namePage.getTagColor());
			editNode.setParentNode(categoryPage.getParentTag());
		}
		return true;
	}

	@Override
	public void addPages() {
		boolean isCreateMode = editNode == null; 
		setWindowTitle(isCreateMode ? TITLE_CREATE : TITLE_EDIT);
	
		namePage = new EnterNameAndColorWizardPage(WIZARD_PAGE_NAME_NAME, WIZARD_PAGE_NAME_TITLE, WIZARD_PAGE_NAME_DESCRIPTION, defaultName, defaultColor);
		addPage(namePage);
		
		categoryPage = new SelectParentTagWizardPage(WIZARD_PAGE_PARENT_NAME, WIZARD_PAGE_PARENT_TITLE, WIZARD_PAGE_PARENT_DESCRIPTION, parentTag, isCreateMode);
		addPage(categoryPage);
	}
}
