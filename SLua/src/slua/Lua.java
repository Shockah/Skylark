package slua;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.pircbotx.User;
import pl.shockah.func.Func1;
import scommands.Command;
import scommands.CommandStack;
import shocky3.Shocky;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class Lua {
	public final Shocky botApp;
	public final Plugin plugin;
	
	public Lua(Plugin plugin) {
		this.botApp = plugin.botApp;
		this.plugin = plugin;
	}
	
	public String parse(final GenericUserMessageEvent e, final String input, String code, final CommandStack stack) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Globals globals = JsePlatform.standardGlobals();
		globals.STDOUT = new PrintStream(baos);
		globals.set("cmd", new TwoArgFunction(){
			public LuaValue call(LuaValue commandName, LuaValue largs) {
				if (commandName.isnil())
					return LuaValue.valueOf("<No command name specified.>");
				
				String sCommandName = commandName.tojstring();
				String sArgs = largs.isnil() ? "" : largs.tojstring();
				
				Command cmd = Plugin.pluginCmd.patternManager.findCommand(e, sCommandName);
				if (cmd == null)
					return LuaValue.valueOf(String.format("<No command '%s' found.>", sCommandName));
				else {
					String result;
					if (stack == null)
						result = cmd.call(e, sArgs, stack);
					else
						result = stack.call(cmd, sArgs);
					return LuaValue.valueOf(result);
				}
			}
		});
		
		StringBuilder sb = build(e, new StringBuilder(), input);
		sb.append(code);
		
		try {
			LuaValue chunk = globals.load(sb.toString());
			chunk.call();
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			return ex.getMessage();
		}
		
		try {
			return baos.toString("UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return code;
	}
	
	protected StringBuilder build(GenericUserMessageEvent e, StringBuilder sb, String args) {
		varStringSimple(sb, "bot", e.getBot().getNick());
		varStringSimple(sb, "sender", e.getUser().getNick());
		varStringSimple(sb, "channel", e.getChannel().getName());
		varStringSimple(sb, "input", args);
		varStringSimple(sb, "ioru", args.equals("") ? e.getUser().getNick() : args);
		varStringSimpleArray(sb, "users", e.getChannel().getUsers(), new Func1<User, String>(){ public String f(User user) { return user.getNick(); } });
		return sb;
	}
	
	protected void varStringSimple(StringBuilder sb, String variable, String value) {
		sb.append(String.format("%s = %s ", variable, formatStringSimple(value)));
	}
	protected String formatStringSimple(String value) {
		if (value == null) {
			return "null";
		} else {
			return String.format("\"%s\"", value.replace("\\", "\\\\").replace("\"", "\\\""));
		}
	}
	
	protected <T> void varStringSimpleArray(StringBuilder sb, String variable, Collection<T> value) {
		varStringSimpleArray(sb, variable, value, new Func1<T, String>(){ public String f(T a) { return a == null ? "null" : a.toString(); } });
	}
	protected <T> void varStringSimpleArray(StringBuilder sb, String variable, Collection<T> value, Func1<T, String> f) {
		sb.append(String.format("%s = %s ", variable, formatStringSimpleArray(value, f)));
	}
	protected <T> String formatStringSimpleArray(Collection<T> value, Func1<T, String> f) {
		StringBuilder sb = new StringBuilder();
		for (T a : value) {
			sb.append(",");
			sb.append(formatStringSimple(f.f(a)));
		}
		return String.format("{%s}", sb.toString().substring(1));
	}
}