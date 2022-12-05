import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

import dataclasses.Cube;
import dataclasses.Sphere;

public class Lamp {
        private SGNode leftLampRoot, rightLampRoot;
        private Camera camera;
        private Light light;
        private double startTime;
        private Model cube, sphere, lightbulb_cube;
        private Texture lamp_tex, cube_tex;
        private Texture lightBulb_tex;

        // constants

        // base
        final float BASE_POS_X = 5f;
        final float BASE_POS_Y = 0f;
        final float BASE_POS_Z = 0f;
        final float BASE_SIZE = 3f;
        final float BASE_HEIGHT = 0.5f;

        // rotate angle
        float rotateAllY = 30f;
        // lower arm
        final float LOWER_ARM_SCALE_X = 1f, LOWER_ARM_SCALE_Y = 5f, LOWER_ARM_SCALE_Z = 1f;
        float rotateLowerAngle = -20f;

        // arm connector
        final float ARM_CONNECTOR_SCALE_X = 1f, ARM_CONNECTOR_SCALE_Y = 1f, ARM_CONNECTOR_SCALE_Z = 1f;

        // upper arm
        final float UPPER_ARM_SCALE_X = 1f, UPPER_ARM_SCALE_Y = 5f, UPPER_ARM_SCALE_Z = 1f;
        float rotateUpperAngle = 40f;
        // head
        final float HEAD_SIZE_X = 1f, HEAD_SIZE_Y = 2f, HEAD_SIZE_Z = 2f;
        float headAngle = 20f;

        // light bulb
        final float LIGHT_BULB_SIZE_X = 0.25f, LIGHT_BULB_SIZE_Y = HEAD_SIZE_Y * 0.8f,
                        LIGHT_BULB_SIZE_Z = HEAD_SIZE_Z * 0.8f;

        public Lamp(GL3 gl, Camera c, Light l) {
                camera = c;
                light = l;
                startTime = System.currentTimeMillis() / 1000.0;

                loadTextures(gl);
                initialise(gl);
        }

        private void loadTextures(GL3 gl) {
                cube_tex = TextureLibrary.loadTexture(gl, "textures/lamp.jpg");
                lamp_tex = TextureLibrary.loadTexture(gl, "textures/wood.jpg");
                lightBulb_tex = TextureLibrary.loadTexture(gl, "textures/light_bulb.jpg");

        }

