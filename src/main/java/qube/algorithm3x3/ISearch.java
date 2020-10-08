package qube.algorithm3x3;

import qube.Color;
import qube.Side;

public interface ISearch
{
    /**
     * Tests whether the location and color are part of the search.
     *
     * @param   side        Side of the cube.
     * @param   location    Location on the side.
     * @param   color       Color at the location.
     *
     * @return              Whether this is part of the search.
     */
    boolean test(Side side, Location location, Color color);
}
