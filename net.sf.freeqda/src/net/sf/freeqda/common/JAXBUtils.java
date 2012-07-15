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
package net.sf.freeqda.common;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.freeqda.bindings.document.AppliedTagType;
import net.sf.freeqda.bindings.document.AppliedTagsListType;
import net.sf.freeqda.bindings.document.FQDADocumentType;
import net.sf.freeqda.bindings.document.FontType;
import net.sf.freeqda.bindings.document.StyleRangeType;
import net.sf.freeqda.bindings.project.FQDAProjectType;
import net.sf.freeqda.bindings.project.TagListType;
import net.sf.freeqda.bindings.project.TagType;
import net.sf.freeqda.bindings.project.TextCategoryListType;
import net.sf.freeqda.bindings.project.TextCategoryType;
import net.sf.freeqda.bindings.project.TextListType;
import net.sf.freeqda.bindings.project.TextType;
import net.sf.freeqda.common.projectmanager.ProjectManager;
import net.sf.freeqda.common.projectmanager.TextCategoryNode;
import net.sf.freeqda.common.projectmanager.TextNode;
import net.sf.freeqda.common.tagregistry.TagManager;
import net.sf.freeqda.common.tagregistry.TagNode;
import net.sf.freeqda.common.widget.TagableStyleRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;




public class JAXBUtils {

	/**
	 * The prefix that is used for tags in the XML file. 
	 */
	protected static final String JAXB_CATEGORY_ID_PREFIX = "Category-"; //$NON-NLS-1$

	/**
	 * The prefix that is used for texts in the XML file. 
	 */
	protected static final String JAXB_TEXT_ID_PREFIX = "Text-"; //$NON-NLS-1$

	/**
	 * The prefix that is used for tags in the XML file. 
	 */
	protected static final String JAXB_TAG_ID_PREFIX = "Tag-"; //$NON-NLS-1$

	/**
	 * The postfix that is used for root IDs in the XML file. 
	 */
	private static final String JAXB_TAG_ROOT_ID_POSTFIX = "-0"; //$NON-NLS-1$

	private static final String EXCEPTION_CATEGORY_NODE_INVALID = Messages.JAXBUtils_ExceptionCategoryNodeInvalid;
	private static final String EXCEPTION_TEXT_NODE_INVALID = Messages.JAXBUtils_ExceptionTextNodeInvalid;
	private static final String EXCEPTION_TAG_NODE_INVALID = Messages.JAXBUtils_ExceptionTagNodeInvalid;
	private static final String EXCEPTION_NOT_A_UNIQUE_ID = Messages.JAXBUtils_ExceptionIdNotUnique;
	private static final String EXCEPTION_UNMARSHALING_PROJECT_DATA = Messages.JAXBUtils_ExceptionUnmarshalingProject;


	
	private static final ProjectManager PROJECT_MANAGER = ProjectManager.getInstance();
	
	private static final TagManager TAG_REGISTRY = TagManager.getInstance();

	private static final net.sf.freeqda.bindings.project.ObjectFactory PROJECT_OBJECT_FACTORY = new net.sf.freeqda.bindings.project.ObjectFactory();

	private static final net.sf.freeqda.bindings.document.ObjectFactory DOCUMENT_OBJECT_FACTORY = new net.sf.freeqda.bindings.document.ObjectFactory();

	public static synchronized FQDAProjectType createBindingsFromProjectData() {

		FQDAProjectType binding = new FQDAProjectType();
		binding.setName(PROJECT_MANAGER.getProjectName());

		/*
		 * 
		 */
		TagListType tagList = new TagListType();
		createTagBindings(TAG_REGISTRY.getRootTag(), tagList.getTag());
		binding.setTagList(tagList);

		/*
		 * 
		 */
		TextCategoryListType categoryList = new TextCategoryListType();
		createCategoryBindings(PROJECT_MANAGER.getRootCategory(), categoryList
				.getTextCategory());
		binding.setTextCategoryList(categoryList);

		/*
		 * 
		 */
		TextListType textList = new TextListType();
		createTextBindings(textList.getText());
		binding.setTextList(textList);

		return binding;
	}

