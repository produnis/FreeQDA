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
package net.sf.freeqda.editor.stylededitor.commands.contributions;

import net.sf.freeqda.view.tagview.commands.contributions.RootDisabledMenuContributionItem;
import net.sf.freeqda.view.tagview.commands.contributions.TagSelectedMenuContributionItem;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;



public class DynamicCompoundContributionItem extends CompoundContributionItem {

	private static final String DYN_APPLY_TAG_CMD_ID = "net.sf.freeqda.tagview.commands.applyTag"; //$NON-NLS-1$
	private static final String DYN_APPLY_TAG_ID = "net.sf.freeqda.tagview.commands.dynamicApplyTag"; //$NON-NLS-1$
	private static final String DYN_APPLY_TAG_LABEL = Messages.DynamicCompoundContributionItem_ApplyTag_Label;

	private static final String DYN_REMOVE_TAG_CMD_ID = "net.sf.freeqda.tagview.commands.removeTag"; //$NON-NLS-1$
	private static final String DYN_REMOVE_TAG_ID = "net.sf.freeqda.tagview.commands.dynamicRemoveTag"; //$NON-NLS-1$
	private static final String DYN_REMOVE_TAG_LABEL = Messages.DynamicCompoundContributionItem_RemoveTag_Label;
	
	private static final String DYN_REMOVE_ALL_TAGS_CMD_ID = "net.sf.freeqda.tagview.commands.removeAllTags"; //$NON-NLS-1$
	private static final String DYN_REMOVE_ALL_TAGS_ID = "net.sf.freeqda.tagview.commands.dynamicRemoveAllTags"; //$NON-NLS-1$
	private static final String DYN_REMOVE_ALL_TAGS_LABEL = Messages.DynamicCompoundContributionItem_RemoveAllTags_Label;	

	private static final String DYN_SHOW_OVERVIEW_TAGS_CMD_ID = "net.sf.freeqda.tagview.commands.showOverview"; //$NON-NLS-1$
	private static final String DYN_SHOW_OVERVIEW_TAGS_ID = "net.sf.freeqda.tagview.commands.dynamicShowTagOverview"; //$NON-NLS-1$
	private static final String DYN_SHOW_OVERVIEW_TAGS_LABEL = Messages.DynamicCompoundContributionItem_ShowOverview_Label;
	
	protected IContributionItem[] getContributionItems() {

		final CommandContributionItemParameter contributionParameterApplyTag = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				DYN_APPLY_TAG_ID,
				DYN_APPLY_TAG_CMD_ID, 
				SWT.NONE
		);
		contributionParameterApplyTag.label = DYN_APPLY_TAG_LABEL;

		final CommandContributionItemParameter contributionParameterRemoveTag = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				DYN_REMOVE_TAG_ID,
				DYN_REMOVE_TAG_CMD_ID, 
				SWT.NONE
		);
		contributionParameterRemoveTag.label = DYN_REMOVE_TAG_LABEL;

		final CommandContributionItemParameter contributionParameterRemoveAllTags = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				DYN_REMOVE_ALL_TAGS_ID,
				DYN_REMOVE_ALL_TAGS_CMD_ID, 
				SWT.NONE
		);
		contributionParameterRemoveAllTags.label = DYN_REMOVE_ALL_TAGS_LABEL;

		final CommandContributionItemParameter contributionParameterShowTagOverview = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				DYN_SHOW_OVERVIEW_TAGS_ID,
				DYN_SHOW_OVERVIEW_TAGS_CMD_ID, 
				SWT.NONE
		);
		contributionParameterShowTagOverview.label = DYN_SHOW_OVERVIEW_TAGS_LABEL;

		return new IContributionItem[] {
				new EditorSelectionEnabledMenuContributionItem(
						contributionParameterApplyTag),
				new TagSelectedMenuContributionItem(
						contributionParameterRemoveTag),
				new EditorSelectionEnabledMenuContributionItem(
						contributionParameterRemoveAllTags),
				new Separator(),
				new RootDisabledMenuContributionItem(contributionParameterShowTagOverview),
		};
	}
}
