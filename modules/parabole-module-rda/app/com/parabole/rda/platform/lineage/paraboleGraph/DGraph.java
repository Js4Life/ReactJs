package com.parabole.rda.platform.lineage.paraboleGraph;

import java.util.LinkedList;
import java.util.List;



//Directed Graph implementation
//Implemented through adjacency list
//Configured with number of Vertices and edges

public class DGraph {

		private int	V; //number of vertices
		private	int E; //number of edges
		private	List<node>	ListNode;	//List containing nodes
		private List<adjedge>[]	AdjList; //Adjacency List
		
		//node class definition
		private class node {
			int 	node_ID;
			String	node_Name;
			String	node_Desc;
			String  Generator;
			String  Reviewer;
			String  Approver;
			
			private node(int node_id, String node_name, String node_desc){
				this.node_ID = node_id;
				this.node_Name = node_name;
				this.node_Desc = node_desc;
				this.Generator = null;
				this.Reviewer = null;
				this.Approver = null;
			}
			private node(int node_id, String node_name, String node_desc, String Generator, String Reviewer, String Approver){
				this.node_ID = node_id;
				this.node_Name = node_name;
				this.node_Desc = node_desc;
				this.Generator = Generator;
				this.Reviewer = Reviewer;
				this.Approver = Approver;
			}
			
		}
		
		//adjacency list element class
		private class adjedge {
			int		dest_ID;
			double	edge_weight;
			private adjedge (int dest_id, double weight){
				dest_ID = dest_id;
				edge_weight = weight;
			}
		}
		
		//class constructor
		public	DGraph (int num_of_vertices, int num_of_edges){
			//Initialize the class elements
			V = num_of_vertices;
			E = num_of_edges;
			
			//create the memory for node list through linked list
			ListNode = (List<node>) new LinkedList<node>();
			
			//create the memory for list of linked list
			AdjList = (List<adjedge>[]) new List[V];
			
			//create the memory for each vertice's linked list
			for(int i = 0; i < V ; i++){
				AdjList[i] = (List<adjedge>) new LinkedList<adjedge>();
			}
			
		}
		
		//utility function for adding nodes in the node list
		public void addNode(int node_id, String node_name, String node_desc){
			node	node_instance = new node(node_id, node_name, node_desc);
			ListNode.add(node_instance);
		}

		//utility function for adding nodes in the node list with ownership data
		public void addNodewithOwnership(int node_id, String node_name, String node_desc, String Generator, String Reviewer, String Approver){
			node	node_instance = new node(node_id, node_name, node_desc, Generator, Reviewer, Approver);
			ListNode.add(node_instance);
		}

		//utility function for adding edges in the adjacency list
		public void addEdge(int src_id, int dest_id, double weight){
			adjedge adjedge_instance = new adjedge(dest_id, weight);
			AdjList[src_id].add(adjedge_instance);
		}
		
		//Print Adjacency List of the graph
		public void PrintGraph (){
			System.out.println("Print Graph");
			for(int i = 0 ; i < V ; i++){
				System.out.println("Adjlist of Node " + i);
				for(int j = 0 ; j < AdjList[i].size() ; j++){
					System.out.println((AdjList[i].get(j)).dest_ID);
				}
			}		
		}
}
