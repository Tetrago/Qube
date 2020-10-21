package qube.algorithm3x3;

import qube.Color;
import qube.Side;

public class DownShape
{
    private final boolean n_, e_, s_, w_;

    public enum Formation
    {
        DOT,
        HOOK,
        LINE,
        STAR
    }

    /**
     * Parses the down side of the cube.
     *
     * @param   cube    Cube to look at.
     */
    public DownShape(ICube cube)
    {
        IFace down = cube.getFace(Side.DOWN);
        final Color yellow = down.getColor(Location.CENTER);

        n_ = down.getColor(Location.TOP) == yellow;
        e_ = down.getColor(Location.RIGHT) == yellow;
        s_ = down.getColor(Location.BOTTOM) == yellow;
        w_ = down.getColor(Location.LEFT) == yellow;
    }

    /**
     * Determines the {@link Formation} of the down side.
     *
     * @return  The determined formation.
     */
    public Formation determineFormation()
    {
        if(n_ && e_ && s_ && w_)
        {
            return Formation.STAR;
        }
        else if(!(n_ || e_ || s_ || w_))
        {
            return Formation.DOT;
        }
        else if((n_ && e_) || (e_ && s_) || (s_ && w_) || (w_ && n_))
        {
            return Formation.HOOK;
        }
        else if((n_ && s_) || (e_ && w_))
        {
            return Formation.LINE;
        }

        throw new IllegalStateException("Unknown error occurred");
    }

    public boolean north() { return n_; }
    public boolean east() { return e_; }
    public boolean south() { return s_; }
    public boolean west() { return w_; }
}
