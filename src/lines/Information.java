package lines;

import java.awt.Color;

public class Information extends Line {

	public Information(String text) {
		super(null);
		comps = new LineComponent[1];
		comps[0] = new LineComponent(text, Color.BLUE);
	}

}
