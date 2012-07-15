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
package net.sf.freeqda.common.projectmanager;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.sf.freeqda.common.GenericTreeNode;
import net.sf.freeqda.common.JAXBUtils;
import net.sf.freeqda.common.tagregistry.TagManager;


public class ProjectManager implements TextCategoryNodeCreatedListener, TextNodeCreatedListener {

	private static final String TEXT_CATEGORY_ROOT_NAME = Messages.ProjectManager_TextCategoryRootName;
	private static final String EXCEPTION_WORKSPACE_UNINITIALIZED = Messages.ProjectManager_ExceptionWorkspaceUninitialized;
	private static final String EXCEPTION_WORKSPACE_INITIALIIZATION_FAILED = Messages.ProjectManager_ExceptionWorkspaceInitializationFailed;
	private static final String WORKSPACE_DEFAULT_NAME = "FQDAWorkspace"; //$NON-NLS-1$
	private static final String EXCEPTION_NODE_IS_NULL = Messages.ProjectManager_ExceptionNodeIsNull;
	private static final String TRASH_DIRECTORY_NAME = "Trash"; //$NON-NLS-1$
	
	public static final String FQDA_FILE_COMMON_NAME = "file_"; //$NON-NLS-1$
	public static final String FQDA_FILE_COMMON_SUFFIX = ".fqf"; //$NON-NLS-1$
	public static final String FQDA_PROJECT_FILE_NAME = "project.fqd"; //$NON-NLS-1$

	/**
	 * Stores the (singleton) instance to this class
	 */
	private static ProjectManager SINGLETON_INSTANCE;

	/**
	 * Holds the reference to the DocumentManager class
	 */
//	private static final DocumentRegistry DOCUMENT_MANAGER = DocumentRegistry.getInstance();

	/**
	 * The category id of the internal root node
	 */
	protected static final int ROOT_CATEGORY_ID = 0;

	/**
	 * The text id of the internal root node
	 */
	protected static final int ROOT_TEXT_ID = 0;

	private static final LinkedList<ProjectDataModifiedListener> listeners = new LinkedList<ProjectDataModifiedListener>();
	
	public static void registerProjectModifiedListener(ProjectDataModifiedListener listener) {
		listeners.add(listener);
	}
	
