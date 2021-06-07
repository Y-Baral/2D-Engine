package com.game.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

//imput class is the infert=face between the game and the player
//class implements four interfaces for input listening and registering
public class input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private engine_ignition ei;

	// keyboard input setup

	private final int Total_Keys = 256;

	// keeping track of all the keys in the keyboard
	private boolean[] keys = new boolean[Total_Keys];

	// Differentiating the key pressed in the last frame so it wont influence the
	// input of the current frame
	private boolean[] keylast = new boolean[Total_Keys];

	// mouse input setup
	private final int Total_Buttons = 5;
	private boolean[] buttons = new boolean[Total_Buttons];
	private boolean[] button_last = new boolean[Total_Buttons];

	// mouse location setup
	private int mouseX, mouseY;

	// mouse scroll wheel setup
	private int scroll;

	// constructor
	public input(engine_ignition ei) {
		this.ei = ei;
		mouseX = 0;
		mouseY = 0;
		scroll = 0;

		// gives window access to the input from all sources
		ei.getWindow().getCanvas().addKeyListener(this);
		ei.getWindow().getCanvas().addMouseListener(this);
		ei.getWindow().getCanvas().addMouseMotionListener(this);
		ei.getWindow().getCanvas().addMouseWheelListener(this);
	}

	// moves the current inputs to last frame
	public void update() {
		scroll = 0;
		for (int i = 0; i < Total_Keys; i++) {
			keylast[i] = keys[i];
		}

		for (int i = 0; i < Total_Buttons; i++) {
			button_last[i] = buttons[i];
		}

	}

	// key input validation
	public boolean isKey(int keycode) {
		return keys[keycode];
	}

	// key up means that it was last pressed and now has been "unpressed"
	public boolean isKeyUp(int keycode) {
		return !keys[keycode] && keylast[keycode];
	}

	// key down means it was last unpressed but now is
	public boolean isKeyDown(int keycode) {
		return keys[keycode] && !keylast[keycode];
	}

	// mouse input validation
	public boolean isButton(int button) {
		return buttons[button];
	}

	public boolean isButtonUp(int button) {
		return !buttons[button] && button_last[button];
	}

	public boolean isButtonDown(int button) {
		return buttons[button] && !button_last[button];
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll = e.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (int) (e.getX() / ei.getScale());
		mouseY = (int) (e.getY() / ei.getScale());

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (int) (e.getX() / ei.getScale());
		mouseY = (int) (e.getY() / ei.getScale());

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;

	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	// getters

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getScroll() {
		return scroll;
	}

}
