package graphs.shortestpaths;

import priorityqueues.ArrayHeapMinPQ;
import priorityqueues.ExtrinsicMinPQ;
import graphs.BaseEdge;
import graphs.Graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        // return new NaiveMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        Set<V> known = new HashSet<>();
        Map<V, Double> distTo = new HashMap<>();
        Map<V, E> edgeTo = new HashMap<>();
        ExtrinsicMinPQ<V> toVisit = createMinPQ();

        //add starting distTo and edgeTo with 0 and null
        known.add(start);
        distTo.put(start, 0.0);
        //edgeTo.put(start, null);
        System.out.println(edgeTo.get(start));
        if (start.equals(end) || graph.outgoingEdgesFrom(start).size() == 0) {
            return edgeTo;
        }
        //add outgoing edges from start to queue, distTo, and edgeTo
        for (E current : graph.outgoingEdgesFrom(start)) {
            if (toVisit.contains(current.to())) {
                if (distTo.get(current.to()) > current.weight()) {
                    distTo.put(current.to(), current.weight());
                    toVisit.changePriority(current.to(), current.weight());
                    edgeTo.put(current.to(), current);
                }
            } else {
                if (!known.contains(current.to())) {
                    toVisit.add(current.to(), current.weight());
                    distTo.put(current.to(), current.weight());
                    edgeTo.put(current.to(), current);
                }
            }
        }
        while (!toVisit.isEmpty() && !known.contains(end)) {
            V currVertex = toVisit.removeMin();
            known.add(currVertex);
            //  for (iterate over each of the outgoing edges from currVertex) {
            for (E currentEdge : graph.outgoingEdgesFrom(currVertex)) {
                Double newWeight = distTo.get(currVertex) + currentEdge.weight();
                // checks if queue already has outgoing edge, if yes
                // compare current distance to edge is better than the new distance
                // if yes, update the vertex's distance, edgeTo, and change the priority
                if (toVisit.contains(currentEdge.to())) {
                    if (distTo.get(currentEdge.to()) > newWeight) {
                        distTo.put(currentEdge.to(), newWeight);
                        toVisit.changePriority(currentEdge.to(), newWeight);
                        edgeTo.put(currentEdge.to(), currentEdge);
                    }
                    // this means that we aren't revisited an edge to update the distance
                    // we are encountering new vertices
                } else {
                    if (!known.contains(currentEdge.to())) {
                        toVisit.add(currentEdge.to(), newWeight);
                        distTo.put(currentEdge.to(), newWeight);
                        edgeTo.put(currentEdge.to(), currentEdge);
                    }
                }
            }
        }
        return edgeTo;
    }

    // differentiation between part 1 and 2, the specific connections?
    // //what's the graph and collection implementation
    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        V currVertex = end;
        //returnList (passed into ShortestPath to return
        List<E> edges = new ArrayList<>();
        if (start == null || end == null) {
            return new ShortestPath.Failure<>();
        }
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        while ((!currVertex.equals(start)) && spt.containsKey(currVertex)) {
            E parent = spt.get(currVertex);
            edges.add(0, parent);
            currVertex = parent.from();
        }
        if (!currVertex.equals(start)) {
            return new ShortestPath.Failure<>();
        }
        return new ShortestPath.Success<>(edges);
    }

}
