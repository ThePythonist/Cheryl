package lines;

import java.awt.Color;

public class Error extends Line {

	public Error(String text) {
		super(null);
		comps = new LineComponent[1];
		comps[0] = new LineComponent(text, Color.RED);
	}

}
