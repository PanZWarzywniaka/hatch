
/* I declare that this code is my own work */
/* Author Aleksander Marcin Osikowicz amosikowicz1@sheffield.ac.uk */
/* Addapted from Dr Steve Maddock  lab merials s.maddock@sheffield.ac.uk */
import gmaths.*;
import java.nio.*;
import java.util.Arrays;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public abstract class Light {

  protected Material material;
  protected boolean on;
  private Vec3 position;
  private Shader shader;
  private Camera camera;
  private float attConstant, attLinear, attQuadratic;

  public Light(GL3 gl, Camera c) {
    material = new Material();
    material.setAmbient(1f, 1f, 1f);
    material.setDiffuse(1f, 1f, 1f);
    material.setSpecular(1f, 1f, 1f);
    position = new Vec3(0f, 0f, 0f);

    on = true;
    attConstant = 1f;
    attLinear = 0.09f;
    attQuadratic = 0.032f;
    fillBuffers(gl);
    shader = new Shader(gl, "shaders/vs_light.txt", "shaders/fs_light.txt");
    camera = c;
  }

  public boolean isOn() {
    return on;
  }

  public void switchLight() {
    this.on = !this.on;
  }

  public float getAttConstant() {
    return attConstant;
  }

  public void setAttConstant(float att_constant) {
    this.attConstant = att_constant;
  }

  public float getAttLinear() {
    return attLinear;
  }

  public void setAttLinear(float att_linear) {
    this.attLinear = att_linear;
  }

  public float getAttQuadratic() {
    return attQuadratic;
  }

  public void setAttQuadratic(float att_quadratic) {
    this.attQuadratic = att_quadratic;
  }

  public Vec3 getPosition() {
    return position;
  }

  public void setMaterial(Material m) {
    material = m;
  }

  public Material getMaterial() {
    return material;
  }

  private void updatePosition(Mat4 modelMatrix) {
    float[] values = modelMatrix.toFloatArrayForGLSL();
    position.x = values[12];
    position.y = values[13];
    position.z = values[14];

  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    // System.out.println("Light model matrix\n" + modelMatrix.toString());

    updatePosition(modelMatrix);

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    shader.setVec3(gl, "aColor", material.getAmbient());
    if (!on) {
      shader.setVec3(gl, "aColor", new Vec3(0.2f));
    }

    gl.glBindVertexArray(vertexArrayId[0]);

    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

  // ***************************************************
  /*
   * THE DATA
   */
  // anticlockwise/counterclockwise ordering

  private float[] vertices = new float[] { // x,y,z
      -0.5f, -0.5f, -0.5f, // 0
      -0.5f, -0.5f, 0.5f, // 1
      -0.5f, 0.5f, -0.5f, // 2
      -0.5f, 0.5f, 0.5f, // 3
      0.5f, -0.5f, -0.5f, // 4
      0.5f, -0.5f, 0.5f, // 5
      0.5f, 0.5f, -0.5f, // 6
      0.5f, 0.5f, 0.5f // 7
  };

  private int[] indices = new int[] {
      0, 1, 3, // x -ve
      3, 2, 0, // x -ve
      4, 6, 7, // x +ve
      7, 5, 4, // x +ve
      1, 5, 7, // z +ve
      7, 3, 1, // z +ve
      6, 4, 0, // z -ve
      0, 2, 6, // z -ve
      0, 4, 5, // y -ve
      5, 1, 0, // y -ve
      2, 3, 7, // y +ve
      7, 6, 2 // y +ve
  };

  private int vertexStride = 3;
  private int vertexXYZFloats = 3;

  // ***************************************************
  /*
   * THE LIGHT BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];

  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);

    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);

    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);

    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    // gl.glBindVertexArray(0); // remove this so shader can be validated. Should be
    // ok as any new object will bind its own VAO
  }

}