/* I declare that this code is my own work */
/* Author Aleksander Marcin Osikowicz amosikowicz1@sheffield.ac.uk */

import com.jogamp.opengl.*;

public class LightNode extends SGNode {

  protected Light light;

  public LightNode(String name, Light l) {
    super(name);
    light = l;
  }

  public void draw(GL3 gl) {
    light.render(gl, worldTransform);
    for (int i = 0; i < children.size(); i++) {
      children.get(i).draw(gl);
    }
  }

}