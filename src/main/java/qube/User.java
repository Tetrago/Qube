package qube;

import processing.core.PConstants;

public class User
{
    private final Cube cube_;
    private boolean ccw_ = false;

    public User(Cube cube)
    {
        cube_ = cube;
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
            cube_.rotate(Side.Front, 0, ccw_);
            break;
        case 'b':
            cube_.rotate(Side.Back, 0, ccw_);
            break;
        case 'u':
            cube_.rotate(Side.Up, 0, ccw_);
            break;
        case 'd':
            cube_.rotate(Side.Down, 0, ccw_);
            break;
        case 'r':
            cube_.rotate(Side.Right, 0, ccw_);
            break;
        case 'l':
            cube_.rotate(Side.Left, 0, ccw_);
            break;
        case 's':
            cube_.scramble(100, 200);
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
