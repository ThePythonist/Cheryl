package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import logic.Message;

public class HttpRequest {

	private static final String USER_AGENT = "Mozilla/5.0";
	public static final int ST_OKAY = 0, ST_MALFORMED_URL = 1, ST_IO_EXCEPTION = 2;
	private static int status = ST_OKAY;

	private static String post(String server, Map<String, String> args) {
		try {
			server = server.replaceAll("/$", "").replaceAll("^http://", "");
			String url = "http://" + server + "/cheryl.php";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "";
			Iterator<Entry<String, String>> it = args.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> pair = (Entry<String, String>) it.next();
				urlParameters += "&" + pair.getKey() + "=" + pair.getValue();
				it.remove();
			}
			urlParameters = urlParameters.substring(1);

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			status = ST_OKAY;
			return response.toString();
		} catch (MalformedURLException e) {
			status = ST_MALFORMED_URL;
		} catch (IOException e) {
			status = ST_IO_EXCEPTION;
		}
		return null;
	}

	public static Response sendPost(String server, Map<String, String> args) {
		String responseString = post(server, args);
		if (responseString != null) {
			try {
				int code = Integer.parseInt(responseString.split("<br />")[0]);
				String text = responseString.split("<br />")[1];
				return new Response(code, text);
			} catch (NumberFormatException e) {
				System.err.println(responseString);
				return new Response(1, "Malformed response string");
			}
		}
		return null;
	}

	public static Message[] requestMessages(String server, Map<String, String> args) {
		String response = post(server, args);
		String[] lines = response.split("<br />");
		Message[] messages = new Message[lines.length / 3];
		for (int i = 0; i < lines.length; i += 3) {
			try{
				messages[i / 3] = new Message(lines[i + 1], lines[i], Integer.parseInt(lines[i + 2]));
			} catch (ArrayIndexOutOfBoundsException e){
				
			}
		}
		return messages;
	}

	public static int getStatus() {
		return status;
	}

}
