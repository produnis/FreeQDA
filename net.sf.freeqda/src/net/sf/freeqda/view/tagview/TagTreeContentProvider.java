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
package net.sf.freeqda.view.tagview;

import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class TagTreeContentProvider implements ITreeContentProvider {

	private static final String EXCEPTION_ILLEGAL_ARGUMENT_TEXT = Messages.TagTreeContentProvider_IllegalArgumentException0;

	@Override
	public Object[] getChildren(Object arg0) {
		return ((TagNode) arg0).getChildren().toArray(); 
	}

	@Override
	public Object getParent(Object arg0) {
		return ((TagNode) arg0).getParentCategory(); 
	}

	@Override
	public boolean hasChildren(Object arg0) {
		if (arg0 instanceof TagNode) {
			return ((TagNode) arg0).getChildren().size() > 0; 			
		}
		return false;
	}

	@Override
	public Object[] getElements(Object arg0) {
		if (arg0 instanceof TagNode) {
			TagNode tag = (TagNode) arg0;
			return tag.getChildren().toArray(); 
		}
		else if (arg0 instanceof TagManager) {
			TagManager registry = (TagManager) arg0;
			return new TagNode[] { registry.getRootTag() }; 
		}
		else {
			throw new IllegalArgumentException(EXCEPTION_ILLEGAL_ARGUMENT_TEXT);
		}
	}

	@Override
	public void dispose() {
		// TODO implement dispose
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO implement inputChanged
	}
}
