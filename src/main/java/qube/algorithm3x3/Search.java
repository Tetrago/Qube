package qube.algorithm3x3;

import qube.Color;
import qube.Side;

public abstract class Search
{
    private Side side_;
    private Location location_;
    private final Color color_;

    public Search(Color color)
    {
        color_ = color;
    }

    /**
     * Determines if the found color is what is being searched for.
     *
     * @return If the found color is valid.
     */
    public abstract boolean valid();

    /**
     * Fills the search with data for {@code valid} and for fetching.
     *
     * @param side     Side of color.
     * @param location Location of color.
     */
    public void fill(Side side, Location location)
    {
        side_ = side;
        location_ = location;
    }

    public Side getSide() { return side_; }
    public Location getLocation() { return location_; }
    public Color getColor() { return color_; }
}