	private static synchronized FQDADocumentType createDocumentBindings(StyleRange[] styleRanges, String textContent) {
		FQDADocumentType binding = new FQDADocumentType();
		binding.setTextContent(textContent);
		binding.getStyleRange().addAll(createJAXBStyleRangesFromStyledText(styleRanges, 0));
		return binding;
	}


	private static void createCategoryBindings(TextCategoryNode rootNode,
			List<TextCategoryType> categoryList) {
		/*
		 * Create a parent dummy category (for parentId only)
		 */
		TextCategoryType root = new TextCategoryType();
		root.setID(JAXB_CATEGORY_ID_PREFIX + rootNode.getUID());

		for (GenericTreeNode<TextCategoryNode> categoryNode : rootNode
				.getChildren()) {

			/*
			 * Create the JAXB binding for the current category node
			 */
			TextCategoryType t = new TextCategoryType();
			TextCategoryNode tn = (TextCategoryNode) categoryNode;

			t.setID(JAXB_CATEGORY_ID_PREFIX + tn.getUID());
			t.setName(tn.getName());
			if (!root.getID().endsWith(JAXB_TAG_ROOT_ID_POSTFIX)) {
				t.setParentID(root);
			}

			categoryList.add(t);

			/*
			 * call recursive
			 */
			createCategoryBindings(tn, categoryList);
		}
	}

	private static void createTagBindings(TagNode rootNode, List<TagType> tagList) {
		/*
		 * Create a parent dummy tag (for parentId only)
		 */
		TagType root = new TagType();
		root.setID(JAXB_TAG_ID_PREFIX + rootNode.getUID());

		for (GenericTreeNode<TagNode> tagNode : rootNode.getChildren()) {

			/*
			 * Create the JAXB binding for the current tag node
			 */
			TagType t = new TagType();
			TagNode child = (TagNode) tagNode;

			t.setID(JAXB_TAG_ID_PREFIX + child.getUID());
			t.setName(child.getName());
			if (rootNode.getUID() != 0)
				t.setParentID(root);
			t.setHexRGBColor(getColorHexCodeFromRGB(child.getRGB()));
			tagList.add(t);

			/*
			 * call recursive
			 */
			createTagBindings(child, tagList);
		}
	}

	private static void createTextBindings(List<TextType> textList) {

		HashMap<Integer, TextNode> lookupMapText = PROJECT_MANAGER.getLookupMapText();
		ArrayList<Integer> textIdList = new ArrayList<Integer>(lookupMapText
				.keySet());
		Collections.sort(textIdList);
		for (Integer textId : textIdList) {
			TextNode textNode = lookupMapText.get(textId);
			TextCategoryNode textCategoryNode = textNode.getCategory();
			/*
			 * create a category dummy
			 */
			TextCategoryType parentCategory = new TextCategoryType();
			parentCategory.setID(JAXB_CATEGORY_ID_PREFIX
					+ textCategoryNode.getUID());

			TextType text = new TextType();
			if (textCategoryNode.getUID() != 0)
				text.setCategoryID(parentCategory);

			text.setID(JAXB_TEXT_ID_PREFIX + textNode.getUID());
			text.setName(textNode.getName());
			text.setActivated(textNode.isActivated());
			textList.add(text);
		}
	}

