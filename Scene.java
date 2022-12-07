import gmaths.*;

import java.util.ArrayList;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

import dataclasses.*;

public class Scene {
        private SGNode root;
        private Camera camera;
        private ArrayList<Light> lights;
        Shader shader;
        private double startTime;
        private Mesh cubeMesh, sphereMesh, twoTriangleMesh;
        private Model plasticCubeModel, woodenSphereModel, woodenCubeModel, alienEggModel, lightBulbModel;
        Room room;

        public Scene(GL3 gl, Camera c) {
                camera = c;
                shader = new Shader(gl, "shaders/vs_default.txt", "shaders/fs_multiple_casters.txt");
                startTime = System.currentTimeMillis() / 1000.0;

                lights = new ArrayList<Light>();

                initialise(gl);
        }

        private void initialise(GL3 gl) {

                makeMeshes(gl);
                makeModels(gl);

                // crate nodes
                root = new NameNode("The Scene root");
                root.addChild(makePointLights(gl));
                root.addChild(makeLamp(gl, false));
                root.addChild(makeLamp(gl, true));

                root.addChild(makeTable(gl));
                root.update();
                root.print(0, false);

                room = new Room(gl, camera, lights);

        }

        // models
        private void makeMeshes(GL3 gl) {
                cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
                sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
                twoTriangleMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        }

        private void makeModels(GL3 gl) {
                plasticCubeModel = makePlasticCube(gl);
                woodenCubeModel = makeWoodenCube(gl);
                woodenSphereModel = makeGiraffeSphere(gl);// makeWoodenSphere(gl);
                lightBulbModel = makeLightBulb(gl);
                alienEggModel = makeAlienEgg(gl);
        }

