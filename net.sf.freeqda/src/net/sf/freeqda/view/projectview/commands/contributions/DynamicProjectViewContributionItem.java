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
package net.sf.freeqda.view.projectview.commands.contributions;

import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.view.projectview.ProjectViewer;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;



public class DynamicProjectViewContributionItem extends CompoundContributionItem {

	private static final String CONTRIBUTION_LABEL_CREATE_TEXT = Messages.DynamicProjectViewContributionItem_LabelCreateText;
	private static final String CONTRIBUTION_LABEL_CREATE_CATEGORY = Messages.DynamicProjectViewContributionItem_LabelCreateCategory;
	private static final String CONTRIBUTION_LABEL_EDIT_CATEGORY = Messages.DynamicProjectViewContributionItem_LabelEditCategory;
	private static final String CONTRIBUTION_LABEL_DELETE_CATEGORY = Messages.DynamicProjectViewContributionItem_LabelDeleteCategory;
	private static final String CONTRIBUTION_LABEL_OPEN_TEXT = Messages.DynamicProjectViewContributionItem_LabelOpenText;
	private static final String CONTRIBUTION_LABEL_EDIT_TEXT = Messages.DynamicProjectViewContributionItem_LabelEditText;
	private static final String CONTRIBUTION_LABEL_DELETE_TEXT = Messages.DynamicProjectViewContributionItem_LabelDeleteText;

	protected IContributionItem[] getContributionItems() {

		final CommandContributionItemParameter contributionParameterCreateText = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicCreateText", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.newText", SWT.NONE); //$NON-NLS-1$
		contributionParameterCreateText.label = CONTRIBUTION_LABEL_CREATE_TEXT;

		final CommandContributionItemParameter contributionParameterCreateTextCategory = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicCreateTextCategory", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.newCategory", SWT.NONE); //$NON-NLS-1$
		contributionParameterCreateTextCategory.label = CONTRIBUTION_LABEL_CREATE_CATEGORY;

		final CommandContributionItemParameter contributionParameterEditTextCategory = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicEditTextCategory", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.editCategory", SWT.NONE); //$NON-NLS-1$
		contributionParameterEditTextCategory.label = CONTRIBUTION_LABEL_EDIT_CATEGORY;

		final CommandContributionItemParameter contributionParameterDeleteTextCategory = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicDeleteTextCategory", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.deleteCategory", SWT.NONE); //$NON-NLS-1$
		contributionParameterDeleteTextCategory.label = CONTRIBUTION_LABEL_DELETE_CATEGORY;

		final CommandContributionItemParameter contributionParameterOpenText = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicOpenText", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.openText", SWT.NONE); //$NON-NLS-1$
		contributionParameterOpenText.label = CONTRIBUTION_LABEL_OPEN_TEXT;
//
		final CommandContributionItemParameter contributionParameterEditText = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicEditText", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.editText", SWT.NONE); //$NON-NLS-1$
		contributionParameterEditText.label = CONTRIBUTION_LABEL_EDIT_TEXT;

		final CommandContributionItemParameter contributionParameterDeleteText = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.projectview.commands.dynamicDeleteText", //$NON-NLS-1$
				"net.sf.freeqda.projectview.commands.deleteText", SWT.NONE); //$NON-NLS-1$
		contributionParameterDeleteText.label = CONTRIBUTION_LABEL_DELETE_TEXT;


		
//		final Action actionActivateText = new Action("Show in tag overview", SWT.TOGGLE) {
//			@Override
//			public void run() {
//				super.run();
//				ISelection selection = categoryTreeViewer.getSelection();
//				if (! selection.isEmpty()) {
//					if (selection instanceof IStructuredSelection) {
//						IStructuredSelection structSelect = (IStructuredSelection) selection;
//						if (structSelect.getFirstElement() instanceof TextNode) {
//							TextNode selectedText = (TextNode) structSelect.getFirstElement();
//							PROJECT_MANAGER.toggleTextNodeActive(selectedText);
//
//							/*
//							 * Update the view
//							 */
////							categoryTreeViewer.refresh(selectedText.getCategory());
//							refresh(selectedText.getCategory());
//
//						}
//						else {
//							throw new RuntimeException("Can only edit TextNodes!");
//						}
//					}
//				}
//			} 
//		};

		
		
		/*
		 * Get the view
		 */
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		ProjectViewer view = (ProjectViewer) page.findView(ProjectViewer.ID);

		/*
		 * Get the selection
		 */
		ISelection selection = view.getSite().getSelectionProvider()
				.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj == null) return new IContributionItem[] {};

			if (obj instanceof TextCategoryNode) {
				return new IContributionItem[] {
						new TextCategorySelectedMenuContributionItem(contributionParameterCreateText),
						new Separator(),
						new TextCategorySelectedMenuContributionItem(contributionParameterCreateTextCategory),
						new TextCategorySelectedMenuContributionItem(contributionParameterEditTextCategory),
						new TextCategorySelectedMenuContributionItem(contributionParameterDeleteTextCategory),
				};
			}
			else if (obj instanceof TextNode) {
				return new IContributionItem[] {
//						mgr.add(actionActivateText);
						new TextNodeSelectedMenuContributionItem(contributionParameterOpenText),
						new Separator(),
						new TextNodeSelectedMenuContributionItem(contributionParameterEditText),
						new TextNodeSelectedMenuContributionItem(contributionParameterDeleteText),
				};
			}
		}
		return new IContributionItem[] {};
	}
}
