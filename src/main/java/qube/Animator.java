package qube;

import processing.core.PApplet;
import qube.algorithm3x3.Location;

import java.util.Arrays;

public class Animator
{
    private static final Location[] LOCATION_ORDER = { Location.LEFT, Location.TOP, Location.RIGHT, Location.BOTTOM };

    private final Cube cube_;
    private final float speed_;
    private boolean rotating_;
    private Side side_;
    private int ccw_;
    private int offset_;
    private float progress_;
    private final int[][] blacklist_ = new int[4][];
    private final Color[][] colors_ = new Color[4][];
    private final Object mutex = new Object();

    public Animator(Cube cube, float speed)
    {
        cube_ = cube;
        speed_ = speed;
    }

    /**
     * Checks if the index on the side should be drawn.
     *
     * @param   side    Side of index.
     * @param   index   Index on side.
     *
     * @return          Whether the side and index are blacklisted.
     */
    public boolean isBlacklisted(Side side, int index)
    {
        if(!rotating_)
        {
            return false;
        }

        for(int i = 0; i < 4; ++i)
        {
            if(side == Location.getEdgeOuterSide(side_, LOCATION_ORDER[i]))
            {
                return Arrays.stream(blacklist_[i]).anyMatch(n -> n == index);
            }
        }

        return false;
    }

    /**
     * Applies rotation for side.
     *
     * @param   side    Side to rotate.
     * @param   ccw     Counterclockwise.
     * @param   offset  Offset from side.
     */
    public void rotate(Side side, boolean ccw, int offset)
    {
        synchronized(mutex)
        {
            rotating_ = true;
            side_ = side;
            ccw_ = ccw ? -1 : 1;
            offset_ = offset;
            progress_ = 0;

            for(int i = 0; i < 4; ++i)
            {
                Face face = (Face)cube_.getFace(Location.getEdgeOuterSide(side_, LOCATION_ORDER[i]));
                blacklist_[i] = getRelativeEdge(side_, LOCATION_ORDER[i], offset_);
                colors_[i] = face.getColors(blacklist_[i]);
            }
        }
    }

    /**
     * Gets the colors on the specified edge relative to the current side.
     *
     * <p>Colors will always be ordered left to right and up to down.</p>
     *
     * @param   side        Current side.
     * @param   location    Edge specifying side to get.
     * @param   offset      Offset of get.
     *
     * @return              Colors indices.
     */
    private int[] getRelativeEdge(Side side, Location location, int offset)
    {
        Side outer = Location.getEdgeOuterSide(side, location);
        Face face = (Face)cube_.getFace(outer);

        switch(side)
        {
        case FRONT:
            switch(outer)
            {
            case UP: return face.retrieveIndices(Side.DOWN, offset, false);
            case DOWN: return face.retrieveIndices(Side.UP, offset, false);
            case RIGHT: return face.retrieveIndices(Side.LEFT, offset, false);
            case LEFT: return face.retrieveIndices(Side.RIGHT, offset, false);
            }
        case BACK:
            switch(outer)
            {
            case UP: return face.retrieveIndices(Side.UP, offset, true);
            case DOWN: return face.retrieveIndices(Side.DOWN, offset, true);
            case RIGHT: return face.retrieveIndices(Side.RIGHT, offset, false);
            case LEFT: return face.retrieveIndices(Side.LEFT, offset, false);
            }
        case UP:
            switch(outer)
            {
            case FRONT: return face.retrieveIndices(Side.UP, offset, false);
            case BACK: return face.retrieveIndices(Side.UP, offset, true);
            case RIGHT: return face.retrieveIndices(Side.UP, offset, true);
            case LEFT: return face.retrieveIndices(Side.UP, offset, false);
            }
        case DOWN:
            switch(outer)
            {
            case FRONT: return face.retrieveIndices(Side.DOWN, offset, false);
            case BACK: return face.retrieveIndices(Side.DOWN, offset, true);
            case RIGHT: return face.retrieveIndices(Side.DOWN, offset, false);
            case LEFT: return face.retrieveIndices(Side.DOWN, offset, true);
            }
        case RIGHT:
            switch(outer)
            {
            case FRONT: return face.retrieveIndices(Side.RIGHT, offset, false);
            case BACK: return face.retrieveIndices(Side.LEFT, offset, false);
            case UP: return face.retrieveIndices(Side.RIGHT, offset, true);
            case DOWN: return face.retrieveIndices(Side.RIGHT, offset, false);
            }
        case LEFT:
            switch(outer)
            {
            case FRONT: return face.retrieveIndices(Side.LEFT, offset, false);
            case BACK: return face.retrieveIndices(Side.RIGHT, offset, false);
            case UP: return face.retrieveIndices(Side.LEFT, offset, false);
            case DOWN: return face.retrieveIndices(Side.LEFT, offset, true);
            }
        }

        throw new IllegalStateException("Unknown error occurred");
    }

    /**
     * Applies possible rotation.
     *
     * @param   canvas  {@link PApplet} to work on.
     * @param   side    {@link Side} that is about to be drawn.
     */
    public void apply(PApplet canvas, Side side)
    {
        synchronized(mutex)
        {
            if(progress_ > 90)
            {
                rotating_ = false;

                for(int i = 0; i < 4; ++i)
                {
                    Arrays.fill(blacklist_[i], -1);
                }

                synchronized(this)
                {
                    notify();
                }
            }

            final float tileSize = (float)Face.TARGET_FACE_SIDE / cube_.getDimensions();
            if(rotating_ && side == side_)
            {
                if(offset_ != 0)
                {
                    canvas.pushMatrix();
                }

                canvas.rotateZ(PApplet.radians(progress_) * ccw_);

                final float[] xyz = { 0, 90, 0, -90, 0, -90, 0, -90, 0, 90, 0, -90 };
                final float[] offsets = { 0, -tileSize, -tileSize, 0 };

                for(int i = 0; i < 4; ++i)
                {
                    canvas.pushMatrix();

                    int move = cube_.getDimensions() / 2 - offset_ - 1;
                    canvas.translate(0, 0, tileSize * move);

                    canvas.rotateX(PApplet.radians(xyz[i * 3]));
                    canvas.rotateY(PApplet.radians(xyz[i * 3 + 1]));
                    canvas.rotateZ(PApplet.radians(xyz[i * 3 + 2]));

                    canvas.translate(0, 0, -Face.TARGET_FACE_SIDE * 0.5f);
                    canvas.stroke(10);

                    canvas.translate(0, -Face.TARGET_FACE_SIDE * 0.5f);

                    for(int j = 0; j < cube_.getDimensions(); ++j)
                    {
                        colors_[i][j].fill(canvas);
                        canvas.rect(offsets[i], j * tileSize, tileSize, tileSize);
                    }

                    canvas.popMatrix();
                }

                if(offset_ != 0)
                {
                    canvas.popMatrix();
                }

                progress_ += speed_;
            }
        }
    }
}
