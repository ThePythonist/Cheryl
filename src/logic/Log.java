package logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lines.Line;

public class Log {

	public static List<Line> lines = new LinkedList<Line>();
	public static List<String> commands = new ArrayList<String>();
	private static int commandIndex = -1;

	public static void addLine(Line line) {
		lines.add(line);
	}

	public static void addCommand(String command) {
		commands.add(command);
		commandIndex = -1;
	}

	public static boolean equals(List<Line> arg) {
		if (lines.size() != arg.size()) {
			return false;
		}
		for (int i = 0; i < lines.size(); i++) {
			if (!lines.get(i).equals(arg.get(i))) {
				return false;
			}
		}
		return true;
	}

	public static void moveUp() {
		if (commandIndex < commands.size() - 1) {
			commandIndex++;
		}
	}

	public static void moveDown() {
		if (commandIndex > 0) {
			commandIndex--;
		}
	}

	public static String getCurrentCommand() {
		return commands.get(commands.size() - 1 - commandIndex);
	}

}
