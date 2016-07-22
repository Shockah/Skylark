package io.shockah.skylark.history;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.history.db.Line;
import io.shockah.skylark.util.TimeDuration;

public class HistoryCommand extends NamedCommand<AbstractHistoryQuery, List<Line>> {
	public final HistoryPlugin plugin;
	
	public HistoryCommand(HistoryPlugin plugin) {
		super("history");
		this.plugin = plugin;
	}
	
	@Override
	public AbstractHistoryQuery convertToInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		if (input instanceof AbstractHistoryQuery)
			return (AbstractHistoryQuery)input;
		return super.convertToInput(e, input);
	}
	
	@Override
	public AbstractHistoryQuery parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		String[] split = input.split("\\s");
		
		Integer baseLineId = null;
		String nick = null;
		Integer lines = null;
		Integer seconds = null;
		boolean fromStart = false;
		Pattern pattern = null;
		
		for (int i = 0; i < split.length; i++) {
			String arg = split[i];
			
			if (arg.equals("|")) {
				int length = 0;
				for (int j = 0; j <= i; j++) {
					length += split[j].length() + 1;
				}
				pattern = Pattern.compile(input.substring(length));
				break;
			}
			
			if (arg.equals("-")) {
				fromStart = true;
				continue;
			}
			
			if (baseLineId == null && arg.charAt(0) == '@') {
				baseLineId = Integer.parseInt(arg.substring(1));
				continue;
			}
			
			if (seconds == null && TimeDuration.TIME_DURATION_PATTERN.matcher(arg).find()) {
				seconds = TimeDuration.parseSeconds(arg);
				continue;
			}
			
			if (lines == null) {
				try {
					int num = Integer.parseInt(arg);
					if (num < 0)
						throw new NumberFormatException("");
					lines = num;
					continue;
				} catch (Exception ex) {
				}
			}
			
			if (nick == null) {
				nick = arg;
				continue;
			}
			
			throw new CommandParseException("Too many arguments.");
		}
		
		if (lines == null && seconds == null)
			throw new CommandParseException("You must specify either the line count or timeframe.");
		
		if (baseLineId == null) {
			HistoryQuery q = new HistoryQuery(e.getChannel());
			q.nick = nick;
			q.lines = lines;
			q.seconds = seconds;
			q.fromStart = fromStart;
			q.pattern = pattern;
			return q;
		} else {
			if (nick != null || pattern != null)
				throw new CommandParseException("Too many arguments.");
			ContextHistoryQuery q = new ContextHistoryQuery(e.getChannel(), baseLineId);
			q.lineCountContext = lines;
			q.secondsContext = seconds;
			return q;
		}
	}

	@Override
	public CommandResult<List<Line>> call(CommandCall call, AbstractHistoryQuery input) {
		List<Line> lines = input.query(plugin);
		if (lines.isEmpty())
			return CommandResult.of(lines, "No lines found.");
		
		int idLength = 0;
		int nickLength = 0;
		for (Line line : lines) {
			idLength = Math.max(idLength, String.valueOf(line.getId()).length());
			if (line.type == Line.Type.Message || line.type == Line.Type.Notice)
				nickLength = Math.max(nickLength, line.nick.length() + 2);
			else
				nickLength = Math.max(nickLength, 1);
		}
		
		final int f_idLength = idLength;
		final int f_nickLength = nickLength;
		
		String message = lines.stream().map(line -> line.toString(f_idLength, 0, f_nickLength)).collect(Collectors.joining("\n"));
		return CommandResult.of(lines, message);
	}
}