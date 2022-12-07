import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import java.util.ArrayList;

public class Model {

  private Mesh mesh;
  private Texture textureId1;
  private Texture textureId2;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private ArrayList<Light> lights;

  public Model(GL3 gl, Camera camera, ArrayList<Light> lights, Shader shader, Material material, Mat4 modelMatrix,
      Mesh mesh,
      Texture textureId1, Texture textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.lights = lights;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }

  public Model(GL3 gl, Camera camera, ArrayList<Light> lights, Shader shader, Material material, Mat4 modelMatrix,
      Mesh mesh,
      Texture textureId1) {
    this(gl, camera, lights, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, ArrayList<Light> lights, Shader shader, Material material, Mat4 modelMatrix,
      Mesh mesh) {
    this(gl, camera, lights, shader, material, modelMatrix, mesh, null, null);
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setShader(Shader s) {
    shader = s;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());
    // System.out.println(lights.size());
    // TODO add transparency to model
    for (int i = 0; i < lights.size(); i++) {

      Light light = lights.get(i);

      boolean is_spotlight = light instanceof SpotLight;

      String target;
      if (is_spotlight) {
        target = String.format("spotLights[%d].", i - 2);
      } else {
        target = String.format("pointLights[%d].", i);
      }
      // System.out.println(light.getPosition());
      shader.setVec3(gl, target + "position", light.getPosition());

      Material m = new Material(new Vec3(), new Vec3(), new Vec3(), 0);
      if (light.isOn()) {
        m = light.getMaterial();
      }

      shader.setVec3(gl, target + "ambient", m.getAmbient());
      shader.setVec3(gl, target + "diffuse", m.getDiffuse());
      shader.setVec3(gl, target + "specular", m.getSpecular());

      shader.setFloat(gl, target + "constant", light.getAttConstant());
      shader.setFloat(gl, target + "linear", light.getAttLinear());
      shader.setFloat(gl, target + "quadratic", light.getAttQuadratic());

      if (is_spotlight) {
        SpotLight l = (SpotLight) light;
        shader.setVec3(gl, target + "direction", l.getDirection());
        shader.setFloat(gl, target + "cutOff", l.getCutOff());
        shader.setFloat(gl, target + "outerCutOff", l.getOuterCutOff());
      }

    }

    shader.setFloat(gl, "material.shininess", material.getShininess());
    if (textureId1 != null) {
      shader.setInt(gl, "material.diffuse", 0); // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      textureId1.bind(gl); // uses JOGL Texture class
    }
    if (textureId2 != null) {
      shader.setInt(gl, "material.specular", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      textureId2.bind(gl); // uses JOGL Texture class
    }
    mesh.render(gl);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1 != null)
      textureId1.destroy(gl);
    if (textureId2 != null)
      textureId2.destroy(gl);
  }

}

// shader.setVec3(gl, "material.ambient", material.getAmbient());
// shader.setVec3(gl, "material.diffuse", material.getDiffuse());
// shader.setVec3(gl, "material.specular", material.getSpecular());