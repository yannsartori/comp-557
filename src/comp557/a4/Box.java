// Yann Sartori 260822776
package comp557.a4;

import javax.vecmath.Point3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	
    
    double[] eyePoint = new double[3];
    double[] viewDirection = new double[3];
    double[] minCoords = new double[3];
    double[] maxCoords = new double[3];
    double[] tMin = new double[3];
    double[] tMax = new double[3];
    double[] normal = new double[3];
 
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		intersect(ray, result, false);
	}	
	
	public void intersect(Ray ray, IntersectResult result, boolean isShadow) {  
		// TODO: Objective 6: intersection of Ray with axis aligned box
			ray.eyePoint.get(eyePoint);
			ray.viewDirection.get(viewDirection);
			min.get(minCoords);
			max.get(maxCoords);
			double minIntersect = Double.NEGATIVE_INFINITY;
			double maxIntersect = Double.POSITIVE_INFINITY;
			for ( int i = 0 ; i < 3; i++ ) {
				if ( viewDirection[i] == 0 ) {
					tMin[i] = Double.NaN;
					tMax[i] = Double.NaN;
				} else {
					tMin[i] = (minCoords[i] - eyePoint[i]) / viewDirection[i];
					tMax[i] = (maxCoords[i] - eyePoint[i]) / viewDirection[i];
				}
				minIntersect = Math.max(minIntersect, Math.min(tMin[i], tMax[i]));
				maxIntersect = Math.min(maxIntersect, Math.max(tMin[i], tMax[i]));
			}
			
			for ( int i = 0; i < 3; i++ ) {
				normal[i] = 0;
			}
			if ( minIntersect < maxIntersect && minIntersect < 0 && maxIntersect > 0 && !isShadow) {
				minIntersect = maxIntersect;
			}
			if ( minIntersect <= maxIntersect && minIntersect > 0 && minIntersect < result.t ) {
				result.t = minIntersect;
				result.material = material;
				result.p.scaleAdd(minIntersect, ray.viewDirection, ray.eyePoint);
				for ( int i = 0; i < 3; i++ ) {
					if ( minIntersect == tMin[i] ) {
						normal[i] = -1;
						break;
					}
					else if ( minIntersect == tMax[i] ) {
						normal[i] = 1;
						break;
					}
				}
				result.n.set(normal);
				if ( minIntersect == maxIntersect ) {
					result.n.scale(-1);
				}
			}
	}

}
