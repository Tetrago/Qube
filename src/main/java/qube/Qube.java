package qube;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class Qube extends PApplet
{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 576;

    public static void main(String[] args)
    {
        PApplet.main(Qube.class.getName());
    }

    private Camera camera_ = new Camera();
    private Cube cube_ = new Cube(10);
    private User user_;

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
        cube_.draw(this);
    }
}
