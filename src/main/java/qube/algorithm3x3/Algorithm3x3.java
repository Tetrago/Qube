package qube.algorithm3x3;

import qube.Color;
import qube.LocationSpace;
import qube.Side;

import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

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
                cube_.rotate(ls.getSide(), false, 1).get();
                cube_.rotate(Side.DOWN, false, 2).get();
                cube_.rotate(ls.getSide(), true, 1).get();
            }
            else if(ls.rotateCorner().rotateCorner().getSide() == Side.UP)
            {
                cube_.rotate(ls.getSide(), true, 1).get();
                cube_.rotate(Side.DOWN, true, 1).get();
                cube_.rotate(ls.getSide(), false, 1).get();
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

        ISearch search = (side, location, color) -> side == Side.DOWN
                && location.getMinor() == Location.Minor.EDGE
                && color != yellow
                && new LocationSpace(side, location, color).flipEdge().determineColor(cube_) != yellow;

        ISearch incorrect = (side, location, color) ->
        {
            LocationSpace space = new LocationSpace(side, location, color);
            for(int i = 0; i < 2; ++i)  // Used to test both sides of the edge.
            {
                if(space.getSide() == Side.DOWN || space.getSide() == Side.UP || space.getLocation().getMinor() != Location.Minor.EDGE)
                {
                    return false;
                }

                space = space.flipEdge();
            }

            return color == yellow || space.determineColor(cube_) == yellow
                    || color != cube_.getFace(side).getColor(Location.CENTER)
                    || space.determineColor(cube_) != cube_.getFace(space.getSide()).getColor(Location.CENTER);
        };

        while(true)
        {
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

                boolean sameColorIsClockwise =
                        cube_.getFace(flip.getSide().move(Side.RIGHT)).getColor(Location.CENTER) == ls.getColor();

                Side opposite = flip.getSide().move(sameColorIsClockwise ? Side.RIGHT : Side.LEFT);

                cube_.rotate(Side.DOWN, sameColorIsClockwise, 1).get();
                cube_.rotate(opposite, sameColorIsClockwise, 1).get();
                cube_.rotate(Side.DOWN, !sameColorIsClockwise, 1).get();
                cube_.rotate(opposite, !sameColorIsClockwise, 1).get();

                cube_.rotate(Side.DOWN, !sameColorIsClockwise, 1).get();
                cube_.rotate(flip.getSide(), !sameColorIsClockwise, 1).get();
                cube_.rotate(Side.DOWN, sameColorIsClockwise, 1).get();
                cube_.rotate(flip.getSide(), sameColorIsClockwise, 1).get();
            }

            boolean changed = false;
            if((ls = cube_.find(incorrect).get()) != null)
            {
                if(ls.getLocation().sideCorner() != Location.RIGHT)
                {
                    ls = ls.flipEdge();
                }

                cube_.rotate(ls.flipEdge().getSide(), true, 1).get();
                cube_.rotate(Side.DOWN, false, 1).get();
                cube_.rotate(ls.flipEdge().getSide(), false, 1).get();

                cube_.rotate(Side.DOWN, false, 1).get();
                cube_.rotate(ls.getSide(), false, 1).get();
                cube_.rotate(Side.DOWN, true, 1).get();
                cube_.rotate(ls.getSide(), true, 1).get();

                changed = true;
            }

            if(!changed)
            {
                break;
            }
        }
    }

    /**
     * Places the bottom colors.
     *
     * <p>Step four for solving a cube.</p>
     */
    private void star() throws ExecutionException, InterruptedException
    {
        DownShape shape;
        while((shape = new DownShape(cube_)).determineFormation() != DownShape.Formation.STAR)
        {
            DownShape.Formation formation = shape.determineFormation();
            if(formation == DownShape.Formation.HOOK || formation == DownShape.Formation.LINE)
            {
                Predicate<DownShape> valid = formation == DownShape.Formation.HOOK
                        ? ds -> ds.north() && ds.west()
                        : ds -> ds.east() && ds.west();

                DownShape wtf;
                while(!valid.test(wtf = new DownShape(cube_)))
                {
                    cube_.rotate(Side.DOWN, false, 1).get();
                }
            }

            cube_.rotate(Side.FRONT, false, 1).get();
            cube_.rotate(Side.LEFT, false, 1).get();
            cube_.rotate(Side.DOWN, false, 1).get();

            cube_.rotate(Side.LEFT, true, 1).get();
            cube_.rotate(Side.DOWN, true, 1).get();
            cube_.rotate(Side.FRONT, true, 1).get();
        }
    }

    /**
     * Fills in the bottom side.
     *
     * <p>Step five for solving a cube.</p>
     */
    private void downSide() throws ExecutionException, InterruptedException
    {
        final Color yellow = cube_.getFace(Side.DOWN).getColor(Location.CENTER);

        DownCorner corner;
        while((corner = new DownCorner(cube_)).determineFormation() != DownCorner.Formation.DONE)
        {
            switch(corner.determineFormation())
            {
            case NONE:
                final BooleanSupplier test = () -> (cube_.getFace(Side.BACK).getColor(Location.BOTTOM_RIGHT) == yellow
                        && cube_.getFace(Side.RIGHT).getColor(Location.BOTTOM_LEFT) == yellow
                        && cube_.getFace(Side.RIGHT).getColor(Location.BOTTOM_RIGHT) == yellow
                        && cube_.getFace(Side.FRONT).getColor(Location.BOTTOM_LEFT) == yellow)
                        || (cube_.getFace(Side.LEFT).getColor(Location.BOTTOM_LEFT) == yellow
                        && cube_.getFace(Side.LEFT).getColor(Location.BOTTOM_RIGHT) == yellow
                        && cube_.getFace(Side.RIGHT).getColor(Location.BOTTOM_LEFT) == yellow
                        && cube_.getFace(Side.RIGHT).getColor(Location.BOTTOM_RIGHT) == yellow);

                while(!test.getAsBoolean())
                {
                    cube_.rotate(Side.DOWN, false, 1).get();
                }
                break;
            case TWO_LINE:
                final BooleanSupplier supplier = () -> (cube_.getFace(Side.RIGHT).getColor(Location.BOTTOM_RIGHT) == yellow
                    && cube_.getFace(Side.RIGHT).getColor(Location.BOTTOM_LEFT) == yellow)
                        || cube_.getFace(Side.FRONT).getColor(Location.BOTTOM_RIGHT) == yellow;

                while(!supplier.getAsBoolean())
                {
                    cube_.rotate(Side.DOWN, false, 1).get();
                }
                break;
            case ONE:
                while(!(new DownCorner(cube_).northEast()))
                {
                    cube_.rotate(Side.DOWN, false, 1).get();
                }
                break;
            }

            cube_.rotate(Side.LEFT, false, 1).get();
            cube_.rotate(Side.DOWN, false, 1).get();
            cube_.rotate(Side.LEFT, true, 1).get();
            cube_.rotate(Side.DOWN, false, 1).get();
            cube_.rotate(Side.LEFT, false, 1).get();
            cube_.rotate(Side.DOWN, false, 2).get();
            cube_.rotate(Side.LEFT, true, 1).get();
        }
    }

    /**
     * Completes most of the cube.
     *
     * <p>Step six for solving a cube.</p>
     */
    private void completeSolver() throws ExecutionException, InterruptedException
    {
        BooleanSupplier tester = () ->
        {
            IFace back = cube_.getFace(Side.BACK);
            return back.getColor(Location.BOTTOM_LEFT) == back.getColor(Location.BOTTOM_RIGHT);
        };

        boolean repeat;
        do
        {
            repeat = false;

            int counter = 0;
            while(!tester.getAsBoolean())
            {
                cube_.rotate(Side.DOWN, false, 1).get();
                ++counter;

                if(counter > 8)
                {
                    repeat = true;
                    break;
                }
            }

            cube_.rotate(Side.LEFT, true, 1).get();
            cube_.rotate(Side.FRONT, false, 1).get();
            cube_.rotate(Side.LEFT, true, 1).get();
            cube_.rotate(Side.BACK, false, 2).get();
            cube_.rotate(Side.LEFT, false, 1).get();
            cube_.rotate(Side.FRONT, true, 1).get();
            cube_.rotate(Side.LEFT, true, 1).get();
            cube_.rotate(Side.BACK, false, 2).get();
            cube_.rotate(Side.LEFT, false, 2).get();
        } while(repeat);
    }

    /**
     * Cycles the bottom corners of the cube.
     *
     * <p>Used during the headlights step.</p>
     *
     * @param   cube    Cube to rotate the edges of.
     */
    private void cycleEdges(ICube cube) throws ExecutionException, InterruptedException
    {
        cube.rotate(Side.LEFT, false, 1).get();
        cube.rotate(Side.DOWN, true, 1).get();
        cube.rotate(Side.LEFT, false, 1).get();
        cube.rotate(Side.DOWN, false, 1).get();
        cube.rotate(Side.LEFT, false, 1).get();
        cube.rotate(Side.DOWN, false, 1).get();
        cube.rotate(Side.LEFT, false, 1).get();
        cube.rotate(Side.DOWN, true, 1).get();
        cube.rotate(Side.LEFT, true, 1).get();
        cube.rotate(Side.DOWN, true, 1).get();
        cube.rotate(Side.LEFT, false, 2).get();
    }

    /**
     * Places bottom edges.
     *
     * <p>Step seven and the final for solving a cube.</p>
     */
    private void headlights() throws ExecutionException, InterruptedException
    {
        ISearch search = (side, location, color) -> location.getMinor() == Location.Minor.EDGE
                && new LocationSpace(side, location, color).flipEdge().getSide() == Side.DOWN
                && cube_.getFace(side).getColor(Location.BOTTOM_LEFT) == color;

        if(cube_.find(search).get() == null)
        {
            cycleEdges(cube_);
        }

        ISearch lineupSearch = (side, location, color) -> location.getMinor() == Location.Minor.EDGE
                && new LocationSpace(side, location, color).flipEdge().getSide() == Side.DOWN
                && cube_.getFace(side).getColor(Location.CENTER) == color
                && cube_.getFace(side).getColor(Location.BOTTOM_LEFT) == color;

        LocationSpace ls;
        while((ls = cube_.find(lineupSearch).get()) == null)
        {
            cube_.rotate(Side.DOWN, false, 1).get();
        }

        ICube remapped = SideRemappedCube.bind(cube_).rebase(Side.BACK, ls.getSide()).build();

        IFace front = cube_.getFace(Side.FRONT);
        while(front.getColor(Location.CENTER) != front.getColor(Location.BOTTOM))
        {
            cycleEdges(remapped);
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
            star();
            downSide();
            completeSolver();
            headlights();
        }
        catch(ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
