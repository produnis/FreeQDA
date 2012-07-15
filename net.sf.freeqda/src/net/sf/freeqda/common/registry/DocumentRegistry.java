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
package net.sf.freeqda.common.registry;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.tagregistry.events.DocumentSelectionAddTagEvent;
import net.sf.freeqda.common.tagregistry.events.DocumentSelectionManipulateTagsListener;
import net.sf.freeqda.common.widget.TagableStyleRange;



public class DocumentRegistry {
	 
	/**
	 * Stores the (singleton) instance to this class
	 */
	private static DocumentRegistry SINGLETON_INSTANCE;
	
	/**
	 * Returns the (singleton) instance of the DocumentRegistry class
	 * @return the (singleton) instance of the DocumentRegistry class
	 */
	public static final DocumentRegistry getInstance() {
		if (SINGLETON_INSTANCE==null) {
			synchronized (DocumentRegistry.class) {
				if (SINGLETON_INSTANCE==null) {
					SINGLETON_INSTANCE = new DocumentRegistry();
				}
			}
		}
		return SINGLETON_INSTANCE;
	}
	
	public static HashMap<TagNode, SimpleRangeList>  createTagRangeList(TextNode textNode) {
		
		HashMap<TagNode, SimpleRangeList> res = new HashMap<TagNode, SimpleRangeList>();
		
		for (TagableStyleRange styleRange: textNode.getNEWStyleRanges()) {

			SimpleRange range = new SimpleRange(styleRange.start, styleRange.length);
			
			if (styleRange.getTags() != null) {
				
				for (TagNode appliedTag: styleRange.getTags()) {
					
					SimpleRangeList tagList = null;
					if (res.containsKey(appliedTag)) {
						tagList = res.get(appliedTag);
					}
					else {
						tagList = new SimpleRangeList();
						res.put(appliedTag, tagList);
					}
					tagList.add(range);				
				}
			}
		}
		return res;
	}

	public static TagableStyleRange[] getTransposedStyleRanges(TextNode textNode, int sectionStart, int sectionStop /*, int charOffset */) {
		
		TagableStyleRange[] stylesInRange = getStylesInRange(textNode, sectionStart, sectionStop);
		
		for (TagableStyleRange range: stylesInRange) {
			range.start -= sectionStart;
		}
		return stylesInRange;
	}
	
	private static TagableStyleRange[] getStylesInRange(TextNode textNode, int sectionStart, int sectionStop) {
		
		LinkedList<TagableStyleRange> res = new LinkedList<TagableStyleRange>();
		
		if (textNode.getNEWStyleRanges() != null) {
			
			for (TagableStyleRange currentRange: textNode.getNEWStyleRanges()) {

				int currentRangeStop = currentRange.start + currentRange.length;
				
				if ((currentRange.start >= sectionStart) && (currentRangeStop <= sectionStop) && (currentRange.length > 0)) {
					res.add((TagableStyleRange)currentRange.clone());
				}
				else if (currentRange.start < sectionStart) {
					/*
					 * range starts before the section
					 */
					if (currentRangeStop >= sectionStart) {
						/*
						 * range ende after section start => overlap
						 */
						TagableStyleRange resultRange = (TagableStyleRange) currentRange.clone();
						if (currentRangeStop <= sectionStop) {
							/*
							 * range ends inside the section
							 */
							resultRange.start = sectionStart;
							resultRange.length = resultRange.length - sectionStart + currentRange.start; // stop at range.stop
						}
						else {
							/*
							 * range exceeds the section
							 */
							resultRange.start = sectionStart;
							resultRange.length = sectionStop - sectionStart; // stop at section stop
						}
						if (resultRange.length > 0) res.add(resultRange);
					}
				}
				else if (currentRangeStop > sectionStop) {
					/*
					 * range ends after the section
					 */
					if (currentRange.start <= sectionStop) {
						/*
						 * range starts before section end => overlap
						 */
						TagableStyleRange resultRange = (TagableStyleRange) currentRange.clone();
						if (currentRange.start >= sectionStart) {
							/*
							 * range starts inside the section 
							 */
							resultRange.start = currentRange.start;
							resultRange.length = currentRange.length - currentRangeStop + sectionStop; // stop at sectionStop
						}
						else {
							/*
							 * range exceeds the section
							 */
							resultRange.start = sectionStart;
							resultRange.length = sectionStop - sectionStart; // stop at section stop
						}
						if (resultRange.length > 0) res.add(resultRange);
					}
				}

			}
		}

		return res.toArray(new TagableStyleRange[0]);
	}
	
