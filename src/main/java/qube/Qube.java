package qube;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class Qube extends PApplet
{
    private static boolean debug_;

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 576;

    public static void main(String[] args)
    {
        PApplet.main(Qube.class.getName());
    }

    private final Camera camera_ = new Camera();
    private final Cube cube_ = new Cube(3);
    private final User user_;

    public Qube()
    {
        user_ = new User(cube_);
    }

    @Override
    public void settings()
    {
        size(WIDTH, HEIGHT, PConstants.P3D);
    }

    @Override
    public void mouseDragged()
    {
        camera_.notifyMouseDragged(this);
    }

    @Override
    public void mouseWheel(MouseEvent event)
    {
        camera_.notifyMouseWheel(event.getCount());
    }

    @Override
    public void keyPressed()
    {
        user_.keyPressed(key, keyCode);

        if(key == TAB)
        {
            debug_ = !debug_;
        }
    }

    @Override
    public void keyReleased()
    {
        user_.keyReleased(key, keyCode);
    }

    @Override
    public void draw()
    {
        background(220);
        translate(width / 2.0f, height / 2.0f, 0);

        camera_.draw(this);

        if(debug_)
        {
            final int size = Face.TARGET_SIDE_SIZE * 2;

            stroke(192, 0, 0);
            line(0, 0, 0, size, 0, 0);

            stroke(0, 192, 0);
            line(0, 0, 0, 0, size, 0);

            stroke(0, 0, 192);
            line(0, 0, 0, 0, 0, size);
        }

        cube_.draw(this);
    }

    public static boolean isDebug() { return debug_; }
}
