
/* I declare that this code is my own work */
/* Author Aleksander Marcin Osikowicz amosikowicz1@sheffield.ac.uk */
import gmaths.*;

import java.util.ArrayList;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import java.util.concurrent.ThreadLocalRandom;

import dataclasses.*;

public class Scene {
        private SGNode root;
        private Camera camera;
        private ArrayList<Light> lights;
        Shader shader;
        private double startTime;
        private Mesh cubeMesh, sphereMesh, twoTriangleMesh;
        private Model metalCubeModel, giraffeSphereModel, zebraSphereModel, giraffeCubeModel, zebraCubeModel,
                        woodenSphereModel, woodenCubeModel, alienEggModel, lightBulbModel;
        Room room;
        boolean animate;

        private TransformNode leftLampYawRotate, leftLampLowerArmRotate, leftLampUpperArmRotate, leftLampHeadRotate;
        // private float leftLampYaw, leftLampLowerArmPitch, leftLampUpperArmPitch,
        // leftLampHeadPitch;

        private TransformNode rightLampYawRotate, rightLampLowerArmRotate, rightLampUpperArmRotate, rightLampHeadRotate;
        private TransformNode eggTransform;
        // private float rightLampYaw, rightLampLowerArmPitch, rightLampUpperArmPitch,
        // rightLampHeadPitch;

        public Scene(GL3 gl, Camera c) {
                camera = c;
                shader = new Shader(gl, "shaders/vs_default.txt", "shaders/fs_multiple_casters.txt");
                startTime = System.currentTimeMillis() / 1000.0;

                lights = new ArrayList<Light>();
                animate = true;
                leftLampYawRotate = new TransformNode("leftLampYawRotate");
                leftLampLowerArmRotate = new TransformNode("leftLampLowerArmRotate");
                leftLampUpperArmRotate = new TransformNode("leftLampUpperArmRotate");
                leftLampHeadRotate = new TransformNode("leftLampHeadRotate");

                rightLampYawRotate = new TransformNode("rightLampYawRotate");
                rightLampLowerArmRotate = new TransformNode("rightLampLowerArmRotate");
                rightLampUpperArmRotate = new TransformNode("rightLampUpperArmRotate");
                rightLampHeadRotate = new TransformNode("rightLampHeadRotate");
                eggTransform = new TransformNode("eggTransform");

                initialise(gl);
        }

