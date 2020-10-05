package qube.algorithm3x3;

import qube.Color;
import qube.Side;

public class DaisySearch extends Search
{
    public DaisySearch(Color color)
    {
        super(color);
    }

    @Override
    public boolean valid()
    {
        return getSide() != Side.DOWN
                && getLocation().getMinor() == Location.Minor.EDGE;
    }
}
