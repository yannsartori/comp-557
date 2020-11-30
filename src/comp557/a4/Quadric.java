package comp557.a4;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;


public class Quadric extends Intersectable {
    
	/**
	 * Radius of the sphere.
	 */
	public Matrix4d Q = new Matrix4d();
	public Matrix3d A = new Matrix3d();
	public Vector3d B = new Vector3d();
	public double C;
	
	/**
	 * The second material, e.g., for front and back?
	 */
	Material material2 = null;
	
	public Quadric() {
	
	}
	
	private Vector4d homoEye = new Vector4d();
	private Vector4d homoDir = new Vector4d();
	private Vector4d tempVec = new Vector4d();
	private Matrix3d tempMat = new Matrix3d();
	private Vector3d grad = new Vector3d();
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		double aTerm, bTerm, cTerm, t;
		homoEye.set(ray.eyePoint.x, ray.eyePoint.y, ray.eyePoint.z, 1);
		homoDir.set(ray.viewDirection.x, ray.viewDirection.y, ray.viewDirection.z, 0);
		
		Q.transform(homoDir, tempVec);
		aTerm = tempVec.dot(homoDir);
		
		Q.transform(homoEye, tempVec);
		bTerm = homoDir.dot(tempVec);
		
		cTerm = homoEye.dot(tempVec);
		
		double discrim = bTerm*bTerm - aTerm*cTerm;
		if ( discrim < 0 ) return;
		discrim = Math.sqrt(discrim);
		t = (-bTerm - discrim) / aTerm >= 0 ? (-bTerm - discrim) / aTerm : (-bTerm + discrim) / aTerm;
		
		if ( !( t >= 0 && t < result.t) ) {
    		return;
    	}
    	
    	result.t = t;
    	result.p.scaleAdd(result.t, ray.viewDirection, ray.eyePoint);
    	result.material = material;

    	tempMat.setZero();
    	tempMat.m00 = result.p.x;
    	tempMat.m01 = result.p.y;
    	tempMat.m02 = result.p.z;
    	tempMat.mul(A);
    	tempMat.getRow(0,grad);
    	grad.scaleAdd(-2, B, grad);
    	
    	result.n.set(grad);
    	result.n.normalize();
    	
    	
	}
	public void intersect(Ray ray, IntersectResult result, boolean isShadow) { intersect(ray, result); }
	
}
