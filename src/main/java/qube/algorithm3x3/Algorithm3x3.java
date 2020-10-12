package qube.algorithm3x3;

import qube.Color;
import qube.LocationSpace;
import qube.Side;

import java.util.concurrent.*;

public class Algorithm3x3 implements Runnable
{
    private final ICube cube_;

    public Algorithm3x3(ICube cube)
    {
        cube_ = cube;
    }

    /**
     * Solves the cube.
     *
     * @return  Future of solve.
     */
    public Future<Void> solve()
    {
        return CompletableFuture.runAsync(this);
    }

    /**
     * Perform the daisy step.
     *
     * <p>Step one for solving a cube.</p>
     */
    private void daisyFlip() throws ExecutionException, InterruptedException
    {
        // The typical color is white, but its possible that the center has been rotated in some way.
        final Color white = cube_.getFace(Side.UP).getColor(Location.CENTER);

        ISearch daisy = (side, location, color) ->
                side != Side.DOWN && location.getMinor() == Location.Minor.EDGE && color == white;

        LocationSpace ls;
        while((ls = cube_.find(daisy).get()) != null)
        {
            if(ls.getSide() == Side.UP)
            {
                Location loc = ls.getLocation().opposite(Side.UP);

                IFace down = cube_.getFace(Side.DOWN);
                while(down.getColor(loc) == white)  // While there is a color in the new position.
                {
                    cube_.rotate(Side.DOWN, false, 1).get();
                }

                Side side = Location.getEdgeOuterSide(ls.getSide(), ls.getLocation());
                cube_.rotate(side, false, 2).get();
            }
            else
            {
                Side side = ls.getLocation() != Location.RIGHT && ls.getLocation() != Location.LEFT
                        ? ls.getSide() : Location.getEdgeOuterSide(ls.getSide(), ls.getLocation());

                while(cube_.getEdgeStrip(side, Side.DOWN)[1] == white)
                {
                    cube_.rotate(Side.DOWN, false, 1).get();
                }

                cube_.rotate(side, false, 1).get();
            }
        }

        ISearch flip = (side, location, color) ->
                side == Side.DOWN && location.getMinor() == Location.Minor.EDGE && color == white;

        while((ls = cube_.find(flip).get()) != null)
        {
            Side side = Location.getEdgeOuterSide(ls.getSide(), ls.getLocation());
            IFace face = cube_.getFace(side);

            while(face.getColor(Location.CENTER) != face.getColor(Location.BOTTOM))
            {
                cube_.rotate(Side.UP, false, 1).get();
                cube_.rotate(Side.UP, false, 1, 1).get();
            }

            cube_.rotate(side, false, 2).get();
        }
    }

    /**
     * Place the white corners.
     *
     * <p>Step two for solving a cube.</p>
     */
    private void whiteCorner() throws ExecutionException, InterruptedException
    {
        final Color white = cube_.getFace(Side.UP).getColor(Location.CENTER);

        ISearch search = (side, location, color) ->
        {
            if(location.getMinor() != Location.Minor.CORNER || color != white)
            {
                return false;
            }

            LocationSpace rotated = new LocationSpace(side, location, color);

            for(int i = 0; i < 2; ++i)  // Checking if the corner is in the right place.
            {
                rotated = rotated.rotateCorner();

                IFace face = cube_.getFace(rotated.getSide());
                if(face.getColor(rotated.getLocation()) != face.getColor(Location.CENTER))
                {
                    return true;
                }
            }

            return false;
        };

        LocationSpace ls;
        while((ls = cube_.find(search).get()) != null)
        {
            if(ls.getSide() == Side.UP || ls.getSide() == Side.DOWN)
            {
                Side rot = ls.rotateCorner().getSide();
                boolean ccw = ls.getSide() == Side.UP;

                cube_.rotate(rot, ccw, 1).get();
                cube_.rotate(Side.DOWN, false, 1).get();
                cube_.rotate(rot, !ccw, 1).get();
            }
            else if(ls.rotateCorner().getSide() == Side.UP)
            {
                cube_.rotate(ls.getSide(), false, 1);
                cube_.rotate(Side.DOWN, false, 1);
                cube_.rotate(ls.getSide(), true, 1);
            }
            else if(ls.rotateCorner().rotateCorner().getSide() == Side.UP)
            {
                cube_.rotate(ls.getSide(), true, 1);
                cube_.rotate(Side.DOWN, true, 1);
                cube_.rotate(ls.getSide(), false, 1);
            }
            else
            {
                LocationSpace rotated = ls.rotateCorner();
                if(ls.getLocation().sideCorner() == Location.LEFT)
                {
                    rotated = rotated.rotateCorner();
                }

                Color find = rotated.determineColor(cube_);
                while(cube_.getFace(rotated.getSide()).getColor(Location.CENTER) != find)
                {
                    cube_.rotate(Side.UP, false, 1).get();
                    cube_.rotate(Side.UP, false, 1, 1).get();
                }

                if(ls.getLocation().sideCorner() == Location.LEFT)
                {
                    cube_.rotate(ls.getSide(), true, 1).get();
                    cube_.rotate(Side.DOWN, true, 1).get();
                    cube_.rotate(ls.getSide(), false, 1).get();
                }
                else
                {
                    cube_.rotate(Side.DOWN, true, 1).get();
                    cube_.rotate(rotated.getSide(), true, 1).get();
                    cube_.rotate(Side.DOWN, false, 1).get();
                    cube_.rotate(rotated.getSide(), false, 1).get();
                }
            }
        }
    }

    /**
     * Places the middle edges in the correct places.
     *
     * <p>Step three for solving a cube.</p>
     */
    private void sideEdgeSolver() throws ExecutionException, InterruptedException
    {
        final Color yellow = cube_.getFace(Side.DOWN).getColor(Location.CENTER);

        ISearch search = (side, location, color) ->
                side == Side.DOWN
                        && cube_.getFace(side).getColor(location) != yellow
                        && new LocationSpace(side, location, color).flipEdge().determineColor(cube_) != yellow
                        && location.getMinor() == Location.Minor.EDGE;

        LocationSpace ls;
        while((ls = cube_.find(search).get()) != null)
        {
            LocationSpace flip = ls.flipEdge();
            Color find = flip.determineColor(cube_);

            while(cube_.getFace(flip.getSide()).getColor(Location.CENTER) != find)
            {
                cube_.rotate(Side.UP, false, 1).get();
                cube_.rotate(Side.UP, false, 1, 1).get();
            }
        }
    }

    @Override
    public void run()
    {
        try
        {
            daisyFlip();
            whiteCorner();
            sideEdgeSolver();
        }
        catch(ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
