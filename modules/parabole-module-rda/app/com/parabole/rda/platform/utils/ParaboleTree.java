// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ParaboleTree.java
//
// =============================================================================
package com.parabole.rda.platform.utils;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * TODO
 *
 * @author Sandip Bhaumik
 * @v1.0
 */
public class ParaboleTree {

	private	List<TreeElement>		headList;	
	
	private		class	TreeElement {
		int					nodeId;
		boolean				traversal_status;
		TreeElement			parent;
		List<TreeElement>	childList;		
	}
	
	//class constructor
	public	ParaboleTree(){
		//create Tree list memory
		headList = (List<TreeElement>) new LinkedList<TreeElement>();
	}
	
	//add node	
	public void addNode(int parent_id, int newnode_id){
		//Find if the headList is empty
		if(headList.isEmpty()){
			//Create the node 
			TreeElement		lTreeElem = new TreeElement();
			lTreeElem.nodeId = newnode_id;
			lTreeElem.traversal_status = false;
			lTreeElem.parent = null;
			lTreeElem.childList = (List<TreeElement>) new LinkedList<TreeElement>();
			
			//add the node to headList
			headList.add(lTreeElem);
			return;
		}
		
		//In the case, parent exists
		//first create the new node
		TreeElement 	lTreeElem = new TreeElement();
		lTreeElem.nodeId = newnode_id;
		lTreeElem.traversal_status = false;
		//now get the parent node using parent_id
		TreeElement 	lNode = SearchNode(headList.get(0), parent_id);
		lTreeElem.parent = lNode;
		lTreeElem.childList = (List<TreeElement>) new LinkedList<TreeElement>();
		
		//now add this node to parent node in the list
		lNode.childList.add(lTreeElem);
	}
	
	private		TreeElement		SearchNode(TreeElement parentnode, int search_id){
		//first check if the tree is empty
		if(headList.isEmpty()) {return null;}
		//Local TreeElement 
		TreeElement 	lTree = parentnode;
		if(lTree.nodeId == search_id) {return parentnode;}	
		//create a child iterator
		Iterator<TreeElement> node_iterator = lTree.childList.iterator();
		
		while(node_iterator.hasNext())
		{
			lTree = SearchNode(node_iterator.next(), search_id);
			if(lTree != null ) {return lTree;}
		}	
		return null;
	}
	
	public		void	ResetTraversal()
	{
		if(headList.isEmpty()) {return;}
		ResetTraversalStatus(headList.get(0));
	}
	
	public		void    ResetTraversalStatus(TreeElement parentnode){
		TreeElement		lTree = parentnode;
		if(lTree == null) {return;}
		lTree.traversal_status = false;
		System.out.println(" Reseting node " + lTree.nodeId);
		
		Iterator<TreeElement>	node_iterator = lTree.childList.iterator();
		
		while(node_iterator.hasNext()){
			ResetTraversalStatus(node_iterator.next());
		}
		return;
	}
	
	private		TreeElement		SearchParentNode(TreeElement parentnode, int search_id){
		//first check if the tree is empty
		if(headList.isEmpty()) {return null;}
		//Local TreeElement 
		TreeElement 	lTree = parentnode;
		if((lTree.traversal_status == false) && (lTree.nodeId == search_id)) { lTree.traversal_status = true; return parentnode;}	
		//create a child iterator
		Iterator<TreeElement> node_iterator = lTree.childList.iterator();
		
		while(node_iterator.hasNext())
		{
			lTree = SearchParentNode(node_iterator.next(), search_id);
			if(lTree != null ) {return lTree;}
		}	
		return null;
	}

	private		boolean	checkParent (TreeElement parentnode, int search_id){
		boolean status = false;	
		if(parentnode == null){return false;}	
		if(parentnode.nodeId == search_id){return true;}
		if(checkParent(parentnode.parent, search_id) == true){
			status = true;
		}
		return status;
	}

	public	boolean 	IsChild(int search_id, int current_node){
		boolean	status = false;
		TreeElement 	lNode = SearchNode(headList.get(0), search_id);
		if(SearchNode(lNode, current_node) != null){
			status = true;
		}	
			
		return status;
	}

	public	boolean		IsParent(int search_id, int current_node){
		boolean	status = false;
		TreeElement 	lNode = null;
		System.out.println("search_id"  + search_id + "current_node"  + current_node );
		do{
			lNode = SearchParentNode(headList.get(0), search_id);
			if(lNode != null){
				System.out.println("Retrieved node " + lNode.nodeId + "current_node " + current_node);
			}
			if(checkParent(lNode, current_node) == true){
				status = true;
				System.out.println("search_id"  + search_id + "current_node"  + current_node);
				break;
			}
		}while(lNode != null);
		ResetTraversalStatus(headList.get(0));
		return  status;		
	}		
}	
