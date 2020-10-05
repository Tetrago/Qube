package qube;

import processing.core.PApplet;
import processing.core.PVector;
import qube.algorithm3x3.*;

public class Cube implements ICube
{
    private final int dimensions_;
    private final Face[] faces_;
    private final int tileSize_;

    /**
     * Constructs a cube with faces.
     *
     * @param   dimensions  Width and height of each face of the cube.
     */
    public Cube(int dimensions)
    {
        dimensions_ = dimensions;
        faces_ = new Face[6];

        Color[] colors = Color.values();
        for(int i = 0; i < 6; ++i)
        {
            faces_[i] = new Face(dimensions, colors[i]);
        }

        tileSize_ = Face.TARGET_SIDE_SIZE / dimensions;
    }

    /**
     * Scrambles a cube.
     *
     * @param   min Minimum turns.
     * @param   max Maximum turns.
     */
    public void scramble(int min, int max)
    {
        int num = (int)(Math.random() * max - min);

        for(int i = 0; i < num; ++i)
        {
            Side side = Side.values()[(int)(Math.random() * 6)];
            int offset = (int)(Math.random() * dimensions_);
            boolean ccw = (int)(Math.random() * 2) == 1;

            rotate(side, offset, ccw);
        }
    }

    /**
     * Draws cube.
     *
     * @param   canvas    Canvas to draw on.
     */
    public void draw(PApplet canvas)
    {
        for(Side side : Side.values())
        {
            PVector pos = side.getPosition();
            PVector rot = side.getRotation();

            float unit = tileSize_ * dimensions_ * 0.5f;

            canvas.pushMatrix();
            canvas.translate(pos.x * unit, pos.y * unit, pos.z * unit);
            canvas.rotate(side.getAngle(), rot.x, rot.y, rot.z);

            faces_[side.ordinal()].draw(canvas);

            canvas.popMatrix();
        }
    }

    @Override
    public void rotate(Side side, boolean ccw, int count)
    {
        for(int i = 0; i < count; ++i)
        {
            rotate(side, 0, ccw);
        }
    }

