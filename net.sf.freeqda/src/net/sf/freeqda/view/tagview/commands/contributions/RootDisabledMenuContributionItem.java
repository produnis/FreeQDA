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
package net.sf.freeqda.view.tagview.commands.contributions;

import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.view.tagview.TagViewer;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;


public class RootDisabledMenuContributionItem extends CommandContributionItem {

	public RootDisabledMenuContributionItem(
			CommandContributionItemParameter contributionParameters) {
		super(contributionParameters);
	}

	@Override
	public boolean isEnabled() {
		/*
		 * Get the view
		 */
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		TagViewer view = (TagViewer) page.findView(TagViewer.ID);

		/*
		 * Get the selection
		 */
		ISelection selection = view.getSite().getSelectionProvider()
				.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			/*
			 * return true if we have a TagNode selected
			 */
			if ((obj != null) && (obj instanceof TagNode)) {
				return (((TagNode) obj).getUID() != 0);
			}		
		}
		return false;
	}

}
