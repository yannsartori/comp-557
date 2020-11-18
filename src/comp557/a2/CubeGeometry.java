// Yann Sartori 260822776
package comp557.a2;

import com.jogamp.opengl.GLAutoDrawable;

import comp557.a2.geom.Cube;

public class CubeGeometry extends GeometryNode {

    
    public CubeGeometry(String name) {
        super(name);
    }

    public CubeGeometry(String name, double tx, double ty, double tz, double sx, double sy, double sz, double cr, 
        double cb, double cg, double rx, double ry, double rz, double ks, double kd) {
        super(name, tx, ty, tz, sx, sy, sz, cr, cb, cg, rx, ry, rz, ks, kd);
    }

    @Override
    public void display(GLAutoDrawable drawable, ShadowPipeline pipeline) {
        pipeline.push();
        // scale before translating because joint movements look better
        pipeline.scale(drawable, sx, sy, sz);
        pipeline.translate(drawable, tx, ty, tz);
        pipeline.rotate(drawable, Math.toRadians(rz), 0, 0, 1);
		pipeline.rotate(drawable, Math.toRadians(ry), 0, 1, 0);
        pipeline.rotate(drawable, Math.toRadians(rx), 1, 0, 0);
        pipeline.setkd(drawable, cr, cb, cg);
        Cube.draw(drawable, pipeline);
        pipeline.pop(drawable);
    }
    
    public String toString() {
    	return name;
    }
}
