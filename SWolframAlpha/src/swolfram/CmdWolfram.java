package swolfram;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import com.github.kevinsawicki.http.HttpRequest;

public class CmdWolfram extends Command {
	public CmdWolfram(Plugin plugin) {
		super(plugin, "wolfram", "wa");
	}
	
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args) {
		String appid = plugin.botApp.settings.getStringForChannel(e.getChannel(), plugin, "appid");
		if (appid == null) {
			e.respond("WolframAlpha plugin can't be used without setting an appid first.");
			return;
		}
		
		try {
			HttpRequest req = HttpRequest.get("http://api.wolframalpha.com/v2/query", true,
				"input", args,
				"appid", appid
			);
			if (req.ok()) {
				Document doc = Jsoup.parse(req.body());
				Elements qress = doc.select("queryresult");
				if (qress.isEmpty()) {
					e.respond("Failed.");
					return;
				}
				
				Element qres = qress.get(0);
				if (!qres.hasAttr("success") || !Boolean.parseBoolean(qres.attr("success"))) {
					e.respond("Failed.");
					return;
				}
				
				StringBuilder sb = new StringBuilder();
				for (Element pod : qres.select("pod")) {
					for (Element subpod : pod.select("subpod")) {
						Elements plaintexts = subpod.select("plaintext");
						if (!plaintexts.isEmpty()) {
							sb.append(" | ");
							sb.append(plaintexts.get(0).val().trim());
						}
					}
				}
				
				e.respond(sb.substring(3));
			}
		} catch (Exception ex) {ex.printStackTrace();}
	}
}