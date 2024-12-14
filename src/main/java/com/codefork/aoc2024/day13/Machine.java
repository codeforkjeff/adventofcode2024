package com.codefork.aoc2024.day13;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public record Machine(Button a, Button b, Prize prize) {

    private static Pattern buttonPattern = Pattern.compile("Button ([AB]): X\\+(\\d+), Y\\+(\\d+)");
    private static Pattern prizePattern = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");

    public record Button(int moveX, int moveY) {

    }

    public record Prize(long x, long y) {

    }

    public record Solution(long countA, long countB) {

    }

    public Optional<Solution> findSolution() {
        // I wrote out on paper the two simultaneous equations, merged them into one using
        // substitution, and solved for the number of times button B is pressed (countB).
        // Once we have that, we can plug it into one of the original equations to get countA
        var numer = (a.moveY() * prize.x()) - (a.moveX() * prize.y());
        var denom = (a.moveY() * b.moveX()) - (a.moveX() * b.moveY());

        // test that countB and countA are whole numbers; we can't push a button a fraction of a time,
        // so reject those solutions
        if (numer % denom == 0) {
            var countB = numer / denom;
            var numerCountA = prize.x() - (b.moveX() * countB);
            if (numerCountA % a.moveX() == 0) {
                var countA = numerCountA / a.moveX();
                return Optional.of(new Solution(countA, countB));
            }
        }
        return Optional.empty();
    }

    /**
     * The nulls here are gross, but IntelliJ warns you're not supposed to use Optionals for parameters,
     * so I'm not sure how else to do this more safely.
     */
    public static List<Machine> consume(Iterator<String> iter, long prizeAddition, List<Machine> machines, Button a, Button b) {
        if (iter.hasNext()) {
            var line = iter.next();
            var buttonMatcher = buttonPattern.matcher(line);
            if (buttonMatcher.find()) {
                var button = buttonMatcher.group(1);
                var x = Integer.parseInt(buttonMatcher.group(2));
                var y = Integer.parseInt(buttonMatcher.group(3));
                if (button.equals("A")) {
                    return consume(iter, prizeAddition, machines, new Button(x, y), b);
                }
                if (button.equals("B")) {
                    return consume(iter, prizeAddition, machines, a, new Button(x, y));
                }
            }
            var prizeMatcher = prizePattern.matcher(line);
            if (prizeMatcher.find()) {
                var x = Integer.parseInt(prizeMatcher.group(1)) + prizeAddition;
                var y = Integer.parseInt(prizeMatcher.group(2)) + prizeAddition;
                var prize = new Prize(x, y);
                // a and b should be present at this point; if they're not, something's wrong with the input
                machines.add(new Machine(a, b, prize));
                return consume(iter, prizeAddition, machines, null, null);
            }
            consume(iter, prizeAddition, machines, a, b);
        }
        return machines;
    }

    public static List<Machine> consume(Iterator<String> iter, long prizeAddition) {
        return consume(iter, prizeAddition, new ArrayList<>(), null, null);
    }
}
