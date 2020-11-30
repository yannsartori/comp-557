// Yann Sartori 260822776
package comp557.a4;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector3d;

public class CompositeGeom extends Intersectable{

	public enum Operation {UNION, INTERSECTION, DIFFERENCE};
	
	public Intersectable leftChild;
	public Intersectable rightChild;
	public Operation operation;

	public CompositeGeom() {
	}
	
	private IntersectResult leftEnterResult = new IntersectResult();
	private IntersectResult leftExitResult = new IntersectResult();
	private IntersectResult rightEnterResult = new IntersectResult();
	private IntersectResult rightExitResult = new IntersectResult();
	private Ray insideRay = new Ray();
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		intersect(ray, result, false);
	}
	
	public void intersect(Ray ray, IntersectResult result, boolean isShadow) { 
		IntersectResult finalResult = null;
		rightExitResult.t = rightEnterResult.t = leftExitResult.t = leftEnterResult.t = Double.POSITIVE_INFINITY;
		leftChild.intersect(ray, leftEnterResult, isShadow);
		rightChild.intersect(ray, rightEnterResult, isShadow);
		double leftMinInt, rightMinInt, leftMaxInt, rightMaxInt;
		if ( leftEnterResult.t < Double.POSITIVE_INFINITY ) {
			insideRay.eyePoint.scaleAdd(0.005, ray.viewDirection, leftEnterResult.p);
			insideRay.viewDirection.set(ray.viewDirection);
			leftChild.intersect(insideRay, leftExitResult);
			leftExitResult.t += leftEnterResult.t;
		}
		if ( rightEnterResult.t < Double.POSITIVE_INFINITY ) {
			insideRay.eyePoint.scaleAdd(0.005, ray.viewDirection, rightEnterResult.p);
			insideRay.viewDirection.set(ray.viewDirection);
			rightChild.intersect(insideRay, rightExitResult);
			rightExitResult.t += rightEnterResult.t;
		}
		
		leftMinInt = Math.min(leftEnterResult.t, leftExitResult.t);
		leftMaxInt = Math.max(leftEnterResult.t, leftExitResult.t);
		rightMinInt = Math.min(rightEnterResult.t, rightExitResult.t);
		rightMaxInt = Math.max(rightEnterResult.t, rightExitResult.t);
		
		
		
		if ( operation == CompositeGeom.Operation.UNION ) {
			double minInt = Math.min(leftMinInt, rightMinInt);
			if ( !(minInt < result.t && minInt >= 0)) return;
			if ( minInt == rightEnterResult.t ) {
				finalResult = rightEnterResult;
			} else if ( minInt == leftEnterResult.t ) {
				finalResult = leftEnterResult;
			}
		} else if ( operation == CompositeGeom.Operation.INTERSECTION ) {
			double minInt = Math.max(leftMinInt, rightMinInt);
			double maxInt = Math.min(leftMaxInt, rightMaxInt);
			if ( !(minInt < result.t && minInt >= 0) || (maxInt == Double.POSITIVE_INFINITY && isShadow)) return;
			if ( !(minInt <= maxInt) ) return;
			if ( minInt == rightEnterResult.t ) {
				finalResult = rightEnterResult;
			} else if ( minInt == leftEnterResult.t ) {
				finalResult = leftEnterResult;
			}
		} else if ( operation == CompositeGeom.Operation.DIFFERENCE ) {
			double minInt = Math.min(leftMinInt, rightMinInt);
			if ( !(minInt < result.t && minInt >= 0)) return;
			if ( minInt == leftMinInt ) {
				finalResult = leftEnterResult;
			} else if ( minInt == rightMinInt && leftMinInt < Double.POSITIVE_INFINITY && rightMaxInt < Double.POSITIVE_INFINITY) {
				rightExitResult.n.scale(-1);
				finalResult = rightExitResult;
			}
			else return;
		}
		result.t = finalResult.t;
		result.p.set(finalResult.p);
		result.n.set(finalResult.n);
		result.material = finalResult.material;
	}

}
