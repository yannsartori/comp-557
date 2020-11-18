// Yann Sartori 260822776
package comp557.a2;

import com.jogamp.opengl.GLAutoDrawable;

import javax.vecmath.Vector3d;

import mintools.parameters.DoubleParameter;

public class RotaryJoint extends GraphNode {

    double tx;
	double ty;
    double tz;
    /* Axis is origin to (x, y, z) */
	double rx;
	double ry;
    double rz;

    DoubleParameter angle;

    Vector3d rotAxis;

    public RotaryJoint(String name, double tx, double ty, double tz, double rx, double ry, 
        double rz, double angleMin, double angleMax) {
        super(name);
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
		this.rx = rx;		
		this.ry = ry;
        this.rz = rz;
        dofs.add( this.angle = new DoubleParameter( name+ " angle", 0, angleMin, angleMax) );

        rotAxis = new Vector3d(rx, ry, rz);
        rotAxis.normalize();
    }

    public RotaryJoint(String name) {
        this(name, 0, 0, 0, 0, 0, 0, -180, 180);
    }

    public void setTranslation(double tx, double ty, double tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    public void setRotationAxis(double rx, double ry, double rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
    }

    public void setMinMaxAngle(double angle, double angleMin, double angleMax) {
        this.angle.setValue(angle);
        this.angle.setDefaultValue(angle);
        this.angle.setMinimum(angleMin);
        this.angle.setMaximum(angleMax);
    }

	@Override
	public void display( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		pipeline.push();
        pipeline.translate(drawable, tx, ty, tz);
        if ( rotAxis.x != rx || rotAxis.y != ry || rotAxis.z != rz ) {
            rotAxis.x = rx;
            rotAxis.y = ry;
            rotAxis.z = rz;
            if ( rotAxis.length() != 0 ) {
                rotAxis.normalize();
            }
        }
        pipeline.rotate(drawable, Math.toRadians(angle.getValue()), rotAxis.x, rotAxis.y, rotAxis.z);

		super.display( drawable, pipeline );		
		pipeline.pop(drawable);
    }
}
