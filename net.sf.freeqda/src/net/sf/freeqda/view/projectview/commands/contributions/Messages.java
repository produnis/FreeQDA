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
package net.sf.freeqda.view.projectview.commands.contributions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.freeqda.view.projectview.commands.contributions.messages"; //$NON-NLS-1$
	public static String DynamicProjectViewContributionItem_LabelCreateCategory;
	public static String DynamicProjectViewContributionItem_LabelCreateText;
	public static String DynamicProjectViewContributionItem_LabelDeleteCategory;
	public static String DynamicProjectViewContributionItem_LabelDeleteText;
	public static String DynamicProjectViewContributionItem_LabelEditCategory;
	public static String DynamicProjectViewContributionItem_LabelEditText;
	public static String DynamicProjectViewContributionItem_LabelOpenText;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
