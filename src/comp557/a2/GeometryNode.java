// Yann Sartori 260822776
package comp557.a2;

import com.jogamp.opengl.GLAutoDrawable;

public abstract class GeometryNode extends GraphNode {

    double tx;
    double ty;
    double tz;

    double sx;
    double sy;
    double sz;

    double rx;
    double ry;
    double rz;

    double cr;
    double cb;
    double cg;

    double ks;
    double kd;

    public GeometryNode(String name, double tx, double ty, double tz, double sx, double sy, double sz, double cr, 
        double cb, double cg, double rx, double rz, double ry, double ks, double kd) {
        super(name);
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
        this.cr = cr;
        this.cb = cb;
        this.cg = cg;
        this.ks = ks;
        this.kd = kd;
    }

    public GeometryNode(String name) {
        // Default to a unit, white geometry
        this(name, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0.8);
    }

    public void setTranslation(double tx, double ty, double tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    public void setScale(double sx, double sy, double sz) {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
    }

    public void setRotation(double rx, double ry, double rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
    }

    public void setColour(double cr, double cb, double cg) {
        this.cr = cr;
        this.cb = cb;
        this.cg = cg;
    }

    public void setKs(double ks) {
        this.ks = ks;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public abstract void display( GLAutoDrawable drawable, ShadowPipeline pipeline );
    
}
