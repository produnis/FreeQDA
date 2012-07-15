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

import java.util.LinkedList;

public class GenericTreeNode<T> {

	/**
	 * Prints the whole tag (sub)tree to System.out.
	 * @param rootTag the tag to start printing from.
	 */
	public static final void printTree(GenericTreeNode<?> rootTag) {
		printTree(rootTag, 0);
	}

	/**
	 * Prints the whole tag (sub)tree to System.out.
	 * @param rootTag the tag to start printing from.
	 * @param indent the indentation level of this tag
	 */
	private static final void printTree(GenericTreeNode<?> rootTag, int indent) {
//		String indentation = StringTools.EMPTY;
//		for (int i=0; i<indent; i++) indentation += "  "; //$NON-NLS-1$
//		GenericTreeNode<?> tn = rootTag;
		for (GenericTreeNode<?> childTag: rootTag.getChildren()) printTree(childTag, indent+1);
	}

	private GenericTreeNode<T> parentNode;

	private LinkedList<GenericTreeNode<T>> children;

	public LinkedList<GenericTreeNode<T>> getChildren() {
		if (children == null) {
			children = new LinkedList<GenericTreeNode<T>>();
		}
		return children;
	}
	
	public void addChild(GenericTreeNode<T> aNode) {
		if (children == null) {
			children = new LinkedList<GenericTreeNode<T>>();
		}
		children.add(aNode);
		aNode.setParentNode(this);
	}

	public GenericTreeNode<T> getParentCategory() {
		return parentNode;
	}

	public void setParentNode(GenericTreeNode<T> parentNode) {
		this.parentNode = parentNode;
	}
}
