package qube;

import processing.core.PApplet;
import processing.core.PVector;

public class Cube
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

            canvas.pushMatrix();
            canvas.translate(pos.x * tileSize_ * dimensions_ * 0.5f, pos.y * tileSize_ * dimensions_ * -0.5f, pos.z * tileSize_ * dimensions_ * 0.5f);
            canvas.rotate(side.getAngle(), rot.x, rot.y, rot.z);

            faces_[side.ordinal()].draw(canvas);

            canvas.popMatrix();
        }
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
        case Front:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.Left, Side.Right, Side.Up, Side.Down, true),        // Left
                    new DirectionOrder(Side.Up, Side.Down, Side.Right, Side.Left, false),       // Up
                    new DirectionOrder(Side.Right, Side.Left, Side.Down, Side.Up, true),        // Right
                    new DirectionOrder(Side.Down, Side.Up, Side.Left, Side.Right, false),       // Down
            };
        case Back:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.Right, Side.Right, Side.Up, Side.Up, false),        // Left
                    new DirectionOrder(Side.Up, Side.Up, Side.Left, Side.Left, true),           // Up
                    new DirectionOrder(Side.Left, Side.Left, Side.Down, Side.Down, false),      // Right
                    new DirectionOrder(Side.Down, Side.Down, Side.Right, Side.Right, true),     // Down
            };
        case Up:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.Left, Side.Up, Side.Back, Side.Up, false),          // Left
                    new DirectionOrder(Side.Back, Side.Up, Side.Right, Side.Up, false),         // Up
                    new DirectionOrder(Side.Right, Side.Up, Side.Front, Side.Up, false),         // Right
                    new DirectionOrder(Side.Front, Side.Up, Side.Left, Side.Up, false),         // Down
            };
        case Down:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.Left, Side.Down, Side.Front, Side.Down, false),     // Left
                    new DirectionOrder(Side.Front, Side.Down, Side.Right, Side.Down, false),    // Up
                    new DirectionOrder(Side.Right, Side.Down, Side.Back, Side.Down, false),     // Right
                    new DirectionOrder(Side.Back, Side.Down, Side.Left, Side.Down, false),      // Down
            };
        case Left:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.Back, Side.Right, Side.Up, Side.Left, true),        // Left
                    new DirectionOrder(Side.Up, Side.Left, Side.Front, Side.Left, false),       // Up
                    new DirectionOrder(Side.Front, Side.Left, Side.Down, Side.Left, false),     // Right
                    new DirectionOrder(Side.Down, Side.Left, Side.Back, Side.Right, true),      // Down
            };
        case Right:
            return new DirectionOrder[] {
                    new DirectionOrder(Side.Front, Side.Right, Side.Up, Side.Right, false),     // Left
                    new DirectionOrder(Side.Up, Side.Right, Side.Back, Side.Left, true),        // Up
                    new DirectionOrder(Side.Back, Side.Left, Side.Down, Side.Right, true),      // Right
                    new DirectionOrder(Side.Down, Side.Right, Side.Front, Side.Right, false)    // Down
            };
        }
    }

    public int getDimensions() { return dimensions_; }
}
