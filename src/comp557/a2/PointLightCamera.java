// Yann Sartori 260822776
package comp557.a2;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a2.geom.FancyAxis;
import comp557.a2.geom.QuadWithTexCoords;
import comp557.a2.geom.WireCube;
import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

public class PointLightCamera extends Camera {	

    public BooleanParameter debugLightFrustum = new BooleanParameter( "debug light frustum" , true );
    public DoubleParameter sigma = new DoubleParameter( "self shadowing offset", 0.0015, 0, 0.002 );
	    
    public PointLightCamera() {
    	super();
    	position.set( new Vector3d(-2, 2, -2) );
    	lookat.set(new Vector3d(1, -1, 1));
    	fovy.setDefaultValue(55.0);
    }
    
    public void getPositionInWorld( Vector4d pos ) {
    	pos.set( position.x, position.y, position.z, 1 );
    }
    
    Matrix4d Vinv = new Matrix4d();
    Matrix4d Pinv = new Matrix4d();
    
    public void draw( GLAutoDrawable drawable, ShadowPipeline pipeline ) {
    	if ( !debugLightFrustum.getValue() ) return;

    	// TODO: Objective 4,5,6: Note lighting disabled for drawing things that should only be drawn in a solid colour using kd.
    	
		pipeline.disableLighting(drawable);
		this.Vinv.invert(this.V);
		this.Pinv.invert(this.P);
		
    	// TODO: Objective 4: draw the light frame using a fancy axis... You must set up the right transformation!
    	pipeline.push();
    	pipeline.multMatrix(drawable, this.Vinv);
		FancyAxis.draw(drawable, pipeline);
		pipeline.pop(drawable);


		// TODO: Objective 5: draw the light camera frustum using the inverse projection with a wire cube. You must set up the right transformation!
		
		pipeline.push();
		pipeline.multMatrix(drawable, Vinv);
		pipeline.multMatrix(drawable, Pinv);
		
		pipeline.setkd( drawable, 1, 1, 1 );
		WireCube.draw( drawable, pipeline );
		pipeline.pop(drawable);

		// TODO: Objective 5: draw the light view on the near plane of the frustum. You must set up the right transformation! 
		// That is, translate and scale the x and y directions of the -1 to 1 quad so that the quad fits exactly the l r t b portion of the near plane

		pipeline.push();
		pipeline.multMatrix(drawable, Vinv);
		pipeline.translate(drawable, 0, 0, -near.getValue());
		pipeline.scale(drawable, getRight(), getTop(), 1);
		pipeline.debugLightTexture(drawable);
		QuadWithTexCoords.draw( drawable, pipeline );
		pipeline.pop(drawable);
		
    }
    
    /**
     * @return controls for the shadow mapped light
     */
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder(new TitledBorder("Point Light Camera Controls"));
        vfp.add( super.getControls() );
        vfp.add( sigma.getControls() );
        vfp.add( debugLightFrustum.getControls() );        
        return vfp.getPanel();
    }
    
}
