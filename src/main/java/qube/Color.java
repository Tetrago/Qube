package qube;

import processing.core.PApplet;

public enum Color
{
    Blue(2, 0, 255),
    Green(1, 255, 2),
    White(255, 255, 255),
    Yellow(255, 255, 1),
    Orange(255, 140, 2),
    Red(255, 0, 1);

    private int r_, g_, b_;

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
