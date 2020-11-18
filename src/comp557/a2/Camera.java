// Yann Sartori 260822776
package comp557.a2;

import javax.swing.JPanel;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import mintools.parameters.DoubleParameter;
import mintools.parameters.Parameter;
import mintools.parameters.ParameterListener;
import mintools.parameters.Vec3Parameter;
import mintools.swing.VerticalFlowPanel;

/**
 * Camera class to be used both for viewing the scene, but also to draw the scene from 
 * a point light.
 */
public class Camera {

	Vec3Parameter position = new Vec3Parameter("position", -6, 2.75, 1.5 );
	Vec3Parameter lookat = new Vec3Parameter("look at", 3, -0.5, -1 );
	Vec3Parameter up = new Vec3Parameter("up", 0, 1, 0 );
	
	Vector3d w = new Vector3d();
	Vector3d upVec = new Vector3d();
	Vector3d u = new Vector3d();
	Vector3d v = new Vector3d();
	
    DoubleParameter near = new DoubleParameter( "near plane", 1, 0.1, 10 );    
    DoubleParameter far = new DoubleParameter( "far plane" , 40, 1, 100 );    
    DoubleParameter fovy = new DoubleParameter( "fovy degrees" , 60, 14, 67 );    	
	 
    /** Viewing matrix to be used by the pipeline */
    Matrix4d V = new Matrix4d();
    /** Projection matrix to be used by the pipeline */
    Matrix4d P = new Matrix4d();
    
    public Camera() {
    	near.addParameterListener( new ParameterListener<Double>() {			
			@Override
			public void parameterChanged(Parameter<Double> parameter) {
				// Let's keep near and far from crossing!
				if ( near.getValue() >= far.getValue() ) {
					far.setValue( near.getValue() + 0.1 );
				}
			}
		});
    	far.addParameterListener( new ParameterListener<Double>() {
    		@Override
    		public void parameterChanged(Parameter<Double> parameter) {
				// Let's keep near and far from crossing!
    			if ( far.getValue() <= near.getValue() ) {
					near.setValue( far.getValue() - 0.1 );
				}
    		}
		});
    }
    
    private double r = 0;
    private double t = 0;
    
    /**
     * Update the projection and viewing matrices
     * We'll do this every time we draw, though we could choose to more efficiently do this only when parameters change.
     * @param width of display window (for aspect ratio)
     * @param height of display window (for aspect ratio)
     */
    public void updateMatrix( double width, double height ) {
    	w.x = -lookat.xp.getValue();
    	w.y = -lookat.yp.getValue();
    	w.z = -lookat.zp.getValue();
    	w.normalize();
    	
    	up.get(upVec);
    	u.cross(upVec, w);
    	u.normalize();
    	
    	v.cross(w, u);
    	// TODO: Objective 2: Replace the default viewing matrix with one constructed from the parameters available in this class!
    	if ( Double.isNaN(w.x) || Double.isNaN(u.x) || Double.isNaN(v.x) ) {
	    	V.set( new double[] {
	        		1,  0,  0,  0,
	        		0,  1,  0,  0,
	        		0,  0,  1, -2.5,
	        		0,  0,  0,  1,
	        } );
    	} else {
    		V.set( new double[] {
    				u.x, v.x, w.x, position.x,
    				u.y, v.y, w.y, position.y,
    				u.z, v.z, w.z, position.z,
    				0,   0,   0,   1
    		});
    		V.invert();
    	}
    	
    	// TODO: Objective 3: Replace the default projection matrix with one constructed from the parameters available in this class!
    	double n = near.getValue();
    	double f = far.getValue();
    	t = Math.tan(Math.toRadians(fovy.getValue()) / 2) * n;
    	r = (width * t) / height;
    	// Since left = -right, bottom = -top, 2n(r-l) = 2n/2r = n/r
        P.set( new double[] {
        		n/r,  0,   0,             0,
        		0,    n/t, 0,             0,
        		0,    0,   (n+f) / (n-f), (2*n*f) / (n-f),
        		0,    0,   -1,            0,
        } );    	    
    	
    }
    
    /**
     * @return controls for the camera
     */
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.add( position );
        vfp.add( lookat );
        vfp.add( up );
        vfp.add( near.getControls() );
        vfp.add( far.getControls() );
        vfp.add( fovy.getControls() );
        return vfp.getPanel();
    }
    
    public double getRight() {
    	return r;
    }
	
    public double getTop() {
    	return t;
    }
}
