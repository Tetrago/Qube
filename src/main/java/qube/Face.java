package qube;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Face
{
    public static final int TARGET_SIDE_SIZE = 300;

    private int dimensions_;
    private Color[] colors_;
    private final int[] borderSeq_;
    private final int tileSize_;

    /**
     * Constructs face.
     *
     * @param   dimensions  Width and height of side.
     * @param   color       The starting color of the side.
     */
    public Face(int dimensions, Color color)
    {
        dimensions_ = dimensions;
        colors_ = new Color[dimensions * dimensions];
        Arrays.fill(colors_, color);

        IntStream top = IntStream.range(0, dimensions - 1);
        IntStream right = IntStream.iterate(dimensions - 1, n -> n + dimensions).limit(dimensions - 1);
        IntStream down = IntStream.iterate(dimensions * dimensions - 1, n -> n - 1).limit(dimensions - 1);
        IntStream left = IntStream.iterate(dimensions * (dimensions - 1), n -> n - dimensions).limit(dimensions - 1);

        borderSeq_ = IntStream.concat(IntStream.concat(top, right), IntStream.concat(down, left)).toArray();
        tileSize_ = TARGET_SIDE_SIZE / dimensions;
    }

    /**
     * Rotates the face.
     *
     * @param   ccw Whether to rotate counterclockwise.
     */
    public void rotate(boolean ccw)
    {
        List<Color> cache = new ArrayList<>(borderSeq_.length);

        for(int i : borderSeq_)
        {
            cache.add(colors_[i]);
        }

        if(!ccw)    // Clockwise
        {
            Collections.rotate(cache, dimensions_ - 1);
        }
        else        // Counterclockwise
        {
            Collections.rotate(cache, -(dimensions_ - 1));
        }

        int index = 0;
        for(int i : borderSeq_)
        {
            colors_[i] = cache.get(index++);
        }
    }

    /**
     * Gets indices of side.
     *
     * @param   side    Side to generate array for.
     * @param   reverse Whether to reverse the indices.
     *
     * @return          Array of indices.
     */
    private int[] retrieveIndices(Side side, boolean reverse)
    {
        IntStream stream;

        switch(side)
        {
        default:
        case Up:
            stream = IntStream.range(0, dimensions_);
            break;
        case Down:
            int offset = dimensions_ * (dimensions_ - 1);
            stream = IntStream.range(offset, offset + dimensions_);
            break;
        case Right:
            stream = IntStream.iterate(dimensions_ - 1, n -> n + dimensions_).limit(dimensions_);
            break;
        case Left:
            stream = IntStream.iterate(0, n -> n + dimensions_).limit(dimensions_);
            break;
        }

        if(reverse)
        {
            return stream.boxed().sorted(Collections.reverseOrder()).mapToInt(i -> i).toArray();
        }

        return stream.toArray();
    }

    /**
     * Gets colors on edge.
     *
     * @param   side    Side to get colors from.
     *
     * @return          Array of colors on {@code side}.
     */
    public Color[] get(Side side)
    {
        return parseIndices(retrieveIndices(side, false));
    }

    /**
     * Sets colors on edge.
     *
     * @param   side    Side to get colors from.
     * @param   colors  Array of colors on {@code side}.
     * @param   reverse Whether to reverse the array.
     */
    public void set(Side side, Color[] colors, boolean reverse)
    {
        parseIndices(retrieveIndices(side, reverse), colors);
    }

    /**
     * Gets colors using indices.
     *
     * @param   indices Array of indices of colors to return.
     *
     * @return          Array of colors.
     */
    private Color[] parseIndices(int[] indices)
    {
        Color[] colors = new Color[indices.length];

        int index = 0;
        for(int i : indices)
        {
            colors[index++] = colors_[i];
        }

        return colors;
    }

    /**
     * Sets colors using indices.
     *
     * @param   indices Array of indices to set.
     * @param   colors  Array of colors to set.
     */
    private void parseIndices(int[] indices, Color[] colors)
    {
        int index = 0;
        for(int i : indices)
        {
            colors_[i] = colors[index++];
        }
    }

    /**
     * Draws face.
     *
     * @param   canvas  Canvas to draw on.
     */
    public void draw(PApplet canvas)
    {
        canvas.pushMatrix();
        canvas.stroke(10);

        for(int y = 0; y < dimensions_; ++y)
        {
            for(int x = 0; x < dimensions_; ++x)
            {
                colors_[y * dimensions_ + x].fill(canvas);
                canvas.rect((x - dimensions_ * 0.5f) * tileSize_, (y - dimensions_ * 0.5f) * tileSize_, tileSize_, tileSize_);
            }
        }

        canvas.popMatrix();
    }
}
