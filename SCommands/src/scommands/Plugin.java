package scommands;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import shocky3.PluginInfo;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class Plugin extends shocky3.ListenerPlugin {
	@Dependency protected static sident.Plugin pluginIdent;
	
	public final CommandPatternManager patternManager;
	public final DefaultCommandPattern pattern;
	public final DefaultCommandProvider provider;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
		patternManager = new CommandPatternManager();
		pattern = new DefaultCommandPattern(this);
		provider = new DefaultCommandProvider(this);
	}
	
	protected void onLoad() {
		botApp.settings.add(this, "characters", ".");
		patternManager.patterns.clear();
		pattern.providers.clear();
		provider.commands.clear();
		
		patternManager.add(pattern);
		pattern.add(provider);
	}
	
	protected void onMessage(MessageEvent e) {
		handleCommands(new GenericUserMessageEvent(e));
	}
	protected void onPrivateMessage(PrivateMessageEvent e) {
		handleCommands(new GenericUserMessageEvent(e));
	}
	protected void onNotice(NoticeEvent e) {
		handleCommands(new GenericUserMessageEvent(e));
	}
	
	public void handleCommands(GenericUserMessageEvent e) {
		CommandStackEntry entry = patternManager.matchCommand(e);
		if (entry != null) {
			CommandStack stack = new CommandStack(e);
			try {
				Object result = stack.call(entry.command, entry.input);
				if (String.class.isInstance(result)) {
					TargetedText ttext = new TargetedText(e);
					ttext.send();
				} else
					throw new RuntimeException("Invalid top-level return type.");
			} catch (Exception ex) {
				e.respond("Exception raised: " + ex);
				ex.printStackTrace();
			}
		}
	}
}