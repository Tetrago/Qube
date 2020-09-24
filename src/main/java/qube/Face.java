package qube;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Face
{
    private int dimensions_;
    private Color[] colors_;
    private final int[] borderSeq_;

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

        borderSeq_ = new int[dimensions * 2 + (dimensions - 2) * 2]; // Adds the lengths of two horizontal and two vertical sides.
        int i = 0;

        for(int y = 0; y < dimensions_; ++y)
        {
            for(int x = 0; x < dimensions_; ++x)
            {
                if(x == 0 || x == dimensions_ - 1 || y == 0 || y == dimensions_ - 1)
                {
                    borderSeq_[i++] =  y * dimensions_ + x;
                }
            }
        }
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
            cache.add(0, cache.get(cache.size() - 1));
            cache.remove(cache.size() - 1);
        }
        else        // Counterclockwise
        {
            cache.add(cache.get(0));
            cache.remove(0);
        }

        int index = 0;
        for(int i : borderSeq_)
        {
            colors_[i] = cache.get(index++);
        }
    }

    /**
     * Retrieves a set of colors based on a pattern.
     *
     * <p>For example, "1XX2XX3XX" would get the left side of this face, if it has the dimension of three.</p>
     *
     * @param   pattern Pattern string.
     *
     * @return          Array of colors.
     */
    public Color[] retrieve(String pattern)
    {
        int[] indices = parsePatternIndices(pattern);
        Color[] colors = new Color[indices.length];

        for(int i = 0; i < indices.length; ++i)
        {
            colors[i] = colors_[indices[i]];
        }

        return colors;
    }

    /**
     * Modifies a set of colors based on a pattern.
     *
     * <p>For example, "1XX2XX3XX" would set the left side of this face, if it has the dimension of three.</p>
     *
     * @param   pattern Pattern string.
     * @param   colors  Colors to replace with.
     */
    public void modify(String pattern, Color[] colors)
    {
        int[] indices = parsePatternIndices(pattern);

        for(int i = 0; i < indices.length; ++i)
        {
            colors_[indices[i]] = colors[i];
        }
    }

    /**
     * Parses color pattern into array indexes.
     *
     * @param   pattern Pattern to parse.
     *
     * @return          Array of corresponding indexes.
     */
    private int[] parsePatternIndices(String pattern)
    {
          int count = pattern.replaceAll("\\D", "").length();
          int[] indices = new int[count];

          int index = 0;

          for(int i = 0; i < pattern.length(); ++i)
          {
              if(pattern.charAt(i) == 'X') continue;
              indices[Integer.parseInt(pattern.substring(i, i + 1))] = i;
          }

          return indices;
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
                canvas.rect((x - dimensions_ * 0.5f) * Cube.TILE_SIZE, (y - dimensions_ * 0.5f) * Cube.TILE_SIZE, Cube.TILE_SIZE, Cube.TILE_SIZE);
            }
        }

        canvas.popMatrix();
    }
}
