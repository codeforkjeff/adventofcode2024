package com.codefork.aoc2024.day21;

public record Button(String symbol, Position pos) {

    public boolean isAdjacent(Button other) {
        var xDiff = Math.abs(pos().x() - other.pos().x());
        var yDiff = Math.abs(pos().y() - other.pos().y());
        return xDiff + yDiff == 1;
    }

    /**
     * returns the action symbol when navigating from this button to (adjacent) target
     */
    public String getAction(Button target) {
        var xDiff = pos().x() - target.pos().x();
        var yDiff = pos().y() - target.pos().y();
        if (xDiff > 0) {
            return "<";
        } else if (xDiff < 0) {
            return ">";
        }
        if (yDiff > 0) {
            return "^";
        } else if (yDiff < 0) {
            return "v";
        }
        throw new RuntimeException("buttons aren't adjacent in getAction=" + this + "," + target);
    }

    public String toString() {
        return symbol + "(" + pos.x() + "," + pos.y() + ")";
    }

    @SuppressWarnings("unused")
    public char asChar() {
        return symbol.charAt(0);
    }
}
