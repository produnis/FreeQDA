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

import java.util.HashMap;
import java.util.Map;

import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.ITaggableStyledTextProvider;
import net.sf.freeqda.common.widget.TaggableStyledText;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;


public class DynamicCompoundContributionItem2 extends CompoundContributionItem {

	private static final String PARAMETER_REMOVE_TAG_FROM_SELECTION_ID = "net.sf.freeqda.stylededitor.commands.dynamicRemoveTagFromSelection"; //$NON-NLS-1$
	private static final String PARAMETER_REMOVE_TAG_FROM_SELECTION_CMD_ID = "net.sf.freeqda.stylededitor.commands.removeTagFromSelection"; //$NON-NLS-1$
	private static final String PARAMETER_REMOVE_TAG_FROM_SELECTION_LABEL = Messages.DynamicCompoundContributionItem2_Label;
	private static final String PARAMETER_REMOVE_TAG_FROM_SELECTION_NAME = Messages.DynamicCompoundContributionItem2_Name;

	protected IContributionItem[] getContributionItems() {

		/* 
		 * Get the current editor
		 */
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();

		if (page.getActiveEditor() instanceof ITaggableStyledTextProvider) {
			
			ITaggableStyledTextProvider provider = (ITaggableStyledTextProvider) page.getActiveEditor();
			
			TagNode[] tagsInRange = null;
			
			if (provider != null) {
				TaggableStyledText taggableStyledText = provider.getActiveStyledText();
				if (taggableStyledText != null) {
					Point selectionRange = taggableStyledText.getSelectionRange();
					tagsInRange = taggableStyledText.getTagsInRange(selectionRange.x, selectionRange.y);
				}
				else {
					tagsInRange = new TagNode[0];
				}
			}
			else {
				tagsInRange = new TagNode[0];
			}
				
			
			IContributionItem[] res = null;

			if (tagsInRange.length == 0) {
				res = new IContributionItem[1];
				CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
						PARAMETER_REMOVE_TAG_FROM_SELECTION_ID,
						PARAMETER_REMOVE_TAG_FROM_SELECTION_CMD_ID, 
						SWT.NONE
				);
				contributionParameter.label = PARAMETER_REMOVE_TAG_FROM_SELECTION_LABEL;
				
				res[0] = new DisabledMenuContributionItem(contributionParameter);
			}
			else {
				res = new IContributionItem[tagsInRange.length];
				for (int n=0; n<tagsInRange.length; n++) {
					CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
							PARAMETER_REMOVE_TAG_FROM_SELECTION_ID,
							PARAMETER_REMOVE_TAG_FROM_SELECTION_CMD_ID, 
							SWT.NONE
					);
					
					contributionParameter.label = tagsInRange[n].getName();
					Map<String, String> parameters = new HashMap<String, String>();
					parameters.put(PARAMETER_REMOVE_TAG_FROM_SELECTION_NAME, String.valueOf(tagsInRange[n].getUID()));
					contributionParameter.parameters = parameters;

					res[n] = new EditorSelectionEnabledMenuContributionItem(contributionParameter);
				}
			}
			return res;
		}
		return new IContributionItem[0];
	}
}
