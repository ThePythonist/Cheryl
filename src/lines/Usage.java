package lines;

import java.awt.Color;

public class Usage extends Line {

	public Usage(String command, String usage) {
		super(null);
		comps = new LineComponent[3];
		comps[0] = new LineComponent("Usage: ", Color.RED);
		comps[1] = new LineComponent(command, Color.BLUE);
		comps[2] = new LineComponent(" " + usage, Color.GREEN);
	}

}
