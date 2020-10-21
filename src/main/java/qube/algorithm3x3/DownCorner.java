package qube.algorithm3x3;

import qube.Color;
import qube.Side;

public class DownCorner
{
    private final boolean nw_, ne_, se_, sw_;

    public enum Formation
    {
        NONE,
        TWO_LINE,
        TWO_ACROSS,
        ONE,
        DONE
    }

    /**
     * Parses the down side of the cube.
     *
     * @param   cube    Cube to look at.
     */
    public DownCorner(ICube cube)
    {
        IFace down = cube.getFace(Side.DOWN);
        final Color yellow = down.getColor(Location.CENTER);

        nw_ = down.getColor(Location.TOP_LEFT) == yellow;
        ne_ = down.getColor(Location.TOP_RIGHT) == yellow;
        se_ = down.getColor(Location.BOTTOM_RIGHT) == yellow;
        sw_ = down.getColor(Location.BOTTOM_LEFT) == yellow;
    }

    private int countCorners()
    {
        int c = 0;
        if(nw_) ++c;
        if(ne_) ++c;
        if(se_) ++c;
        if(sw_) ++c;
        return c;
    }

    /**
     * Determines the {@link Formation} of the down side.
     *
     * @return  The determined formation.
     */
    public Formation determineFormation()
    {
        if(nw_ && ne_ && se_ && sw_)
        {
            return Formation.DONE;
        }
        else if(!(nw_ || ne_ || se_ || sw_))
        {
            return Formation.NONE;
        }
        else if(countCorners() == 2)
        {
            return (nw_ && ne_) || (ne_ && se_) || (se_ && sw_) || (sw_ && nw_)
                    ? Formation.TWO_LINE : Formation.TWO_ACROSS;
        }
        if(countCorners() == 1)
        {
            return Formation.ONE;
        }

        throw new IllegalStateException("Unknown error occurred");
    }

    public boolean northWest() { return nw_; }
    public boolean northEast() { return ne_; }
    public boolean southEast() { return se_; }
    public boolean southWest() { return sw_; }
}