        private void makeCube(GL3 gl) {

                Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
                Shader shader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 1, 1), Mat4Transform.translate(0, 0.5f, 0));
                cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, cube_tex);

        }

        private void makeSphere(GL3 gl) {
                Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
                Shader shader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.5f, 0.5f, 0.5f),
                                32.0f);
                Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 1, 1), Mat4Transform.translate(0, 0.5f, 0));
                sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, lamp_tex);

        }

        private void makeLightBulb(GL3 gl) {
                Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
                Shader shader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
                Material material = new Material(new Vec3(0.2f, 0.2f, 0.2f), new Vec3(0.2f, 0.2f, 0.2f),
                                new Vec3(0.1f, 0.1f, 0.1f),
                                2.0f);
                Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 1, 1), Mat4Transform.translate(0, 0.5f, 0));
                lightbulb_cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, lightBulb_tex);

        }

        private void makeModels(GL3 gl) {
                makeCube(gl);
                makeSphere(gl);
                makeLightBulb(gl);

        }

        private SGNode makeLamp(GL3 gl, boolean right) {
                String name = right ? "Right" : "Left";
                SGNode root = new NameNode(name + " lamp root");

                float X_POS = right ? BASE_POS_X : -BASE_POS_X;
                TransformNode lampPosTransform = new TransformNode("lampPosTransform",
                                Mat4Transform.translate(X_POS, BASE_POS_Y, BASE_POS_Z));

                NameNode lampBase = new NameNode("lampBase");
                Mat4 m = Mat4Transform.scale(BASE_SIZE, BASE_HEIGHT, BASE_SIZE);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lampBaseTransform = new TransformNode("lampBaseTransform", m);
                ModelNode lampBaseShape = new ModelNode("lampBaseTransform(Cube)", cube);

                // rotate all
                float rotateAllY_angle = right ? rotateAllY : 180 - rotateAllY;
                TransformNode rotateLamp = new TransformNode("rotateLamp",
                                Mat4Transform.rotateAroundY(rotateAllY_angle));

                NameNode lowerArm = new NameNode("lower arm");
                TransformNode lowerArmPosTransform = new TransformNode("lowerArmPosTransform",
                                Mat4Transform.translate(0, 0, 0));

                TransformNode rotateLower = new TransformNode("rotateAroundZ(" + rotateLowerAngle + ")",
                                Mat4Transform.rotateAroundZ(rotateLowerAngle));
                m = Mat4Transform.scale(LOWER_ARM_SCALE_X, LOWER_ARM_SCALE_Y, LOWER_ARM_SCALE_Z);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode lowerArmTransform = new TransformNode("lowerArmTransform", m);
                ModelNode lowerArmShape = new ModelNode("lowerArmShape(Sphere)", sphere);

                TransformNode translateTopLowerArm = new TransformNode("translateTopLowerArm",
                                Mat4Transform.translate(0, LOWER_ARM_SCALE_Y, 0));

                NameNode armConnector = new NameNode("Arm connector");
                m = Mat4Transform.scale(ARM_CONNECTOR_SCALE_X, ARM_CONNECTOR_SCALE_Y, ARM_CONNECTOR_SCALE_Z);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode armConnectorTransform = new TransformNode(
                                "armConnectorTransform: (" + ARM_CONNECTOR_SCALE_X + ","
                                                + ARM_CONNECTOR_SCALE_Y + "," + ARM_CONNECTOR_SCALE_Z + ")",
                                m);
                ModelNode armConnectorShape = new ModelNode("armConnectorShape(Sphere)", sphere);

                NameNode upperArm = new NameNode("Upper Arm");
                TransformNode rotateUpper = new TransformNode("rotateAroundZ(" + rotateUpperAngle + ")",
                                Mat4Transform.rotateAroundZ(rotateUpperAngle));

                m = Mat4Transform.scale(UPPER_ARM_SCALE_X, UPPER_ARM_SCALE_Y, UPPER_ARM_SCALE_Z);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
                TransformNode upperArmTransform = new TransformNode("upperArmTransform", m);
                ModelNode upperArmShape = new ModelNode("upperArmShape(Sphere)", sphere);

                TransformNode translateTopUpperArm = new TransformNode("translateTopUpperArm",
                                Mat4Transform.translate(0, UPPER_ARM_SCALE_Y, 0));

                NameNode head = new NameNode("head");

                TransformNode rotateHead = new TransformNode("rotateAroundZ(" + headAngle + ")",
                                Mat4Transform.rotateAroundZ(headAngle));

                m = Mat4Transform.scale(HEAD_SIZE_X, HEAD_SIZE_Y, HEAD_SIZE_Z);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode headTransform = new TransformNode("headTransform", m);
                ModelNode headShape = new ModelNode("headShape(cube)", cube);

                TransformNode translateToLightBulb = new TransformNode("translateToLightBulb",
                                Mat4Transform.translate(-HEAD_SIZE_X / 2, 0, 0));

                NameNode lightBulb = new NameNode("light bulb");
                m = Mat4Transform.scale(LIGHT_BULB_SIZE_X, LIGHT_BULB_SIZE_Y, LIGHT_BULB_SIZE_Z);
                m = Mat4.multiply(m, Mat4Transform.translate(0, 0f, 0));
                TransformNode lightBulbTransform = new TransformNode("lightBulbTransform", m);
                ModelNode lightBulbShape = new ModelNode("lightBulbShape(lightBulb)", lightbulb_cube);

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
                translateToLightBulb.addChild(lightBulb);

                lightBulb.addChild(lightBulbTransform);
                lightBulbTransform.addChild(lightBulbShape);

                root.update();
                root.print(0, false);

                return root;
        }

        private void initialise(GL3 gl) {
                makeModels(gl);

                // crate nodes
                leftLampRoot = makeLamp(gl, false);
                rightLampRoot = makeLamp(gl, true);

        }

        public void render(GL3 gl) {
                leftLampRoot.update();
                rightLampRoot.update();

                leftLampRoot.draw(gl);
                rightLampRoot.draw(gl);
        }

        /* Clean up memory, if necessary */
        public void dispose(GL3 gl) {
                cube.dispose(gl);
                sphere.dispose(gl);
        }
}
