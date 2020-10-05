package qube.algorithm3x3;

import qube.Side;

public interface ICube
{
    default void rotate(Side side, boolean ccw)
    {
        rotate(side, ccw, 1);
    }

    void rotate(Side side, boolean ccw, int count);
    boolean find(Search search);

    IFace getFace(Side side);
}
