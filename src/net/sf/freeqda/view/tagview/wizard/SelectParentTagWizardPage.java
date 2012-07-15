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
package net.sf.freeqda.view.tagview.wizard;

import net.sf.freeqda.common.GenericTreeNode;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.view.tagview.TagTreeContentProvider;
import net.sf.freeqda.view.tagview.TagTreeLabelProvider;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;


public class SelectParentTagWizardPage extends WizardPage {

	private Composite container;
	private TreeViewer treeViewerCategory;
	
	private GenericTreeNode<TagNode> parentTag;
	private boolean showParentSelection;
	
	private SelectParentTagWizardPage(String pageName) {
		super(pageName);
	}
	
	public SelectParentTagWizardPage(String pageName, String title, String description, GenericTreeNode<TagNode> parentTag, boolean showParentSelection) {
		this(pageName);
		setTitle(title);
		setDescription(description);
		if (parentTag== null) this.parentTag = TagManager.getInstance().getRootTag();
		else this.parentTag = parentTag;
		this.showParentSelection = showParentSelection;
	}

	
	@Override
	public void createControl(Composite parent) {
		
		container = new Composite(parent, SWT.NONE);

		GridLayout thisLayout = new GridLayout();
		thisLayout.makeColumnsEqualWidth = true;
		container.setLayout(thisLayout);
		{
			GridData treeViewerCategoryLData = new GridData();
			treeViewerCategoryLData.grabExcessHorizontalSpace = true;
			treeViewerCategoryLData.grabExcessVerticalSpace = true;
			treeViewerCategoryLData.horizontalAlignment = GridData.FILL;
			treeViewerCategoryLData.verticalAlignment = GridData.FILL;
			treeViewerCategory = new TreeViewer(container, SWT.BORDER);
			treeViewerCategory.getControl().setLayoutData(treeViewerCategoryLData);
			treeViewerCategory.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {

					ISelection selection = event.getSelection();
					if (selection != null && selection instanceof IStructuredSelection) {
						Object obj = ((IStructuredSelection) selection).getFirstElement();

						if ((obj != null) && (obj instanceof TagNode)) {
							setPageComplete(true);
						}
					}
				}
			});
			
		}
		
		ILabelDecorator documentDecorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		treeViewerCategory.setContentProvider(new TagTreeContentProvider());
		treeViewerCategory.setLabelProvider(new DecoratingLabelProvider(new TagTreeLabelProvider(), documentDecorator));
		
		Tree tree = treeViewerCategory.getTree();
		tree.setLinesVisible(true);
		tree.setBackground(new Color(container.getDisplay(), 255, 255, 255));

		treeViewerCategory.setInput(TagManager.getInstance());
		treeViewerCategory.expandAll();
		treeViewerCategory.setSelection(new StructuredSelection(parentTag));
		
		//FIXME implement a proper "parent tag editing"
		treeViewerCategory.getControl().setEnabled(showParentSelection);
		
		container.layout();
		
		setControl(container);
		setPageComplete(true);
	}	
	
	public TagNode getParentTag() {
		ISelection selection = treeViewerCategory.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			if ((obj != null) && (obj instanceof TagNode)) {
				return (TagNode) obj;
			}
		}
		return TagManager.getInstance().getRootTag(); 
	}
}
