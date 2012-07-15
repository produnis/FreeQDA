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
package net.sf.freeqda.common.tagregistry;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;

import net.sf.freeqda.common.GenericTreeNode;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.tagregistry.events.TagActivatedEvent;
import net.sf.freeqda.common.tagregistry.events.TagNodeCreatedEvent;
import net.sf.freeqda.common.tagregistry.events.TagNodeDeletedEvent;
import net.sf.freeqda.common.widget.ITagModificationListener;

import org.eclipse.swt.graphics.RGB;


public class TagManager implements ITagModificationListener {

	/**
	 * Stores the (singleton) instance to this class
	 */
	private static TagManager SINGLETON_INSTANCE;

	private static final ProjectManager PROJECT_MANAGER = ProjectManager.getInstance();

	private static final String EXCEPTION_NULL_NODE = Messages.TagManager_ExceptionNullNode;
//	private static final String ROOT_TAG_NAME = Messages.TagManager_RootTagName;
	
	/**
	 * The tag id of the internal root node
	 */
	protected static final int ROOT_TAG_ID = 0;

	private LinkedList<ITagModificationListener> tagModifiactionListenerList;

	/**
	 * Returns the (singleton) instance of the TagRegistry class
	 * @return the (singleton) instance of the TagRegistry class
	 */
	public static final TagManager getInstance() {
		if (SINGLETON_INSTANCE==null) {
			synchronized (TagManager.class) {
				if (SINGLETON_INSTANCE==null) {
					SINGLETON_INSTANCE = new TagManager();
				}
			}
		}
		return SINGLETON_INSTANCE;
	}

	public void registerListener(ITagModificationListener modificationListener) {
		if (! tagModifiactionListenerList.contains(modificationListener)) {
			tagModifiactionListenerList.add(modificationListener);
		}
	}

	public void deregisterListener(ITagModificationListener modificationListener) {
		tagModifiactionListenerList.remove(modificationListener);
	}
	
	/**
	 * Stores the highest TagNode UID that is used in a project
	 */
	private int highestTagUID;
	
	private TagNode activeTag;

	/**
	 * Internal Root node for the tag tree. 
	 */
	private TagNode rootTag;
	
	/**
	 * Contains all known tags.  
	 */
	private HashMap<Integer, TagNode> lookupMapTag;
	
	/**
	 * Initializes the TagRegistry
	 */
	private TagManager() {
		reset();
		 tagModifiactionListenerList = new LinkedList<ITagModificationListener>();
	}

	/**
	 * Creates a new TagNode object with default values for name and color.
	 * This method is synchronized in order to prevent two threads from creating 
	 * a TagNode with the same UID.
	 * The created TagNode is also stored in the TAG_LOOKUP_MAP.
	 * 
	 * @return a new instance of TagNode
	 */
	public synchronized TagNode createTagNode() {
		highestTagUID++;
		TagNode res = new TagNode(MessageFormat.format(Messages.TagManager_DefaultTagName, new Object[] {highestTagUID}), new RGB(255,255,255), highestTagUID); 
		lookupMapTag.put(highestTagUID, res);
		return  res;
	}

	public void fireTagNodeDeletedEvent(TagNodeDeletedEvent deletedEvent) {
		for (ITagModificationListener listener: tagModifiactionListenerList) {
			listener.NodeDeleted(deletedEvent);
		}
	}

	public void fireTagNodeCreatedEvent(TagNodeCreatedEvent createdEvent) {
		NodeCreated(createdEvent);
		for (ITagModificationListener listener: tagModifiactionListenerList) {
			listener.NodeCreated(createdEvent);
		}
	}

	private void fireTagActivatedEvent(TagActivatedEvent activatedEvent) {
		for (ITagModificationListener listener: tagModifiactionListenerList) {
			listener.TagActivated(activatedEvent);
		}
	}




	public TagNode getActiveTag() {
		return activeTag;
	}

	protected int getHighestTagUID() {
		return highestTagUID;
	}
	
	public HashMap<Integer, TagNode> getLookupMapTag() {
		return lookupMapTag;
	}

	/**
	 * Returns the root tag of the tag tree. 
	 */
	public TagNode getRootTag() {
		return rootTag;
	}
	
	public TagNode getTagByUID(int uid) {
		TagNode res = null;
		res = this.lookupMapTag.get(uid);
		return res;
	}
	
