// Yann Sartori 260822776
package comp557.a2;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

/** 
 * Left Mouse Drag Arcball
 * @author kry
 */
public class ArcBall {
		
	private DoubleParameter fit = new DoubleParameter( "Fit", 1, 0.5, 2 );
	private DoubleParameter gain = new DoubleParameter( "Gain", 1, 0.5, 2, true );
	
	/** The accumulated rotation of the arcball */
	Matrix4d R = new Matrix4d();

	public ArcBall() {
		R.setIdentity();
	}
	
	/** Properties to determine rotations. */
	
	private Vector3d prevMouseVec = new Vector3d(0, 0, 0);
	private Vector3d curMouseVec = new Vector3d(0, 0, 0);
	private Vector3d rotAxis = new Vector3d(0, 0, 0);
	private double angle = 0.0;
	private Matrix4d matrixContainer = new Matrix4d();
	/** 
	 * Convert the x y position of the mouse event to a vector for your arcball computations 
	 * @param e
	 * @param v
	 */
	public void setVecFromMouseEvent( MouseEvent e, Vector3d v ) {
		Component c = e.getComponent();
		Dimension dim = c.getSize();
		double width = dim.getWidth();
		double height = dim.getHeight();
		int mousex = e.getX();
		int mousey = e.getY();
		
		// TODO: Objective 1: finish arcball vector helper function
		double radius = Math.min(width,  height) / fit.getValue();
		v.x = (mousex - (width / 2)) / radius;
		v.y = ((height / 2) - mousey) / radius;
		double squareSum = v.x * v.x + v.y * v.y;
		if ( squareSum > 1 ) {
			double scale = 1 / Math.sqrt(squareSum);
			v.x = scale * v.x;
			v.y = scale * v.y;
			v.z = 0;
		} else {
			v.z = Math.sqrt(1 - squareSum);
		}
		
		
	}
		
	public void attach( Component c ) {
		c.addMouseMotionListener( new MouseMotionListener() {			
			@Override
			public void mouseMoved( MouseEvent e ) {}
			@Override
			public void mouseDragged( MouseEvent e ) {				
				if ( (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 ) {
					// TODO: Objective 1: Finish arcball rotation update on mouse drag when button 1 down!
					updateRotationMatrix(e);
				}
			}
		});
		c.addMouseListener( new MouseListener() {
			@Override
			public void mouseReleased( MouseEvent e) {
				prevMouseVec.x = 0;
				prevMouseVec.y = 0;
				prevMouseVec.z = 0;
			}
			@Override
			public void mousePressed( MouseEvent e) {
				// TODO: Objective 1: arcball interaction starts when mouse is clicked
				updateRotationMatrix(e);
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
	}
	
	private void updateRotationMatrix(MouseEvent e) {
		setVecFromMouseEvent(e, curMouseVec);
		if ( !(prevMouseVec.x == 0 && prevMouseVec.y == 0 && prevMouseVec.z == 0) ) {
			rotAxis.cross(prevMouseVec, curMouseVec);
			if (rotAxis.length() <= 0.0 ) {
				return;
			}
			rotAxis.normalize();
			angle = gain.getValue() * Math.acos(prevMouseVec.dot(curMouseVec));
			matrixContainer.set(new AxisAngle4d(rotAxis, angle));
			R.mul(matrixContainer);
		}
		setVecFromMouseEvent(e, prevMouseVec);
	}
	
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.setBorder( new TitledBorder("ArcBall Controls"));
		vfp.add( fit.getControls() );
		vfp.add( gain.getControls() );
		return vfp.getPanel();
	}
		
}
