import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

import dataclasses.Cube;
import dataclasses.TwoTriangles;

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
    startTime = getSeconds();
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
    room.dispose(gl);
    table.dispose(gl);
    light.dispose(gl);
    lamp.dispose(gl);
  }

  // ***************************************************
  /*
   * THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Room room;
  private Light light;
  private Table table;
  private Lamp lamp;

  public void initialise(GL3 gl) {

    light = new Light(gl);
    light.setCamera(camera);
    room = new Room(gl, camera, light);
    table = new Table(gl, camera, light);
    lamp = new Lamp(gl, camera, light);
  }

  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    light.setPosition(getLightPosition()); // changing light position each frame
    light.render(gl);

    room.render(gl);
    table.render(gl);
    lamp.render(gl);
  }

  // The light's position is continually being changed, so needs to be calculated
  // for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds() - startTime;
    float x = 5.0f * (float) (Math.sin(Math.toRadians(elapsedTime * 50)));
    float y = 3.4f;
    float z = 5.0f * (float) (Math.cos(Math.toRadians(elapsedTime * 50)));
    return new Vec3(x, y, z);
  }

  // ***************************************************
  /*
   * TIME
   */

  private double startTime;

  public double getSeconds() {
    return System.currentTimeMillis() / 1000.0;
  }

}