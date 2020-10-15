package qube;

import processing.core.PConstants;
import qube.algorithm3x3.Algorithm3x3;

import java.util.concurrent.Future;

public class User
{
    private final Cube cube_;
    private boolean ccw_ = false;
    private final Algorithm3x3 algorithm_;
    private Future<Void> future_;

    public User(Cube cube)
    {
        algorithm_ = new Algorithm3x3(cube_ = cube);
    }

    /**
     * Notifies key pressed.
     *
     * @param   key     The key pressed.
     * @param   keyCode Possible key code pressed.
     */
    public void keyPressed(char key, int keyCode)
    {
        if(key == PConstants.CODED && keyCode == PConstants.SHIFT)
        {
            ccw_ = true;
        }
        else if(key == 's')
        {
            cube_.scramble(2500, 5000, 0);
        }

        if(future_ == null || future_.isDone())
        {
            switch(Character.toLowerCase(key))
            {
            case 'f':
                future_ = cube_.rotate(Side.FRONT, ccw_, 1);
                break;
            case 'b':
                future_ = cube_.rotate(Side.BACK, ccw_, 1);
                break;
            case 'u':
                future_ = cube_.rotate(Side.UP, ccw_, 1);
                break;
            case 'd':
                future_ = cube_.rotate(Side.DOWN, ccw_, 1);
                break;
            case 'r':
                future_ = cube_.rotate(Side.RIGHT, ccw_, 1);
                break;
            case 'l':
                future_ = cube_.rotate(Side.LEFT, ccw_, 1);
                break;
            case ' ':
                future_ = algorithm_.solve();
                break;
            }
        }
    }

    /**
     * Notifies key released.
     *
     * @param   key     The key released.
     * @param   keyCode Possible key code released.
     */
    public void keyReleased(char key, int keyCode)
    {
        if(key == PConstants.CODED && keyCode == PConstants.SHIFT)
        {
            ccw_ = false;
        }
    }
}
