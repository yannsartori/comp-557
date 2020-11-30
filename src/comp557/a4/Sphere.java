package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    private Vector3d alpha = new Vector3d();
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 2: intersection of ray with sphere
    	// Intersect ||X - c||^2 - r^2 = 0 with r(t) = p + td
    	alpha.sub(ray.eyePoint, this.center);
    	double alphaDotD = alpha.dot(ray.viewDirection);
    	double alphaDotAlpha = alpha.dot(alpha);
    	double discrim = alphaDotD*alphaDotD - (alphaDotAlpha - this.radius * this.radius);
    	if ( discrim < 0) {
    		return;
    	}
    	discrim = Math.sqrt(discrim);
    	double t = (-alphaDotD - discrim) >= 0 ? -alphaDotD - discrim : -alphaDotD + discrim;
    	if ( !( t >= 0 && t < result.t) ) {
    		return;
    	}
    	result.t = t;
    	result.p.scaleAdd(result.t, ray.viewDirection, ray.eyePoint);
    	result.n.sub(result.p, this.center);
    	result.n.normalize();
    	result.material = material;
    }
    
    public void intersect(Ray ray, IntersectResult result, boolean isShadow) { intersect(ray, result); }
}
