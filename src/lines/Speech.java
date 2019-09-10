package lines;

import java.awt.Color;

public class Speech extends Line {

	public Speech(String username, String text) {
		super(null);
		comps = new LineComponent[2];
		comps[0] = new LineComponent("<" + username + ">: ", Color.BLUE);
		comps[1] = new LineComponent(text, Color.BLACK);
	}

	public Speech(String username, String text, int time) {
		this(username, text);
		this.time = time;
	}

}
