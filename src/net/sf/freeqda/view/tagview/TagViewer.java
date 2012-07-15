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

import net.sf.freeqda.common.projectmanager.ProjectDataModifiedListener;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.tagregistry.events.TagActivatedEvent;
import net.sf.freeqda.common.tagregistry.events.TagNodeCreatedEvent;
import net.sf.freeqda.common.tagregistry.events.TagNodeDeletedEvent;
import net.sf.freeqda.common.widget.ITagModificationListener;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class TagViewer extends ViewPart implements ProjectDataModifiedListener {

	public static final String ID = "net.sf.freeqda.tagview.TagViewer"; //$NON-NLS-1$

	private static final ProjectManager PROJECT_MANAGER = ProjectManager.getInstance();
	
	private static final TagManager TAG_MANAGER = TagManager.getInstance();
	
	private TreeViewer treeViewer;
	private Composite parentComposite;
	
	public TagViewer() {
		ProjectManager.registerProjectModifiedListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		
		parentComposite = parent;
		
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		parent.setLayout(fillLayout);
		
		/*
		 * Initialize the tree viewer
		 */
		ILabelDecorator tagNodeDecorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();

		treeViewer = new TreeViewer(parent, SWT.NONE);
		treeViewer.setContentProvider(new TagTreeContentProvider());
		treeViewer.setLabelProvider(new DecoratingLabelProvider(new TagTreeLabelProvider(), tagNodeDecorator));

		/*
		 *  Make the selection available to the workbench
		 */
		getSite().setSelectionProvider(treeViewer);
		
		Tree tree = treeViewer.getTree();
		tree.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		tree.setLinesVisible(true);
		
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

		treeViewer.setInput(TAG_MANAGER);
		treeViewer.expandAll();
		
		TAG_MANAGER.registerListener(new ITagModificationListener() {
			
			@Override
			public void TagActivated(TagActivatedEvent evt) {
			}
			
			@Override
			public void NodeDeleted(TagNodeDeletedEvent evt) {
			}
			
			@Override
			public void NodeCreated(TagNodeCreatedEvent evt) {
				/*
				 * Update the view
				 */
				if (evt.getParentTagNode() != null) treeViewer.refresh(evt.getParentTagNode());
				else treeViewer.refresh();
			}
		});
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				/*
				 *  Get the selection
				 */
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();

					if ((obj != null) && (obj instanceof TagNode)) {
						TagNode tag = (TagNode) obj;
						if (tag.getUID() != 0) {
							
							TAG_MANAGER.setActiveTag(tag);
							
//							IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//							IWorkbenchPage page = window.getActivePage();
//
//							IEditorPart editorPart = page.getActiveEditor();
//							if (editorPart instanceof ITaggableStyledText) {
//								ITaggableStyledText editor = (ITaggableStyledText) editorPart;
//								
//								/*
//								 * update all StyleRanges to show the selected tag
//								 */
//								if (editor != null)	TAG_MANAGER.setActiveTag(tag);
//							}
						}
					}
				}
			}
		});
		parent.setEnabled(false);
	}

	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
	
	@Override
	public void ProjectDataModified() {
		refresh(null);
//		treeViewer.refresh();
//		treeViewer.expandAll();
		parentComposite.setEnabled(PROJECT_MANAGER.isActive());
	}
	
	public void refresh(TagNode rootNode) {
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
