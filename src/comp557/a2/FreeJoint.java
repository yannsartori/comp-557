// Yann Sartori 260822776
package comp557.a2;

import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class FreeJoint extends GraphNode {

	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
	DoubleParameter sx;
	DoubleParameter sy;
	DoubleParameter sz;
		
	public FreeJoint( String name ) {
		super(name);
		tx = new DoubleParameter( name+" tx", 0, -2, 2 );		
		ty = new DoubleParameter( name+" ty", 0, -2, 2 );
		tz = new DoubleParameter( name+" tz", 0, -2, 2 );
		rx = new DoubleParameter( name+" rx", 0, -180, 180 );	
		ry = new DoubleParameter( name+" ry", 0, -180, 180 );
		rz = new DoubleParameter( name+" rz", 0, -180, 180 );
		sx = new DoubleParameter( name+" sx", 1, 0, 2 );
		sy = new DoubleParameter( name+" sy", 1, 0, 2 );
		sz = new DoubleParameter( name+" sz", 1, 0, 2 );
	}
	
	// Allows us to create objects without exposing their degrees of freedom by default
	public void addDofs() {
		dofs.add( tx );		
		dofs.add( ty );
		dofs.add( tz );
		dofs.add( rx );		
		dofs.add( ry );
		dofs.add( rz );
	}

	public void setTranslation(double tx, double ty, double tz) {
		this.tx.setValue(tx);
        this.ty.setValue(ty);
		this.tz.setValue(tz);
		this.tx.setDefaultValue(tx);
        this.ty.setDefaultValue(ty);
		this.tz.setDefaultValue(tz);
	}

	public void setRotation(double rx, double ry, double rz) {
		this.rx.setValue(rx);
		this.ry.setValue(ry);
		this.rz.setValue(rz);
		this.rx.setDefaultValue(rx);
		this.ry.setDefaultValue(ry);
		this.rz.setDefaultValue(rz);
	}
	
	public void setScale(double sx, double sy, double sz) {
		this.sx.setValue(sx);
		this.sy.setValue(sy);
		this.sz.setValue(sz);
		this.sx.setDefaultValue(sx);
		this.sy.setDefaultValue(sy);
		this.sz.setDefaultValue(sz);
    }

	@Override
	public void display( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
		pipeline.push();
		pipeline.translate(drawable, tx.getValue(), ty.getValue(), tz.getValue());
		pipeline.scale(drawable, sx.getValue(), sy.getValue(), sz.getValue());
		pipeline.rotate(drawable, Math.toRadians(rz.getValue()), 0, 0, 1);
		pipeline.rotate(drawable, Math.toRadians(ry.getValue()), 0, 1, 0);
		pipeline.rotate(drawable, Math.toRadians(rx.getValue()), 1, 0, 0);
		
		super.display( drawable, pipeline );		
		pipeline.pop(drawable);
	}
	
}
