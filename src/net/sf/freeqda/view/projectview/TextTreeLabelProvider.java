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

import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.common.projectmanager.TextNode;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class TextTreeLabelProvider implements ILabelProvider {

	private static final Image DIRECTORY_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

	private static final Image TEXT_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	
	private static final String EXCEPTION_INVALID_OBJECT = Messages.TextTreeLabelProvider_InvalidObject;
	
	@Override
	public Image getImage(Object arg0) {
		if (arg0 instanceof TextCategoryNode) return DIRECTORY_IMAGE; 
		else if (arg0 instanceof TextNode) return TEXT_IMAGE;
		else throw new IllegalArgumentException(EXCEPTION_INVALID_OBJECT);
	}

	@Override
	public String getText(Object arg0) {
		if (arg0 instanceof TextCategoryNode) return ((TextCategoryNode) arg0).getName();
		else if (arg0 instanceof TextNode) return ((TextNode) arg0).getName();
		else throw new IllegalArgumentException(EXCEPTION_INVALID_OBJECT);
	}

	@Override
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
	}
}
