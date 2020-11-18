// Yann Sartori 260822776

package comp557.a3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Queue;
import java.util.LinkedList;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;

import mintools.parameters.IntParameter;
import mintools.viewer.ShadowPipeline;
import mintools.viewer.geom.Sphere;

/**
 * BezierMesh is a class for loads a collection of Bezier patches and displaying them using OpenGL.
 */
public class BezierPatchWork {
    
	/**
	 * Bezier geometry patches, indexed first by patch number, then by axis.
	 * For instance:
	 * coordinatePatch[0][0] gives the 4 by 4 matrix of x control points for the first patch.
	 * coordinatePatch[0][1] gives the 4 by 4 matrix of y control points for the first patch.
	 * coordinatePatch[0][2] gives the 4 by 4 matrix of z control points for the first patch.
	 */
    private Matrix4d coordinatePatch[][];
    
    /**
     * Used in the case of rational. Same principles as above apply
     */
    private Matrix3d rationalPatch[][];
    
    /**
     * The number of evaluations that should be made along each parameter direction 
     * when drawing the quad mesh for a given patch.
     */
    public IntParameter subdivisions = new IntParameter( "Bezier Patch Refinement", 12, 2, 24 );
		
    /**
     * @return the number of patches in the loaded model
     */
    public int getNumPatches() {
        return (coordinatePatch != null) ? coordinatePatch.length : rationalPatch.length;
    }

    /**
     * Draws the control points of the specified Bezier patch
     * @param gl 
     * @param patch 
     */
    public void drawControlPoints( GLAutoDrawable drawable, ShadowPipeline pipeline, int patch ) {
    	pipeline.setkd(drawable, 0.0, 0.9, 0.0 );

    	// TODO: Objective 1: Draw the control points of the selected patch (so that you can draw all of them)
    	if ( coordinatePatch != null ) {
	    	Matrix4d x = coordinatePatch[patch][0];
	    	Matrix4d y = coordinatePatch[patch][1];
	    	Matrix4d z = coordinatePatch[patch][2];
	    	for ( int i = 0; i < 4; i++ ) {
	    		for ( int j = 0; j < 4; j++ ) {
	    			pipeline.push();
	    			pipeline.translate(drawable, x.getElement(i, j), y.getElement(i, j), z.getElement(i, j));
	    			pipeline.scale(drawable, 0.05, 0.05, 0.05);
	    			Sphere.draw(drawable, pipeline);
	    			pipeline.pop(drawable);
	    		}
	    	}
    	} else {
    		Matrix3d x = rationalPatch[patch][0];
	    	Matrix3d y = rationalPatch[patch][1];
	    	Matrix3d z = rationalPatch[patch][2];
	    	Matrix3d w = rationalPatch[patch][3];
	    	for ( int i = 0; i < 3; i++ ) {
	    		for ( int j = 0; j < 3; j++ ) {
	    			pipeline.push();
	    			pipeline.translate(drawable, x.getElement(i, j)/w.getElement(i, j), 
	    					y.getElement(i, j)/w.getElement(i, j), z.getElement(i, j)/w.getElement(i, j));
	    			pipeline.scale(drawable, 0.05, 0.05, 0.05);
	    			Sphere.draw(drawable, pipeline);
	    			pipeline.pop(drawable);
	    		}
	    	}
    	}
    	
    }
    
    FloatBuffer vertexBuffer;
	FloatBuffer normalBuffer;
	ShortBuffer indexBuffer;
    FloatBuffer lineVertexBuffer;
	ShortBuffer lineIndexBuffer;
    
	// TODO: Note that there is not check if this buffer is exceeded!
	final int maxVerts = 10000*3;
	final int maxInds = 10000*3;
	
	int positionBufferID;
	int normalBufferID;
	int elementBufferID;
	
	int linePositionBufferID;
	int lineElementBufferID;

	/**
	 * Create the vertex normal and index buffers for both drawing a patch, but also for drawing a debugging line for each tangent.
	 * @param drawable
	 */
	public void init( GLAutoDrawable drawable ) {
	    vertexBuffer = GLBuffers.newDirectFloatBuffer( maxVerts );
		normalBuffer = GLBuffers.newDirectFloatBuffer( maxVerts );
		indexBuffer = GLBuffers.newDirectShortBuffer( maxInds );
		lineVertexBuffer = GLBuffers.newDirectFloatBuffer( 6 );
		lineIndexBuffer = GLBuffers.newDirectShortBuffer( new short[] { 0 , 1 } );
		GL4 gl = drawable.getGL().getGL4();
        int[] bufferIDs = new int[5];
        gl.glGenBuffers( 5, bufferIDs, 0 );
        positionBufferID = bufferIDs[0];
        normalBufferID = bufferIDs[1];
        elementBufferID = bufferIDs[2];
        linePositionBufferID = bufferIDs[3];
        lineElementBufferID = bufferIDs[4];
        // Actually don't need to bind it now... just when we need to fill it out again.
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, normalBuffer.capacity() * Float.BYTES, normalBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * Short.BYTES, indexBuffer, GL4.GL_DYNAMIC_DRAW );

