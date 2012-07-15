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

import net.sf.freeqda.common.projectmanager.ProjectDataModifiedListener;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.common.projectmanager.TextNode;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;


public class ProjectViewer extends ViewPart implements
		ProjectDataModifiedListener {

	public static final String ID = "net.sf.freeqda.projectview.ProjectViewer"; //$NON-NLS-1$
	private static final String EDITOR_COMMAND_ID = "net.sf.freeqda.common.callEditor"; //$NON-NLS-1$

	private static final ProjectManager PROJECT_MANAGER = ProjectManager
			.getInstance();

	private TreeViewer treeViewer;
	private Composite parentComposite;
	
	public ProjectViewer() {
		ProjectManager.registerProjectModifiedListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = parent;
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		ILabelDecorator documentDecorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();

		treeViewer = new TreeViewer(parent, SWT.NONE);
		treeViewer.setContentProvider(new TextTreeContentProvider(TextTreeContentProvider.SHOW_CATEGORY_AND_TEXT));
		treeViewer.setLabelProvider(new DecoratingLabelProvider(new TextTreeLabelProvider(), documentDecorator));
		
		Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setBackground(new Color(parent.getDisplay(), 255, 255, 255));

		/*
		 *  First we create a menu Manager
		 */
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(treeViewer.getTree());

		/*
		 *  Set the MenuManager
		 */
		treeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, treeViewer);

		treeViewer.setInput(PROJECT_MANAGER);
		treeViewer.expandAll();

		/*
		 *  Make the selection available
		 */
		getSite().setSelectionProvider(treeViewer);

		hookDoubleClickCommand();
		
		parent.setEnabled(false);
	}
	
	private void hookDoubleClickCommand() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openSelectedTextInEditor();
			}
		});
	}

	public void openSelectedTextInEditor() {

		/*
		 * If we have a selected TextNode then open the editor
		 */
		ISelection selection = getSite().getSelectionProvider().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if ((obj != null) && (obj instanceof TextNode)) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(EDITOR_COMMAND_ID, null);
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new RuntimeException(EXCEPTION_INVALID_OBJECT);
				}
			}
		}
	}

	private static final String EXCEPTION_INVALID_OBJECT = Messages.ProjectViewer_InvalidObject;
	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	@Override
	public void ProjectDataModified() {
		refresh(null);
		parentComposite.setEnabled(PROJECT_MANAGER.isActive());
	}
	
	public void refresh(TextCategoryNode rootNode) {
		//FIXME save changes on refresh?
		if (rootNode == null) {
			treeViewer.refresh();
			treeViewer.expandAll();
		}
		else {
			treeViewer.refresh(rootNode);
			treeViewer.expandToLevel(rootNode, TreeViewer.ALL_LEVELS);
		}
	}
}
