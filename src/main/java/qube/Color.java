package qube;

import processing.core.PApplet;

public enum Color
{
    BLUE(2, 0, 255),
    GREEN(1, 255, 2),
    WHITE(255, 255, 255),
    YELLOW(255, 255, 1),
    ORANGE(255, 140, 2),
    RED(255, 0, 1),
    PURPLE(255, 0, 255);

    private final int r_, g_, b_;

    /**
     * Color values from 0-255.
     *
     * @param r Red.
     * @param g Green.
     * @param b Blue.
     */
    Color(int r, int g, int b)
    {
        r_ = r;
        b_ = b;
        g_ = g;
    }

    public void fill(PApplet canvas)
    {
        canvas.fill(r_, g_, b_);
    }
}
