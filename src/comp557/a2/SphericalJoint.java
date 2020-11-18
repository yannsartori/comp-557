// Yann Sartori 260822776
package comp557.a2;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class SphericalJoint extends GraphNode {

	DoubleParameter rx;
	DoubleParameter ry;
    DoubleParameter rz;

    double tx;
    double ty;
    double tz;

    public SphericalJoint(String name, double rx, double ry, double rz) {
		super(name);
		dofs.add( this.rx = new DoubleParameter( name+" rx", rx, -180, 180 ) );		
		dofs.add( this.ry = new DoubleParameter( name+" ry", ry, -180, 180 ) );
        dofs.add( this.rz = new DoubleParameter( name+" rz", rz, -180, 180 ) );
    }

    public SphericalJoint(String name) {
        this(name, 0, 0, 0);
    }

    public void setRotation(double rx, double ry, double rz) {
        this.rx.setValue(rx);
        this.ry.setValue(ry);
        this.rz.setValue(rz);
        this.rx.setDefaultValue(rx);
        this.ry.setDefaultValue(ry);
        this.rz.setDefaultValue(rz);
    }
    
    public void setMin(double rx, double ry, double rz) {
        this.rx.setMinimum(rx);
        this.ry.setMinimum(ry);
        this.rz.setMinimum(rz);
    }

    public void setMax(double rx, double ry, double rz) {
        this.rx.setMaximum(rx);
        this.ry.setMaximum(ry);
        this.rz.setMaximum(rz);
    }

    public void setTranslation(double tx, double ty, double tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
	}

	@Override
	public void display( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
        pipeline.push();
        pipeline.translate(drawable, tx, ty, tz);
        pipeline.rotate(drawable, Math.toRadians(rz.getValue()), 0, 0, 1);
		pipeline.rotate(drawable, Math.toRadians(ry.getValue()), 0, 1, 0);
		pipeline.rotate(drawable, Math.toRadians(rx.getValue()), 1, 0, 0);
		pipeline.setkd(drawable, 0,0,0);
		super.display( drawable, pipeline );		
		pipeline.pop(drawable);
    }
}