	/** 
	 * This map holds the modifications applied to each text node
	 */
	private HashMap<TextNode, DocumentData> documentDataMap;
	
	private final HashMap<TextNode, LinkedList<DocumentSelectionManipulateTagsListener>> tagAddedListener = new HashMap<TextNode, LinkedList<DocumentSelectionManipulateTagsListener>>();
	
	private DocumentRegistry() {
		documentDataMap = new HashMap<TextNode, DocumentData>();
	}
	
	public void init(List<TextNode> textNodeList) {

		cleanup();
		for (TextNode textNode: textNodeList) {
			documentDataMap.put(textNode, new DocumentData(textNode));
		}

		updateCodeStats();
		
		ProjectManager.getInstance().fireProjectDataModifiedEvent();
	}
	
	public void cleanup() {
		documentDataMap.clear();
		tagAddedListener.clear();
		ProjectManager.getInstance().fireProjectDataModifiedEvent();
	}
	
	public void registerDocumentSelectionManipulateTagsListener(TextNode textNode, DocumentSelectionManipulateTagsListener listener) {
		LinkedList<DocumentSelectionManipulateTagsListener> listenerList = tagAddedListener.get(textNode);
		if (listenerList == null) {
			listenerList = new LinkedList<DocumentSelectionManipulateTagsListener>();
			tagAddedListener.put(textNode, listenerList);
		}
		listenerList.add(listener);
	}

	public void removeDocumentSelectionManipulateTagsListener(TextNode textNode, DocumentSelectionManipulateTagsListener listener) {
		LinkedList<DocumentSelectionManipulateTagsListener> listenerList = tagAddedListener.get(textNode);
		if (listenerList == null) {
//			throw new NullPointerException("Should remove a listener for text node ("+textNode+") but the listener list is null!");
		}
		else {
			listenerList.remove(listener);
			if (listenerList.size() == 0) {
				tagAddedListener.keySet().remove(textNode);
			}
		}
	}

