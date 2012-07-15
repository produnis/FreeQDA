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
package net.sf.freeqda.editor.stylededitor.commands;

import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.ITaggableStyledTextProvider;
import net.sf.freeqda.common.widget.TaggableStyledText;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class RemoveTagFromSelectionHandler extends AbstractHandler {

//	private static final TagRegistry REGISTRY = TagRegistry.getInstance();

//	private DocumentRegistry DOCUMENT_REGISTRY = DocumentRegistry.getInstance();
	
	private static final String REMOVE_TAG_PARAMETER = "net.sf.freeqda.stylededitor.commands.removeTagFromSelection.tagUID"; //$NON-NLS-1$
	
	//FIXME looks like redundant code (see ...tagview.commands.DocumentSelectionRemoveTag)
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/* 
		 * Get the current editor
		 */
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();

		String tagUIDString = event.getParameter(REMOVE_TAG_PARAMETER);
		IEditorPart editor = page.getActiveEditor();
		if ((tagUIDString != null) && (editor instanceof ITaggableStyledTextProvider)) {
			ITaggableStyledTextProvider provider = (ITaggableStyledTextProvider) page.getActiveEditor();
			
			TaggableStyledText taggableStyledText  = provider.getActiveStyledText();
			Point selectionRange = taggableStyledText.getSelectionRange();
			
			TagNode tag = TagManager.getInstance().getTagByUID(Integer.parseInt(tagUIDString));
			
			
			DocumentRegistry.getInstance().removeCodeFromRange(provider.getActiveTextNode(), tag, selectionRange.x + taggableStyledText.getCharacterOffset(), selectionRange.y);
			//FIXME DocumentRegistry: removeTagFromRange 
//			REGISTRY.removeTagFromRange(provider.getActiveTextNode(), tag, selectionRange.x + taggableStyledText.getCharacterOffset(), selectionRange.y);
		}
		return null;	
	}
}
