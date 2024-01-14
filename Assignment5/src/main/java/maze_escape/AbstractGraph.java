package maze_escape;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class AbstractGraph<V> {

    /** Graph representation:
     *  this class implements graph search algorithms on a graph with abstract vertex type V
     *  for every vertex in the graph, its neighbours can be found by use of abstract method getNeighbours(fromVertex)
     *  this abstraction can be used for both directed and undirected graphs
     **/

    public AbstractGraph() { }

    /**
     * retrieves all neighbours of the given fromVertex
     * if the graph is directed, the implementation of this method shall follow the outgoing edges of fromVertex
     * @param fromVertex
     * @return
     */
    public abstract Set<V> getNeighbours(V fromVertex);

    /**
     * retrieves all vertices that can be reached directly or indirectly from the given firstVertex
     * if the graph is directed, only outgoing edges shall be traversed
     * firstVertex shall be included in the result as well
     * if the graph is connected, all vertices shall be found
     * @param firstVertex   the start vertex for the retrieval
     * @return
     */
    public Set<V> getAllVertices(V firstVertex) {
        Set<V> allVertices = new HashSet<>();
        // Recursive method call to explore vertices
        exploreVertices(firstVertex, allVertices);
        return allVertices;
    }

    // Helper method to explore vertices recursively
    private void exploreVertices(V vertex, Set<V> visited) {
        if (!visited.contains(vertex)) {
            visited.add(vertex);
            Set<V> neighbours = this.getNeighbours(vertex);
            for (V neighbour : neighbours) {
                exploreVertices(neighbour, visited);
            }
        }
    }


    /**
     * Formats the adjacency list of the subgraph starting at the given firstVertex
     * according to the format:
     *  	vertex1: [neighbour11,neighbour12,…]
     *  	vertex2: [neighbour21,neighbour22,…]
     *  	…
     * Uses a pre-order traversal of a spanning tree of the sub-graph starting with firstVertex as the root
     * if the graph is directed, only outgoing edges shall be traversed
     * , and using the getNeighbours() method to retrieve the roots of the child subtrees.
     * @param firstVertex
     * @return
     */
    public String formatAdjacencyList(V firstVertex) {
        StringBuilder stringBuilder = new StringBuilder("Graph adjacency list:\n");
        // Recursively build the adjacency list
        Set<V> visited = new HashSet<>();
        formatHelper(firstVertex, visited, stringBuilder);

        // Return the result
        return stringBuilder.toString();
    }

    private void formatHelper(V vertex, Set<V> visited, StringBuilder stringBuilder) {
        if (!visited.contains(vertex)) {
            visited.add(vertex);
            Set<V> neighbours = this.getNeighbours(vertex);

            // Append the vertex and its neighbors to the adjacency list
            stringBuilder.append(vertex).append(": ").append(neighbours).append("\n");

            for (V neighbour : neighbours) {
                formatHelper(neighbour, visited, stringBuilder);
            }
        }
    }

    /**
     * represents a directed path of connected vertices in the graph
     */
    public class GPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are neighbours in the graph,
         *    i.e. FOR ALL i: 1 < i < vertices.length: getNeighbours(vertices[i-1]).contains(vertices[i])
         * 2. a path with one vertex equal start and target vertex
         * 3. a path without vertices is empty, does not have a start nor a target
         * totalWeight is a helper attribute to capture total path length from a function on two neighbouring vertices
         * visited is a helper set to be able to track visited vertices in searches, only for analysis purposes
         **/
        private static final int DISPLAY_CUT = 10;
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%.2f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            int count = 0;
            final int tailCut = this.vertices.size()-1 - DISPLAY_CUT;
            for (V v : this.vertices) {
                // limit the length of the text representation for long paths.
                if (count < DISPLAY_CUT || count > tailCut) {
                    sb.append(separator).append(v.toString());
                    separator = ", ";
                } else if (count == DISPLAY_CUT) {
                    sb.append(separator).append("...");
                }
                count++;
            }
            sb.append(")");
            return sb.toString();
        }

        /**
         * recalculates the total weight of the path from a given weightMapper that calculates the weight of
         * the path segment between two neighbouring vertices.
         * @param weightMapper
         */
        public void reCalculateTotalWeight(BiFunction<V,V,Double> weightMapper) {
            this.totalWeight = 0.0;
            V previous = null;
            for (V v: this.vertices) {
                // the first vertex of the iterator has no predecessor and hence no weight contribution
                if (previous != null) this.totalWeight += weightMapper.apply(previous, v);
                previous = v;
            }
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public void setTotalWeight(double totalWeight) {
            this.totalWeight = totalWeight;
        }

        public Set<V> getVisited() { return this.visited; }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     * @param startVertex
     * @param targetVertex
     * @return  the path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath depthFirstSearch(V startVertex, V targetVertex) {
        GPath path = new GPath();
        if (startVertex == null || targetVertex == null) return null;
        return depthFirstSearch(startVertex, targetVertex, new HashSet<>(), path) ;   // replace by a proper outcome, if any
    }

    private GPath depthFirstSearch (V current, V target, Set<V> visited, GPath path) {
        if (visited.contains(current)) return null;
        visited.add(current);
        path.visited.add(current);

        if (current.equals(target)) {
            path.vertices.addLast(current);
            return path;
        }

        for (V neighbour: this.getNeighbours(current)) {

             GPath subPath = depthFirstSearch(neighbour, target, visited, path);

            if (subPath != null) {
                subPath.vertices.addFirst(current);
                return subPath;
            }
        }
        return null;
    }


    /**
     * Uses a breadth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     * @param startVertex
     * @param targetVertex
     * @return  the path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath breadthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) return null;

        Set<V> visited = new HashSet<>();
        Map<V, V> parentMap = new HashMap<>();
        Queue<V> queue = new LinkedList<>();
        GPath path = new GPath();

        queue.offer(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            V currentVertex = queue.poll();

            if (currentVertex.equals(targetVertex)) {
                // Reconstruct the path from target to start using parentMap
                while (currentVertex != null) {
                    path.getVertices().add(currentVertex);
                    currentVertex = parentMap.get(currentVertex);
                }
                Collections.reverse((List<?>) path.getVertices()); // Reverse to get start to target path
                path.getVisited().addAll(visited);
                return path;
            }

            for (V neighbor : getNeighbours(currentVertex)) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, currentVertex);
                }
            }
        }

        return null; // Target not found
    }


    // helper class to build the spanning tree of visited vertices in Dijkstra's shortest path algorithm
    // you may change this class or delete it altogether to follow a different approach in your implementation
    private class MSTNode implements Comparable<MSTNode> {
        protected V vertex;                // the graph vertex that is concerned with this MSTNode
        protected V parentVertex = null;   // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path towards this node's vertex

        // Constructor with parameters for parentVertex, vertex, and weightSumTo
        private MSTNode(V parentVertex, V vertex, double weightSumTo) {
            this.parentVertex = parentVertex;
            this.vertex = vertex;
            this.weightSumTo = weightSumTo;
        }

        // Constructor without parameters
        private MSTNode(V vertex) {
            this.vertex = vertex;
        }

        // Comparable interface helps to find a node with the shortest current path so far
        @Override
        public int compareTo(MSTNode otherMSTNode) {
            return Double.compare(weightSumTo, otherMSTNode.weightSumTo);
        }
    }


    /**
     * Calculates the edge-weighted shortest path from the startVertex to targetVertex in the subgraph
     * according to Dijkstra's algorithm of a minimum spanning tree
     * @param startVertex
     * @param targetVertex
     * @param weightMapper   provides a function(v1,v2) by which the weight of an edge from v1 to v2
     *                       can be retrieved or calculated
     * @return  the shortest path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath dijkstraShortestPath(V startVertex, V targetVertex, BiFunction<V, V, Double> weightMapper) {
        if (startVertex == null || targetVertex == null || weightMapper == null) {
            return null; // Return null for invalid inputs
        }
        // Initialize the result path of the search
        GPath path = new GPath();
        path.visited.add(startVertex);
        // Easy target
        if (startVertex.equals(targetVertex)) {
            path.vertices.add(startVertex);
            path.setTotalWeight(0.0);
            return path;
        }
        Map<V, MSTNode> minimumSpanningTree = new HashMap<>();
        minimumSpanningTree.put(startVertex, new MSTNode(startVertex, null, 0.0));

        PriorityQueue<MSTNode> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(node -> node.weightSumTo));
        priorityQueue.offer(minimumSpanningTree.get(startVertex));

        while (!priorityQueue.isEmpty()) {
            MSTNode nearestMSTNode = priorityQueue.poll();
            // Check if nearestMSTNode.vertex is null
            if (nearestMSTNode.parentVertex == null) {
                continue; // Skip this node and proceed to the next one
            }
            // Continue Dijkstra's algorithm to process nearestMSTNode
            if (nearestMSTNode.parentVertex.equals(targetVertex)) {
                constructPath(minimumSpanningTree, path, startVertex, targetVertex);
                return path;
            }
            for (V neighbor : this.getNeighbours(nearestMSTNode.parentVertex)) {
                double tentativeWeight = nearestMSTNode.weightSumTo + weightMapper.apply(nearestMSTNode.parentVertex, neighbor);

                if (!minimumSpanningTree.containsKey(neighbor) || tentativeWeight < minimumSpanningTree.get(neighbor).weightSumTo) {
                    minimumSpanningTree.put(neighbor, new MSTNode(nearestMSTNode.parentVertex, neighbor, tentativeWeight));
                    priorityQueue.offer(new MSTNode(neighbor, null, tentativeWeight));
                    path.visited.add(neighbor);
                }
            }
        }
        // If no target can be found return null
        return null;
    }

    private void constructPath(Map<V, MSTNode> minimumSpanningTree, GPath path, V startVertex, V targetVertex) {
        List<V> verticesInReverseOrder = new ArrayList<>();
        V currentVertex = targetVertex;

        while (!currentVertex.equals(startVertex)) {
            verticesInReverseOrder.add(currentVertex);
            currentVertex = minimumSpanningTree.get(currentVertex).parentVertex;
        }
        verticesInReverseOrder.add(startVertex);

        Collections.reverse(verticesInReverseOrder);

        path.vertices.addAll(verticesInReverseOrder);
        path.setTotalWeight(minimumSpanningTree.get(targetVertex).weightSumTo);
    }
}
