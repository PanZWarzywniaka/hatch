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

public class Room {

  private Model[] walls;
  private Camera camera;
  private Light light;
  private float size = 16f;
  private double startTime;
  private Texture floor_tex;
  private Texture wall_tex;
  private Texture window_tex, window_spec_tex;
  private Texture skybox_tex;
  private Vec3 basecolor;
  private Material wall_mat, window_mat;
  private Shader wallShader, windowShader, skyboxShader;

  public Room(GL3 gl, Camera c, Light l) {
    camera = c;
    light = l;
    startTime = System.currentTimeMillis() / 1000.0;
    System.out.println(startTime);
    basecolor = new Vec3(0.5f, 0.5f, 0.5f); // grey
    wall_mat = new Material(basecolor, basecolor, new Vec3(0.0f, 0.0f, 0.0f), 1.0f);

    Vec3 base_win_color = Vec3.multiply(basecolor, 0.1f);
    window_mat = new Material(base_win_color, base_win_color, new Vec3(0.3f, 0.3f, 0.3f), 32f);

    wallShader = new Shader(gl, "shaders/vs_tt_05.txt", "shaders/fs_tt_05.txt");
    windowShader = new Shader(gl, "shaders/vs_tt_05.txt", "shaders/fs_window.txt");
    skyboxShader = new Shader(gl, "shaders/vs_skybox.txt", "shaders/fs_skybox.txt");
    loadTextures(gl);
    makeAllWalls(gl);
  }

  private void loadTextures(GL3 gl) {
    floor_tex = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    wall_tex = TextureLibrary.loadTexture(gl, "textures/brick.jpeg");
    window_tex = TextureLibrary.loadTexture(gl, "textures/glass.jpg");
    window_spec_tex = TextureLibrary.loadTexture(gl, "textures/glass_specular.jpg");
    skybox_tex = TextureLibrary.loadTexture(gl, "textures/cloud2.jpg");
  }

  private void makeAllWalls(GL3 gl) {
    walls = new Model[5];

    walls[0] = makeFloor(gl);
    walls[1] = makeWall(gl, true); // right wall
    walls[2] = makeWall(gl, false); // left wall
    walls[3] = makeSkybox(gl);
    walls[4] = makeWindow(gl);

  }

  private Model makeSurface(GL3 gl, Mat4 modelMatrix, Shader shdr, Material mat, Texture tex1, Texture tex2) {

    // create floor
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());

    Model model = new Model(gl, camera, light, shdr, mat, modelMatrix, mesh, tex1, tex2);
    return model;
  }

  private Model makeSkybox(GL3 gl) {
    Mat4 modelMatrix = new Mat4(1);
    float sb_size = size * 2f;
    modelMatrix = Mat4.multiply(Mat4Transform.scale(sb_size, 1f, sb_size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, size / 1.5f, -sb_size * 0.5f), modelMatrix);
    return makeSurface(gl, modelMatrix, skyboxShader, wall_mat, skybox_tex, null);
  }

  private Model makeFloor(GL3 gl) {
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(size, 1f, size), new Mat4(1));
    return makeSurface(gl, modelMatrix, wallShader, wall_mat, floor_tex, null);
  }

  private Model makeWall(GL3 gl, boolean right) {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size, 1f, size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);

    int z_rotate = right ? 90 : -90;
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(z_rotate), modelMatrix);

    float x_translate = size * 0.5f;
    x_translate = right ? x_translate : -x_translate;
    modelMatrix = Mat4.multiply(Mat4Transform.translate(x_translate, size *
        0.5f, 0), modelMatrix);
    return makeSurface(gl, modelMatrix, wallShader, wall_mat, wall_tex, null);

  }

  private Model makeWindow(GL3 gl) {
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size, 1f, size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, size * 0.5f, -size * 0.5f), modelMatrix);

    return makeSurface(gl, modelMatrix, windowShader, window_mat, window_tex, window_spec_tex);
  }

  public void render(GL3 gl) {
    double currentTime = System.currentTimeMillis() / 1000.0;
    double elapsed = currentTime - startTime;
    // System.out.println(elapsed);
    double t = elapsed * 0.01;
    // float offsetX = 0.01f * (float) (Math.sin(Math.toRadians(elapsed * 50)));
    float offsetX = (float) t;
    skyboxShader.use(gl);
    skyboxShader.setFloat(gl, "offset", offsetX, 0);
    for (int i = 0; i < walls.length; i++) {
      walls[i].render(gl);
    }

    // test
    // wall[2].render(gl);
  }

  public void dispose(GL3 gl) {
    for (int i = 0; i < walls.length; i++) {
      walls[i].dispose(gl);
    }
  }
}
