package qube.algorithm3x3;

import qube.Color;
import qube.LocationSpace;
import qube.Side;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Used to make rotations relative to a side.
 */
public class SideRemappedCube implements ICube
{
    private final ICube cube_;
    private final Side[] sides_;

    public static class Factory
    {
        private final ICube cube_;
        private final Side[] sides_;

        private Factory(ICube cube)
        {
            cube_ = cube;

            sides_ = new Side[Side.values().length];
            for(Side side : Side.values())
            {
                sides_[side.ordinal()] = side;
            }
        }

        /**
         * Remaps side {@code from} to side {@code to}.
         *
         * @param   from    Side to remap from.
         * @param   to      Side to remap to.
         *
         * @return          Self.
         */
        public Factory remap(Side from, Side to)
        {
            sides_[from.ordinal()] = to;

            return this;
        }

        /**
         * Completely remaps the cube based on the mapping of {@code from} to {@code to}.
         *
         * @param   from    Side to remap.
         * @param   to      Side to remap {@code from} to.
         *
         * @return          Self.
         */
        public Factory rebase(Side from, Side to)
        {
            remap(from, to);
            remap(from.move(Side.LEFT).move(Side.LEFT), to.move(Side.LEFT).move(Side.LEFT));
            remap(from.move(Side.LEFT), to.move(Side.LEFT));
            remap(from.move(Side.RIGHT), to.move(Side.RIGHT));
            remap(from.move(Side.UP), to.move(Side.UP));
            remap(from.move(Side.DOWN), to.move(Side.DOWN));

            return this;
        }

        /**
         * Creates a {@link SideRemappedCube} from configured values.
         *
         * @return  Constructed and configured {@link SideRemappedCube}.
         */
        public ICube build()
        {
            return new SideRemappedCube(cube_, sides_);
        }
    }

    public static Factory bind(ICube cube)
    {
        return new Factory(cube);
    }

    private SideRemappedCube(ICube cube, Side[] sides)
    {
        cube_ = cube;
        sides_ = sides;
    }

    @Override
    public Future<Void> rotate(Side side, boolean ccw, int count, int offset)
    {
        return cube_.rotate(getRemappedSide(side), ccw, count, offset);
    }

    @Override
    public Future<LocationSpace> find(ISearch search)
    {
        return cube_.find(search);
    }

    @Override
    public Future<List<LocationSpace>> findAll(ISearch search)
    {
        return cube_.findAll(search);
    }

    @Override
    public Color[] getEdgeStrip(Side side, Side edge)
    {
        return cube_.getEdgeStrip(getRemappedSide(side), edge);
    }

    @Override
    public IFace getFace(Side side)
    {
        return cube_.getFace(getRemappedSide(side));
    }

    @Override
    public boolean isComplete()
    {
        return cube_.isComplete();
    }

    /**
     * Gets the side remapped from {@code side}.
     *
     * @param   side    Relative side to find,
     *
     * @return          Remapped side.
     */
    private Side getRemappedSide(Side side)
    {
        return sides_[side.ordinal()];
    }
}