        private void initialise(GL3 gl) {

                makeMeshes(gl);
                makeModels(gl);

                leftLampPosition1();
                rightLampPosition1();
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
                metalCubeModel = makeMetalCube(gl);
                woodenCubeModel = makeWoodenCube(gl);
                woodenSphereModel = makeWoodenSphere(gl);
                giraffeSphereModel = makeGiraffeSphere(gl);
                zebraSphereModel = makeZebraSphere(gl);
                lightBulbModel = makeLightBulb(gl);
                alienEggModel = makeAlienEgg(gl);
                zebraCubeModel = makeZebraCube(gl);
                giraffeCubeModel = makeGiraffeCube(gl);
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

        private Model makeMetalCube(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                64f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture cube_tex = TextureLibrary.loadTexture(gl, "textures/metal.jpeg");
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

        private Model makeZebraCube(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture tex = TextureLibrary.loadTexture(gl, "textures/zebra.jpg");
                return new Model(gl, camera, lights, shader, material, modelMatrix, cubeMesh, tex);
        }

        private Model makeGiraffeCube(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture tex = TextureLibrary.loadTexture(gl, "textures/giraffe.jpg");
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

        private Model makeZebraSphere(GL3 gl) {
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.5f, 0.5f, 0.5f),
                                32.0f);
                Mat4 modelMatrix = Mat4Transform.translate(0, 0.5f, 0);
                Texture tex = TextureLibrary.loadTexture(gl, "textures/zebra.jpg");
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

        public void leftLampPosition1() {
                float leftLampYaw = 150f;
                leftLampYawRotate.setTransform(Mat4Transform.rotateAroundY(leftLampYaw));

                float leftLampLowerArmPitch = -20f;
                leftLampLowerArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampLowerArmPitch));

                float leftLampUpperArmPitch = 40f;
                leftLampUpperArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampUpperArmPitch));

                float leftLampHeadPitch = 20f;
                leftLampHeadRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampHeadPitch));
                System.out.println("Left lamp in position 1");
        }

        public void rightLampPosition1() {
                float rightLampYaw = 30f;
                rightLampYawRotate.setTransform(Mat4Transform.rotateAroundY(rightLampYaw));

                float rightLampLowerArmPitch = -20f;
                rightLampLowerArmRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampLowerArmPitch));

                float rightLampUpperArmPitch = 40f;
                rightLampUpperArmRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampUpperArmPitch));

                float rightLampHeadPitch = 20f;
                rightLampHeadRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampHeadPitch));
                System.out.println("Right lamp in position 1");
        }

        public void leftLampPosition2() {
                float leftLampYaw = -110f;
                leftLampYawRotate.setTransform(Mat4Transform.rotateAroundY(leftLampYaw));

                float leftLampLowerArmPitch = -60f;
                leftLampLowerArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampLowerArmPitch));

                float leftLampUpperArmPitch = 120f;
                leftLampUpperArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampUpperArmPitch));

                float leftLampHeadPitch = 0f;
                leftLampHeadRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampHeadPitch));
                System.out.println("Left lamp in position 2");
        }

        public void rightLampPosition2() {
                float rightLampYaw = 90f;
                rightLampYawRotate.setTransform(Mat4Transform.rotateAroundY(rightLampYaw));

                float rightLampLowerArmPitch = -70f;
                rightLampLowerArmRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampLowerArmPitch));

                float rightLampUpperArmPitch = 140f;
                rightLampUpperArmRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampUpperArmPitch));

                float rightLampHeadPitch = 0f;
                rightLampHeadRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampHeadPitch));
                System.out.println("Right lamp in position 2");
        }

        public void leftLampPosition3() {
                float leftLampYaw = 20f;
                leftLampYawRotate.setTransform(Mat4Transform.rotateAroundY(leftLampYaw));

                float leftLampLowerArmPitch = 32f;
                leftLampLowerArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampLowerArmPitch));

                float leftLampUpperArmPitch = -60f;
                leftLampUpperArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampUpperArmPitch));

                float leftLampHeadPitch = -50f;
                leftLampHeadRotate.setTransform(Mat4Transform.rotateAroundZ(leftLampHeadPitch));
                System.out.println("Left lamp in position 3");
        }

        public void rightLampPosition3() {
                float rightLampYaw = 20f;
                rightLampYawRotate.setTransform(Mat4Transform.rotateAroundY(rightLampYaw));

                float rightLampLowerArmPitch = -32f;
                rightLampLowerArmRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampLowerArmPitch));

                float rightLampUpperArmPitch = 60f;
                rightLampUpperArmRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampUpperArmPitch));

                float rightLampHeadPitch = -50f;
                rightLampHeadRotate.setTransform(Mat4Transform.rotateAroundZ(rightLampHeadPitch));
                System.out.println("Right lamp in position 3");
        }

        // Scene graphs
        private SGNode makePointLights(GL3 gl) {

                Vec3 LIGHT_POINT_SCALE = new Vec3(0.4f);

                NameNode rootPointLights = new NameNode("Point lights");

                TransformNode toCelling = new TransformNode("to celling transform", Mat4Transform.translate(0, 10, 10));

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
                final Vec3 BASE_SCALE = new Vec3(3.5f, 0.5f, 2f);

                final Model ARM_MODEL = right ? giraffeSphereModel : zebraSphereModel;
                final Model HEAD_MODEL = right ? giraffeCubeModel : zebraCubeModel;

                // lower arm
                Vec3 LOWER_ARM_SCALE = new Vec3(1f, 5f, 1f);

                if (right)
                        LOWER_ARM_SCALE.y *= 0.8f;
                // arm connector
                final Vec3 JOINT_SCALE = new Vec3(1f, 1f, 1f);

                // upper arm
                Vec3 UPPER_ARM_SCALE = new Vec3(1f, 5f, 1f);
                if (right)
                        UPPER_ARM_SCALE.y *= 1.5f;

                // head
                final Vec3 HEAD_SCALE = new Vec3(1f, 1f, 1f);

                // light bulb
                final Vec3 LIGHT_BULB_SCALE = new Vec3(0.15f, 0.8f, 0.8f);

                String name = right ? "Right" : "Left";
                SGNode root = new NameNode(name + " lamp root");

                float X_POS = right ? BASE_POS.x : -BASE_POS.x;
                TransformNode lampPosTransform = new TransformNode("transform to lamp",
                                Mat4Transform.translate(X_POS, BASE_POS.y, BASE_POS.z));

                NameNode lampBase = new NameNode("lampBase");
                Mat4 m = Mat4Transform.scale(BASE_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lampBaseTransform = new TransformNode("lampBaseTransform", m);
                ModelNode lampBaseShape = new ModelNode("lampBase(Cube)", metalCubeModel);

                // rotate all
                // float rotateAllY_angle = right ? rotateAllY : 180 - rotateAllY;
                // TransformNode rotateLamp = new TransformNode("rotateLamp",
                // Mat4Transform.rotateAroundY(rotateAllY_angle));
                TransformNode rotateLamp = right ? rightLampYawRotate : leftLampYawRotate;

                NameNode lowerArm = new NameNode("lower arm");
                TransformNode lowerArmPosTransform = new TransformNode("lowerArmPosTransform",
                                Mat4Transform.translate(0, 0, 0)); // lower arm is anchored in base not on top of it

                // TransformNode rotateLower = new TransformNode("rotateAroundZ(" +
                // rotateLowerAngle + ")",
                // Mat4Transform.rotateAroundZ(rotateLowerAngle));

                TransformNode rotateLower = right ? rightLampLowerArmRotate : leftLampLowerArmRotate;

                m = Mat4Transform.scale(LOWER_ARM_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lowerArmTransform = new TransformNode("lowerArmTransform", m);
                ModelNode lowerArmShape = new ModelNode("lowerArmShape(Sphere)", ARM_MODEL);

                TransformNode translateTopLowerArm = new TransformNode("translateTopLowerArm",
                                Mat4Transform.translate(0, LOWER_ARM_SCALE.y, 0));

                NameNode armConnector = new NameNode("armConnector");
                m = Mat4Transform.scale(JOINT_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode armConnectorTransform = new TransformNode(
                                "armConnectorTransform: (" + JOINT_SCALE + ")", m);
                ModelNode armConnectorShape = new ModelNode("armConnectorShape(Sphere)", ARM_MODEL);

                NameNode upperArm = new NameNode("Upper Arm");

                TransformNode rotateUpper = right ? rightLampUpperArmRotate : leftLampUpperArmRotate;
                // TransformNode rotateUpper = new TransformNode("rotateAroundZ(" +
                // rotateUpperAngle + ")",
                // Mat4Transform.rotateAroundZ(rotateUpperAngle));

                m = Mat4Transform.scale(UPPER_ARM_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode upperArmTransform = new TransformNode("upperArmTransform", m);
                ModelNode upperArmShape = new ModelNode("upperArmShape(Sphere)", ARM_MODEL);

                TransformNode translateTopUpperArm = new TransformNode("translateTopUpperArm",
                                Mat4Transform.translate(0, UPPER_ARM_SCALE.y, 0));

                NameNode head = new NameNode("head");

                TransformNode rotateHead = right ? rightLampHeadRotate : leftLampHeadRotate;
                // TransformNode rotateHead = new TransformNode("rotateAroundZ(" + headAngle +
                // ")",
                // Mat4Transform.rotateAroundZ(headAngle));

                m = Mat4Transform.scale(HEAD_SCALE);
                TransformNode headTransform = new TransformNode("headTransform", m);
                ModelNode headShape = new ModelNode("headShape(cube)", HEAD_MODEL);

                TransformNode translateToLightBulb = new TransformNode("translateToLightBulb",
                                Mat4Transform.translate(-HEAD_SCALE.x / 2, 0, 0));

                NameNode lightBulbBranch = new NameNode("light bulb");
                m = Mat4Transform.scale(LIGHT_BULB_SCALE);
                TransformNode lightBulbTransform = new TransformNode("lightBulbTransform", m);

                SpotLight lightBulb = new SpotLight(gl, camera);

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

                if (right) { // doing ears
                        TransformNode translateToLeftEar = new TransformNode("translateToLightBulb",
                                        Mat4Transform.translate(0, 0, -HEAD_SCALE.z / 2));

                        NameNode leftEarBranch = new NameNode("leftEarBranch");
                        final Vec3 EAR_SCALE = new Vec3(1, 1, 1);
                        m = Mat4Transform.scale(EAR_SCALE);

                        TransformNode leftEarTransform = new TransformNode("leftEarTransform", m);
                        ModelNode leftEarShape = new ModelNode("leftEarShape(Sphere)", ARM_MODEL);

                        TransformNode translateToRightEar = new TransformNode("translateToRightEar",
                                        Mat4Transform.translate(0, 0, HEAD_SCALE.z / 2));

                        NameNode rightEarBranch = new NameNode("rightEarBranch");
                        m = Mat4Transform.scale(EAR_SCALE);

                        TransformNode rightEarTransform = new TransformNode("rightEarTransform", m);
                        ModelNode rightEarShape = new ModelNode("rightEarShape(Sphere)", ARM_MODEL);

                        rotateHead.addChild(translateToLeftEar);
                        translateToLeftEar.addChild(leftEarBranch);
                        leftEarBranch.addChild(leftEarTransform);
                        leftEarTransform.addChild(leftEarShape);

                        rotateHead.addChild(translateToRightEar);
                        translateToRightEar.addChild(rightEarBranch);
                        rightEarBranch.addChild(rightEarTransform);
                        rightEarTransform.addChild(rightEarShape);

                } else { // zebra back

                        NameNode backBranch = new NameNode("backBranch");

                        Vec3 BACK_SCALE = UPPER_ARM_SCALE;
                        BACK_SCALE.multiply(0.8f);

                        m = Mat4Transform.scale(BACK_SCALE);
                        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

                        m = Mat4.multiply(Mat4Transform.translate(0.4f, UPPER_ARM_SCALE.y / 4.0f, 0), m);
                        TransformNode backTransformNode = new TransformNode("backTransformNode", m);
                        ModelNode backShape = new ModelNode("back shape", ARM_MODEL);

                        rotateUpper.addChild(backBranch);
                        backBranch.addChild(backTransformNode);
                        backTransformNode.addChild(backShape);

                }

                return root;
        }

        private SGNode makeTable(GL3 gl) {
                // constans
                final Vec3 LEG_SCALE = new Vec3(0.5f, 3, 0.5f);

                final Vec3 TABLE_TOP_SCALE = new Vec3(5, 0.2f, 5);

                final Vec3 EGG_BASE_SCALE = new Vec3(1, 0.5f, 1);

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

        public double getSeconds() {
                return System.currentTimeMillis() / 1000.0;
        }

        public void updateEggTransform() {
                final Vec3 ALIEN_EGG_SCALE = new Vec3(2, 3, 2);
                Mat4 m = Mat4Transform.scale(ALIEN_EGG_SCALE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                if (animate) {
                        double elapsedTime = startTime - getSeconds();
                        double y_move = Math.sin(elapsedTime * 2);
                        if (y_move < 0)
                                y_move = 0;
                        m = Mat4.multiply(Mat4Transform.translate(0, (float) y_move / 2, 0), m);

                        double rotate_factor = 1.0 / (1.0 + Math.pow(Math.E, -10 * (y_move - 0.5)));
                        // System.out.println(rotate_factor);
                        m = Mat4.multiply(Mat4Transform.rotateAroundY((float) rotate_factor * 360.0f + 180f), m);

                        eggTransform.setTransform(m);
                } else {
                        eggTransform.setTransform(m);
                }

        }

        public void render(GL3 gl) {
                updateEggTransform();
                root.update();
                root.draw(gl);
                room.render(gl);
        }

        /* Clean up memory, if necessary */
        public void dispose(GL3 gl) {
                metalCubeModel.dispose(gl);
                woodenSphereModel.dispose(gl);
                woodenCubeModel.dispose(gl);
                lightBulbModel.dispose(gl);
                alienEggModel.dispose(gl);
                giraffeSphereModel.dispose(gl);
                zebraSphereModel.dispose(gl);
                giraffeCubeModel.dispose(gl);
                zebraCubeModel.dispose(gl);
        }

        public void toggleAnimation() {
                this.animate = !this.animate;
                System.out.println("Animation is now set: " + this.animate);
        }

        public void toggleLight(int i) {
                lights.get(i).switchLight();
        }
}
