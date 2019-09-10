package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import logic.Cursor;
import logic.Log;

public class Frame {

	private JFrame frame;
	public JPanel bg;

	public Frame() {
		init();
	}

	public synchronized void init() {
		frame = new JFrame("Cheryl");
		frame.setResizable(true);
		frame.setPreferredSize(new Dimension(640, 360));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		FontUIResource f = new javax.swing.plaf.FontUIResource("Consolas", Font.PLAIN, 16);
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}

		bg = new JPanel();
		bg.setLayout(new BoxLayout(bg, BoxLayout.Y_AXIS));
		bg.add(Box.createVerticalStrut(20));
		bg.setBackground(Color.WHITE);

		ResizingTextField field = new ResizingTextField(bg, 0.95);
		field.setBorder(BorderFactory.createCompoundBorder(field.getBorder(), new EmptyBorder(0, 5, 0, 5)));
		field.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					Log.addCommand(field.getText());
					Cursor.execute(field.getText());
					field.setText("");
				}

				if (arg0.getKeyCode() == KeyEvent.VK_UP) {
					Log.moveUp();
					field.setText(Log.getCurrentCommand());
				}

				if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
					Log.moveDown();
					field.setText(Log.getCurrentCommand());
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

		});
		bg.add(field);
		bg.add(Box.createVerticalStrut(20));

		frame.add(bg);

		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

		frame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				populateConsoles(true);
				ResizingTextField.recalculate();
				Console.recalculate();
			}
		});

		frame.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				populateConsoles(true);
				ResizingTextField.recalculate();
				Console.recalculate();
			}
		});
	}

	public synchronized void populateConsoles(boolean update) {
		Console.clearConsoles();
		if (update) {
			SwingUtilities.updateComponentTreeUI(frame);
		}
		int count = (int) Math.floor((bg.getHeight() - 20 - 30 - 20) / 30);
		for (int i = 0; i < count; i++) {
			Console console = new Console(bg, 0.95);
			bg.add(console);
		}
		Console.update();
	}

}