        // 6 values for the end points of 2 lines, and then 2 static (never changing) indices 0 and 1 to draw the line
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, lineVertexBuffer.capacity() * Float.BYTES, lineVertexBuffer, GL4.GL_DYNAMIC_DRAW ); 
        // This one won't change, so we can make it a static draw.
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, lineElementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, lineIndexBuffer.capacity() * Short.BYTES, lineIndexBuffer, GL4.GL_STATIC_DRAW );
	}
    
	/**
	 * Draws the specified Bezier patch.
	 * @param gl     OpenGL drawing context
	 * @param patch  the index of the patch to draw
	 */
	public void draw( GLAutoDrawable drawable, ShadowPipeline pipeline, int patch ) {
		GL4 gl = drawable.getGL().getGL4();
		
        Vector3d coord, normal;
		
        // TODO: Note that this code will evaluate your surface and fill buffers for drawing
        vertexBuffer.rewind();
        normalBuffer.rewind();
		int N = subdivisions.getValue();
		int vertDataCount = 3*N*N;
		for ( int i = 0; i < N; i++ ) {
			for ( int j = 0; j < N; j++ ) {
			    double s1 = (double)i/(N-1);
			    double t1 = (double)j/(N-1);
				coord = evaluateCoordinate( s1, t1, patch );
				normal = evalNormal( s1, t1, patch );
				vertexBuffer.put( (float) coord.x );
				vertexBuffer.put( (float) coord.y );
				vertexBuffer.put( (float) coord.z );
				normalBuffer.put( (float) normal.x );
				normalBuffer.put( (float) normal.y );
				normalBuffer.put( (float) normal.z );				
			}
		}

		indexBuffer.rewind();
		int numInds = 2*N*(N-1);
		for ( int i = 0; i < N-1; i++ ) {
			for ( int j = 0; j < N; j++ ) {
				indexBuffer.put( (short)(i*N+j) );
				indexBuffer.put( (short)((i+1)*N+j) );
			}
		}
		
		vertexBuffer.rewind();
		normalBuffer.rewind();
		indexBuffer.rewind();
		
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertDataCount * Float.BYTES, vertexBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, normalBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertDataCount * Float.BYTES, normalBuffer, GL4.GL_DYNAMIC_DRAW );
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, numInds * Short.BYTES, indexBuffer, GL4.GL_DYNAMIC_DRAW );
		
		pipeline.currentGLSLProgram.bindPositionBuffer(gl, positionBufferID);
		pipeline.currentGLSLProgram.bindNormalBuffer(gl, normalBufferID);
		gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
		for ( int i = 0; i < N-1; i++ ) {
			gl.glDrawElements( GL4.GL_TRIANGLE_STRIP, 2*N, GL4.GL_UNSIGNED_SHORT, i*2*N*Short.BYTES );
		}
	
	}
	
	/**
	 * Draws a local surface tangents on the selected patch at the given coordinates
	 * @param gl 
	 * @param patch 
	 * @param s
	 * @param t
	 */
	public void drawSurfaceTangents( GLAutoDrawable drawable, ShadowPipeline pipeline, int patch, double s, double t ) {
	    GL4 gl = drawable.getGL().getGL4();	    
		
	    // TODO: Note this code to draw your tangents for objective 3
		Vector3d coord,ds,dt;
		coord = evaluateCoordinate( s, t, patch );
		ds = differentiateS(s, t, patch);
        dt = differentiateT(s, t, patch);
                
        pipeline.disableLighting(drawable);
		pipeline.currentGLSLProgram.bindPositionBuffer( gl, linePositionBufferID );

        // draw a red line
        pipeline.setkd(drawable, 1, 0, 0);
        lineVertexBuffer.rewind();
        lineVertexBuffer.put( new float[] { 
        		(float) coord.x, 
        		(float) coord.y, 
        		(float) coord.z, 
        		(float) (coord.x + ds.x), 
        		(float) (coord.y + ds.y), 
        		(float) (coord.z + ds.z) } );
        lineVertexBuffer.rewind();         
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, linePositionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, lineVertexBuffer.capacity() * Float.BYTES, lineVertexBuffer, GL4.GL_DYNAMIC_DRAW ); 
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, lineElementBufferID);
        gl.glDrawElements( GL4.GL_LINES, 2, GL4.GL_UNSIGNED_SHORT, 0 );

        // draw a green line
        pipeline.setkd(drawable, 0, 1, 0);
        lineVertexBuffer.rewind();
        lineVertexBuffer.put( new float[] { 
        		(float) coord.x, 
        		(float) coord.y, 
        		(float) coord.z, 
        		(float) (coord.x + dt.x), 
        		(float) (coord.y + dt.y), 
        		(float) (coord.z + dt.z) } );
        lineVertexBuffer.rewind();         
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, linePositionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, lineVertexBuffer.capacity() * Float.BYTES, lineVertexBuffer, GL4.GL_DYNAMIC_DRAW ); 
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, lineElementBufferID );
        gl.glDrawElements( GL4.GL_LINES, 2, GL4.GL_UNSIGNED_SHORT, 0 );
        
        pipeline.enableLighting(drawable);        
	}
	
	/**
	 * Constructor: Loads a Bezier Mesh contained in a file
	 * @param file 
	 */
	public BezierPatchWork( String file ) {	    
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			if (input != null) {
				String firstLine = input.readLine();
				if ( firstLine.equals("r") ) {
					// Surface of revolution about the z-axis (due to laziness)
					
					int curveCount = Integer.parseInt(input.readLine());
					int patchesPerCurve = Integer.parseInt(input.readLine());
					double dTheta = (2 * Math.PI) / (patchesPerCurve * 3);
					coordinatePatch = new Matrix4d[curveCount * patchesPerCurve][3];
					// An array where each entry is a 4 tuple of control points
					// 4 entries form a patch
					double[][][] rotatedPoints = new double[patchesPerCurve * 3][4][3];
					String[] controls = null;
					String[] firstControls = input.readLine().split(",");
					for ( int curCurve = 0; curCurve < curveCount; curCurve++) {
						double x = Double.parseDouble(firstControls[0]);
						double y = Double.parseDouble(firstControls[1]);
						double z = Double.parseDouble(firstControls[2]);
						double theta = 0;
						for ( int count = 0; count < rotatedPoints.length; count++, theta += dTheta ) {
							rotatedPoints[count][0][0] = Math.cos(theta) * x - Math.sin(theta) * y;
							rotatedPoints[count][0][1] = Math.sin(theta) * x + Math.cos(theta) * y;
							rotatedPoints[count][0][2] = z;
						}
						for ( int point = 1; point < 4; point++ ) {
							controls = input.readLine().split(",");
							x = Double.parseDouble(controls[0]);
							y = Double.parseDouble(controls[1]);
							z = Double.parseDouble(controls[2]);

							for ( int count = 0; count < rotatedPoints.length; count++, theta += dTheta ) {
								rotatedPoints[count][point][0] = Math.cos(theta) * x - Math.sin(theta) * y;
								rotatedPoints[count][point][1] = Math.sin(theta) * x + Math.cos(theta) * y;
								rotatedPoints[count][point][2] = z;
							}
						}
						for ( int patch = 0; patch < patchesPerCurve; patch++ ) {
							for (int coord = 0; coord < 3; coord++ ) {
								coordinatePatch[curCurve*patchesPerCurve + patch][coord] = new Matrix4d();
								for (int i = 0; i < 4; i++ ) {
									for ( int j = 0; j < 4; j++ ) {
										coordinatePatch[curCurve*patchesPerCurve + patch][coord].setElement(
												i, j,
												rotatedPoints[(patch*3 + i) % rotatedPoints.length][j][coord]
										);
									}
								}
								
							}
						}
						
						firstControls = controls;
					}
				} else {
					// Cubic by default
					int dimPatch = 4;
					// Non homogenous by default
					int numCoords = 3;
					boolean isRational = false;
					if ( firstLine.equals("rational") ) {
						dimPatch = 3;
						numCoords = 4;
						firstLine = input.readLine();
						isRational = true;
					}
					int numPatches = Integer.parseInt(firstLine);
					String[] controls;
					int[][][] controlQuads = new int[numPatches][dimPatch][dimPatch];
					for ( int i = 0; i < numPatches ; i++ ) {
						controls = input.readLine().split(",");
						for ( int j = 0 ; j < dimPatch ; j++ ) {
							for ( int k = 0; k < dimPatch ; k++ ) {
								controlQuads[i][j][k] = Integer.parseInt(controls[j*dimPatch+k])-1;						
							}
						}
					}
					int numPoints = Integer.parseInt(input.readLine());
					float [][] coords = new float[numPoints][numCoords];
					for (int i=0; i < numPoints ; i++ ) {
						controls = input.readLine().split(",");
						for (int j=0;j<numCoords;j++) {
							coords[i][j] = Float.parseFloat(controls[j]);
						}
					}
					if ( isRational ) {
						// Could be embedded in coordinate patch, but this is easier
						rationalPatch = new Matrix3d[controlQuads.length][numCoords];
						for ( int i = 0; i < controlQuads.length; i++ ) {
							for ( int j = 0; j < numCoords; j++) {
								rationalPatch[i][j] = new Matrix3d();
								for ( int k = 0; k < dimPatch; k++ ) {
									for ( int l = 0; l < dimPatch; l++ ) {
										rationalPatch[i][j].setElement(k, l, coords[controlQuads[i][k][l]][j]);
									}
								}
							}
						}
						
					} else {
						coordinatePatch = new Matrix4d[controlQuads.length][numCoords];
						for (int i=0; i < controlQuads.length ; i++) {
							for (int j=0; j < numCoords ; j++) {
								coordinatePatch[i][j] = new Matrix4d();
								for (int k=0; k < dimPatch ; k++) {
									for (int l=0; l < dimPatch ; l++) {
										coordinatePatch[i][j].setElement(k, l, coords[controlQuads[i][k][l]][j]);
									}
								}
							}
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	/**
	 *  returns the xyz coordinates of the Bezier mesh at the parametric point (s,t)
	 */
	
	// auxiliary fields to avoid excessive instantiation 
	private Matrix4d cBezierToPower = new Matrix4d(
			-1,  3, -3, 1,
			 3, -6,  3, 0,
			-3,  3,  0, 0,
			 1,  0,  0, 0
	);
	private Matrix4d cIntermediateMat = new Matrix4d();
	private Vector4d cSVec = new Vector4d();
	private Vector4d cTVec = new Vector4d();
	private Vector4d cIntermediateVec = new Vector4d();
	private double[] cCoords = new double[3];
	
	// Cubic, non homogenous
	private Vector3d evalBezAtBases(Vector4d b1, Vector4d b2, int patch) {
		for ( int i = 0; i < 3; i++ ) {
    		cIntermediateMat.set(coordinatePatch[patch][i]);
    		cIntermediateMat.transpose();
    		cBezierToPower.transpose();
    		cIntermediateMat.mul(cBezierToPower);
    		leftVecMult(cIntermediateMat, b1, cIntermediateVec);
    		cBezierToPower.transpose();
    		leftVecMult(cBezierToPower, cIntermediateVec, cIntermediateVec);
    		cCoords[i] = cIntermediateVec.dot(b2);
    	}
		return new Vector3d(cCoords);
	}
	
	// auxiliary fields to avoid excessive instantiation 
	private Matrix3d qBezierToPower = new Matrix3d(
			1, -2, 1,
			-2, 2, 0,
			1,  0, 0
	);
	private Matrix3d qIntermediateMat = new Matrix3d();
	private Vector3d qSVec = new Vector3d();
	private Vector3d qTVec = new Vector3d();
	private Vector3d qIntermediateVec = new Vector3d();
	private double[] qCoords = new double[4];
	
	// Quadratic, homogenous
	private Vector3d evalBezAtBases(Vector3d b1, Vector3d b2, int patch, boolean isVec) {
		for ( int i = 0; i < 4; i++ ) {
    		qIntermediateMat.set(rationalPatch[patch][i]);
    		qIntermediateMat.transpose();
    		qBezierToPower.transpose();
    		qIntermediateMat.mul(qBezierToPower);
    		leftVecMult(qIntermediateMat, b1, qIntermediateVec);
    		qBezierToPower.transpose();
    		leftVecMult(qBezierToPower, qIntermediateVec, qIntermediateVec);
    		qCoords[i] = qIntermediateVec.dot(b2);
    	}
		if ( !isVec ) return new Vector3d(qCoords[0]/qCoords[3], qCoords[1]/qCoords[3], qCoords[2]/qCoords[3]);
		else return new Vector3d(qCoords[0], qCoords[1], qCoords[2]);
	}
	
	private Vector3d evaluateCoordinate( double s, double t, int patch ) {
		// TODO: Objective 2: Evaluate the surface positions (as opposed to the zero vector)
		if ( coordinatePatch != null ) {
	    	cSVec.set(s*s*s, s*s, s, 1);
	    	cTVec.set(t*t*t, t*t, t, 1);
	    	return evalBezAtBases(cSVec, cTVec, patch);
		} else {
			qSVec.set(s*s, s, 1);
			qTVec.set(t*t, t, 1);
			return evalBezAtBases(qSVec, qTVec, patch, false);
		}
    	
	}
	
	// Calculates M * v and puts it in result, where v and result are column vectors
	private void leftVecMult(Matrix4d M, Vector4d v, Vector4d result) {
		result.set(
				M.m00*v.x + M.m01*v.y + M.m02*v.z + M.m03*v.w,
				M.m10*v.x + M.m11*v.y + M.m12*v.z + M.m13*v.w,
				M.m20*v.x + M.m21*v.y + M.m22*v.z + M.m23*v.w,
				M.m30*v.x + M.m31*v.y + M.m32*v.z + M.m33*v.w
		);
	}
	
	private void leftVecMult(Matrix3d M, Vector3d v, Vector3d result) {
		result.set(
				M.m00*v.x + M.m01*v.y + M.m02*v.z,
				M.m10*v.x + M.m11*v.y + M.m12*v.z,
				M.m20*v.x + M.m21*v.y + M.m22*v.z
		);
	}
	/**
	 *  differentiates the Bezier mesh along the parametric 's' direction
	 */
	private Vector3d differentiateS(double s,double t,int patch) {
		// TODO: Objective 3: Evaluate the surface derivative in the s direction
		if ( coordinatePatch != null ) {
			cSVec.set(3*s*s, 2*s, 1, 0);
			cTVec.set(t*t*t, t*t, t, 1);
	    	Vector3d result = evalBezAtBases(cSVec, cTVec, patch);
	    	if( result.length() != 0 ) result.normalize();
	    	return result;
		} else {
			qSVec.set(2*s, 1, 0);
	    	qTVec.set(t*t, t, 1);
	    	Vector3d result = evalBezAtBases(qSVec, qTVec, patch, true);
	    	if( result.length() != 0 ) result.normalize();
	    	return result;
		}
		
	}
	/**
	 *  differentiates the Bezier mesh along the parametric 't' direction
	 */
	private Vector3d differentiateT(double s,double t,int patch) {
		// TODO: Objective 3: Evaluate the surface derivative in the t direction
		if ( coordinatePatch != null ) {
			cSVec.set(s*s*s, s*s, s, 1);
			cTVec.set(3*t*t, 2*t, 1, 0);
	    	Vector3d result = evalBezAtBases(cSVec, cTVec, patch);
	    	if( result.length() != 0 ) result.normalize();
	    	return result;
		} else {
			qSVec.set(s*s, s, 1);
	    	qTVec.set(2*t, 1, 0);
	    	Vector3d result = evalBezAtBases(qSVec, qTVec, patch, true);
	    	if( result.length() != 0 ) result.normalize();
	    	return result;
		}
	}
	
	
	/**
	 *  returns the normal of the Bezier mesh at the parametric (s,t) point.
	 */
	private Vector3d evalNormal(double s, double t, int patch) {
		// TODO: Objective 4,5: compute the normal, and make sure the normal is always well defined!
		Queue<Double> sQueue = new LinkedList<>();
		Queue<Double> tQueue = new LinkedList<>();
		Vector3d result = new Vector3d();
		sQueue.add(s);
		tQueue.add(t);
		double sNudge = 0.05;
		double tNudge = 0.05;
		double curS, curT;
		Vector3d tTan, sTan;
		while ( result.length() <= Double.MIN_VALUE ) {
			curS = sQueue.remove();
			curT = tQueue.remove();
			
			// We are out of range of our current spline
			if (curS > 1 || curT > 1 || curS < 0 || curT < 0) continue;
			
			// Pushes the "pairs" (+ delta, + delta), (+ delta, - delta), (- delta, + delta), (- delta, - delta)
			sQueue.add(curS + sNudge);
			sQueue.add(curS + sNudge);
			sQueue.add(curS - sNudge);
			sQueue.add(curS - sNudge);
			
			tQueue.add(curT + tNudge);
			tQueue.add(curT - tNudge);
			tQueue.add(curT + tNudge);
			tQueue.add(curT - tNudge);
			
			tTan = differentiateT(curS, curT, patch);
			sTan = differentiateS(curS, curT, patch);
			result.cross(sTan, tTan);
		}
		return result;
	}
}
