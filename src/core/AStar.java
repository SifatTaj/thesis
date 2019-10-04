package core;

import java.util.ArrayList;
import java.util.List;

class Node {
    Node parent;
    int[] position;
    double g;
    double h;
    double f;

    public Node(Node parent, int[] position) {
        this.parent = parent;
        this.position = position;
    }
}

public class AStar {
    public static List<int[]> findPath(int[][] maze, int[] start, int[] end) {
        Node startNode = new Node(null, start);
        startNode.g = startNode.h = startNode.f = 0;
        Node endNode = new Node(null, end);
        endNode.g = endNode.h = endNode.f = 0;

        List<Node> openNodes = new ArrayList<>();
        List<Node> closedNodes = new ArrayList<>();

        openNodes.add(startNode);

        while (!openNodes.isEmpty()) {
            Node currentNode = openNodes.get(0);
            int currentIndex = 0;

            for(int i = 0 ; i < openNodes.size() ; ++i) {
                if (openNodes.get(i).f < currentNode.f) {
                    currentNode = openNodes.get(i);
                    currentIndex = i;
                }
            }

            openNodes.remove(currentIndex);
            closedNodes.add(currentNode);

            if (currentNode.position[0] == endNode.position[0] & currentNode.position[1] == endNode.position[1]) {
                List<int[]> path = new ArrayList<>();
                Node current = currentNode;
                while (current != null) {
                    path.add(current.position);
                    current = current.parent;
                }
                return path;
            }

            List<Node> children = new ArrayList<>();
            List<int[]> adjacentSquare = new ArrayList<>();
            adjacentSquare.add(new int[]{0, -1});
            adjacentSquare.add(new int[]{0, 1});
            adjacentSquare.add(new int[]{-1, 0});
            adjacentSquare.add(new int[]{1, 0});
            adjacentSquare.add(new int[]{-1, -1});
            adjacentSquare.add(new int[]{-1, 1});
            adjacentSquare.add(new int[]{1, -1});
            adjacentSquare.add(new int[]{1, 1});

            for(int[] newPos : adjacentSquare) {
                int x = currentNode.position[0] + newPos[0];
                int y = currentNode.position[1] + newPos[1];
                int[] nodePos = {x, y};

                if(nodePos[0] > maze.length - 1 | nodePos[0] < 0 | nodePos[1] > maze[0].length | nodePos[1] < 0)
                    continue;
                if (maze[nodePos[0]][nodePos[1]] != 0)
                    continue;

                children.add(new Node(currentNode, nodePos));
            }

            for(Node child : children) {
                for (Node closedChild : closedNodes) {
                    if (child.equals(closedChild))
                        continue;
                }

                child.g = currentNode.g + 1;
                child.h = Math.pow((child.position[0] - endNode.position[0]), 2) + Math.pow((child.position[1] - endNode.position[1]), 2);
                child.f = child.g + child.h;

                for(Node openNode : openNodes) {
                    if (child.equals(openNode) & child.g > openNode.g)
                        continue;
                }

                openNodes.add(child);

            }

        }
        return null;
    }

    public static void main(String[] args) {
        int[][] maze = {{0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

        int[] start = {0, 0};
        int[] end = {7, 6};

        List<int[]> path = findPath(maze, start, end);

        for (int[] pos : path) {
            System.out.println(pos[0] + "," + pos[1]);
        }
    }
}