        private Model makeAlienEgg(GL3 gl) {

                Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f),
                                new Vec3(0.5f, 0.5f, 0.5f),
                                32.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);

                Texture tex = TextureLibrary.loadTexture(gl, "textures/egg.jpg");
                Texture spec_tex = TextureLibrary.loadTexture(gl, "textures/egg_spec.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, sphereMesh, tex, spec_tex);
        }

        private Model makePlasticCube(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture cube_tex = TextureLibrary.loadTexture(gl, "textures/plastic.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, cubeMesh, cube_tex);

        }

        private Model makeWoodenCube(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture tex = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, cubeMesh, tex);
        }

        private Model makeWoodenSphere(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.5f, 0.5f, 0.5f),
                                32.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture tex = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, sphereMesh, tex);

        }

        private Model makeGiraffeSphere(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.5f, 0.5f, 0.5f),
                                32.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture tex = TextureLibrary.loadTexture(gl, "textures/giraffe.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, sphereMesh, tex);

        }

        private Model makeLightBulb(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 1, 1), Mat4Transform.translate(0, 0.5f, 0));
                Texture tex = TextureLibrary.loadTexture(gl, "textures/light_bulb.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, cubeMesh, tex);

        }

        // Scene graphs
        private SGNode makePointLights(GL3 gl) {

                Vec3 LIGHT_POINT_SCALE = new Vec3(0.4f);

                NameNode rootPointLights = new NameNode("Point lights");

                TransformNode toCelling = new TransformNode("to celling transform", Mat4Transform.translate(0, 10, 5));

                float x_offset = 5f;
                NameNode leftLightBranch = new NameNode("left light");

                Mat4 scale = Mat4Transform.scale(LIGHT_POINT_SCALE);

                TransformNode leftLightTransform = new TransformNode("left light transform",
                                Mat4.multiply(Mat4Transform.translate(x_offset, 0, 0), scale));

                PointLight leftPointLight = new PointLight(gl, camera);
                lights.add(leftPointLight);
                LightNode leftLight = new LightNode("left light", leftPointLight);

                NameNode rightLightBranch = new NameNode("right light");
                TransformNode rightLightTransform = new TransformNode("right light transform",
                                Mat4.multiply(Mat4Transform.translate(-x_offset, 0, 0), scale));

                PointLight rightPointLight = new PointLight(gl, camera);
                lights.add(rightPointLight);
                LightNode rightLight = new LightNode("right light", rightPointLight);

                rootPointLights.addChild(toCelling);

                toCelling.addChild(leftLightBranch);
                leftLightBranch.addChild(leftLightTransform);
                leftLightTransform.addChild(leftLight);

                toCelling.addChild(rightLightBranch);
                rightLightBranch.addChild(rightLightTransform);
                rightLightTransform.addChild(rightLight);
                return rootPointLights;
        }

        private SGNode makeLamp(GL3 gl, boolean right) {

                // constants
                // base
                final Vec3 BASE_POS = new Vec3(5f, 0f, 0f);
                final Vec3 BASE_SCALE = new Vec3(3f, 0.5f, 3f);

                // rotate angle //TODO change name
                float rotateAllY = 30f;

                // lower arm
                final Vec3 LOWER_ARM_SCALE = new Vec3(1f, 5f, 1f);

                float rotateLowerAngle = -20f;

                // arm connector
                final Vec3 JOINT_SCALE = new Vec3(1f, 1f, 1f);

                // upper arm
                final Vec3 UPPER_ARM_SCALE = new Vec3(1f, 5f, 1f);
                float rotateUpperAngle = 40f;

                // head
                final Vec3 HEAD_SCALE = new Vec3(2f, 1f, 1f);
                float headAngle = 20f;

                // light bulb
                final Vec3 LIGHT_BULB_SCALE = new Vec3(0.25f, HEAD_SCALE.y * 0.8f, HEAD_SCALE.z * 0.8f);

                String name = right ? "Right" : "Left";
                SGNode root = new NameNode(name + " lamp root");

                float X_POS = right ? BASE_POS.x : -BASE_POS.x;
                TransformNode lampPosTransform = new TransformNode("transform to lamp",
                                Mat4Transform.translate(X_POS, BASE_POS.y, BASE_POS.z));

                NameNode lampBase = new NameNode("lampBase");
                Mat4 m = Mat4Transform.scale(BASE_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lampBaseTransform = new TransformNode("lampBaseTransform", m);
                ModelNode lampBaseShape = new ModelNode("lampBase(Cube)", plasticCubeModel);

                // rotate all
                float rotateAllY_angle = right ? rotateAllY : 180 - rotateAllY;
                TransformNode rotateLamp = new TransformNode("rotateLamp",
                                Mat4Transform.rotateAroundY(rotateAllY_angle));

                NameNode lowerArm = new NameNode("lower arm");
                TransformNode lowerArmPosTransform = new TransformNode("lowerArmPosTransform",
                                Mat4Transform.translate(0, 0, 0)); // lower arm is anchored in base not on top of it

                TransformNode rotateLower = new TransformNode("rotateAroundZ(" + rotateLowerAngle + ")",
                                Mat4Transform.rotateAroundZ(rotateLowerAngle));
                m = Mat4Transform.scale(LOWER_ARM_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lowerArmTransform = new TransformNode("lowerArmTransform", m);
                ModelNode lowerArmShape = new ModelNode("lowerArmShape(Sphere)", woodenSphereModel);

                TransformNode translateTopLowerArm = new TransformNode("translateTopLowerArm",
                                Mat4Transform.translate(0, LOWER_ARM_SCALE.y, 0));

                NameNode armConnector = new NameNode("armConnector");
                m = Mat4Transform.scale(JOINT_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode armConnectorTransform = new TransformNode(
                                "armConnectorTransform: (" + JOINT_SCALE + ")", m);
                ModelNode armConnectorShape = new ModelNode("armConnectorShape(Sphere)", woodenSphereModel);

                NameNode upperArm = new NameNode("Upper Arm");
                TransformNode rotateUpper = new TransformNode("rotateAroundZ(" + rotateUpperAngle + ")",
                                Mat4Transform.rotateAroundZ(rotateUpperAngle));

                m = Mat4Transform.scale(UPPER_ARM_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode upperArmTransform = new TransformNode("upperArmTransform", m);
                ModelNode upperArmShape = new ModelNode("upperArmShape(Sphere)", woodenSphereModel);

                TransformNode translateTopUpperArm = new TransformNode("translateTopUpperArm",
                                Mat4Transform.translate(0, UPPER_ARM_SCALE.y, 0));

                NameNode head = new NameNode("head");

                TransformNode rotateHead = new TransformNode("rotateAroundZ(" + headAngle + ")",
                                Mat4Transform.rotateAroundZ(headAngle));

                m = Mat4Transform.scale(HEAD_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode headTransform = new TransformNode("headTransform", m);
                ModelNode headShape = new ModelNode("headShape(cube)", plasticCubeModel);

                TransformNode translateToLightBulb = new TransformNode("translateToLightBulb",
                                Mat4Transform.translate(-HEAD_SCALE.x / 2, 0, 0));

                NameNode lightBulbBranch = new NameNode("light bulb");
                m = Mat4Transform.scale(LIGHT_BULB_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode lightBulbTransform = new TransformNode("lightBulbTransform", m);

                SpotLight lightBulb = new SpotLight(gl, camera);
                Vec3 spotLightDirection = new Vec3(0, -1, 0); // TODO: it should get direction from spot light
                spotLightDirection.x = right ? -1 : 1;
                lightBulb.setDirection(spotLightDirection);
                lights.add(lightBulb);
                LightNode lighBulbNode = new LightNode(name + " light bulb", lightBulb);

                root.addChild(lampPosTransform);
                lampPosTransform.addChild(lampBase);

                // lamp base
                lampBase.addChild(lampBaseTransform);
                lampBaseTransform.addChild(lampBaseShape);

                lampBase.addChild(rotateLamp);
                rotateLamp.addChild(lowerArm);
                lowerArm.addChild(lowerArmPosTransform);
                lowerArmPosTransform.addChild(rotateLower);
                rotateLower.addChild(lowerArmTransform);
                lowerArmTransform.addChild(lowerArmShape);

                rotateLower.addChild(translateTopLowerArm);
                translateTopLowerArm.addChild(armConnector);
                armConnector.addChild(armConnectorTransform);
                armConnectorTransform.addChild(armConnectorShape);

                armConnector.addChild(upperArm);
                upperArm.addChild(rotateUpper);
                rotateUpper.addChild(upperArmTransform);
                upperArmTransform.addChild(upperArmShape);

                rotateUpper.addChild(translateTopUpperArm);
                translateTopUpperArm.addChild(head);
                head.addChild(rotateHead);
                rotateHead.addChild(headTransform);
                headTransform.addChild(headShape);

                rotateHead.addChild(translateToLightBulb);
                translateToLightBulb.addChild(lightBulbBranch);

                lightBulbBranch.addChild(lightBulbTransform);
                lightBulbTransform.addChild(lighBulbNode);

                return root;
        }

        private SGNode makeTable(GL3 gl) {
                // constans
                final Vec3 LEG_SCALE = new Vec3(0.5f, 3, 0.5f);

                final Vec3 TABLE_TOP_SCALE = new Vec3(5, 0.2f, 5);

                final Vec3 EGG_BASE_SCALE = new Vec3(1, 0.5f, 1);
                final Vec3 ALIEN_EGG_SCALE = new Vec3(2, 3, 2);

                NameNode tableRoot = new NameNode("table root");
                TransformNode bottomUpTransform = new TransformNode("base table top transform",
                                Mat4Transform.translate(0, LEG_SCALE.y, 0));

                NameNode tableTop = new NameNode("table top");
                Mat4 m = Mat4Transform.scale(TABLE_TOP_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode tableTopTransform = new TransformNode("table top transform", m);
                ModelNode tableTopShape = new ModelNode("Tabletop(Cube)", woodenCubeModel);

                NameNode tableSurface = new NameNode("table surface");
                TransformNode tableSurfaceTransform = new TransformNode("table surface transform",
                                Mat4Transform.translate(0, TABLE_TOP_SCALE.y, 0));

                m = Mat4Transform.scale(EGG_BASE_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode eggBaseTransform = new TransformNode("eggBaseTransform", m);
                ModelNode eggBaseShape = new ModelNode("eggBase(Cube)", woodenCubeModel);

                m = Mat4Transform.scale(ALIEN_EGG_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode eggTransform = new TransformNode("eggTransform", m);
                ModelNode eggShape = new ModelNode("eggShape(egg)", alienEggModel);

                tableRoot.addChild(bottomUpTransform);
                bottomUpTransform.addChild(tableTop);

                // table top
                tableTop.addChild(tableTopTransform);
                tableTopTransform.addChild(tableTopShape);

                for (int i = 0; i < 4; i++) {
                        tableTop = (NameNode) addLeg(i, tableTop, LEG_SCALE, TABLE_TOP_SCALE);
                }

                tableTop.addChild(tableSurface);
                tableSurface.addChild(tableSurfaceTransform);
                tableSurfaceTransform.addChild(eggBaseTransform);
                eggBaseTransform.addChild(eggBaseShape);

                tableSurfaceTransform.addChild(eggTransform);
                eggTransform.addChild(eggShape);

                return tableRoot;
        }

        private SGNode addLeg(int i, SGNode tableTop, Vec3 LEG_SCALE, Vec3 TABLE_TOP_SCALE) {
                NameNode leg = new NameNode("leg" + i);
                Mat4 m = Mat4Transform.scale(LEG_SCALE);

                float offset = TABLE_TOP_SCALE.x / 2 - LEG_SCALE.x / 2;
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
                m = Mat4.multiply(Mat4Transform.translate(xoffset, -LEG_SCALE.y / 2, zoffset), m);
                TransformNode legTransform = new TransformNode("leg" + i + "Transform", m);
                ModelNode legShape = new ModelNode("leg" + i + "Shape(Cube)", woodenCubeModel);

                tableTop.addChild(leg);
                leg.addChild(legTransform);
                legTransform.addChild(legShape);

                return tableTop;
        }

        private SGNode makeRoom(GL3 gl) {
                NameNode roomRoot = new NameNode("room branch");

                return roomRoot;
        }

        public double getSeconds() {
                return System.currentTimeMillis() / 1000.0;
        }

        public void render(GL3 gl) {
                root.update();
                root.draw(gl);
                room.render(gl);
        }

        /* Clean up memory, if necessary */
        public void dispose(GL3 gl) {
                plasticCubeModel.dispose(gl);
                woodenSphereModel.dispose(gl);
                woodenCubeModel.dispose(gl);
                lightBulbModel.dispose(gl);
                alienEggModel.dispose(gl);
        }
}
