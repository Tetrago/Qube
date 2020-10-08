package qube.algorithm3x3;

import qube.Color;
import qube.LocationSpace;
import qube.Side;

import java.util.concurrent.Future;

public interface ICube
{
    /**
     * Rotates the cube on the specified side.
     *
     * @param   side    Side to rotate.
     * @param   ccw     Whether to rotate counterclockwise.
     * @param   count   Number of times to rotate.
     *
     * @return          Future of rotation.
     */
    default Future<Void> rotate(Side side, boolean ccw, int count) { return rotate(side, ccw, count, 0); }

    /**
     * Rotates the cube on the specified side.
     *
     * @param   side    Side to rotate.
     * @param   ccw     Whether to rotate counterclockwise.
     * @param   count   Number of times to rotate.
     * @param   offset  Offset from side.
     *
     * @return          Future of rotation.
     */
    Future<Void> rotate(Side side, boolean ccw, int count, int offset);

    /**
     * Finds a location using conditions.
     *
     * @param   search  Search conditions.
     *
     * @return          {@link LocationSpace} or {@code null}.
     */
    Future<LocationSpace> find(ISearch search);

    /**
     * Gets the edges around a side
     *
     * @param   side    Side to look around.
     * @param   edge    Edge of side.
     *
     * @return          Colors on the edge.
     */
    Color[] getEdgeStrip(Side side, Side edge);

    IFace getFace(Side side);
}
