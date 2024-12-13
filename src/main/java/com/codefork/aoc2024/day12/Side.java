package com.codefork.aoc2024.day12;

/**
 * A side is an edge along a plot
 */
public record Side(Plot plot, Edge edge) {

    public boolean isAdjacent(Side other) {
        if(edge.equals(other.edge())) {
            if(edge.equals(Edge.Left) || edge.equals(Edge.Right)) {
                var yDiff = Math.abs(other.plot().y() - plot().y());
                return other.plot().x() == plot().x() && yDiff == 1;
            } else if (edge.equals(Edge.Top) || edge.equals(Edge.Bottom)) {
                var xDiff = Math.abs(other.plot().x() - plot().x());
                return other.plot().y() == plot().y() && xDiff == 1;
            }
        }
        return false;
    }
}