	public static final void dumpData() {
		System.out.println("Projectname: " + PROJECT_MANAGER.getProjectName()); //$NON-NLS-1$

		HashMap<Integer, TagNode> lookupMapTag = TAG_REGISTRY.getLookupMapTag();
		HashMap<Integer, TextCategoryNode> lookupMapCategory = PROJECT_MANAGER
				.getLookupMapCategory();
		HashMap<Integer, TextNode> lookupMapText = PROJECT_MANAGER.getLookupMapText();

		System.out.println("Available Tags:"); //$NON-NLS-1$
		ArrayList<Integer> tagIdList = new ArrayList<Integer>(lookupMapTag
				.keySet());
		Collections.sort(tagIdList);
		for (Integer tagId : tagIdList) {
			System.out.println("  " + tagId + " -> " + lookupMapTag.get(tagId)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		System.out.println("Available Categories:"); //$NON-NLS-1$
		ArrayList<Integer> categoryIdList = new ArrayList<Integer>(
				lookupMapCategory.keySet());
		Collections.sort(categoryIdList);
		for (Integer categoryId : categoryIdList) {
			System.out.println("  " + categoryId + " -> " //$NON-NLS-1$ //$NON-NLS-2$
					+ lookupMapCategory.get(categoryId));
		}

		System.out.println("Available Texts:"); //$NON-NLS-1$
		ArrayList<Integer> textIdList = new ArrayList<Integer>(lookupMapText
				.keySet());
		Collections.sort(textIdList);
		for (Integer textId : textIdList) {
			System.out.println("  " + textId + " -> " //$NON-NLS-1$ //$NON-NLS-2$
					+ lookupMapText.get(textId));
		}

	}

	/**
	 * Creates an integer UID from the category id in the JAXB file.
	 * 
	 * @param categoryId
	 *            the category id string to convert
	 * @return an integer UID that represents the id from the JAXB file.
	 */
	private static final int getCategoryId(String categoryId) {
		if (categoryId.startsWith(JAXB_CATEGORY_ID_PREFIX)) {
			return Integer.parseInt(categoryId.replace(
					JAXB_CATEGORY_ID_PREFIX, StringTools.EMPTY));
		} else {
			throw new IllegalArgumentException(MessageFormat.format(EXCEPTION_CATEGORY_NODE_INVALID, new Object[] {categoryId, JAXB_CATEGORY_ID_PREFIX}));
		}
	}

	private static final byte[] getColorHexCodeFromRGB(RGB color) {
		return new byte[] { (byte) color.red, (byte) color.green, (byte) color.blue };
	}

	/**
	 * Creates a RGB object from the color hex code stored in the JAXB file.
	 * 
	 * @param colorHexCode
	 *            the color hex code string to convert
	 * @return a RGB object that represents the color hex code from the JAXB
	 *         file.
	 */
	private static final RGB getRGBValueFromColorHexCode(byte[] colorCodes) {
		return new RGB(colorCodes[0] & 0xFF, colorCodes[1] & 0xFF, colorCodes[2] & 0xFF);
	}

	/**
	 * Creates an integer UID from the category id in the JAXB file.
	 * 
	 * @param categoryId
	 *            the category id string to convert
	 * @return an integer UID that represents the id from the JAXB file.
	 */
	private static final int getTextUIDFromId(String textId) {
		if (textId.startsWith(JAXB_TEXT_ID_PREFIX)) {
			return Integer.parseInt(textId.replace(
					JAXB_TEXT_ID_PREFIX, StringTools.EMPTY));
		} else {
			throw new IllegalArgumentException(MessageFormat.format(EXCEPTION_TEXT_NODE_INVALID, new Object[] {textId, JAXB_TEXT_ID_PREFIX}));
		}
	}

	/**
	 * Creates an integer UID from the tag id in the JAXB file.
	 * 
	 * @param tagId
	 *            the tag id string to convert
	 * @return an integer UID that represents the id from the JAXB file.
	 */
	private static final int getUIDFromTagId(String tagId) {
		if (tagId.startsWith(JAXB_TAG_ID_PREFIX)) {
			return Integer.parseInt(tagId.replace(
					JAXB_TAG_ID_PREFIX, StringTools.EMPTY));
		} else {
			throw new IllegalArgumentException(MessageFormat.format(EXCEPTION_TAG_NODE_INVALID, new Object[] {tagId, JAXB_TAG_ID_PREFIX}));
		}
	}

	/**
	 * Initialises the project from the JAXB bindings
	 * 
	 * @param project
	 */
	private synchronized static void initProjectManager(FQDAProjectType project, File projectFile) {

		PROJECT_MANAGER.reset();
		PROJECT_MANAGER.setProjectName(project.getName());
		PROJECT_MANAGER.setProjectFile(projectFile);

		HashMap<Integer, TagNode> lookupMapTag = TAG_REGISTRY.getLookupMapTag();
		HashMap<Integer, TextCategoryNode> lookupMapCategory = PROJECT_MANAGER
				.getLookupMapCategory();
		HashMap<Integer, TextNode> lookupMapText = PROJECT_MANAGER.getLookupMapText();

		/*
		 * First run attaches only "root" tags to our real root node
		 */
		for (TagType aTag : project.getTagList().getTag()) {
			
			/*
			 * Create a TagNode object from the JAXB tag object
			 */
			TagNode currentTag = new TagNode(aTag.getName(),
					getRGBValueFromColorHexCode(aTag.getHexRGBColor()),
					getUIDFromTagId(aTag.getID()));

			/*
			 * Adapt the highest UID if the current UID is greater than the
			 * highest UID found so far
			 */
			TAG_REGISTRY.updateHighestTagUID(currentTag.getUID());

			/*
			 * tag ids have to be unique. If we see the same tag id here more
			 * than once then the data is inconsistent! //TODO Handle data
			 * inconsistency in TagManager
			 */
			if (lookupMapTag.containsKey(currentTag.getUID())) {
				throw new RuntimeException(MessageFormat.format(EXCEPTION_NOT_A_UNIQUE_ID, new Object[] {currentTag}));
			}

			/*
			 * The current Tag is unique. Store it in the lookup map
			 */
			lookupMapTag.put(currentTag.getUID(), currentTag);

			/*
			 * Tags without a parent tag are "root tags". These tags are
			 * appended to the TagManager's ROOT_TAG object.
			 * 
			 * In the second run the TAG_LOOKUP_MAP is filled and we can attach
			 * all non-root tags to their parent tags.
			 */
			if (aTag.getParentID() == null) {
				/*
				 * tag has no parent -> tag is a root tag
				 */
				TAG_REGISTRY.getRootTag().addChild(currentTag);
			}
		}
		/*
		 * Second run attaches all "non-root" tags to their parent tag
		 */
		for (TagType aTag : project.getTagList().getTag()) {
			/*
			 * get the TagNode object from the TAG_LOOKUP_MAP
			 */
			TagNode currentTag = lookupMapTag
					.get(getUIDFromTagId(aTag.getID()));
			/*
			 * Process only tags with a parent id (non-root tags). Retrieve the
			 * parent tag from the TAG_LOOKUP_MAP.
			 */
			if (aTag.getParentID() != null) {
				TagNode parentTag = lookupMapTag
						.get(getUIDFromTagId(((TagType) aTag.getParentID()).getID()));
				/*
				 * attach this tag to his parent tag.
				 */
				parentTag.addChild(currentTag);
			}
		}

		/*
		 * First run attaches only "root" tags to our real root node
		 */
		for (TextCategoryType aCategory : project.getTextCategoryList()
				.getTextCategory()) {
			/*
			 * Create a TagNode object from the JAXB tag object
			 */
			TextCategoryNode currentCategory = new TextCategoryNode(aCategory
					.getName(), getCategoryId(aCategory.getID()));

			/*
			 * Adapt the highest UID if the current UID is greater than the
			 * highest UID found so far
			 */
			PROJECT_MANAGER.updateHighestCategoryUID(currentCategory.getUID());

			/*
			 * tag ids have to be unique. If we see the same tag id here more
			 * than once then the data is inconsistent! //TODO Handle data
			 * inconsistency in TagManager
			 */
			if (lookupMapCategory.containsKey(currentCategory.getUID())) {
				throw new RuntimeException(MessageFormat.format(EXCEPTION_NOT_A_UNIQUE_ID, new Object[] {currentCategory}));
			}

			/*
			 * The current Tag is unique. Store it in the lookup map
			 */
			lookupMapCategory.put(currentCategory.getUID(), currentCategory);

			/*
			 * Tags without a parent tag are "root tags". These tags are
			 * appended to the TagManager's ROOT_TAG object.
			 * 
			 * In the second run the TAG_LOOKUP_MAP is filled and we can attach
			 * all non-root tags to their parent tags.
			 */
			if (aCategory.getParentID() == null) {
				/*
				 * tag has no parent -> tag is a root tag
				 */
				PROJECT_MANAGER.getRootCategory().addChild(currentCategory);
			}
		}
		/*
		 * Second run attaches all "non-root" categories to their parent
		 * category
		 */
		for (TextCategoryType aCategory : project.getTextCategoryList()
				.getTextCategory()) {
			/*
			 * get the TagNode object from the TAG_LOOKUP_MAP
			 */
			TextCategoryNode currentCategory = lookupMapCategory
					.get(getCategoryId(aCategory.getID()));
			/*
			 * Process only tags with a parent id (non-root tags). Retrieve the
			 * parent tag from the TAG_LOOKUP_MAP.
			 */
			if (aCategory.getParentID() != null) {
				TextCategoryNode parentCategory = lookupMapCategory
						.get(getCategoryId(((TextCategoryType) aCategory
								.getParentID()).getID()));
				/*
				 * attach this tag to his parent tag.
				 */
				parentCategory.addChild(currentCategory);
			}
		}

		for (TextType text : project.getTextList().getText()) {
			TextNode currentTextNode = new TextNode(text.getName(), getTextUIDFromId(text.getID()), text.isActivated() != null ? text.isActivated() : false);

			/*
			 * Adapt the highest UID if the current UID is greater than the
			 * highest UID found so far
			 */
			PROJECT_MANAGER.updateHighestTextUID(currentTextNode.getUID());

			/*
			 * load data into the text node
			 */
			try {
				FQDADocumentType documentData = loadDocument(getFileForTextNode(currentTextNode));
				currentTextNode.setStyleRanges(createTagableStyleRanges(documentData.getStyleRange()));
				currentTextNode.setTextContent(documentData.getTextContent());
			} catch (IOException e) {
				//FIXME handle exception (project file could not be loaded)!
				e.printStackTrace();
			}

			currentTextNode.reset();
			
			/*
			 * Assign the text node to its parent node
			 */
			TextCategoryNode parentCategoryNode = null;
			if (text.getCategoryID() != null) {
				parentCategoryNode = lookupMapCategory
						.get(getCategoryId(((TextCategoryType) text.getCategoryID())
								.getID()));
				currentTextNode.setCategory(parentCategoryNode);
				parentCategoryNode.addTextFile(currentTextNode);

			} else {
				currentTextNode.setCategory(PROJECT_MANAGER.getRootCategory());
				PROJECT_MANAGER.getRootCategory().addTextFile(currentTextNode);
			}
			lookupMapText.put(currentTextNode.getUID(), currentTextNode);
			
			currentTextNode.updateCodeStats();
		}

		for (TagNode tagNode: lookupMapTag.values()) {
			tagNode.updateCodeStats();
		}
		
		PROJECT_MANAGER.setLookupMapCategory(lookupMapCategory);
		PROJECT_MANAGER.setLookupMapText(lookupMapText);
		TAG_REGISTRY.setLookupMapTag(lookupMapTag);
		
		TAG_REGISTRY.updateCodeStats();
	}

	public static File getFileForTextNode(TextNode textNode) {
		return new File(PROJECT_MANAGER.getProjectFile().getParentFile(), ProjectManager.FQDA_FILE_COMMON_NAME+textNode.getUID()+ProjectManager.FQDA_FILE_COMMON_SUFFIX);
	}

	public static void loadProject(File projectFile) throws IOException {
		String packageName = FQDAProjectType.class.getPackage().getName();
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<?> e = (JAXBElement<?>) u.unmarshal(projectFile);
			FQDAProjectType project = (FQDAProjectType) e.getValue();
			initProjectManager(project, projectFile);
			
		} catch (JAXBException e) {
			PROJECT_MANAGER.reset();
			throw new IOException(EXCEPTION_UNMARSHALING_PROJECT_DATA, e);
		}
	}
	
	public static void saveProject(File projectFile) throws IOException {
		FQDAProjectType project2Save = JAXBUtils.createBindingsFromProjectData();
		String packageName = FQDAProjectType.class.getPackage().getName();
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Marshaller m = jc.createMarshaller();
			m.marshal(PROJECT_OBJECT_FACTORY.createFQDAProject(project2Save), projectFile);
		} catch (JAXBException e) {
			throw new IOException(EXCEPTION_UNMARSHALING_PROJECT_DATA, e);
		}
	}
	
