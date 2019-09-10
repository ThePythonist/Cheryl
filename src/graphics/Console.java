package graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import logic.Log;

public class Console extends JLabel {

	private static final long serialVersionUID = 7108929043197152177L;

	private static List<Console> consoles = new ArrayList<Console>();
	private JPanel parent;
	private double proportion;

	public Console(JPanel parent, double proportion) {
		super();
		this.parent = parent;
		this.proportion = proportion;
		init();
	}

	private synchronized void init() {
		int preferredWidth = (int) (parent.getWidth() * proportion);
		setPreferredSize(new Dimension(preferredWidth < parent.getWidth() ? preferredWidth : parent.getWidth(), 30));
		setSize(new Dimension(preferredWidth < parent.getWidth() ? preferredWidth : parent.getWidth(), 30));
		setMaximumSize(getPreferredSize());
		setAlignmentX(Component.CENTER_ALIGNMENT);
		consoles.add(this);
	}

	public static synchronized void recalculate() {
		for (Console console : consoles) {
			int preferredWidth = (int) (console.parent.getWidth() * console.proportion);
			console.setPreferredSize(new Dimension(
					preferredWidth < console.parent.getWidth() ? preferredWidth : console.parent.getWidth(), 30));
			console.setSize(new Dimension(
					preferredWidth < console.parent.getWidth() ? preferredWidth : console.parent.getWidth(), 30));
			console.setMaximumSize(console.getPreferredSize());
		}
	}

	public static synchronized void clearConsoles() {
		for (Console c : consoles) {
			c.parent.remove(c);
		}
		consoles.clear();
	}

	public static synchronized void update() {
		int linesToDisplay = Log.lines.size() > consoles.size() ? consoles.size() : Log.lines.size();
		int startingLine = Log.lines.size() > consoles.size() ? Log.lines.size() - consoles.size() : 0;
		for (int i = 0; i < linesToDisplay; i++) {
			consoles.get(i).setText("" + Log.lines.get(startingLine + i));
		}
	}

}
