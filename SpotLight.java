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
    material.setAmbient(1f, 1f, 0.5f);
    material.setDiffuse(1f, 1f, 0.5f);
    material.setSpecular(1f, 1f, 0.5f);
    direction = new Vec3(1, -1, 0); // face right by default
    cutOff = 0.91f;// 12.5f;
    outerCutOff = 0.82f; // 17.5f;
  }

  public void setDirection(Vec3 direction) {
    this.direction = direction;
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