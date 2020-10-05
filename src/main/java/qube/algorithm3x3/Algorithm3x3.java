package qube.algorithm3x3;

import qube.Color;
import qube.Side;

public class Algorithm3x3
{
    private ICube cube_;

    public Algorithm3x3(ICube cube)
    {
        cube_ = cube;
    }

    /**
     * Perform the daisy step.
     *
     * <p>Step one for solving a cube.</p>
     */
    public void daisy()
    {
        // The typical color is white, but its possible that the center has been rotated in some way.
        final Color white = cube_.getFace(Side.UP).getColor(Location.CENTER);
        Search daisy = new DaisySearch(white);

        while(cube_.find(daisy))
        {
            if(daisy.getSide() == Side.UP)
            {
                cube_.rotate(daisy.getSide(), false, 2);
            }
        }
    }
}
