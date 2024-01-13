package maze_escape;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExtraTests {

    private static final long SEED = 20231113L;
    private static final int WIDTH = 100;
    private static final int HEIGHT = WIDTH;
    private static final int REMOVE = 250;

    @Test
    void verifyGeneratedMazeStats() {
        Maze maze = generateMaze();


        Set<Integer> vertices = maze.getAllVertices(maze.getStartNode());

        assertEquals(100, maze.getWidth());
        assertEquals(100, maze.getHeight());
        assertEquals(5428, vertices.size());
        assertEquals(10000, maze.getNumberOfCells());
    }

    @Test
    void verifyDepthFirstSearchResults() {
        Maze maze = generateMaze();


        AbstractGraph<Integer>.GPath dfsPath = maze.depthFirstSearch(maze.getStartNode(), 96);
        assertNotNull(dfsPath);

        if(dfsPath.getTotalWeight() == 0.0) dfsPath.reCalculateTotalWeight(maze::manhattanTime);

        assertEquals(463.00, dfsPath.getTotalWeight());
        assertEquals(162, dfsPath.getVertices().size());
        assertTrue(dfsPath.getVisited().containsAll(Set.of(6666, 6563, 6663, 6665, 6765, 6766, 6767, 6768, 6769, 7069, 788, 790, 692, 693, 493, 393, 293, 193, 194, 96)));
    }

    @Test
    void verifyBreadthFirstSearchResults() {
        Maze maze = generateMaze();


        AbstractGraph<Integer>.GPath bfsPath = maze.breadthFirstSearch(maze.getStartNode(), 96);
        assertNotNull(bfsPath);

        if(bfsPath.getTotalWeight() == 0.0) bfsPath.reCalculateTotalWeight(maze::manhattanTime);


        assertEquals(226.00, bfsPath.getTotalWeight());
        assertEquals(79, bfsPath.getVertices().size());
        assertTrue(bfsPath.getVisited().containsAll(Set.of(6666, 6563, 6462, 6460, 6459, 6359, 6259, 6157, 6057, 5756, 788, 790, 692, 693, 493, 393, 293, 193, 194, 96)));
    }

    @Test
    void verifyDijkstraShortestPathResults() {
        Maze maze = generateMaze();


        AbstractGraph<Integer>.GPath dijkstraPath = maze.dijkstraShortestPath(maze.getStartNode(), 96, maze::manhattanTime);
        assertNotNull(dijkstraPath);

        if(dijkstraPath.getTotalWeight() == 0.0) dijkstraPath.reCalculateTotalWeight(maze::manhattanTime);


        assertEquals(226.00, dijkstraPath.getTotalWeight());
        assertEquals(79, dijkstraPath.getVertices().size());
        assertTrue(dijkstraPath.getVisited().containsAll(Set.of(6666, 6563, 6462, 6460, 6459, 6359, 6259, 6157, 6057, 5756, 788, 790, 692, 693, 493, 393, 293, 193, 194, 96)));
    }

    @Test
    void verifyStartAndExitNodes() {
        Maze maze = generateMaze();

        int startNode = maze.getStartNode();
        int exitNode = maze.getExitNode();

        assertNotEquals(startNode, exitNode, "Start and exit nodes should be different");
    }


    private Maze generateMaze() {
        Maze.reSeedRandomizer(SEED);
        Maze maze = new Maze(WIDTH, HEIGHT);
        maze.generateRandomizedPrim();
        maze.configureInnerEntry();
        maze.removeRandomWalls(REMOVE);

        return maze;
    }
}
