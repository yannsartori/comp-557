package comp557.a4;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        cam.camComputations();
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        Ray ray = new Ray();
        Color4f total = new Color4f();
    	Color4f c = new Color4f();
    	Color4f specular = new Color4f();
    	
        render.init(w, h, showPanel);
        int xDivisions = (int) Math.ceil(Math.sqrt(render.samples));
        double[][] offset = new double[xDivisions * xDivisions][2];
        double step = 1.0 / xDivisions;
        int jitter = (render.jitter) ? 1 : 0;
        for ( int i = 0; i < xDivisions; i++ ) {
        	for ( int j = 0; j < xDivisions; j++ ) {
        		offset[i*xDivisions + j][0] = -0.5 + step*(i + 0.5) + jitter * step * (Math.random() - 0.5);
        		offset[i*xDivisions + j][1] = -0.5 + step*(j + 0.5) + jitter * step * (Math.random() - 0.5);
        	}
        }
//        for ( int j = 0; j < h && !render.isDone(); j++ ) {
//            for ( int i = 0; i < w && !render.isDone(); i++ ) {
        for ( int j = h - 1; j >= 0 && !render.isDone(); j-- ) {
            for ( int i = w - 1; i >= 0 && !render.isDone(); i-- ) {
            	total.set(0, 0, 0, 0);
            	for ( int sample = 0; sample < offset.length; sample++ ) {
	                // TODO: Objective 1: generate a ray (use the generateRay method)
	            	Scene.generateRay(i, j, offset[sample], cam, ray);
	            	c.set(0, 0, 0, 0);
	            	specular.set(1, 1, 1, 1);
	            	calculateRayColor(ray, c, specular);
	            	total.add(c);
	            }
            	// Here is an example of how to calculate the pixel value.
            	total.scale((float) (1.0 / offset.length));
            	//Ambient
            	total.x += ambient.x;
            	total.y += ambient.y;
            	total.z += ambient.z;
            	total.w = 1;
            	total.clamp(0, 1);
            	int r = (int)(255*total.x);
                int g = (int)(255*total.y);
                int b = (int)(255*total.z);
                int a = (int)(255*total.w);
                int argb = (a<<24 | r<<16 | g<<8 | b);    
                
                // update the render image
                render.setPixel(i, j, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
    static Point3d sPoint = new Point3d(); 
    static Vector3d scaledU = new Vector3d();
    static Vector3d scaledV = new Vector3d();
    static Vector3d scaledW = new Vector3d();
    static Vector3d direction = new Vector3d();

	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		
		// TODO: Objective 1: generate rays given the provided parmeters
		double u = -cam.r + (2 * cam.r)*(i + offset[0] + 0.5) / cam.imageSize.getWidth();
		double v = -(-cam.t + (2 * cam.t)*(j + offset[1] + 0.5) / cam.imageSize.getHeight());

		scaledU.set(cam.u);
		scaledV.set(cam.v);
		scaledU.scale(u);
		scaledV.scale(v);
		scaledW.set(cam.w);
		scaledW.scale(-cam.d);
		
		sPoint.add(cam.from, scaledU);
		sPoint.add(scaledV);
		sPoint.add(scaledW);
		
		direction.sub(sPoint, cam.from);
		direction.normalize();
		
		ray.set(cam.from, direction);
		
	}

    private Vector3d lightDirection = new Vector3d();
    private Vector3d halfVector = new Vector3d();
    private Vector3d reflectionVector = new Vector3d();
    private Vector4f temp = new Vector4f();
    private Ray shadowRay = new Ray();
    private IntersectResult shadowResult = new IntersectResult();
    private IntersectResult result = new IntersectResult();
	private void calculateRayColor(Ray ray, Color4f c, Color4f specular) {
        // TODO: Objective 3: compute the shaded result for the intersection point (perhaps requiring shadow rays)
    	lightDirection.set(0, 0, 0);
        halfVector.set(0, 0, 0);
        reflectionVector.set(0, 0, 0);
        temp.set(0, 0, 0, 0);
        int shadowCount = 0;
        if ( specular.x < 1e-9 && specular.y < 1e-9 && specular.z < 1e-9 ) return;
    	for ( Intersectable surface : surfaceList ) {
    		surface.intersect(ray, result);
    	}
    	if ( Double.isFinite(result.t) ) {
    		result.t = Double.POSITIVE_INFINITY;
			for ( Light l : lights.values() ) {
				shadowCount = 0;
				lightDirection.sub(l.from, result.p);
				lightDirection.normalize();
				// Shadow Ray
				if ( !render.disableShadows) {
					for ( int j = 0; j < l.shadowSamples; j++ ) {
						shadowResult.t = Double.POSITIVE_INFINITY;
						computeLightDirection(shadowRay.viewDirection, l, result.p);
						for (Intersectable surface : surfaceList ) {
							if ( inShadow(result, l, surface, shadowResult, shadowRay) ) {
								shadowCount++;
								break;
							}
						}
					}
				}
				
				// don't need to compute
				if ( shadowCount == l.shadowSamples ) continue;
				
				// Lambertian
				temp.set(l.color.x, l.color.y, l.color.z, l.color.w);
				temp.scale((float) (Math.max(0, result.n.dot(lightDirection)) * l.power));
				temp.x *= specular.x * result.material.diffuse.x;
				temp.y *= specular.y * result.material.diffuse.y;
				temp.z *= specular.z * result.material.diffuse.z;
				temp.w *= specular.w * result.material.diffuse.w;
				temp.scale(1 - (shadowCount / l.shadowSamples));
				c.add(temp);
				
				//Blinn phong
				if ( !result.material.shiny ) {
    				halfVector.scaleAdd(-1, ray.viewDirection, lightDirection);
    				halfVector.normalize();
    				temp.set(l.color.x, l.color.y, l.color.z, l.color.w);
    				temp.scale((float) (Math.pow(Math.max(0, result.n.dot(halfVector)), result.material.shinyness) * l.power));
    				temp.x *= specular.x * result.material.specular.x;
    				temp.y *= specular.y * result.material.specular.y;
    				temp.z *= specular.z * result.material.specular.z;
    				temp.w *= specular.w * result.material.specular.w;
    				temp.scale(1 - (shadowCount / l.shadowSamples));
    				c.add(temp);
				} else {
					//Glossy reflection
					reflectionVector.scaleAdd(-2 * result.n.dot(ray.viewDirection), result.n, ray.viewDirection);
					IntersectResult oldResult = new IntersectResult();
					oldResult.n.set(result.n);
					oldResult.p.set(result.p);
					oldResult.material = result.material;
					Ray oldRay = new Ray();
					oldRay.set(ray.eyePoint, ray.viewDirection);
					result.p.scaleAdd(0.005, result.n, result.p);
					ray.eyePoint.set(result.p);
					ray.viewDirection.set(reflectionVector);
					specular.x *= result.material.specular.x;
					specular.y *= result.material.specular.y;
					specular.z *= result.material.specular.z;
					specular.w *= result.material.specular.w;
					calculateRayColor(ray, c, specular);
					ray.set(oldRay.eyePoint, oldRay.viewDirection);
					result.n.set(oldResult.n);
					result.p.set(oldResult.p);
					result.material = oldResult.material;
				}
			}
			
		}
    	if ( c.x == c.y && c.y == c.z && c.z == 0 ) {
    		c.x = render.bgcolor.x;
    		c.y = render.bgcolor.y;
    		c.z = render.bgcolor.z;
    	}
        
	}
	
	private static Point3d lightPoint = new Point3d();
	private static Vector3d tempVec = new Vector3d();
	private static void computeLightDirection(Vector3d lightDirection, Light l, Point3d intersection) {
		
		if ( l.type.equals("point") ) {
			lightPoint.set(l.from);
		} else if ( l.type.equals("area") ) {
			// Random number between -l.radius and l.radius
			// Generate a point in the sphere of the light
			double xRad = l.radius * (2*Math.random() - 1); 
			double yRad = l.radius * (2*Math.random() - 1); 
			double zRad = l.radius * (2*Math.random() - 1);
			lightPoint.set(l.from.x + xRad, l.from.y + yRad, l.from.z + zRad);
			// Project the point onto the light plane defined by the normal and from
			tempVec.sub(lightPoint, l.from);
			tempVec.scale(l.n.dot(tempVec), l.n);
			lightPoint.sub(lightPoint, tempVec);
			
		}
		lightDirection.sub(lightPoint, intersection);
		lightDirection.normalize();
	}
	
	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	
	static Vector3d sigmaRay = new Vector3d();
	public static boolean inShadow(final IntersectResult result, final Light light, final Intersectable root, IntersectResult shadowResult, Ray shadowRay) {
		
		// TODO: Objective 5: check for shdows and use it in your lighting computation
//		if ( true) return false;
		sigmaRay.scaleAdd(0.005, shadowRay.viewDirection, result.p);
		shadowRay.eyePoint.set(sigmaRay);
		root.intersect(shadowRay, shadowResult, true);
		return Double.isFinite(shadowResult.t);
	}    
}