	private static FQDADocumentType loadDocument(File documentFile) throws IOException {
		String packageName = FQDADocumentType.class.getPackage().getName();
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<?> e = (JAXBElement<?>) u.unmarshal(documentFile);
			FQDADocumentType document = (FQDADocumentType) e.getValue();
			return document;
		} catch (JAXBException e) {
			PROJECT_MANAGER.reset();
			throw new IOException(EXCEPTION_UNMARSHALING_PROJECT_DATA, e);
		}
	}
	
	private static void saveDocument(FQDADocumentType document, File destinationFile) throws IOException {
		String packageName = FQDADocumentType.class.getPackage().getName();
		try {
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Marshaller m = jc.createMarshaller();
			m.marshal(DOCUMENT_OBJECT_FACTORY.createFQDADocument(document), destinationFile);
		} catch (JAXBException e) {
			throw new IOException(EXCEPTION_UNMARSHALING_PROJECT_DATA, e);
		}
	}

	public static void saveDocument(StyleRange[] styleRanges, String textContent, File destinationFile) throws IOException {
		FQDADocumentType document2Save = JAXBUtils.createDocumentBindings(styleRanges, textContent);
		saveDocument(document2Save, destinationFile);
	}

	private static TagableStyleRange[] createTagableStyleRanges(List<StyleRangeType> styleRangeList) {
		/* 
		 * initialise StyleRanges
		 */
		TagableStyleRange[] res = new TagableStyleRange[styleRangeList.size()]; 
		for (int n=0; n<styleRangeList.size(); n++) {
			StyleRangeType range = styleRangeList.get(n);
			TagableStyleRange styleRange = new TagableStyleRange();
			styleRange.start = range.getStart();
			styleRange.length = range.getLength();
			if (range.getFont() != null) {
				styleRange.fontStyle = range.getFont().getFontStyle();
			}
			if (range.getTagList() != null) {
				for (AppliedTagType tag: range.getTagList().getTag()) {
					TagNode tagNode = TAG_REGISTRY.getLookupMapTag().get(tag.getID());
					styleRange.addTag(tagNode);
				}
			}
			res[n] = styleRange;
		}
		return res;
	}

	private static synchronized List<StyleRangeType> createJAXBStyleRangesFromStyledText(StyleRange[] styleRanges, int characterOffset) {

		List<StyleRangeType> styleList = new LinkedList<StyleRangeType>();
		
		for (StyleRange sr: styleRanges) {
			
			TagableStyleRange range = (TagableStyleRange) sr;

			StyleRangeType bindingStyle = new StyleRangeType();
			
			bindingStyle.setStart(range.start + characterOffset);
			bindingStyle.setLength(range.length);
			
			if (range.fontStyle != SWT.NORMAL) {
				FontType fontType = new FontType();
				fontType.setFontStyle(range.fontStyle);
				bindingStyle.setFont(fontType);
			}
			
			TagNode[] appliedTags = range.getTags();

			if (appliedTags != null) {
				AppliedTagsListType appliedTagListType = new AppliedTagsListType();
				List<AppliedTagType> appliedTagsList = appliedTagListType.getTag();
				for (TagNode tag: appliedTags) {
					
					AppliedTagType bindingTag = new AppliedTagType();
					bindingTag.setID(tag.getUID());
					appliedTagsList.add(bindingTag);
				}
				bindingStyle.setTagList(appliedTagListType);
			}
			styleList.add(bindingStyle);
		}
		return styleList;
	}
}