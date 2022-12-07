import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Hatch extends JFrame implements ActionListener {

  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private HatchGLEventListener glEventListener;
  private final FPSAnimator animator;

  public static void main(String[] args) {
    Hatch b1 = new Hatch("Hatch");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public Hatch(String textForTitleBar) {
    super(textForTitleBar);
    setUpCanvas();
    setUpButtons();
    getContentPane().add(canvas, BorderLayout.CENTER);
    addWindowListener(new windowHandler());
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  private void setUpCanvas() {
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    Camera camera = new Camera(Camera.DEFAULT_POSITION,
        Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new HatchGLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
  }

  private void setUpButtons() {

    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    JMenu fileMenu = new JMenu("File");
    JMenuItem quitItem = new JMenuItem("Quit");
    quitItem.addActionListener(this);
    fileMenu.add(quitItem);
    menuBar.add(fileMenu);

    JPanel panel = new JPanel();

    panel.setLayout(new GridLayout(2, 1));

    JButton b = new JButton("First point light");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Second point light");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("First spot light");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Second spot light");
    b.addActionListener(this);
    panel.add(b);

    b = new JButton("Toggle animation");
    b.addActionListener(this);
    panel.add(b);

    b = new JButton("Quit");
    b.addActionListener(this);
    panel.add(b);

    b = new JButton("Left Lamp Position 1");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Left Lamp Position 2");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Left Lamp Position 3");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Right Lamp Position 1");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Right Lamp Position 2");
    b.addActionListener(this);
    panel.add(b);
    b = new JButton("Right Lamp Position 3");
    b.addActionListener(this);
    panel.add(b);

    this.add(panel, BorderLayout.SOUTH);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("First point light")) {
      glEventListener.toggleLight(1);
    } else if (e.getActionCommand().equalsIgnoreCase("Second point light")) {
      glEventListener.toggleLight(0);
    } else if (e.getActionCommand().equalsIgnoreCase("First spot light")) {
      glEventListener.toggleLight(2);
    } else if (e.getActionCommand().equalsIgnoreCase("Second spot light")) {
      glEventListener.toggleLight(3);
    } else if (e.getActionCommand().equalsIgnoreCase("quit")) {
      System.exit(0);
    } else if (e.getActionCommand().equalsIgnoreCase("Left Lamp Position 1")) {
      glEventListener.leftLampPosition1();
    } else if (e.getActionCommand().equalsIgnoreCase("Left Lamp Position 2")) {
      glEventListener.leftLampPosition2();
    } else if (e.getActionCommand().equalsIgnoreCase("Left Lamp Position 3")) {
      glEventListener.leftLampPosition3();
    } else if (e.getActionCommand().equalsIgnoreCase("Right Lamp Position 1")) {
      glEventListener.rightLampPosition1();
    } else if (e.getActionCommand().equalsIgnoreCase("Right Lamp Position 2")) {
      glEventListener.rightLampPosition2();
    } else if (e.getActionCommand().equalsIgnoreCase("Right Lamp Position 3")) {
      glEventListener.rightLampPosition3();
    }

  }

  private class windowHandler extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      animator.stop();
      remove(canvas);
      dispose();
      System.exit(0);
    }
  }
}

class MyKeyboardInput extends KeyAdapter {
  private Camera camera;

  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }

  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
        m = Camera.Movement.LEFT;
        break;
      case KeyEvent.VK_RIGHT:
        m = Camera.Movement.RIGHT;
        break;
      case KeyEvent.VK_UP:
        m = Camera.Movement.UP;
        break;
      case KeyEvent.VK_DOWN:
        m = Camera.Movement.DOWN;
        break;
      case KeyEvent.VK_A:
        m = Camera.Movement.FORWARD;
        break;
      case KeyEvent.VK_Z:
        m = Camera.Movement.BACK;
        break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;

  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e instance of MouseEvent
   */
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx = (float) (ms.x - lastpoint.x) * sensitivity;
    float dy = (float) (ms.y - lastpoint.y) * sensitivity;
    // System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers() == MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e instance of MouseEvent
   */
  public void mouseMoved(MouseEvent e) {
    lastpoint = e.getPoint();
  }

}