	public void addTagToRange(TextNode textNode, TagNode tag, int selectionStart, int selectionLength) {
		
		DocumentData documentData = documentDataMap.get(textNode);
		if (documentData == null) {
			documentData = new DocumentData(textNode);
			documentDataMap.put(textNode, documentData);
		}
		
		/*
		 * add the desired range to the list
		 */
		HashMap<TagNode,SimpleRangeList> codedRanges = documentData.getCodedRanges();
		SimpleRangeList tagRangeList = codedRanges.get(tag);
		if (tagRangeList == null) {
			tagRangeList = new SimpleRangeList();
			codedRanges.put(tag, tagRangeList);
		}
		tagRangeList.add(new SimpleRange(selectionStart, selectionLength));

		LinkedList<TagableStyleRange> res = new LinkedList<TagableStyleRange>();
		
		int processingStart = selectionStart;
		int selectionEnd = selectionStart + selectionLength;
		
		/*
		 */
		ArrayList<TagableStyleRange> workingRanges = documentData.getWorkingStyleRanges(); 
		
		for (TagableStyleRange range: workingRanges) {

			int rangeEnd = range.start + range.length - 1;
			
			/*
			 * check if the range is completed before the selection  
			 */
			if (rangeEnd < processingStart) {
				res.add(range);
			}
			/*
			 * check if the range is after the selection  
			 */
			else if (range.start > selectionEnd) {
				
				if (processingStart < selectionEnd) {
					/*
					 * there is a gap between the last range inside the selection and 
					 * the selection end that needs to be filled here
					 */
					TagableStyleRange fillRange = new TagableStyleRange(processingStart, selectionEnd-processingStart, new TagNode[] { tag });
					res.add(fillRange);
					processingStart = selectionEnd + 1;
				}
				
				res.add(range);
				//TODO add all remaining ranges and exit the loop
			}
			/*
			 * the range overlaps the selection 
			 */
			else {
				
				TagableStyleRange processedRange = (TagableStyleRange) range.clone();
				int processedRangeEnd = processedRange.start + processedRange.length;
				
				if (processedRange.start < selectionStart) {

					/*
					 * Split the range apart, add the first part unmodified and continue with the second one
					 */
					TagableStyleRange unmodifiedRange = (TagableStyleRange) range.clone();
					unmodifiedRange.length = selectionStart - processedRange.start;

					/*
					 * commit changes to the processed range
					 */
					processedRange.length -= unmodifiedRange.length;
					processedRange.start = selectionStart;
					
					res.add(unmodifiedRange);
				}
								
				/*
				 * check for a gap between processing start and range.start
				 */
				if (processingStart < processedRange.start) {
					/*
					 * fill the gap
					 */
					TagableStyleRange fillRange = new TagableStyleRange(processingStart, processedRange.start-processingStart, new TagNode[] { tag }); 
					res.add(fillRange);
					
					processingStart = processedRange.start + 1;
				}
				
				/*
				 * check if the (rest of the) range is completely inside the selection
				 */

				if (processedRangeEnd <= selectionEnd) {
					
					/*
					 * modify range and add it to result
					 */
					processedRange.addTag(tag);
					res.add(processedRange);
					processingStart = processedRangeEnd;
					
				}
				else {

					/*
					 * update the remaining part of the range 
					 */
					TagableStyleRange remainingRange = (TagableStyleRange) processedRange.clone();
					remainingRange.length = processedRangeEnd - selectionEnd;
					remainingRange.start = selectionEnd;

					/*
					 * modify range inside the selection and add it to result
					 */
					processedRange.length -= remainingRange.length;
					processedRange.addTag(tag);
					
					res.add(processedRange);
					res.add(remainingRange);

					processingStart = selectionEnd + 1;
				}
 			}
		}
		
		if (processingStart < selectionEnd) {
			/*
			 * there is a gap between the last range inside the selection and 
			 * the selection end that needs to be filled here
			 */
			TagableStyleRange fillRange = new TagableStyleRange(processingStart, selectionEnd-processingStart, new TagNode[] { tag });
			res.add(fillRange);
		}

		ArrayList<TagableStyleRange> resultArrayList = new ArrayList<TagableStyleRange>(res);
		documentData.setWorkingStyleRanges(resultArrayList);

		/*
		 * notify observer 
		 */
		fireDocumentSelectionManipulationEvent(textNode, resultArrayList, selectionStart, selectionLength);
		
		textNode.updateCodeStats();
		tag.updateCodeStats();
		
		ProjectManager.getInstance().fireProjectDataModifiedEvent();
	}

	public void removeCodeFromRange(TextNode textNode, TagNode code, int selectionStart, int selectionLength) {
		
		DocumentData documentData = documentDataMap.get(textNode);
		if (documentData == null) {
			documentData = new DocumentData(textNode);
			documentDataMap.put(textNode, documentData);
		}
		
		if (code != null) {
			/*
			 * remove the desired range from the list
			 */
			HashMap<TagNode,SimpleRangeList> codedRanges = documentData.getCodedRanges();
			SimpleRangeList tagRangeList = codedRanges.get(code);

			if (tagRangeList == null) {
				/*
				 * Nothing to do - return
				 */
				return;
			}

			tagRangeList.remove(new SimpleRange(selectionStart, selectionLength));	
		}
		else {
			/*
			 * remove all tags from the range
			 */
			HashMap<TagNode,SimpleRangeList> codedRanges = documentData.getCodedRanges();
			for (SimpleRangeList rangeList: codedRanges.values()) {
				rangeList.remove(new SimpleRange(selectionStart, selectionLength));
			}
		}
		
		LinkedList<TagableStyleRange> res = new LinkedList<TagableStyleRange>();
		
		int processingStart = selectionStart;
		int selectionEnd = selectionStart + selectionLength;
		
		/*
		 */
		for (TagableStyleRange range: documentData.getWorkingStyleRanges()) {

			int rangeEnd = range.start + range.length - 1;
			
			/*
			 * check if the range is completed before or the selection or starts after it  
			 */
			if ((rangeEnd < processingStart) || (range.start > selectionEnd)) {
				res.add(range);
			}
			/*
			 * Selection overlaps the range
			 */
			else {
				/*
				 * check if the range contains the code to remove 
				 */
				if ((code == null) || (range.containsTag(code))) {
					
					/*
					 * check if the front, end or both overlap with the selection
					 */
					TagableStyleRange processedRange = (TagableStyleRange) range.clone();
					int processedRangeEnd = processedRange.start + processedRange.length;
					
					if (processedRange.start < selectionStart) {

						/*
						 * Split the range apart, add the first part unmodified and continue with the second one
						 */
						TagableStyleRange frontRange = (TagableStyleRange) processedRange.clone();
						frontRange.length = selectionStart - processedRange.start;

						/*
						 * commit changes to the processed range
						 */
						processedRange.length -= frontRange.length;
						processedRange.start = selectionStart;
						
						res.add(frontRange);
					}
					
					TagableStyleRange remainingRange = null;
					
					if (processedRangeEnd > selectionEnd) {

						remainingRange = (TagableStyleRange) processedRange.clone();
						remainingRange.length = processedRangeEnd - selectionEnd;
						remainingRange.start = selectionEnd;
						
						/*
						 * modify range inside the selection
						 */
						processedRange.length -= remainingRange.length;
					}
					
					if (code != null) {
						/*
						 * remove the code from the (remaining) range
						 */
						processedRange.removeTag(code);
					}
					else {
						/*
						 * remove the all codes from the (remaining) range
						 */
						processedRange.removeAllTags();
					}
					
					res.add(processedRange);
					if (remainingRange != null) res.add(remainingRange);
				}
				else {
					res.add(range);
				}
			}
		}
		
		ArrayList<TagableStyleRange> resultArrayList = new ArrayList<TagableStyleRange>(res);
		documentData.setWorkingStyleRanges(resultArrayList);

		/*
		 * notify observer 
		 */
		fireDocumentSelectionManipulationEvent(textNode, resultArrayList, selectionStart, selectionLength);

		textNode.updateCodeStats();
		code.updateCodeStats();
		ProjectManager.getInstance().fireProjectDataModifiedEvent();
	}

