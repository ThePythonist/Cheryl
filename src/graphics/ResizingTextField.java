package graphics;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ResizingTextField extends JTextField {

	private static final long serialVersionUID = -3245207275115544201L;
	private static List<ResizingTextField> fields = new ArrayList<ResizingTextField>();

	private JPanel parent;
	private double proportion;

	public ResizingTextField(JPanel parent, double proportion) {
		super();
		this.parent = parent;
		this.proportion = proportion;
		int preferredWidth = (int) (parent.getWidth() * proportion);
		setPreferredSize(new Dimension(preferredWidth < parent.getWidth() ? preferredWidth : parent.getWidth(), 30));
		setSize(new Dimension(preferredWidth < parent.getWidth() ? preferredWidth : parent.getWidth(), 30));
		setMaximumSize(getPreferredSize());
		setAlignmentX(Component.CENTER_ALIGNMENT);
		fields.add(this);
	}

	public static synchronized void recalculate() {
		for (ResizingTextField field : fields) {
			int preferredWidth = (int) (field.parent.getWidth() * field.proportion);
			field.setPreferredSize(new Dimension(
					preferredWidth < field.parent.getWidth() ? preferredWidth : field.parent.getWidth(), 30));
			field.setSize(new Dimension(
					preferredWidth < field.parent.getWidth() ? preferredWidth : field.parent.getWidth(), 30));
			field.setMaximumSize(field.getPreferredSize());
		}
	}

}
