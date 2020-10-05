package qube;

import processing.core.PConstants;
import qube.algorithm3x3.Algorithm3x3;

public class User
{
    private final Cube cube_;
    private boolean ccw_ = false;
    private Algorithm3x3 algorithm_;

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

        switch(Character.toLowerCase(key))
        {
        case 'f':
            cube_.rotate(Side.FRONT, 0, ccw_);
            break;
        case 'b':
            cube_.rotate(Side.BACK, 0, ccw_);
            break;
        case 'u':
            cube_.rotate(Side.UP, 0, ccw_);
            break;
        case 'd':
            cube_.rotate(Side.DOWN, 0, ccw_);
            break;
        case 'r':
            cube_.rotate(Side.RIGHT, 0, ccw_);
            break;
        case 'l':
            cube_.rotate(Side.LEFT, 0, ccw_);
            break;
        case 's':
            cube_.scramble(100, 200);
            break;
        case ' ':
            algorithm_.daisy();
            break;
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
