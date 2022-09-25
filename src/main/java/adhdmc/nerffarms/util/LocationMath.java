package adhdmc.nerffarms.util;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LocationMath {

    /**
     * Returns a set containing the locations of locations adjacent to the start, toward a specified target.
     * The search will check locations (rotated around y) -45, 0, +45 degrees from the given direction.
     * @param start Location to calculate math from.
     * @param target Location to determine direction
     * @return Set of block locations in this direction.
     */
    public static @NotNull Set<Location> getAdjacentTowards(@NotNull Location start, @NotNull Location target) {
        HashSet<Location> locations = new HashSet<>();
        start = start.toBlockLocation();
        target = target.toBlockLocation();
        double x = target.getX() - start.getX();
        double y = target.getY() - start.getY();
        double z = target.getZ() - start.getZ();
        double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z, 2));
        Vector v = new Vector(x/d, y/d, z/d);
        locations.add(start.add(v).toBlockLocation());
        start.subtract(v);
        v.rotateAroundY(-Math.PI/4);
        locations.add(start.add(v).toBlockLocation());
        start.subtract(v);
        v.rotateAroundY(Math.PI/2);
        locations.add(start.add(v).toBlockLocation());
        return locations;
    }

    /**
     * Returns a list of locations that form a square of side length r+1, centered around a specified location.
     * The search will match the y-coordinate of the given location.
     * @param loc Location to calculate the ring around.
     * @param r Distance from the given location.
     * @return Set of locations that form the square.
     */
    public static @NotNull Set<Location> getSquareAround(@NotNull Location loc, int r) {
        HashSet<Location> locations = new HashSet<>();
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                locations.add(loc.add(x, 0, z));
            }
        }
        return locations;
    }

}
