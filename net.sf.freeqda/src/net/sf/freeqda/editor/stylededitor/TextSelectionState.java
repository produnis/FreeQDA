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
package net.sf.freeqda.editor.stylededitor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class TextSelectionState extends AbstractSourceProvider {

	public static final String SOURCE_VARIABLE = "net.sf.freeqda.stylededitor.textSelected"; //$NON-NLS-1$

	private static final String SELECTED = "ENABLED"; //$NON-NLS-1$
	private static final String DESELECTED = "DISABLED"; //$NON-NLS-1$
	
	private boolean isSelected;
	
	public TextSelectionState() {
		isSelected = false;
	}

	@Override
	public void dispose() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map getCurrentState() {
		Map map = new HashMap(1);
		map.put(SOURCE_VARIABLE, isSelected ? SELECTED : DESELECTED);
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SOURCE_VARIABLE }; 
	}

	public void setTextSelected(boolean isSelected) {
		this.isSelected = isSelected;
		fireSourceChanged(ISources.WORKBENCH, SOURCE_VARIABLE, isSelected ? SELECTED : DESELECTED);
	}
	
	public boolean isTextSelected() {
		return isSelected;
	}
}
