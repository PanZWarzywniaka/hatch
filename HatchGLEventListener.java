import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

public class HatchGLEventListener implements GLEventListener {

  private static final boolean DISPLAY_SHADERS = false;
  private Camera camera;

  /* The constructor is not used to initialise anything */
  public HatchGLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0f, 8f, 20f));
    this.camera.setTarget(new Vec3(0f, 5f, 0f));
  }

  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    // gl.glClearColor(0.2f, 0.2f, 1f, 1.0f); //blue
    gl.glClearColor(0f, 0f, 0f, 1.0f); // black
    gl.glClearDepth(1.0f);

    // blend
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW); // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled' so needs to be enabled
    gl.glCullFace(GL.GL_BACK); // default is 'back', assuming CCW
    initialise(gl);
  }

  /* Called to indicate the drawing surface has been moved and/or resized */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float) width / (float) height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    scene.dispose(gl);
  }

  // ***************************************************
  /*
   * THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Scene scene;

  public void initialise(GL3 gl) {
    scene = new Scene(gl, camera);

  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    scene.render(gl);
  }

  public void toggleLight(int i) {
    scene.toggleLight(i);
  }

  public void leftLampPosition1() {
    scene.leftLampPosition1();
  }

  public void leftLampPosition2() {
    scene.leftLampPosition2();
  }

  public void leftLampPosition3() {
    scene.leftLampPosition3();
  }

  public void rightLampPosition1() {
    scene.rightLampPosition1();
  }

  public void rightLampPosition2() {
    scene.rightLampPosition2();
  }

  public void rightLampPosition3() {
    scene.rightLampPosition3();
  }

}