package lines;

import java.awt.Color;

public class LineComponent {

	public String text;
	public Color color;

	public LineComponent(String text, Color color) {
		this.text = text;
		this.color = color;
	}

	private String getColorCode() {
		String red = Integer.toHexString(color.getRed());
		while (red.length() < 2) {
			red = "0" + red;
		}
		String green = Integer.toHexString(color.getGreen());
		while (green.length() < 2) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(color.getBlue());
		while (blue.length() < 2) {
			blue = "0" + blue;
		}
		return "#" + red + green + blue;
	}

	@Override
	public String toString() {
		return "<font color=\"" + getColorCode() + "\">" + text.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				+ "</font>";
	}

	public boolean equals(LineComponent arg) {
		return text.equals(arg.text) && color.equals(arg.color);
	}

}
