// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// KGraphPathFinder.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.knowledge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.*;
import java.util.Stack;

import com.parabole.rda.platform.BaseDTO;

/**
 * Finds Paths between Two Nodes in a Knowledge Graph.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class KGraphPathFinder {

    final KGraph graph;

    public KGraphPathFinder(final KGraph graph) {
        this.graph = graph;
    }

    public TraverseResult findAllPathsBetweenTwoNodes(final int source, final int destination, final Integer maxPathLength, final Float minPathThreshold) {
        final TraverseCondition condition = new TraverseCondition(source, destination, maxPathLength, minPathThreshold);
        final TraverseResult result = new TraverseResult();
        findAllPathsByDFS(condition, result);
        return result;
    }

    private void findAllPathsByDFS(final TraverseCondition condition, final TraverseResult result) {
        if (condition.reachedDestination()) {
            if (condition.acceptPath()) {
				System.out.println(" ********** Recording Path **************");                
				condition.recordResult(result);				
				condition.updateNodeOnSuccessPath(result);
            }
        } else {
            if (condition.goDown()) {
                graph.getEdges(condition.current).forEach((final KEdge edge) -> {
                    if (condition.checkRoute(edge)) {
						//System.out.println("Go to the next search for node " + condition.current);
                        findAllPathsByDFS(condition, result);
                    }
                });
            }
        }
        condition.backTrack();
    }

    private class TraverseCondition extends BaseDTO {
        private static final long serialVersionUID = 6343501579246483292L;
        int current;
        int dest;
        int maxPathLength = 10;
        int currentPathLength = 0;
        BigDecimal minPathThreshold = new BigDecimal("0.5");
        BigDecimal currentPathWeight = new BigDecimal("1.0");
        Stack<Integer> visitedVertices = new Stack<Integer>();
		LinkedList<Integer> visitedStatus = new LinkedList<Integer>();
		LinkedList<Integer> nodeOnSuccessPath = new LinkedList<Integer>();
        Stack<Integer> visitedEdges = new Stack<Integer>();
        Stack<BigDecimal> currentPathWeights = new Stack<BigDecimal>();

        public TraverseCondition(final int current, final int dest, final Integer maxPathLength, final Float minPathThreshold) {
            this.current = current;
            this.dest = dest;
            if (null != maxPathLength) {
                this.maxPathLength = maxPathLength;
            }
            if (null != minPathThreshold) {
                this.minPathThreshold = new BigDecimal(minPathThreshold.toString());
            }
            visitedVertices.push(current);
			visitedStatus.add(current);
        }

        public boolean reachedDestination() {
            return (current == dest);
        }

        public boolean acceptPath() {
            return (currentPathLength <= maxPathLength);
        }

        public void recordResult(final TraverseResult result) {
            result.pathListVertices.add(new ArrayList<Integer>(visitedVertices));
            result.pathListEdges.add(new ArrayList<Integer>(visitedEdges));
            result.pathWeights.add(currentPathWeight);
            result.totalWeight = result.totalWeight.add(currentPathWeight).setScale(4, BigDecimal.ROUND_HALF_UP);
        }
		
		public void updateNodeOnSuccessPath(final TraverseResult result){
			Stack<Integer>  copiedStack = (Stack<Integer>)  visitedVertices.clone();
			
			while(!copiedStack.isEmpty()){
				if(!nodeOnSuccessPath.contains(copiedStack.peek())){
					System.out.println("adding to successpath " + copiedStack.peek());
					nodeOnSuccessPath.add(copiedStack.pop());
				}else{
					//take out the element without inserting
					copiedStack.pop();
				}
			}
		}	

        public boolean goDown() {
            return (currentPathLength < maxPathLength) && (currentPathWeight.compareTo(minPathThreshold) > 0);
        }

        public boolean checkRoute(final KEdge edge) {
            final int neighbor = edge.getDestination();
            //if (visitedVertices.contains(neighbor)) {
            if (visitedStatus.contains(neighbor)) {
                return false;
            } else {
                final int currentEdgeId = edge.getId();
                final BigDecimal currentEdgeWeight = new BigDecimal(Float.toString(edge.getWeight()));
                this.current = neighbor;
                visitedVertices.push(neighbor);
				visitedStatus.add(neighbor);
                visitedEdges.push(currentEdgeId);
                currentPathLength++;
                currentPathWeights.push(currentPathWeight);
                currentPathWeight = currentPathWeight.multiply(currentEdgeWeight).setScale(4, BigDecimal.ROUND_HALF_UP);
                return true;
            }
        }

		public void updateVisistedStatus(){
			//System.out.println("called to remove node " + visitedVertices.peek());
			if(nodeOnSuccessPath.contains(visitedVertices.peek()))
			{
				//remove the node from nodeOnSuccessPath and visistedStatus
				System.out.println("removing node " + visitedVertices.peek());
				nodeOnSuccessPath.remove(visitedVertices.peek());
				visitedStatus.remove(visitedVertices.peek());
			}	
		}
		
        public void backTrack() {
			updateVisistedStatus();
            visitedVertices.pop();
            if (currentPathLength > 0) {
                visitedEdges.pop();
                currentPathWeight = currentPathWeights.pop();
            }
            currentPathLength--;
        }
    }

    public class TraverseResult extends BaseDTO {
        private static final long serialVersionUID = 6323445679246483292L;
        private BigDecimal totalWeight = new BigDecimal("0.0");
        private final List<BigDecimal> pathWeights = new ArrayList<BigDecimal>();
        private final List<ArrayList<Integer>> pathListVertices = new ArrayList<ArrayList<Integer>>();
        private final List<ArrayList<Integer>> pathListEdges = new ArrayList<ArrayList<Integer>>();

        public BigDecimal getTotalWeight() {
            return totalWeight;
        }

        public List<BigDecimal> getPathWeights() {
            return pathWeights;
        }

        public List<ArrayList<Integer>> getPathListVertices() {
            return pathListVertices;
        }

        public List<ArrayList<Integer>> getPathListEdges() {
            return pathListEdges;
        }
    }
}
