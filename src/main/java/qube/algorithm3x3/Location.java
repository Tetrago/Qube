package qube.algorithm3x3;

public enum Location
{
    CENTER(Minor.CENTER),

    TOP(Minor.EDGE),
    RIGHT(Minor.EDGE),
    BOTTOM(Minor.EDGE),
    LEFT(Minor.EDGE),

    TOP_LEFT(Minor.CORNER),
    TOP_RIGHT(Minor.CORNER),
    BOTTOM_RIGHT(Minor.CORNER),
    BOTTOM_LEFT(Minor.CORNER);

    public enum Minor
    {
        CENTER,
        EDGE,
        CORNER
    }

    private Minor minor_;

    Location(Minor minor)
    {
        minor_ = minor;
    }

    public Minor getMinor() { return minor_; }
}