    @Override
    public boolean find(Search search)
    {
        for(Side side : Side.values())
        {
            Face face = faces_[side.ordinal()];

            for(Location location : Location.values())
            {
                search.fill(side, location);

                if(search.valid())
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public IFace getFace(Side side)
    {
        return faces_[side.ordinal()];
    }

    private static class DirectionOrder
    {
        private final Side from_, to_;
        private final Side fromSide_, toSide_;
        private final boolean reverse_;

        public DirectionOrder(Side from, Side fromSide, Side to, Side toSide, boolean reverse)
        {
            from_ = from;
            to_ = to;
            fromSide_ = fromSide;
            toSide_ = toSide;
            reverse_ = reverse;
        }

        /**
         * Caches the colors to set.
         *
         * @param   cube    Cube to get from.
         * @param   offset  Offset of side.
         *
         * @return          Returns colors of from {@link Side}.
         */
        public Color[] cache(Cube cube, int offset)
        {
            Face face = cube.faces_[from_.ordinal()];
            return face.get(face.retrieveIndices(fromSide_, offset, false));
        }

        /**
         * Sets the specified {@code colors} to the side.
         *
         * @param   cube    Cube to set to.
         * @param   colors  {@link Color} array to use.
         * @param   offset  Offset of side.
         */
        public void execute(Cube cube, Color[] colors, int offset)
        {
            Face face = cube.faces_[to_.ordinal()];
            face.set(face.retrieveIndices(toSide_, offset, reverse_), colors);
        }

        public Side getFrom() { return from_; }
        public Side getTo() { return to_; }
        public Side getFromSide() { return fromSide_; }
        public Side getToSide() { return toSide_; }
        public boolean isReverse() { return reverse_; }
    }

    /**
     * Rotates a face.
     *
     * @param   side    The side to rotate.
     * @param   offset  Offset from side.
     * @param   ccw     Whether to rotate counterclockwise.
     */
    public void rotate(Side side, int offset, boolean ccw)
    {
        if(offset == 0 || offset == dimensions_ - 1)
        {
            faces_[side.ordinal()].rotate(ccw);
        }

        internalRotate(side, offset);

        if(ccw)
        {
            for(int i = 0; i < 2; ++i)  // Three clockwise rotations is equivalent to one counterclockwise rotation
            {
                internalRotate(side, offset);
            }
        }
    }

    /**
     * Rotates the edges of a side.
     *
     * @param   side    Side edges to rotate.
     * @param   offset  Offset of rotation.
     */
    private void internalRotate(Side side, int offset)
    {
        DirectionOrder[] orders = getRotateOrders(side);
        Color[][] colors = new Color[4][];

        for(int i = 0; i < colors.length; ++i)
        {
            colors[i] = orders[i].cache(this, offset);
        }

        for(int i = 0; i < colors.length; ++i)
        {
            orders[i].execute(this, colors[i], offset);
        }
    }

    /**
     * Gets details necessary to rotate a cube.
     *
     * @param   side    The side to rotate.
     *
     * @return          Array of {@link DirectionOrder}s.
     */
    private DirectionOrder[] getRotateOrders(Side side)
    {
        // Can be simplified into map.

        switch(side)
        {
        default:
        case FRONT:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.LEFT, Side.RIGHT, Side.UP, Side.DOWN, true),        // Left
                    new DirectionOrder(Side.UP, Side.DOWN, Side.RIGHT, Side.LEFT, false),       // Up
                    new DirectionOrder(Side.RIGHT, Side.LEFT, Side.DOWN, Side.UP, true),        // Right
                    new DirectionOrder(Side.DOWN, Side.UP, Side.LEFT, Side.RIGHT, false),       // Down
            };
        case BACK:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.RIGHT, Side.RIGHT, Side.UP, Side.UP, false),        // Left
                    new DirectionOrder(Side.UP, Side.UP, Side.LEFT, Side.LEFT, true),           // Up
                    new DirectionOrder(Side.LEFT, Side.LEFT, Side.DOWN, Side.DOWN, false),      // Right
                    new DirectionOrder(Side.DOWN, Side.DOWN, Side.RIGHT, Side.RIGHT, true),     // Down
            };
        case UP:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.LEFT, Side.UP, Side.BACK, Side.UP, false),          // Left
                    new DirectionOrder(Side.BACK, Side.UP, Side.RIGHT, Side.UP, false),         // Up
                    new DirectionOrder(Side.RIGHT, Side.UP, Side.FRONT, Side.UP, false),         // Right
                    new DirectionOrder(Side.FRONT, Side.UP, Side.LEFT, Side.UP, false),         // Down
            };
        case DOWN:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.LEFT, Side.DOWN, Side.FRONT, Side.DOWN, false),     // Left
                    new DirectionOrder(Side.FRONT, Side.DOWN, Side.RIGHT, Side.DOWN, false),    // Up
                    new DirectionOrder(Side.RIGHT, Side.DOWN, Side.BACK, Side.DOWN, false),     // Right
                    new DirectionOrder(Side.BACK, Side.DOWN, Side.LEFT, Side.DOWN, false),      // Down
            };
        case LEFT:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.BACK, Side.RIGHT, Side.UP, Side.LEFT, true),        // Left
                    new DirectionOrder(Side.UP, Side.LEFT, Side.FRONT, Side.LEFT, false),       // Up
                    new DirectionOrder(Side.FRONT, Side.LEFT, Side.DOWN, Side.LEFT, false),     // Right
                    new DirectionOrder(Side.DOWN, Side.LEFT, Side.BACK, Side.RIGHT, true),      // Down
            };
        case RIGHT:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.FRONT, Side.RIGHT, Side.UP, Side.RIGHT, false),     // Left
                    new DirectionOrder(Side.UP, Side.RIGHT, Side.BACK, Side.LEFT, true),        // Up
                    new DirectionOrder(Side.BACK, Side.LEFT, Side.DOWN, Side.RIGHT, true),      // Right
                    new DirectionOrder(Side.DOWN, Side.RIGHT, Side.FRONT, Side.RIGHT, false)    // Down
            };
        }
    }

    public int getDimensions() { return dimensions_; }
}