	private void fireDocumentSelectionManipulationEvent(TextNode textNode, ArrayList<TagableStyleRange> affectedRanges, int start, int length) {
		if ((textNode != null) && (affectedRanges != null)){
			
			DocumentSelectionAddTagEvent event = new DocumentSelectionAddTagEvent(this, affectedRanges, start, length);
			LinkedList<DocumentSelectionManipulateTagsListener> listeners = tagAddedListener.get(textNode);
			if (listeners == null) return;
			for (DocumentSelectionManipulateTagsListener listener: listeners) {
				listener.DocumentSelectionTagAdded(event);
			}
		}

	}

	public SimpleRangeList getPassagesFor(TextNode textNode, TagNode tag) {
		HashMap<TagNode, SimpleRangeList> tagMap = textNode.getCodedRangesList();
		if (tagMap.containsKey(tag)) {
			return tagMap.get(tag);
		}
		else return new SimpleRangeList();
	}
	
	public HashMap<TagNode, SimpleRange> getPassagesAt(TextNode textNode, int charOffset) {
		
		HashMap<TagNode, SimpleRange> res = new HashMap<TagNode, SimpleRange>();
		DocumentData docData = documentDataMap.get(textNode);
		HashMap<TagNode, SimpleRangeList> codedRangesMap = docData.getCodedRanges();
		
		for (TagNode code: codedRangesMap.keySet()) {
			SimpleRangeList codedRangesList = codedRangesMap.get(code);
			SimpleRange fittingRange = codedRangesList.getRangeAt(charOffset);
			if (fittingRange != null) {
				res.put(code, fittingRange);
			}
		}
		return res;
	}
	
	public DocumentData getDocumentData(TextNode textNode) {
		return documentDataMap.get(textNode);
	}
	
	public HashMap<TextNode, DocumentData> getDocumentDataMap() {
		return documentDataMap;
	}
	
	public Set<TextNode> getTextNodes() {
		return documentDataMap.keySet();
	}
	
	public void resetDocumentData(TextNode textNode) {
		
		DocumentData docData = new DocumentData(textNode);
		documentDataMap.put(textNode, docData);
		textNode.updateCodeStats();
		ProjectManager.getInstance().fireProjectDataModifiedEvent();
	}

	
	public String dump() {
		StringBuilder sb = new StringBuilder();
		for (TextNode node: documentDataMap.keySet()) {
			sb.append(MessageFormat.format("TextNode: %1\nDocument Data: %2", new Object[] {node.getName(), documentDataMap.get(node)})); //$NON-NLS-1$
		}
		return documentDataMap.toString();
	}
	
	public void updateCodeStats() {
		for (TextNode textNode: documentDataMap.keySet()) {
			textNode.updateCodeStats();
		}
	}
}
