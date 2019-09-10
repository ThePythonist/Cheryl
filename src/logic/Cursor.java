package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lines.Error;
import lines.Information;
import lines.Speech;
import lines.Success;
import lines.Usage;
import network.HttpRequest;
import network.Response;

public class Cursor {

	private static final int CODE_OKAY = 0, CODE_FAILURE = 1, CODE_SPEECH = 2, CODE_USAGE = 3;

	private static String escape(String line) {
		return line.replaceAll("%", "%per;").replaceAll("\\\\\\\\", "%sla;").replaceAll("\\\\\"", "%quo;");
	}

	private static String unescape(String line) {
		return line.replaceAll("%quo;", "\"").replaceAll("%sla;", "\\\\").replaceAll("%per;", "%");
	}

	private static String[] getTokens(String line) {
		List<String> tokenList = new ArrayList<String>();

		boolean inQuote = false;
		String token = "";
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '"') {
				inQuote = !inQuote;
				if (inQuote) {
					token = "";
				} else {
					tokenList.add(token);
				}
			} else if (inQuote) {
				token += line.charAt(i);
			}
		}

		String[] tokens = new String[tokenList.size()];
		for (int i = 0; i < tokenList.size(); i++) {
			tokens[i] = tokenList.get(i);
		}
		return tokens;
	}

	private static String tokenise(String line) {
		int counter = 0;

		boolean inQuote = false;
		String tokenised = "";
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '"') {
				inQuote = !inQuote;
				if (!inQuote) {
					tokenised += "%" + counter++ + ";";
				}
			} else if (!inQuote) {
				tokenised += line.charAt(i);
			}
		}
		return tokenised;
	}

	private static String untokenise(String tokenised, String[] tokens) {
		String untokenised = "";

		int depth = 0;
		String token = "";
		for (int i = 0; i < tokenised.length(); i++) {
			if (tokenised.charAt(i) == '%') {
				depth++;
			} else if (tokenised.charAt(i) == ';') {
				depth--;
				if (depth <= 0) {
					try {
						int tokenIndex = Integer.parseInt(token);
						untokenised += tokens[tokenIndex];
					} catch (NumberFormatException e) {
						untokenised += "%" + token + ";";
					}
					token = "";
				}
			} else if (depth > 0) {
				token += tokenised.charAt(i);
			} else {
				untokenised += tokenised.charAt(i);
			}
		}
		return untokenised;
	}

	public static String[] readCommand(String line) {
		List<String> argList = new ArrayList<String>();
		String escaped = escape(line);

		String[] tokens = getTokens(escaped);
		String tokenised = tokenise(escaped);

		String[] words = tokenised.split(" ");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String arg = unescape(untokenise(word, tokens));
			argList.add(arg);
		}

		String[] args = new String[argList.size()];
		for (int i = 0; i < argList.size(); i++) {
			args[i] = argList.get(i);
		}
		return args;
	}

	public synchronized static void execute(String line) {
		String[] command = readCommand(line);
		if (command.length > 0) {
			Map<String, String> args = new HashMap<String, String>();
			if (command[0].equals("connect")) {
				if (command.length >= 2) {
					args.put("command", "connect");
					if (command.length >= 3) {
						String hashedPassword = Hasher.hash(command[2]);
						args.put("pwd", hashedPassword);
					}
					Response response = HttpRequest.sendPost(command[1], args);
					switch (HttpRequest.getStatus()) {
					case HttpRequest.ST_OKAY:
						if (response.text.equals("access denied")) {
							ConsoleBuffer.addLine(new Error("Access denied to server: "
									+ command[1].replaceAll("/$", "").replaceAll("^http://", "")));
						} else if (response.text.equals("access granted")) {
							ConsoleBuffer.addLine(new Success("Connected to server: "
									+ command[1].replaceAll("/$", "").replaceAll("^http://", "")));
							Program.server = command[1];
							if (command.length >= 3) {
								Program.password = command[2];
							}
							Program.channel = null;
							Program.username = null;
							Program.userpwd = null;
						} else {
							ConsoleBuffer.addLine(new Error("Failed to connect: " + response.text));
						}
						break;
					case HttpRequest.ST_MALFORMED_URL:
						ConsoleBuffer.addLine(new Error(
								"Invalid server: " + command[1].replaceAll("/$", "").replaceAll("^http://", "")));
						break;
					case HttpRequest.ST_IO_EXCEPTION:
						ConsoleBuffer.addLine(new Error("Failed to connect to server: "
								+ command[1].replaceAll("/$", "").replaceAll("^http://", "")));
						break;
					}
				} else {
					ConsoleBuffer.addLine(new Usage("connect", "server [password]"));
				}
			} else if (command[0].equals("disconnect")) {
				if (Program.server == null) {
					ConsoleBuffer.addLine(new Error("You're not connected to a server"));
				} else {
					ConsoleBuffer.addLine(new Success("You have been disconnected"));
					Program.server = null;
					Program.password = null;
					Program.channel = null;
					Program.username = null;
					Program.userpwd = null;
				}
			} else if (command[0].equals("register")) {
				if (Program.server == null) {
					ConsoleBuffer.addLine(new Error("You're not connected to a server"));
				} else if (Program.channel == null) {
					if (command.length >= 3) {
						if (Program.password != null) {
							String hashedPassword = Hasher.hash(Program.password);
							args.put("pwd", hashedPassword);
						}
						args.put("command", "register");
						args.put("arg0", command[1]);
						args.put("arg1", command[2]);
						Response request = HttpRequest.sendPost(Program.server, args);
						switch (HttpRequest.getStatus()) {
						case HttpRequest.ST_OKAY:
							if (request.text.equals("access denied")) {
								ConsoleBuffer.addLine(new Error("You've been disconnected from the server"));
								Program.server = null;
								Program.password = null;
								Program.channel = null;
								Program.username = null;
								Program.userpwd = null;
							} else if (request.code != 0) {
								ConsoleBuffer.addLine(new Error("Failed to register user: " + request.text));
							} else {
								ConsoleBuffer.addLine(new Success("Registered user: " + command[1]));
							}
							break;
						case HttpRequest.ST_IO_EXCEPTION:
							ConsoleBuffer.addLine(new Error("Failed to register user"));
							break;
						}
					} else {
						ConsoleBuffer.addLine(new Usage("register", "username password"));
					}
				} else {
					ConsoleBuffer.addLine(new Error("You're already on a channel"));
				}
			} else if (command[0].equals("login")) {
				if (Program.server == null) {
					ConsoleBuffer.addLine(new Error("You're not connected to a server"));
				} else {
					if (command.length >= 3) {
						if (Program.password != null) {
							String hashedPassword = Hasher.hash(Program.password);
							args.put("pwd", hashedPassword);
						}
						args.put("command", "login");
						args.put("arg0", command[1]);
						args.put("arg1", command[2]);
						Response request = HttpRequest.sendPost(Program.server, args);
						switch (HttpRequest.getStatus()) {
						case HttpRequest.ST_OKAY:
							if (request.text.equals("access denied")) {
								ConsoleBuffer.addLine(new Error("You've been disconnected from the server"));
								Program.server = null;
								Program.password = null;
								Program.channel = null;
								Program.username = null;
								Program.userpwd = null;
							} else if (!request.text.equals("Correct login")) {
								ConsoleBuffer.addLine(new Error("Failed to login to user: " + request.text));
							} else {
								ConsoleBuffer.addLine(new Success("Logged in to user: " + command[1]));
								Program.username = command[1];
								Program.userpwd = command[2];
							}
							break;
						case HttpRequest.ST_IO_EXCEPTION:
							ConsoleBuffer.addLine(new Error("Failed to login to user"));
							break;
						}
					} else {
						ConsoleBuffer.addLine(new Usage("login", "username password"));
					}
				}
			} else if (command[0].equals("whoami")) {
				if (Program.username == null) {
					ConsoleBuffer.addLine(new Error("You're not logged in"));
				} else {
					ConsoleBuffer.addLine(new Information("You're logged in as " + Program.username));
				}
			} else if (command[0].equals("logout")) {
				if (Program.username == null) {
					ConsoleBuffer.addLine(new Error("You're not logged in"));
				} else {
					ConsoleBuffer.addLine(new Success("You've been logged out"));
					Program.username = null;
					Program.userpwd = null;
					Program.channel = null;
				}
			} else if (command[0].equals("join")) {
				if (Program.server == null) {
					ConsoleBuffer.addLine(new Error("You're not connected to a server"));
				} else {
					if (Program.password != null) {
						String hashedPassword = Hasher.hash(Program.password);
						args.put("pwd", hashedPassword);
					}
					if (Program.username != null) {
						args.put("username", Program.username);
						args.put("userpwd", Program.userpwd);
					}
					if (Program.channel != null) {
						args.put("channel", Program.channel);
					}
					args.put("command", command[0]);
					for (int i = 0; i < command.length - 1; i++) {
						args.put("arg" + i, command[i + 1]);
					}
					Response response = HttpRequest.sendPost(Program.server, args);
					switch (HttpRequest.getStatus()) {
					case HttpRequest.ST_OKAY:
						if (response.text.equals("access denied")) {
							ConsoleBuffer.addLine(new Error("You've been disconnected from the server"));
							Program.server = null;
							Program.password = null;
							Program.channel = null;
							Program.username = null;
							Program.userpwd = null;
						} else {
							if (response.code == CODE_OKAY) {
								ConsoleBuffer.addLine(new Success(response.text));
								Program.channel = command[1];
							} else if (response.code == CODE_FAILURE) {
								ConsoleBuffer.addLine(new Error(response.text));
							} else if (response.code == CODE_SPEECH) {
								ConsoleBuffer.addLine(new Speech("Cheryl", response.text));
							} else if (response.code == CODE_USAGE) {
								ConsoleBuffer.addLine(new Usage(command[0], response.text));
							}
						}
						break;
					case HttpRequest.ST_IO_EXCEPTION:
						ConsoleBuffer.addLine(new Error("Failed to execute command"));
						break;
					}
				}
			} else if (command[0].equals("leave")) {
				if (Program.channel == null) {
					ConsoleBuffer.addLine(new Error("You're not on a channel"));
				} else {
					ConsoleBuffer.addLine(new Success("You've left the channel"));
					Program.channel = null;
				}
			} else if (command[0].equals("channel")) {
				if (Program.channel == null) {
					ConsoleBuffer.addLine(new Error("You're not on a channel"));
				} else {
					ConsoleBuffer.addLine(new Success("You are on the channel: " + Program.channel));
				}
			} else if (command[0].equals("read")) {
				if (Program.channel == null) {
					ConsoleBuffer.addLine(new Error("You're not connected to a server"));
				} else {
					ConsoleBuffer.addLine(new Error("Unknown command: read"));
				}
			} else if (!command[0].equals("")) {
				if (Program.server == null) {
					ConsoleBuffer.addLine(new Error("You're not connected to a server"));
				} else {
					if (Program.password != null) {
						String hashedPassword = Hasher.hash(Program.password);
						args.put("pwd", hashedPassword);
					}
					if (Program.username != null) {
						args.put("username", Program.username);
						args.put("userpwd", Program.userpwd);
					}
					if (Program.channel != null) {
						args.put("channel", Program.channel);
					}
					args.put("command", command[0]);
					for (int i = 0; i < command.length - 1; i++) {
						args.put("arg" + i, command[i + 1]);
					}
					Response response = HttpRequest.sendPost(Program.server, args);
					switch (HttpRequest.getStatus()) {
					case HttpRequest.ST_OKAY:
						if (response.text.equals("access denied")) {
							ConsoleBuffer.addLine(new Error("You've been disconnected from the server"));
							Program.server = null;
							Program.password = null;
							Program.channel = null;
							Program.username = null;
							Program.userpwd = null;
						} else {
							if (response.code == CODE_OKAY) {
								ConsoleBuffer.addLine(new Success(response.text));
							} else if (response.code == CODE_FAILURE) {
								ConsoleBuffer.addLine(new Error(response.text));
							} else if (response.code == CODE_SPEECH) {
								ConsoleBuffer.addLine(new Speech("Cheryl", response.text));
							} else if (response.code == CODE_USAGE) {
								ConsoleBuffer.addLine(new Usage(command[0], response.text));
							}
						}
						break;
					case HttpRequest.ST_IO_EXCEPTION:
						ConsoleBuffer.addLine(new Error("Failed to execute command"));
						break;
					}
				}
			}
		}
	}

}
