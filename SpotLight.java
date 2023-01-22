/* I declare that this code is my own work */
/* Author Aleksander Marcin Osikowicz amosikowicz1@sheffield.ac.uk */

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class SpotLight extends Light {

  private Vec3 direction;

  private float cutOff;
  private float outerCutOff;

  public SpotLight(GL3 gl, Camera c) {
    super(gl, c);
    Vec3 lightColour = new Vec3(1f, 1f, 0.5f); // war yellow ish light
    material.setAmbient(lightColour);
    material.setDiffuse(lightColour);
    material.setSpecular(lightColour);
    direction = new Vec3(1, 0, 0); // face right by default
    cutOff = 0.91f;
    outerCutOff = 0.82f;
  }

  private void updateDirection(Mat4 modelMatrix) { // direction of the spotlight where the head is pointing to
    float[] values = modelMatrix.toFloatArrayForGLSL();
    direction.x = -values[0];
    direction.y = -values[1];
    direction.z = -values[2];
  }

  public void setDirection(Vec3 direction) {
    this.direction = direction;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    updateDirection(modelMatrix);
    super.render(gl, modelMatrix);
  }

  public Vec3 getDirection() {
    return direction;
  }

  public float getCutOff() {
    return cutOff;
  }

  public float getOuterCutOff() {
    return outerCutOff;
  }

}