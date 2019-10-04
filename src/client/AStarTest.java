package client;

import core.AStar;
import model.Node;

import java.util.List;

public class AStarTest {

    public static int height = 6;
    public static int width = 7;

    public static Node createNode(int x, int y) {
        y = 5 - y;
        return new Node(y,x);
    }

    public static int[][] createBlocks(int[][] blocks) {
        int[][] translatedCoordinates = new int[blocks.length][2];
        for (int i = 0 ; i < blocks.length ; ++i) {
            translatedCoordinates[i] = new int[]{5 - blocks[i][1], blocks[i][0]};
        }
        return translatedCoordinates;
    }

    public static void main(String[] args) {
        Node initialNode = createNode(1, 3);
        Node finalNode = createNode(5,3);
        AStar aStar = new AStar(height, width, initialNode, finalNode, 10, 100);
        int[][] blocksArray = createBlocks(new int[][]{{3, 2}, {3, 3}, {3, 4},{3, 5}});
        aStar.setBlocks(blocksArray);
        List<Node> path = aStar.findPath();
        for (Node node : path) {
            System.out.println(node);
        }

        //Search Area
        // 5    -   -   -   B   -   -   -
        // 4    -   -   -   B   -   -   -
        // 3    -   I   -   B   -   F   -
        // 2    -   -   -   B   -   -   -
        // 1    -   -   -   -   -   -   -
        // 0    -   -   -   -   -   -   -
        //      0   1   2   3   4   5   6

        //Expected output with diagonals
        //Node [row=2, col=1]
        //Node [row=1, col=2]
        //Node [row=0, col=3]
        //Node [row=1, col=4]
        //Node [row=2, col=5]

        //Search Path with diagonals
        //      0   1   2   3   4   5   6
        // 0    -   -   -   *   -   -   -
        // 1    -   -   *   B   *   -   -
        // 2    -   I*  -   B   -  *F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -

        //Expected output without diagonals
        //Node [row=2, col=1]
        //Node [row=2, col=2]
        //Node [row=1, col=2]
        //Node [row=0, col=2]
        //Node [row=0, col=3]
        //Node [row=0, col=4]
        //Node [row=1, col=4]
        //Node [row=2, col=4]
        //Node [row=2, col=5]

        //Search Path without diagonals
        //      0   1   2   3   4   5   6
        // 0    -   -   *   *   *   -   -
        // 1    -   -   *   B   *   -   -
        // 2    -   I*  *   B   *  *F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -
    }
}
