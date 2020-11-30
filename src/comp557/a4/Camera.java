// Yann Sartori 260822776
package comp557.a4;

import java.awt.Dimension;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple camera object, which could be extended to handle a variety of 
 * different camera settings (e.g., aperature size, lens, shutter)
 */
public class Camera {
	
	/** Camera name */
    public String name = "camera";

    /** The eye position */
    public Point3d from = new Point3d(0,0,10);
    
    /** The "look at" position */
    public Point3d to = new Point3d(0,0,0);
    
    /** Up direction, default is y up */
    public Vector3d up = new Vector3d(0,1,0);
    
    /** Vertical field of view (in degrees), default is 45 degrees */
    public double fovy = 45.0;
    
    /** The rendered image size */
    public Dimension imageSize = new Dimension(640,480);

    /**
     * Default constructor
     */
    
    public Vector3d w = new Vector3d();
    public Vector3d u = new Vector3d();
    public Vector3d v = new Vector3d();
    
    public double t;
    public double r;
    
    public double d;

    public Camera() {
    	camComputations();
    	
    }
    
    public void camComputations() {
    	Vector3d vecDiff = new Vector3d();
    	w.x = from.x - to.x;
    	w.y = from.y - to.y;
    	w.z = from.z - to.z;
    	w.normalize();
    	
    	u.cross(up, w);
    	u.normalize();
    	
    	v.cross(w, u);
    	
    	vecDiff.sub(from, to);
    	d = vecDiff.length();
 
    	t = Math.tan(Math.toRadians(fovy / 2)) * d;
    	r = (imageSize.getWidth() * t) / imageSize.getHeight();
    }
}

