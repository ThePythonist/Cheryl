package logic;

import lines.Line;
import lines.Speech;

public class Message {

	public String body, sender;
	public int time;

	public Message(String body, String sender, int time) {
		this.body = body;
		this.sender = sender;
		this.time = time;
	}

	public Line getLine() {
		return new Speech(sender, body, time);
	}

	public String toString() {
		return getLine().toString();
	}

}
