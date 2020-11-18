// Yann Sartori 260822776
package comp557.a2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a2.CharacterMaker;
import comp557.a2.geom.Cube;
import comp557.a2.geom.FancyAxis;
import comp557.a2.geom.Quad;
import comp557.a2.geom.Sphere;
import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

/**
 *  A procedurally generated scene for testing shadows. 
 */
public class Scene {
	
	public Scene() {
		createCharacter();
	}
	
	public void display( GLAutoDrawable drawable, ShadowPipeline pipeline ) {

		// draw world frame
		FancyAxis.draw(drawable, pipeline);

		// make a ground plane with y up
		pipeline.push();
			pipeline.rotate( drawable, -Math.PI/2, 1,0,0 );
			pipeline.push();
				pipeline.scale( drawable, 2, 2, 2 );
				pipeline.setkd( drawable, 1, 0.5, 0 );
				Quad.draw( drawable, pipeline );
			pipeline.pop(drawable);
			
			Random r = new Random(0);
			for ( int ii = 0; ii < 9; ii++ ) {
			
				pipeline.setkd( drawable, 0.7, 0.7, 0.7 );
				pipeline.push();
					double x = (ii/3-1.0);
					double y = (ii%3-1.0);
					pipeline.translate( drawable, x, y, 0);
					pipeline.scale(drawable,0.1, 0.1, 0.1);
					for ( int i = 0; i < 10; i++ ) {
						pipeline.translate(drawable,0, 0, 1.2);
						double theta = 0.1*Math.cos(System.nanoTime()*1e-9 + Math.PI*i/4.0 + 6*r.nextDouble());
						pipeline.rotate(drawable, theta, 1, 0 ,0 );
						double alpha = 0.1*Math.sin(System.nanoTime()*1.3e-9 + Math.PI*i/4.0 + 6*r.nextDouble());
						pipeline.rotate( drawable, alpha, 0, 1 ,0 );
						Cube.draw( drawable, pipeline );
					}
					pipeline.translate(drawable,0, 0, 1.5);
					Sphere.draw( drawable, pipeline );
				pipeline.pop(drawable);
			}
		pipeline.pop(drawable);
		
        if ( root != null ) root.display( drawable, pipeline );
	}
	
	/** The root of the scene graph / character */
    private GraphNode root = null;
    
    /** Master list of degrees of freedom */
    private List<DoubleParameter> degreesOfFreedom = new ArrayList<DoubleParameter>();
    private JPanel vfpPosePanel; 
    private VerticalFlowPanel vfpPose = new VerticalFlowPanel();
	
	public void createCharacter() {
        degreesOfFreedom.clear();
        vfpPose.removeAll();
    	root = CharacterMaker.create();
    	if ( root == null ) return;
    	root.getDOFs( degreesOfFreedom );
    	int labelWidth = DoubleParameter.DEFAULT_SLIDER_LABEL_WIDTH;
    	int textWidth = DoubleParameter.DEFAULT_SLIDER_TEXT_WIDTH;
    	DoubleParameter.DEFAULT_SLIDER_LABEL_WIDTH = 50;
    	DoubleParameter.DEFAULT_SLIDER_TEXT_WIDTH = 50;
    	vfpPose.add( root.getControls() );    	
    	if (vfpPosePanel != null ) {
    		vfpPosePanel.updateUI();
    	}
    	DoubleParameter.DEFAULT_SLIDER_LABEL_WIDTH = labelWidth;
    	DoubleParameter.DEFAULT_SLIDER_TEXT_WIDTH = textWidth;
    }
	
	public JPanel getControls() {
		return vfpPose.getPanel();
	}
}
