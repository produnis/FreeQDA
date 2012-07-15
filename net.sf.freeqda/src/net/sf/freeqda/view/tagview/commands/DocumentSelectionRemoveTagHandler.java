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
package net.sf.freeqda.view.tagview.commands;

import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.registry.DocumentRegistry;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.ITaggableStyledTextProvider;
import net.sf.freeqda.common.widget.TaggableStyledText;
import net.sf.freeqda.view.tagview.TagViewer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class DocumentSelectionRemoveTagHandler extends AbstractHandler {

//	private static final TagRegistry REGISTRY = TagRegistry.getInstance();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/* 
		 * Get the current editor
		 */
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();

		TagViewer view = (TagViewer) page.findView(TagViewer.ID);
		IEditorPart editorPart = page.getActiveEditor();
		
		/*
		 *  Get the selection
		 */
		ISelection selection = view.getSite().getSelectionProvider().getSelection();
		if (selection != null && selection instanceof IStructuredSelection && editorPart instanceof ITaggableStyledTextProvider) {

			Object obj = ((IStructuredSelection) selection).getFirstElement();
			ITaggableStyledTextProvider provider = (ITaggableStyledTextProvider) editorPart;
			
			if ((obj != null) && (obj instanceof TagNode)) {
				
				TagNode tag = (TagNode) obj;
				
				TextNode textNode = provider.getActiveTextNode();
				TaggableStyledText styledText = provider.getActiveStyledText(); 
				Point selectionRange = styledText.getSelectionRange();
//				FQDADocumentType document = (FQDADocumentType) provider.getActiveDocument();
				//FIXME DocumentRegistry: removeTagFromRange! 
				DocumentRegistry.getInstance().removeCodeFromRange(textNode, tag, selectionRange.x + styledText.getCharacterOffset(), selectionRange.y);
//				REGISTRY.removeTagFromRange(textNode, tag, selectionRange.x + styledText.getCharacterOffset(), selectionRange.y);
			}
		}
		return null;	
	}
}
