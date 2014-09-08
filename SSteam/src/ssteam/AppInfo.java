package ssteam;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.Colors;

public final class AppInfo {
	public final int id;
	public String name, priceType;
	public List<String> developers = new LinkedList<>();
	public int priceBase = 0, price = 0, metascore = 0;
	public double discount = 0;
	public boolean forWindows = false, forLinux = false, forOSX = false;
	
	public AppInfo(int id) {
		this.id = id;
	}
	
	public String format(boolean checkmarks) {
		return format(checkmarks, false);
	}
	public String format(boolean checkmarks, boolean includeUrl) {
		try {
			DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
			symbols.setGroupingSeparator(',');
			symbols.setDecimalSeparator('.');
			DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.format(" | &b%s&r", name));
			
			StringBuilder sb2 = new StringBuilder();
			for (String dev : developers) {
				sb2.append(", " + dev);
			}
			sb.append(String.format(" | by &b%s&r", sb2.toString().substring(2)));
			
			if (priceBase != 0) {
				sb.append(" | ");
				String formatted = formatter.format(priceBase / 100d);
				switch (priceType) {
					case "EUR": formatted = formatted + "€"; break;
					case "USD": formatted = "$" + formatted; break;
				}
				sb.append(formatted);
				
				if (priceBase != price) {
					sb.append(String.format(" -%d%%", (int)Math.round(discount * 100)));
					
					formatted = formatter.format(price / 100d);
					switch (priceType) {
						case "EUR": formatted = formatted + "€"; break;
						case "USD": formatted = "$" + formatted; break;
					}
					sb.append(" = ");
					sb.append(formatted);
				}
			}
			
			if (metascore != 0) {
				sb.append(String.format(" | metascore: %d/100", metascore));
			}
			
			if (forWindows || forLinux || forOSX) {
				sb2 = new StringBuilder();
				if (checkmarks) {
					if (forWindows) {
						sb2.append(", ");
						sb2.append('✔');
						sb2.append("Windows");
					}
					if (forLinux) {
						sb2.append(", ");
						sb2.append('✔');
						sb2.append("Linux");
					}
					if (forOSX) {
						sb2.append(", ");
						sb2.append('✔');
						sb2.append("OSX");
					}
				} else {
					if (forWindows) {
						sb2.append(", ");
						sb2.append(Colors.DARK_GREEN);
						sb2.append("Windows");
						sb2.append(Colors.NORMAL);
					}
					if (forLinux) {
						sb2.append(", ");
						sb2.append(Colors.DARK_GREEN);
						sb2.append("Linux");
						sb2.append(Colors.NORMAL);
					}
					if (forOSX) {
						sb2.append(", ");
						sb2.append(Colors.DARK_GREEN);
						sb2.append("OSX");
						sb2.append(Colors.NORMAL);
					}
				}
				sb.append(String.format(" | %s", sb2.toString().substring(2)));
			}
			
			if (includeUrl) {
				sb.append(String.format(" | http://store.steampowered.com/app/%d", id));
			}
			
			String ret = sb.toString().substring(3);
			ret = ret.replace("&b", Colors.BOLD);
			ret = ret.replace("&r", Colors.NORMAL);
			return ret;
		} catch (Exception e) {e.printStackTrace();}
		return "";
	}
}