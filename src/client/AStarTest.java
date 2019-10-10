package client;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import core.AStar;
import model.FloorLayout;
import model.Node;
import util.MongoDBHelper;

import java.util.List;

public class AStarTest {

    private final int startx;
    private final int starty;
    private final int endx;
    private final int endy;
    private final int floor;
    private final String place;

    public AStarTest(int startx, int starty, int endx, int endy, int floor, String place) {
        this.startx = startx;
        this.starty = starty;
        this.endx = endx;
        this.endy = endy;
        this.floor = floor;
        this.place = place;
    }

    public static int yTranslateBy;

    public static Node createNode(int x, int y) {
        y = yTranslateBy - y;
        return new Node(y,x);
    }

    public static int[][] createBlocks(int[][] blocks) {
        int[][] translatedCoordinates = new int[blocks.length][2];
        for (int i = 0 ; i < blocks.length ; ++i) {
            translatedCoordinates[i] = new int[]{yTranslateBy - blocks[i][1], blocks[i][0]};
        }
        return translatedCoordinates;
    }

    public List<Node> run(FloorLayout floorLayout) {

        yTranslateBy = floorLayout.getHeight() - 1;

        Node initialNode = createNode(startx, starty);
        Node finalNode = createNode(endx,endy);
        AStar aStar = new AStar(floorLayout.getHeight(), floorLayout.getWidth(), initialNode, finalNode, 10, 100);
        int[][] blocksArray = createBlocks(floorLayout.getWalls());
        aStar.setBlocks(blocksArray);
        List<Node> path = aStar.findPath();
        
        for (Node node : path) {
            node.setY(Math.abs(node.getY() - AStar.yTranslateBy));
            System.out.println(node);
        }
        return path;
    }
}

//Home
// 4      -   -   -   X   24  -   25  X   26  -   17
// 3.5    -   -   -   X   X   X   -   X   X   X   -
// 3      18  -   19  -   20  -   21  X   22  -   23
// 2.5    X   X   X   X   -   -   -   X   -   -   -
// 2      12  -   13  -   14  -   15  -   16  -   17
// 1.5    X   X   X   X   -   -   -   -   -   X   X
// 1      6   -   7   -   8   -   9   -   10  -   11
// .5     -   -   -   X   -   -   -   X   -   -   -
// 0      0   -   1   X   2   -   3   X   4   -   5

//        0   .5  1  1.5  2  2.5  3  3.5  4  4.5  5

//Home
// 8      X   X   X   X   24  -   25  X   26  -   17
// 7      X   X   X   X   X   X   -   X   X   X   -
// 6      18  -   19  -   20  -   21  X   22  -   23
// 5      X   X   X   X   -   -   -   X   -   -   -
// 4      12  -   13  -   14  -   15  -   16  -   17
// 3      X   X   X   X   -   -   -   -   -   X   X
// 2      6   -   7   -   8   -   9   -   10  -   11
// 1      -   -   -   X   -   -   -   X   -   -   -
// 0      0   -   1   X   2   -   3   X   4   -   5

//        0   1   2   3   4   5   6   7   8   9   10

//Blocks: {0,8},{0,7},{0,3},{0,5},{1,8},{1,7},{1,3},{1,5},{2,8},{2,7},{2,3},{2,5},
// {3,8},{3,7},{3,3},{3,5},{3,0},{3,1},
// {4,7},{5,7},
// {7,8},{7,7},{7,5},{7,0},{7,1},{7,6},
// {8,7},{9,3},{9,7},{10,3}

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
