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
package net.sf.freeqda.view.projectview;

import java.util.LinkedList;

import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.common.projectmanager.TextNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class TextTreeContentProvider implements ITreeContentProvider {

	private static final String EXCEPTION_INVALID_OBJECT_GET_PARENT = Messages.TextTreeContentProvider_InvalidObjectGetParent;
	private static final String EXCEPTION_INVALID_OBJECT_GET_ELEMENTS = Messages.TextTreeContentProvider_InvalidObjectGetElements;

	public static boolean SHOW_CATEGORY_AND_TEXT = true;
	public static boolean SHOW_CATEGORY = false;
	
	private boolean showCategoryAndText;
	
	public TextTreeContentProvider(boolean showCategoryAndText) {
		this.showCategoryAndText = showCategoryAndText;
	}
	
	@Override
	public Object[] getChildren(Object arg0) {
		TextCategoryNode parentNode = (TextCategoryNode) arg0;
		LinkedList<Object> res = new LinkedList<Object>();
		res.addAll(parentNode.getChildren());
		if (showCategoryAndText) {
			res.addAll(parentNode.getTextFileDescriptors());
		}
		return res.toArray(); 
	}

	@Override
	public Object getParent(Object arg0) {
		if (arg0 instanceof TextCategoryNode) {
			return ((TextCategoryNode) arg0).getParentCategory(); 
		}
		else if (showCategoryAndText && arg0 instanceof TextNode) {
			TextNode tf = (TextNode) arg0;
			return tf.getCategory();
		}
		else {
			throw new IllegalArgumentException(EXCEPTION_INVALID_OBJECT_GET_PARENT);
		}
	}

	@Override
	public boolean hasChildren(Object arg0) {
		if (arg0 instanceof TextCategoryNode) {
			TextCategoryNode cn = (TextCategoryNode) arg0; 
			if (showCategoryAndText) {
				return cn.getChildren().size() + cn.getTextFileDescriptors().size() > 0; 			
			}
			else {
				return cn.getChildren().size() > 0; 			
			}
		}
		return false;
	}

	@Override
	public Object[] getElements(Object arg0) {
		if (arg0 instanceof TextCategoryNode) {
			return getChildren(arg0);
		}
		else if (arg0 instanceof ProjectManager) {
			ProjectManager mgr = (ProjectManager) arg0;
			return new TextCategoryNode[] { mgr.getRootCategory() }; 
		}
		else {
			throw new IllegalArgumentException(EXCEPTION_INVALID_OBJECT_GET_ELEMENTS);
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
