package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
import graphs.shortestpaths.ShortestPath;
import graphs.shortestpaths.ShortestPathFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DijkstraSeamFinder implements SeamFinder {
    // replace all 4 references to "Object" on the line below with whatever vertex type
    //  you choose for your graph
    private final ShortestPathFinder<Graph<Pixel, Edge<Pixel>>, Pixel, Edge<Pixel>> pathFinder;
    private static final Pixel NODEEND = new Pixel(Integer.MAX_VALUE, Integer.MAX_VALUE);
    private static final Pixel NODESTART = new Pixel(-1, -1);


    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        /*
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
        return new DijkstraShortestPathFinder<>();
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        //create list to return
        List<Integer> yCoords = new ArrayList<>();
        //graph instantiation
        Graph<Pixel, Edge<Pixel>> horGraph = new MyGraph(energies, "h");
        //code for dummy node initialization
        //Pixel horDummyNodeStart = new Pixel(-1, -1);
        // Pixel horDummyNodeEnd = new Pixel(Integer.MAX_VALUE, Integer.MAX_VALUE);

        ShortestPath<Pixel, Edge<Pixel>> lol = pathFinder.findShortestPath(horGraph, NODESTART, NODEEND);
        for (int i = 1; i < lol.vertices().size() - 1; i++) {
            yCoords.add(lol.vertices().get(i).getY());
            //System.out.println(lol.vertices().get(i).getY());
        }

        return yCoords;
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        //create list to return
        List<Integer> xCoords = new ArrayList<>();
        //graph instantiation
        Graph<Pixel, Edge<Pixel>> verGraph = new MyGraph(energies, "v");
        //code for dummy node initialization
        //Pixel verDummyNodeStart = new Pixel(-1, -1);
        //Pixel verDummyNodeEnd = new Pixel(Integer.MAX_VALUE, Integer.MAX_VALUE);

        ShortestPath<Pixel, Edge<Pixel>> lol = pathFinder.findShortestPath(verGraph, NODESTART, NODEEND);
        for (int i = 1; i < lol.vertices().size() - 1; i++) {
            xCoords.add(lol.vertices().get(i).getX());
           // System.out.println(lol.vertices().get(i).getX());
        }

        return xCoords;
    }

    private static class Pixel {
        int x;
        int y;
        public Pixel(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pixel pixel = (Pixel) o;
            return x == pixel.x && y == pixel.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private class MyGraph implements Graph<Pixel, Edge<Pixel>> {
        // implement starting dummy to connect to all the horizontal or vertical pixels
        private double[][] energies;
        private String orientation;

        public MyGraph(double[][] energies, String orientation) {
            this.orientation = orientation;
            this.energies = energies;
        }
        @Override
        public Collection<Edge<Pixel>> outgoingEdgesFrom(Pixel vertex) {
            Set<Edge<Pixel>> outGoingEdges = new HashSet<>();
            // r = rightward edges (horizontal seams)
            // d = downward edges (vertical seams)
            int xCord = vertex.getX();
            int yCord = vertex.getY();

            //need to define edge with weight, and start initialize end
            if (orientation.equals("v")) {
                if (xCord == -1) {
                    int row1Length = energies.length;
                    for (int i = 0; i < row1Length; i++) {
                        Edge<Pixel> newPix = new Edge<>(vertex, new Pixel(i, 0), energies[i][0]);
                        outGoingEdges.add(newPix);
                    }
                }
                if (yCord != energies[0].length - 1 && xCord != -1 && xCord != NODEEND.getX()) {
                    Edge<Pixel> dMiddle = new Edge<>(vertex, new Pixel(xCord, yCord + 1), energies[xCord][yCord + 1]);
                    outGoingEdges.add(dMiddle);
                    if (xCord != 0) {
                        Edge<Pixel> dLeft = new Edge<>(vertex, new Pixel(xCord - 1, yCord + 1),
                            energies[xCord - 1][yCord + 1]);
                        outGoingEdges.add(dLeft);
                    }
                    if (xCord != energies.length - 1) {
                        Edge<Pixel> dRight = new Edge<>(vertex,
                            new Pixel(xCord + 1, yCord + 1), energies[xCord + 1][yCord + 1]);
                        outGoingEdges.add(dRight);
                    }
                }
                if (yCord == energies[0].length - 1) {
                    Edge<Pixel> newPix = new Edge<>(new Pixel(xCord, energies[0].length - 1),
                            NODEEND, 0);
                        outGoingEdges.add(newPix);
                }
            } else {
                if (xCord == -1) {
                    int col1Length = energies[0].length;
                    //Pixel minStart = horDummyNode; //pixel start of horizontal seam
                    //double minEnergy = energies[0][0]; //energy of start pixel for horizontal seam
                    for (int i = 0; i < col1Length; i++) {
                        Edge<Pixel> newPix = new Edge<>(vertex, new Pixel(0, i), energies[0][i]);
                        outGoingEdges.add(newPix);
                    }
                }
                if (xCord != energies.length - 1 && xCord != -1 && xCord != NODEEND.getX()) {
                    Edge<Pixel> rMiddle = new Edge<>(vertex, new Pixel(xCord + 1, yCord), energies[xCord + 1][yCord]);
                    outGoingEdges.add(rMiddle);
                    if (yCord != 0) {
                        Edge<Pixel> rUp = new Edge<>(vertex, new Pixel(xCord + 1, yCord - 1),
                            energies[xCord + 1][yCord - 1]);
                        outGoingEdges.add(rUp);
                    }
                    if (yCord != energies[0].length - 1) {
                        Edge<Pixel> rDown = new Edge<>(vertex, new Pixel(xCord + 1, yCord + 1),
                            energies[xCord + 1][yCord + 1]);
                        outGoingEdges.add(rDown);
                    }
                }
                if (xCord == energies.length - 1) {
                        Edge<Pixel> newPix = new Edge<>(new Pixel(energies.length - 1, yCord),
                            NODEEND, 0);
                        outGoingEdges.add(newPix);
                }
            }
            return outGoingEdges;
        }
    }
}
