package lines;

import java.awt.Color;

public class Success extends Line {

	public Success(String text) {
		super(null);
		comps = new LineComponent[1];
		comps[0] = new LineComponent(text, Color.GREEN);
	}

}
