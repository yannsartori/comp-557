package comp557.a1;

import com.jogamp.opengl.DebugGL4;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class FreeJoint extends GraphNode {

	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public FreeJoint( String name ) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -2, 2 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -2, 2 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -2, 2 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	@Override
	public void display( GLAutoDrawable drawable, BasicPipeline pipeline ) {
		drawable.setGL(new DebugGL4(drawable.getGL().getGL4()));
		GL4 gl = drawable.getGL().getGL4();
		pipeline.setModelingMatrixUniform(gl);
		pipeline.push();
		pipeline.translate(tx.getValue(), ty.getValue(), tz.getValue());
		pipeline.rotate(rz.getValue(), 0, 0, 1);
		pipeline.rotate(ry.getValue(), 0, 1, 0);
		pipeline.rotate(rx.getValue(), 1, 0, 0);
		// TODO: Objective 3: Freejoint, transformations must be applied before drawing children
		
		super.display( drawable, pipeline );		
		pipeline.pop();
	}
	
}
