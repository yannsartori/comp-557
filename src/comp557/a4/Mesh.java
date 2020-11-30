// Yann Sartori 260822776
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
		

	private Vector3d AB = new Vector3d();
	private Vector3d AC = new Vector3d();
	private Vector3d BC = new Vector3d();
	private Vector3d CA = new Vector3d();
	private Vector3d triangleNormal = new Vector3d();
	
	private Vector3d temp = new Vector3d();
	private Vector3d inOutVec = new Vector3d();
	private Point3d intPoint = new Point3d();
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		
		// TODO: Objective 7: ray triangle intersection for meshes
		for ( int[] face : soup.faceList ) {
			Vertex a = soup.vertexList.get(face[0]);
			Vertex b = soup.vertexList.get(face[1]);
			Vertex c = soup.vertexList.get(face[2]);
			
			// For the triangle normal
			AB.sub(b.p, a.p);
			AC.sub(c.p, a.p);

			// For the inside-outside test (with edge AB)
			BC.sub(c.p, b.p);
			CA.sub(a.p, c.p);
			
			
			triangleNormal.cross(AB, AC);
			triangleNormal.normalize();
			
			
			double denom = ray.viewDirection.dot(triangleNormal);
			if ( Math.abs(denom) < 1e-6) return;
			
			// r(t) = e + td, (x - a)*n = 0 ==> t = ((a - x)*n)/(d*n)
			temp.sub(a.p, ray.eyePoint);
			double t = temp.dot(triangleNormal) / denom;

	    	if ( !( t >= 0 && t < result.t) ) {
	    		continue;
	    	}
	    	
	    	intPoint.scaleAdd(t, ray.viewDirection, ray.eyePoint);
	    	
	    	//AB
	    	Vector3d vec1 = new Vector3d();
	    	vec1.sub(intPoint, a.p);
	    	inOutVec.sub(intPoint, a.p);
	    	temp.cross(AB, inOutVec);
	    	if ( triangleNormal.dot(temp) < 0 ) continue;
	    	
	    	//BC
	    	inOutVec.sub(intPoint, b.p);
	    	temp.cross(BC, inOutVec);
	    	if ( triangleNormal.dot(temp) < 0 ) continue;
	    	
	    	//CA
	    	inOutVec.sub(intPoint, c.p);
	    	temp.cross(CA, inOutVec);
	    	if ( triangleNormal.dot(temp) < 0 ) continue;
	    	
	    	
			result.t = t;
			result.p.set(intPoint);
			result.n.set(triangleNormal);
			result.material = material;
		}
	}
	public void intersect(Ray ray, IntersectResult result, boolean isShadow) { intersect(ray, result); }

}
