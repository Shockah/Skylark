package swolfram;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.pircbotx.Colors;
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
				Document doc = Jsoup.parse(req.body(), "", Parser.xmlParser());
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
					String id = pod.attr("id");
					if (id.equals("VisualRepresentation")) continue;
					if (id.equals("Illustration")) continue;
					if (id.equals("Interpretation")) continue;
					if (id.startsWith("ComparisonAs")) continue;
					
					for (Element subpod : pod.select("subpod")) {
						Elements plaintexts = subpod.select("plaintext");
						if (!plaintexts.isEmpty()) {
							String text = plaintexts.get(0).text().trim();
							if (text.isEmpty()) continue;
							if (text.indexOf('\n') != -1 || text.indexOf('\r') != -1) continue;
							String title = pod.attr("title");
							
							if (id.equals("Result") || id.equals("Input")) {
								title = String.format("%s%s%s", Colors.BOLD, title, Colors.NORMAL);
								if (id.equals("Result")) {
									text = String.format("%s%s%s", Colors.BOLD, text, Colors.NORMAL);
								}
							}
							sb.append(String.format(" | %s: %s", title, text));
						}
					}
					
					if (id.equals("Input")) {
						Elements assumptionss = qres.select("assumptions");
						if (!assumptionss.isEmpty()) {
							for (Element assumption : assumptionss.select("assumption")) {
								sb.append(String.format(" | Assuming '%s' for '%s'", assumption.select("value").get(0).attr("desc"), assumption.attr("word")));
							}
						}
					}
				}
				
				e.respond(sb.substring(3));
			}
		} catch (Exception ex) {ex.printStackTrace();}
	}
}