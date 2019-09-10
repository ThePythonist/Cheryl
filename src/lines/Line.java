package lines;

public class Line {

	public LineComponent[] comps;
	protected int time = -1;

	public Line(LineComponent[] comps) {
		this.comps = comps;
	}

	@Override
	public String toString() {
		String line = "<html>";
		for (LineComponent comp : comps) {
			line += comp;
		}
		return line + "</html>";
	}

	public boolean equals(Line arg) {
		if (comps.length != arg.comps.length) {
			return false;
		}
		for (int i = 0; i < comps.length; i++) {
			if (!comps[i].equals(arg.comps[i])) {
				return false;
			}
		}
		return time == arg.time;
	}

}
