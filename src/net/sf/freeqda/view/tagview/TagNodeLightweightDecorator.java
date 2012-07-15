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

import java.util.LinkedList;
import java.util.List;

import net.sf.freeqda.common.tagregistry.TagNode;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;


public class TagNodeLightweightDecorator implements ILightweightLabelDecorator {

	private static final String COUNTER_PREFIX = " ("; //$NON-NLS-1$
	private static final String COUNTER_POSTFIX = ")"; //$NON-NLS-1$
	
	private final List<ILabelProviderListener> listenerList = new LinkedList<ILabelProviderListener>();
	
	@Override
	public void decorate(Object element, IDecoration decoration) {
		TagNode tag = (TagNode) element;
		
		if (tag.getOccuranceCounter() >= 0) 	
			decoration.addSuffix(COUNTER_PREFIX + tag.getOccuranceCounter() + COUNTER_POSTFIX);
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		listenerList.remove(listener);
	}

}
