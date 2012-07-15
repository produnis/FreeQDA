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
package net.sf.freeqda.common.handler;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.sf.freeqda.common.handler.messages"; //$NON-NLS-1$
	public static String LoadProjectHandler_DialogLoadCanceled_ButtonConfirm;
	public static String LoadProjectHandler_DialogLoadCanceled_Message;
	public static String LoadProjectHandler_DialogLoadCanceled_Title;
	public static String LoadProjectHandler_DialogProjectLoaded_ButtonConfirm;
	public static String LoadProjectHandler_DialogProjectLoaded_Message;
	public static String LoadProjectHandler_DialogProjectLoaded_Title;
	public static String LoadProjectHandler_DialogProjectLoadingFailed_ButtonConfirm;
	public static String LoadProjectHandler_DialogProjectLoadingFailed_Message;
	public static String LoadProjectHandler_DialogProjectLoadingFailed_Title;
	public static String LoadProjectHandler_DialogText;
	public static String LoadProjectHandler_Filter_AllFiles;
	public static String LoadProjectHandler_Filter_FQDAProjects;
	public static String PrintCurrentHandler_FooterAdditionPage;
	public static String PrintCurrentHandler_HeaderAddition;
	public static String SaveProjectHandler_SaveProjectFailed_ButtonConfirm;
	public static String SaveProjectHandler_SaveProjectFailed_Message;
	public static String SaveProjectHandler_SaveProjectFailed_Title;
	public static String SaveProjectHandler_SaveProjectSuccess_ButtonConfirm;
	public static String SaveProjectHandler_SaveProjectSuccess_Message;
	public static String SaveProjectHandler_SaveProjectSuccess_Title;
	public static String SaveProjectHandler_SaveProjectUnhandled_ButtonConfirm;
	public static String SaveProjectHandler_SaveProjectUnhandled_Message;
	public static String SaveProjectHandler_SaveProjectUnhandled_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
