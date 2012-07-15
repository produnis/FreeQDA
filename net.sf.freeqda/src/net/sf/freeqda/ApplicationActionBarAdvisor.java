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
package net.sf.freeqda;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

//    private IWorkbenchAction exitAction;
//    private IWorkbenchAction aboutAction;
//    private IWorkbenchAction showHelpAction; 
//    private IWorkbenchAction searchHelpAction;
//    private IWorkbenchAction dynamicHelpAction;
    
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

//	 protected void makeActions(final IWorkbenchWindow window) {
//		    exitAction = ActionFactory.QUIT.create(window);
//		    register(exitAction);
//
//		    aboutAction = ActionFactory.ABOUT.create(window);
//		    register(aboutAction);
//
//		    showHelpAction = ActionFactory.HELP_CONTENTS.create(window); // NEW
//		    register(showHelpAction); // NEW

//		    searchHelpAction = ActionFactory.HELP_SEARCH.create(window); 
//		    register(searchHelpAction); 
//
//		    dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window); 
//		    register(dynamicHelpAction); 
//	 }
	 
	 protected void fillMenuBar(IMenuManager menuBar) {
//		   MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
///		   MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);

//		   menuBar.add(fileMenu);
		   // Add a group marker indicating where action set menus will appear.
//		   menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
//		   menuBar.add(helpMenu);
//
//		   // File
//		   fileMenu.add(newWindowAction);
//		   fileMenu.add(new Separator());
//		   fileMenu.add(messagePopupAction);
//		   fileMenu.add(openViewAction);
//		   fileMenu.add(new Separator());
//		   fileMenu.add(exitAction);

		   // Help
///	        helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
///	        helpMenu.add(new Separator());
	        
//	        helpMenu.add();
//	        org.eclipse.equinox.p2.ui.ProvisioningUI.getDefaultUI().
//		   helpMenu.add(aboutAction);
//		   helpMenu.add(showHelpAction); 
//		   helpMenu.add(searchHelpAction); 
//		   helpMenu.add(dynamicHelpAction); 
	 }
}
