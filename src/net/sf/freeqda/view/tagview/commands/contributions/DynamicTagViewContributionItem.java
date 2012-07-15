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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

public class DynamicTagViewContributionItem extends CompoundContributionItem {

	protected IContributionItem[] getContributionItems() {

		final CommandContributionItemParameter contributionParameterCreateTagNode = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.tagview.commands.dynamicCreateTagNode", //$NON-NLS-1$
				"net.sf.freeqda.tagview.commands.createTagNode", SWT.NONE); //$NON-NLS-1$
		contributionParameterCreateTagNode.label = Messages.DynamicTagViewContributionItem_CreateTagNode_Label;

		final CommandContributionItemParameter contributionParameterEditTagNode = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.tagview.commands.dynamicEditTagNode", //$NON-NLS-1$
				"net.sf.freeqda.tagview.commands.editTagNode", SWT.NONE); //$NON-NLS-1$
		contributionParameterEditTagNode.label = Messages.DynamicTagViewContributionItem_EditTagNode_Label;

		final CommandContributionItemParameter contributionParameterDeleteTagNode = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"net.sf.freeqda.tagview.commands.dynamicDeleteTagNode", //$NON-NLS-1$
				"net.sf.freeqda.tagview.commands.deleteTagNode", SWT.NONE); //$NON-NLS-1$
		contributionParameterDeleteTagNode.label = Messages.DynamicTagViewContributionItem_DeleteTagNode_Label;

		return new IContributionItem[] {
				new RootEnabledMenuContributionItem(
						contributionParameterCreateTagNode),
				new RootDisabledMenuContributionItem(
						contributionParameterEditTagNode),
				new RootDisabledMenuContributionItem(
						contributionParameterDeleteTagNode), 
		};
	}
}
