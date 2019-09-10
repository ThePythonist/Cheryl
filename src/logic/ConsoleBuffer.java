package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lines.Line;
import network.HttpRequest;

public class ConsoleBuffer extends Thread implements Runnable {

	private static List<Line> linesToAdd = new ArrayList<Line>();

	public void run() {
		while (true) {
			calc();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized boolean alreadyThere(Line line) {
		for (Line logLine : Log.lines) {
			if (logLine.equals(line)) {
				return true;
			}
		}
		return false;
	}

	private synchronized void calc() {
		Log.lines.addAll(linesToAdd);

		if (Program.server != null && Program.channel != null) {
			Map<String, String> args = new HashMap<String, String>();
			if (Program.password != null) {
				String hashedPassword = Hasher.hash(Program.password);
				args.put("pwd", hashedPassword);
			}
			args.put("channel", Program.channel);
			args.put("command", "read");
			args.put("username", Program.username);
			args.put("userpwd", Program.userpwd);
			Message[] messages = HttpRequest.requestMessages(Program.server, args);
			for (Message message : messages) {
				if (!alreadyThere(message.getLine())) {
					Log.lines.add(message.getLine());
					Program.frame.populateConsoles(false);
				}
			}
		}
		if (linesToAdd.size() > 0) {
			Program.frame.populateConsoles(false);
		}
		linesToAdd.clear();
	}

	public void start() {
		run();
	}

	public static synchronized void addLine(Line line) {
		linesToAdd.add(line);
	}

}
