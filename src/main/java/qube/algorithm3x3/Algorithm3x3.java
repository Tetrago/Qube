package qube.algorithm3x3;

import qube.Color;
import qube.Face;
import qube.LocationSpace;
import qube.Side;

import java.util.concurrent.*;

public class Algorithm3x3 implements Runnable
{
    private final ICube cube_;
    private CompletableFuture<Void> future_;

    public Algorithm3x3(ICube cube)
    {
        cube_ = cube;
    }

    /**
     * Solves the cube.
     */
    public void solve()
    {
        if(future_ == null || future_.isDone())
        {
            future_ = CompletableFuture.runAsync(this);
        }
    }

    /**
     * Perform the daisy step.
     *
     * <p>Step one for solving a cube.</p>
     */
    public void daisyFlip() throws ExecutionException, InterruptedException
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

    @Override
    public void run()
    {
        try
        {
            daisyFlip();
        }
        catch(ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