	/**
	 * Inserts a TagNode into the TagTree.
	 *  
	 * @param toAdd
	 * @param parentNode
	 */
	public synchronized void insert(TagNode toAdd, TagNode parentNode) {

		/*
		 * Check contracts: The node to insert must not be null and its UID must be managed by this class
		 */
		if (toAdd == null)  {
			throw new NullPointerException(EXCEPTION_NULL_NODE);
		}
		if (!lookupMapTag.containsKey(toAdd.getUID())) {
			throw new IllegalArgumentException(MessageFormat.format(Messages.TagManager_ExceptionTagNotManaged, new Object[] {toAdd}));
		}

		LinkedList<GenericTreeNode<TagNode>> listToAddTo = null;
		if (parentNode == null) listToAddTo = rootTag.getChildren();
		else listToAddTo = parentNode.getChildren();
		
		if (! listToAddTo.contains(toAdd)) {
			listToAddTo.add(toAdd);
			toAdd.setParentNode(parentNode);
		}
		
		/*
		 * save the changes to disk
		 */
		PROJECT_MANAGER.save();
	}

//	public void registerDocumentSelectionManipulateTagsListener(TextNode text, DocumentSelectionManipulateTagsListener listener) {
//		REGISTRY.registerDocumentSelectionManipulateTagsListener(text, listener);
//	}
//
//	public void removeDocumentSelectionManipulateTagsListener(TextNode text, DocumentSelectionManipulateTagsListener listener) {
//		REGISTRY.removeDocumentSelectionManipulateTagsListener(text, listener);
//	}

	/**
	 * Removes a TagNode from the TagTree. 
	 * The created TagNode is also removed from the TAG_LOOKUP_MAP.
	 * @param toRemove
	 */
	public synchronized void remove(TagNode toRemove) {
		/*
		 * Check contracts: The node to remove must not be null and its UID must be managed by this class
		 */
		if (toRemove == null)  {
			throw new NullPointerException(EXCEPTION_NULL_NODE);
		}
		if (!lookupMapTag.containsKey(toRemove.getUID())) {
			throw new IllegalArgumentException(MessageFormat.format(Messages.TagManager_ExceptionTagNotManaged, new Object[] {toRemove}));
		}
		
		LinkedList<GenericTreeNode<TagNode>> listToRemoveFrom = null;
		if (toRemove.getParentCategory() == null) listToRemoveFrom = rootTag.getChildren();
		else listToRemoveFrom = toRemove.getParentCategory().getChildren();
		listToRemoveFrom.remove(toRemove);
		
		lookupMapTag.remove(toRemove);
		
		/*
		 * save the changes to disk
		 */
		PROJECT_MANAGER.save();
		
		/*
		 * 
		 */
		fireTagNodeDeletedEvent(new TagNodeDeletedEvent(this, toRemove));
	}

	public void reset() {

		highestTagUID = ROOT_TAG_ID;
		lookupMapTag = new HashMap<Integer, TagNode>();
		
		tagModifiactionListenerList = new LinkedList<ITagModificationListener>();
		setRootTag(new TagNode(Messages.TagManager_RootTagName, new RGB(255,255,255), ROOT_TAG_ID));
	}

	public void setActiveTag(TagNode activeTag) {
		this.activeTag = activeTag;
		fireTagActivatedEvent(new TagActivatedEvent(this, activeTag));
	}

	public void setLookupMapTag(HashMap<Integer, TagNode> lookupMapTag) {
		this.lookupMapTag = lookupMapTag;
	}
	
	protected void setRootTag(TagNode rootTag) {
		this.rootTag = rootTag;
	}
	
	public void updateHighestTagUID(int uid) {
		if (highestTagUID < uid) {
			highestTagUID = uid;
		}
	}

	@Override
	public void NodeCreated(TagNodeCreatedEvent evt) {
		insert(evt.getCreatedTagNode(), evt.getParentTagNode());

		for (ITagModificationListener listener: tagModifiactionListenerList) {
			listener.NodeCreated(evt);
		}
		
		/*
		 * save the changes to disk
		 */
		PROJECT_MANAGER.save();
	}
	
	@Override
	public void NodeDeleted(TagNodeDeletedEvent evt) {
	}

	@Override
	public void TagActivated(TagActivatedEvent evt) {
	}
	
	public void updateCodeStats() {
//		for (TextNode textNode: documentDataMap.keySet()) {
//			textNode.updateCodeStats();
//		}
	
		for (TagNode tagNode: getLookupMapTag().values()) {
			tagNode.updateCodeStats();
		}
	}

}
