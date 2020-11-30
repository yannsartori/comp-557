// Yann Sartori 260822776
package comp557.a4;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Light {
	
	/** Light name */
    public String name = "";
    
    /** Light colour, default is white */
    public Color4f color = new Color4f(1,1,1,1);
    
    /** Light position, default is the origin */
    public Point3d from = new Point3d(0,0,0);
    
    /** Light intensity, I, combined with colour is used in shading */
    public double power = 1.0;
    
    /** Type of light, default is a point light */
    public String type = "point";
    
    /** Number of samples for softer shadows */
    public float shadowSamples = 1;
    
    /** Radius of the light (only pertinent for area lights) */
    public double radius = 1.0;
    
    /** Normal of the plane on which the light is defined (only pertinent for area lights) */
    public Vector3d n = new Vector3d(0, 1, 0);
    
    /**
     * Default constructor 
     */
    public Light() {
    	// do nothing
    }
}
