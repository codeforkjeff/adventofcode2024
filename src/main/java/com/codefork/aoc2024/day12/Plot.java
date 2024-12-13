package com.codefork.aoc2024.day12;

public record Plot(int x, int y) {

    public boolean isAdjacent(Plot other) {
        var xDiff = Math.abs(other.x() - x());
        var yDiff = Math.abs(other.y() - y());
        return xDiff + yDiff == 1;
    }

    /**
     * returns the edge along which the other plot is adjacent
     * @param other
     * @return
     */
    public Edge getAdjacentEdge(Plot other) {
        if(other.x() == x() && other.y() == y() - 1) {
            return Edge.Top;
        } else if(other.x() == x() && other.y() == y() + 1) {
            return Edge.Bottom;
        } else if(other.x() == x() - 1 && other.y() == y()) {
            return Edge.Left;
        } else if(other.x() == x() + 1 && other.y() == y()) {
            return Edge.Right;
        }
        throw new RuntimeException("this should never happen");
    }

}
