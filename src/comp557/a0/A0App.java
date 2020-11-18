package comp557.a0;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.swing.JFrame;
import javax.vecmath.Matrix4f;

import com.jogamp.opengl.DebugGL4;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * Test class for checking JOGL setup
 */
public class A0App implements GLEventListener {

    public static void main(String[] args) {
        new A0App();
    }
    
    public A0App() {
    	GLProfile glprofile = GLProfile.get( GLProfile.GL4 );
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        GLCanvas glcanvas = new GLCanvas( glcapabilities );
        glcanvas.addGLEventListener(this);
        FPSAnimator animator; 
        animator = new FPSAnimator(glcanvas, 60);
        animator.start();
        final JFrame jframe = new JFrame( "JOGL OpenGL Setup Test" ); 
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });
        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 500, 500 );
        jframe.setVisible( true );
	}

    /** Model matrix */
    private Matrix4f MMatrix = new Matrix4f();
    /** View matrix */
    private Matrix4f VMatrix = new Matrix4f();
    /** Projection matrix */
    private Matrix4f PMatrix = new Matrix4f();
    
    private int glslProgramID;

    private int MMatrixID;
    private int VMatrixID;
    private int PMatrixID;
    private int positionAttributeID;
    
    private int positionBufferID;
    private int elementBufferID;
    
    /** Vertices */
    private float[] positionData = {
    	    -1, -1,  1,
    	     1, -1,  1,
    	     1,  1,  1,
    	    -1,  1,  1,
    	    -1, -1, -1,
    	     1, -1, -1,
    	     1,  1, -1,
    	    -1,  1, -1,
    	  };

    /** Triangles */
    private short[] elementData = { 
    		0, 1, 2,
    		2, 3, 0,
    		1, 5, 6,
    		6, 2, 1,
    		7, 6, 5,
    		5, 4, 7,
    		4, 0, 3,
    		3, 7, 4,
    		4, 5, 1,
    		1, 0, 4,
    		3, 2, 6,
    		6, 7, 3,
    	};

    
    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL4(drawable.getGL().getGL4()));
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glEnable( GL4.GL_BLEND );
		gl.glBlendFunc( GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA );
		gl.glEnable( GL4.GL_LINE_SMOOTH );

		// Create the GLSL program 
		glslProgramID = createProgram( drawable, "minimal" );
		
		// Get the IDs of the parameters (i.e., uniforms and attributes)
        gl.glUseProgram( glslProgramID );
        MMatrixID = gl.glGetUniformLocation( glslProgramID, "M" );
        VMatrixID = gl.glGetUniformLocation( glslProgramID, "V" );
        PMatrixID = gl.glGetUniformLocation( glslProgramID, "P" );
        positionAttributeID = gl.glGetAttribLocation( glslProgramID, "position" );

        // Initialize the vertex and index buffers
        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(positionData);
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);
        int[] bufferIDs = new int[2];
        gl.glGenBuffers( 2, bufferIDs, 0 );
        positionBufferID = bufferIDs[0];
        elementBufferID = bufferIDs[1];
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );
        gl.glBufferData( GL4.GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, GL4.GL_STATIC_DRAW );
        gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
        gl.glBufferData( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, GL4.GL_STATIC_DRAW );
    }
    
    /**
	 * Creates a GLSL program from the .vp and .fp code provided in the shader directory 
	 * @param drawable
	 * @param name
	 * @return
	 */
	private int createProgram( GLAutoDrawable drawable, String name ) {
		GL4 gl = drawable.getGL().getGL4();
		ShaderCode vsCode = ShaderCode.create( gl, GL4.GL_VERTEX_SHADER, this.getClass(), "glsl", "glsl/bin", name, false );
		ShaderCode fsCode = ShaderCode.create( gl, GL4.GL_FRAGMENT_SHADER, this.getClass(), "glsl", "glsl/bin", name, false );
		ShaderProgram shaderProgram = new ShaderProgram();
		shaderProgram.add( vsCode );
		shaderProgram.add( fsCode );
		if ( !shaderProgram.link( gl, System.err ) ) {
			throw new GLException( "Couldn't link program: " + shaderProgram );
		}	
		shaderProgram.init(gl);
        return shaderProgram.program();
	}
    
	@Override
	public void dispose( GLAutoDrawable drawable ) {
		// do nothing
	}

	@Override
	public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		// do nothing
	}
    
    private float[] columnMajorMatrixData = new float[16];
    
    /**
     * Wrapper to glUniformMatrix4fv for vecmath Matrix4f
     * @param gl
     * @param ID
     * @param M
     */
    public void glUniformMatrix( GL4 gl, int ID, Matrix4f M ) {
    	columnMajorMatrixData[0] = M.m00;
        columnMajorMatrixData[1] = M.m10;
        columnMajorMatrixData[2] = M.m20;
        columnMajorMatrixData[3] = M.m30;
        columnMajorMatrixData[4] = M.m01;
        columnMajorMatrixData[5] = M.m11;
        columnMajorMatrixData[6] = M.m21;
        columnMajorMatrixData[7] = M.m31;
        columnMajorMatrixData[8] = M.m02;
        columnMajorMatrixData[9] = M.m12;
        columnMajorMatrixData[10] = M.m22;
        columnMajorMatrixData[11] = M.m32;
        columnMajorMatrixData[12] = M.m03;
        columnMajorMatrixData[13] = M.m13;
        columnMajorMatrixData[14] = M.m23;
        columnMajorMatrixData[15] = M.m33;
        gl.glUniformMatrix4fv( ID, 1, false, columnMajorMatrixData, 0 );
    }
    
    @Override
    public void display( GLAutoDrawable drawable ) {
    	GL4 gl = drawable.getGL().getGL4();
        gl.glClear( GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT );
        
        MMatrix.set( new float[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0,  1,  0,
        		0,  0,  0,  1,
        } );
        VMatrix.set( new float[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0,  1, -2.5f,
        		0,  0,  0,  1,
        } );
        PMatrix.set( new float[] {
        		1,  0,  0,  0,
        		0,  1,  0,  0,
        		0,  0, -2, -3,
        		0,  0, -1,  1,
        } );
        
        MMatrix.rotY( (System.currentTimeMillis() % 10000) /1e3f );
        
        glUniformMatrix( gl, MMatrixID, MMatrix );
        glUniformMatrix( gl, VMatrixID, VMatrix );
        glUniformMatrix( gl, PMatrixID, PMatrix );

        gl.glUseProgram( glslProgramID );
        
        gl.glEnableVertexAttribArray( positionAttributeID );
        gl.glBindBuffer( GL4.GL_ARRAY_BUFFER, positionBufferID );		
		gl.glVertexAttribPointer( positionAttributeID, 3, GL4.GL_FLOAT, false, 3*Float.BYTES, 0 );
		gl.glBindBuffer( GL4.GL_ELEMENT_ARRAY_BUFFER, elementBufferID );
		gl.glPolygonMode( GL4.GL_FRONT_AND_BACK, GL4.GL_LINE ); // otherwise GL_FILL is the default
		gl.glDrawElements( GL4.GL_TRIANGLES, elementData.length, GL4.GL_UNSIGNED_SHORT, 0 );
		gl.glDisableVertexAttribArray( positionAttributeID );
   }
    
}