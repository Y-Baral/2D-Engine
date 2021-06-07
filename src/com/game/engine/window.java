package com.game.engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class window {
	private JFrame frame;
	private BufferedImage image;
	private Canvas canvas;
	private BufferStrategy bs;
	private Graphics g;

	public window(engine_ignition ei) {
		// want to store RGB image
		image = new BufferedImage(ei.getWidth(), ei.getHeight(), BufferedImage.TYPE_INT_RGB);
		canvas = new Canvas();
		// scaled by the scale amount but height : width ratio stays the same
		Dimension s = new Dimension((int) (ei.getWidth() * ei.getScale()), (int) (ei.getHeight() * ei.getScale()));
		// canvas setup according to the dimension object
		canvas.setPreferredSize(s);
		canvas.setMaximumSize(s);
		canvas.setMinimumSize(s);

		// frame setup
		frame = new JFrame(ei.getTitle());
		// program terminates when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// matching up the canvas to the frame
		frame.setLayout(new BorderLayout());
		frame.add(canvas, BorderLayout.CENTER);
		// sets frame to the size of the canvas
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();
		g = bs.getDrawGraphics();

	}

	// method that updates window
	public void update() {
		// draws a image to the window
		g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
		// shows the updated window
		bs.show();
	}

	public BufferedImage getImage() {
		return image;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public JFrame getFrame() {
		return frame;
	}
}