	public static void removeProjectLoadedListener(ProjectDataModifiedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Stores the location of the workspace directory
	 */
	private File workspaceDirectory;
	
	/**
	 * Stores the highest CategoryNode UID that is used in a project
	 */
	private int highestCategoryUID;

	/**
	 * Stores the highest TextNode UID that is used in a project
	 */
	private int highestTextUID;

	private File projectFile;
	
	private File projectTrash;

	private boolean isActive;
	
	/**
	 * Returns the (singleton) instance of the TagManager class
	 * @return the (singleton) instance of the TagManager class
	 */
	public static final ProjectManager getInstance() {
		if (SINGLETON_INSTANCE==null) {
			synchronized (ProjectManager.class) {
				if (SINGLETON_INSTANCE==null) {
					SINGLETON_INSTANCE = new ProjectManager();
				}
			}
		}
		return SINGLETON_INSTANCE;
	}	
	
	public void reset() {
		
		highestCategoryUID = ROOT_CATEGORY_ID;
		highestTextUID = ROOT_TEXT_ID;
		lookupMapCategory = new HashMap<Integer, TextCategoryNode>();
		lookupMapText = new HashMap<Integer, TextNode>();
		
		setRootCategory(new TextCategoryNode(TEXT_CATEGORY_ROOT_NAME, ROOT_CATEGORY_ID));
		setProjectName(null);

		try {
			workspaceDirectory = createWorkspaceDirectory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TagManager.getInstance().reset();
		setActive(false);
		
	}
	
	
	public File getWorkspaceDirectory() {
		if (workspaceDirectory != null) return workspaceDirectory;
		else throw new NullPointerException(EXCEPTION_WORKSPACE_UNINITIALIZED);
	}

	public File createWorkspaceDirectory() throws IOException {
		
		if (workspaceDirectory != null) return workspaceDirectory;
		
		//FIXME replace workspace retrieval and creation by something more flexible
		
		File workspaceDir1 = new File(System.getProperty("user.dir"), WORKSPACE_DEFAULT_NAME); //$NON-NLS-1$
		if (workspaceDir1.exists() && workspaceDir1.isDirectory() && workspaceDir1.canRead() && workspaceDir1.canExecute() && workspaceDir1.canWrite()) return workspaceDir1;

		File workspaceDir3 = new File(System.getProperty("user.home"), WORKSPACE_DEFAULT_NAME); //$NON-NLS-1$
		if (workspaceDir3.exists() && workspaceDir3.isDirectory() && workspaceDir3.canRead() && workspaceDir3.canExecute() && workspaceDir3.canWrite()) return workspaceDir3;

		if (workspaceDir1.mkdirs()) return workspaceDir1;

		if (workspaceDir3.mkdirs()) return workspaceDir3;

		throw new IOException(EXCEPTION_WORKSPACE_INITIALIIZATION_FAILED); 
	}

	protected int getHighestCategoryUID() {
		return highestCategoryUID;
	}

	protected int getHighestTextUID() {
		return highestTextUID;
	}





	/**
	 * Internal Root node for the category tree. 
	 */
	private TextCategoryNode rootCategory;

	/**
	 * Contains all known categories.  
	 */
	private HashMap<Integer, TextCategoryNode> lookupMapCategory;

	/**
	 * Contains all known categories.  
	 */
	private HashMap<Integer, TextNode> lookupMapText;

	public HashMap<Integer, TextCategoryNode> getLookupMapCategory() {
		return lookupMapCategory;
	}

	public void setLookupMapCategory(
			HashMap<Integer, TextCategoryNode> lookupMapCategory) {
		this.lookupMapCategory = lookupMapCategory;
	}

	public HashMap<Integer, TextNode> getLookupMapText() {
		return lookupMapText;
	}

	public void setLookupMapText(HashMap<Integer, TextNode> lookupMapText) {
		this.lookupMapText = lookupMapText;
	}

	protected void setRootCategory(TextCategoryNode rootCategory) {
		this.rootCategory = rootCategory;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void updateHighestCategoryUID(int uid) {
		if (highestCategoryUID < uid) {
			highestCategoryUID = uid;
		}
	}

	public void updateHighestTextUID(int uid) {
		if (highestTextUID < uid) {
			highestTextUID = uid;
		}
	}


	
	/**
	 * The project name
	 */
	private String projectName;
	
	/**
	 * Initializes the ProjectManager
	 */
	private ProjectManager() {
		reset();
	}

	/**
	 * Creates a new TextCategoryNode object with a default name.
	 * This method is synchronized in order to prevent two threads from creating 
	 * a TextCategoryNode with the same UID.
	 * The created TextCategoryNode is also stored in the CATEGORY_LOOKUP_MAP.
	 * 
	 * @return a new instance of TextCategoryNode
	 */
	public synchronized TextCategoryNode createCategoryNode() {
		highestCategoryUID++;
		TextCategoryNode res = new TextCategoryNode(Messages.ProjectManager_CategoryNodeDefaultName+highestCategoryUID, highestCategoryUID); 
		lookupMapCategory.put(highestCategoryUID, res);
		return  res;
	}
	
	/**
	 * Creates a new TextNode object with a default name.
	 * This method is synchronized in order to prevent two threads from creating 
	 * a TextNode with the same UID.
	 * The created TextNode is also stored in the TEXT_LOOKUP_MAP.
	 * 
	 * @return a new instance of TextNode
	 */
	public synchronized TextNode createTextNode() {
		highestTextUID++;
		TextNode res = new TextNode(MessageFormat.format(Messages.ProjectManager_TextNodeDefaultName, new Object[] {highestTextUID}), highestTextUID, false); 
		lookupMapText.put(highestTextUID, res);
		return  res;
	}
	
	/**
	 * Returns the root category of the category tree. 
	 */
	public TextCategoryNode getRootCategory() {
		return rootCategory;
	}
	
	/**
	 * Inserts a TextCategoryNode into the TextCategoryTree.
	 *  
	 * @param toAdd
	 * @param parentNode
	 */
	public synchronized void insert(TextCategoryNode toAdd, TextCategoryNode parentNode) {
		/*
		 * Check contracts: The node to insert must not be null and its UID must be managed by this class
		 */
		if (toAdd == null)  {
			throw new NullPointerException(EXCEPTION_NODE_IS_NULL);
		}
		if (!lookupMapCategory.containsKey(toAdd.getUID())) {
			throw new IllegalArgumentException(MessageFormat.format(Messages.ProjectManager_ExceptionUnmanagedCategory, new Object[] { toAdd }));
		}

		LinkedList<GenericTreeNode<TextCategoryNode>> listToAddTo = null;
		if (parentNode == null) listToAddTo = rootCategory.getChildren();
		else listToAddTo = parentNode.getChildren();
		
		if (! listToAddTo.contains(toAdd)) {
			listToAddTo.add(toAdd);
			toAdd.setParentNode(parentNode);
		}
		
		/*
		 * save the changes to disk
		 */
		save();
	}

	
	/**
	 * Inserts a TextNode into the TextCategoryTree.
	 *  
	 * @param toAdd
	 * @param parentNode
	 */
	public synchronized void insert(TextNode toAdd, TextCategoryNode parentNode) {
		/*
		 * Check contracts: The node to insert must not be null and its UID must be managed by this class
		 */
		if (toAdd == null)  {
			throw new NullPointerException(EXCEPTION_NODE_IS_NULL);
		}
		if (!lookupMapText.containsKey(toAdd.getUID())) {
			throw new IllegalArgumentException(MessageFormat.format(Messages.ProjectManager_ExceptionUnmanagedText, new Object[] { toAdd }));
		}

		TextCategoryNode nodeToAddTo = null;
		if (parentNode == null) nodeToAddTo = rootCategory;
		else nodeToAddTo = parentNode;
		
		List<TextNode> listToAddTo = nodeToAddTo.getTextFileDescriptors(); 
		if (! listToAddTo.contains(toAdd)) {
			listToAddTo.add(toAdd);
		}
		
		/*
		 * save the changes to disk
		 */
		save();
	}
	
	@Override
	public void NodeCreated(TextCategoryNodeCreatedEvent evt) {
		insert(evt.getCreatedNode(), evt.getParentNode());

		/*
		 * save the changes to disk
		 */
		save();
	}

	@Override
	public void NodeCreated(TextNodeCreatedEvent evt) {
		insert(evt.getCreatedNode(), evt.getParentNode());	

		/*
		 * save the changes to disk
		 */
		save();
	}
	
	/**
	 * Removes a TextCategoryNode from the TextCategoryTree. 
	 * The created TextCategoryNode is also removed from the CATEGORY_LOOKUP_MAP.
	 * @param toRemove
	 */
	public synchronized void remove(TextCategoryNode toRemove) {
		/*
		 * Check contracts: The node to remove must not be null and its UID must be managed by this class
		 */
		if (toRemove == null)  {
			throw new NullPointerException(EXCEPTION_NODE_IS_NULL);
		}
		if (!lookupMapCategory.containsKey(toRemove.getUID())) {
			throw new IllegalArgumentException(MessageFormat.format(Messages.ProjectManager_ExceptionUnmanagedCategory, new Object[] { toRemove }));
		}

		/*
		 * Delete all child categories and texts recursivly
		 */
		removeRecursivly(toRemove);
		
		/*
		 * save the changes to disk
		 */
		save();
	}

	private void removeRecursivly(TextCategoryNode toRemove) {
		/*
		 * remove all text files in this category
		 */
		TextNode[] textFiles = toRemove.getTextFileDescriptors().toArray(new TextNode[0]);
		for (int n=0; n<textFiles.length; n++) {
			remove(textFiles[n], toRemove);
		}

		/*
		 * remove all child categories
		 */
		Object[] childCategories = toRemove.getChildren().toArray();
		for (int n=0; n<childCategories.length; n++) {
			remove((TextCategoryNode) childCategories[n]); 
		}

		/*
		 * remove this category
		 */
		LinkedList<GenericTreeNode<TextCategoryNode>> listToRemoveFrom = null;
		if (toRemove.getParentCategory() == null) listToRemoveFrom = rootCategory.getChildren();
		else listToRemoveFrom = toRemove.getParentCategory().getChildren();
		listToRemoveFrom.remove(toRemove);
		
		lookupMapCategory.remove(toRemove);
	}

	/**
	 * Removes a TextNode from the TextCategoryTree. 
	 * The TextNode is also removed from the TEXT_LOOKUP_MAP.
	 * @param toRemove
	 */
	public synchronized void remove(TextNode toRemove, TextCategoryNode parentNode) {
		/*
		 * Check contracts: The node to remove must not be null and its UID must be managed by this class
		 */
		if (toRemove == null)  {
			throw new NullPointerException(EXCEPTION_NODE_IS_NULL);
		}
		if (!lookupMapText.containsKey(toRemove.getUID())) {
			throw new IllegalArgumentException(MessageFormat.format(Messages.ProjectManager_ExceptionUnmanagedText, new Object[] { toRemove }));
		}
		
		if (parentNode == null) parentNode = rootCategory;
		if (parentNode.getTextFileDescriptors().remove(toRemove)) {
			TextNode removedNode = lookupMapText.remove(toRemove.getUID());
			if (removedNode != null) {
				File fileToRemove = new File(projectFile.getParentFile(), MessageFormat.format(FQDA_FILE_COMMON_NAME+"%1"+FQDA_FILE_COMMON_SUFFIX, new Object[] {toRemove.getUID()})); //$NON-NLS-1$
				if (! fileToRemove.renameTo(new File(projectTrash, fileToRemove.getName()))) {
					//TODO what if the move fails?
				}
				
			}
		}

		/*
		 * save the changes to disk
		 */
		save();
	}

	public void toggleTextNodeActive(TextNode textNode) {
		if (textNode != null) {
			textNode.setActivated(! textNode.isActivated());
			save();
		}
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public File getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
		this.projectTrash = new File(projectFile.getParentFile(), TRASH_DIRECTORY_NAME);
		if (!projectTrash.exists()) {
			projectTrash.mkdir();
		}
	}
	
	public void save() {
		if (projectFile == null) return;
		try {
			JAXBUtils.saveProject(projectFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(File toLoad) throws IOException {
		
	    JAXBUtils.loadProject(toLoad);
	    setActive(true);

	}
	
//	
//	
//	public FQDADocumentType getFQDADocumentForTextNode(TextNode node) throws IOException {
//		File documentFile = new File(getProjectFile().getParentFile(), "file_"+node.getUID()+".fqf");
//		return JAXBUtils.loadDocument(documentFile);
//	}

	public boolean isActive() {
		return isActive;
	}
	
	private void setActive(boolean isActive) {
		this.isActive = isActive;
		/*
		 * Update views
		 */
		fireProjectDataModifiedEvent();
	}
	
	public void fireProjectDataModifiedEvent() {
		for (ProjectDataModifiedListener listener: listeners) {
			listener.ProjectDataModified();
		}		
		save();
	}
	
	public List<TextNode> getActiveTextNodes() {
		LinkedList<TextNode> res = new LinkedList<TextNode>();
		
		for (TextNode textNode: this.lookupMapText.values()) {
			if (textNode.isActivated()) res.add(textNode);
		}
		
		return res;
	}
}
