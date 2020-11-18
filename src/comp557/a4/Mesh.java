package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import comp557.a4.PolygonSoup.Vertex;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
		
	
	private Vector3d temp = new Vector3d();
	
	private Point3d intersectPoint = new Point3d();
	private Vector3d triangleNormal = new Vector3d();
	//AC x AB = normal, CA, AB, BC are used for inside test.
	private Vector3d edgeAC = new Vector3d();
	private Vector3d edgeCA = new Vector3d();
	private Vector3d edgeAB = new Vector3d();
	private Vector3d edgeBC = new Vector3d();
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 7: ray triangle intersection for meshes
		for ( int[] face : soup.faceList ) {
			Vertex c = soup.vertexList.get(face[0]);
			Vertex a = soup.vertexList.get(face[1]);
			Vertex b = soup.vertexList.get(face[2]);
			
			// Ray plane intersection
			edgeAC.sub(c.p, a.p);
			edgeAB.sub(b.p, a.p);

			triangleNormal.cross(edgeAB, edgeAC);
			triangleNormal.normalize();
	
			if ( ray.viewDirection.dot(triangleNormal) <= 1e-6 ) continue;
			
			temp.set(a.p);
			double d = triangleNormal.dot(temp);
			
			temp.set(ray.eyePoint);
			
			double t = (temp.dot(triangleNormal) + d) / (ray.viewDirection.dot(triangleNormal));
			if ( !( t >= 0 && t < result.t) ) {
	    		continue;
	    	}
			
			// Inside test
			intersectPoint.scaleAdd(t, ray.viewDirection, ray.eyePoint);
			
			edgeCA.sub(a.p, c.p);
			edgeAB.sub(b.p, a.p);
			edgeBC.sub(c.p, b.p);
			
			temp.sub(intersectPoint, c.p);
			temp.cross(edgeCA, temp);
			if ( temp.dot(triangleNormal) <= 0 ) continue;
			
			temp.sub(intersectPoint, a.p);
			temp.cross(edgeAB, temp);
			if ( temp.dot(triangleNormal) <= 0 ) continue;
			
			temp.sub(intersectPoint, b.p);
			temp.cross(edgeBC, temp);
			if ( temp.dot(triangleNormal) <= 0 ) continue;
			
			result.t = t;
			result.p.set(intersectPoint);
			result.n.set(triangleNormal);
			result.material = material;
		}
		
	}

}
