package skylark.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import skylark.pircbotx.Bot;
import skylark.pircbotx.event.GenericUserMessageEvent;
import skylark.settings.Setting;

public class DefaultCommandPattern extends CommandPattern {
	public static final String
		TRIGGER_CHARACTERS_KEY = "TriggerCharacters";

	public static final String
		DEFAULT_TRIGGER_CHARACTERS = ".?!";
	
	public final Plugin plugin;
	protected Setting<String> triggerCharactersSetting;
	
	public DefaultCommandPattern(Plugin plugin) {
		super(plugin);
		this.plugin = plugin;
		triggerCharactersSetting = plugin.settingsPlugin.<String>getSetting(plugin, TRIGGER_CHARACTERS_KEY);
		triggerCharactersSetting.putDefault(DEFAULT_TRIGGER_CHARACTERS);
	}
	
	public Call match(GenericUserMessageEvent e) {
		String triggerCharacters = e.getChannel() == null ? triggerCharactersSetting.get(((Bot)e.getUser().getBot()).manager.name) : triggerCharactersSetting.get(e.getChannel());
		Pattern pattern = Pattern.compile("^[" + Pattern.quote(triggerCharacters) + "](.+?)(?:\\s(.+))$");
		Matcher m = pattern.matcher(e.getMessage());
		if (m.find()) {
			String command = m.group(1);
			String args = m.groupCount() == 2 ? m.group(2) : "";
			return new Call(command, args);
		}
		return null;
	}
}