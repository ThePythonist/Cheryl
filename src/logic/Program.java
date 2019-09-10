package logic;

import graphics.Frame;

public class Program {

	public static Frame frame;
	public static String server = null, channel = null, password = null, username = null, userpwd = null;

	public static void main(String[] args) {
		frame = new Frame();
		new ConsoleBuffer().start();
	}

}
