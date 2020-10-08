package qube;

import qube.algorithm3x3.Location;

public class LocationSpace
{
    private final Side side_;
    private final Location location_;
    private final Color color_;

    public LocationSpace(Side side, Location location, Color color)
    {
        side_ = side;
        location_ = location;
        color_ = color;
    }

    public Side getSide() { return side_; }
    public Location getLocation() { return location_; }
    public Color getColor() { return color_; }
}
