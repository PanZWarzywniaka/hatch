import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import java.util.ArrayList;
import dataclasses.Cube;
import dataclasses.Sphere;
import dataclasses.TwoTriangles;

public class Table {
  private SGNode tableRoot;
  private Camera camera;
  private ArrayList<Light> lights;
  private double startTime;
  private Model cube, egg;
  private Texture wood_tex;
  private Texture egg_tex, egg_spec_tex;

  // constans
  final float LEG_HEIGHT = 3f;

  final float TABLE_TOP_HEIGHT = 0.2f;
  final float TABLE_TOP_LENGTH = 5f;

  final float LEG_LENGTH = 0.5f;

  final float EGG_BASE_LENGTH = 1f;
  final float EGG_BASE_HEIGHT = 0.5f;
  final float EGG_SCALE_X = 2f;
  final float EGG_SCALE_Z = EGG_SCALE_X;
  final float EGG_SCALE_Y = 3f;

  public Table(GL3 gl, Camera c, ArrayList<Light> l) {
    camera = c;
    lights = l;
    startTime = System.currentTimeMillis() / 1000.0;

    loadTextures(gl);
    initialise(gl);
  }

  private void loadTextures(GL3 gl) {
    egg_tex = TextureLibrary.loadTexture(gl, "textures/egg.jpg");
    egg_spec_tex = TextureLibrary.loadTexture(gl, "textures/egg_spec.jpg");
    wood_tex = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
  }

  private void makeCube(GL3 gl) {

    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_default.txt", "shaders/fs_cube_04.txt");
    Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
        new Vec3(0.1f, 0.1f, 0.1f),
        2.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 1, 1), Mat4Transform.translate(0, 0.5f, 0));
    cube = new Model(gl, camera, lights, shader, material, modelMatrix, mesh, wood_tex);

  }

  private void makeEgg(GL3 gl) {
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_default.txt", "shaders/fs_cube_04.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f),
        new Vec3(0.5f, 0.5f, 0.5f),
        32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 1, 1), Mat4Transform.translate(0, 0.5f, 0));
    egg = new Model(gl, camera, lights, shader, material, modelMatrix, mesh, egg_tex, egg_spec_tex);

  }

  private void makeModels(GL3 gl) {
    makeCube(gl);
    makeEgg(gl);

  }

  private void initialise(GL3 gl) {
    makeModels(gl);

    // crate nodes
    tableRoot = new NameNode("root");
    TransformNode bottomUpTransform = new TransformNode("base table top transform",
        Mat4Transform.translate(0, LEG_HEIGHT, 0));

    NameNode tableTop = new NameNode("table top");
    Mat4 m = Mat4Transform.scale(TABLE_TOP_LENGTH, TABLE_TOP_HEIGHT, TABLE_TOP_LENGTH);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
    TransformNode tableTopTransform = new TransformNode("table top transform", m);
    ModelNode tableTopShape = new ModelNode("Tabletop(Cube)", cube);

    NameNode tableSurface = new NameNode("table surface");
    TransformNode tableSurfaceTransform = new TransformNode("table surface transform",
        Mat4Transform.translate(0, TABLE_TOP_HEIGHT, 0));

    m = Mat4Transform.scale(EGG_BASE_LENGTH, EGG_BASE_HEIGHT, EGG_BASE_LENGTH);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
    TransformNode eggBaseTransform = new TransformNode("eggBaseTransform", m);
    ModelNode eggBaseShape = new ModelNode("eggBase(Cube)", cube);

    m = Mat4Transform.scale(EGG_SCALE_X, EGG_SCALE_Y, EGG_SCALE_Z);
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
    TransformNode eggTransform = new TransformNode("eggTransform", m);
    ModelNode eggShape = new ModelNode("eggShape(egg)", egg);

    tableRoot.addChild(bottomUpTransform);
    bottomUpTransform.addChild(tableTop);

    // table top
    tableTop.addChild(tableTopTransform);
    tableTopTransform.addChild(tableTopShape);

    for (int i = 0; i < 4; i++) {
      tableTop = (NameNode) addLeg(i, tableTop);
    }

    tableTop.addChild(tableSurface);
    tableSurface.addChild(tableSurfaceTransform);
    tableSurfaceTransform.addChild(eggBaseTransform);
    eggBaseTransform.addChild(eggBaseShape);

    tableSurfaceTransform.addChild(eggTransform);
    eggTransform.addChild(eggShape);

    tableRoot.update();
    tableRoot.print(0, false);
  }

  private SGNode addLeg(int i, SGNode tableTop) {
    NameNode leg = new NameNode("leg" + i);
    Mat4 m = Mat4Transform.scale(LEG_LENGTH, LEG_HEIGHT, LEG_LENGTH);

    float offset = TABLE_TOP_LENGTH / 2 - LEG_LENGTH / 2;
    float xoffset = offset;
    float zoffset = offset;
    switch (i) {
      case 0:
        xoffset *= -1;
        zoffset *= -1;
        break;
      case 1:
        xoffset *= -1;
        break;
      case 2:
        zoffset *= -1;
        break;
    }
    m = Mat4.multiply(Mat4Transform.translate(xoffset, -LEG_HEIGHT / 2, zoffset), m);
    TransformNode legTransform = new TransformNode("leg" + i + "Transform", m);
    ModelNode legShape = new ModelNode("leg" + i + "Shape(Cube)", cube);

    tableTop.addChild(leg);
    leg.addChild(legTransform);
    legTransform.addChild(legShape);

    return tableTop;

  }

  public void render(GL3 gl) {
    tableRoot.draw(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GL3 gl) {
    cube.dispose(gl);
    egg.dispose(gl);
  }
}
