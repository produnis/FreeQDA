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
package net.sf.freeqda.editor.tagoverview;

import java.text.MessageFormat;

import net.sf.freeqda.common.tagregistry.TagNode;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class TaggedPassagesEditorInput implements IEditorInput {

	private TagNode selectedTag;
	
	public TaggedPassagesEditorInput(TagNode tag) {
		this.selectedTag = tag;
	}
	
	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return MessageFormat.format(Messages.TaggedPassagesEditorInput_Name, new Object[] {selectedTag.getName()});
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	public TagNode getSelectedTag() {
		return selectedTag;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof TaggedPassagesEditorInput)) {
			TaggedPassagesEditorInput input = (TaggedPassagesEditorInput) obj;
			return selectedTag.getUID() == input.selectedTag.getUID();
		}
		return false;
	}
	
	
}
