package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.db.Factoid;

public class SimpleFactoidCommand extends NamedCommand<String, String> {
	public final Factoid factoid;
	
	public SimpleFactoidCommand(Factoid factoid) {
		super(factoid.name);
		this.factoid = factoid;
	}
	
	@Override
	public String parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<String> call(CommandCall call, String input) {
		String output = factoid.raw;
		input = input == null ? "" : input;
		String user = call.event.getUser().getNick();
		String bot = call.event.getBot().getUserBot().getNick();
		String inputOrUser = input.isEmpty() ? user : input;
		String channel = call.event.getChannel() == null ? null : call.event.getChannel().getName();
		String hostname = call.event.getUserHostmask().getHostname();
		
		output = output.replaceAll("(?iu)\\%(input|inp)\\%", input);
		output = output.replaceAll("(?iu)\\%(user|sender)\\%", user);
		output = output.replaceAll("(?iu)\\%bot\\%", bot);
		output = output.replaceAll("(?iu)\\%(inputoruser|ioru)\\%", inputOrUser);
		output = output.replaceAll("(?iu)\\%(hostname|host)\\%", hostname);
		
		if (channel != null)
			output = output.replaceAll("(?iu)\\%channel\\%", channel);
		
		return CommandResult.of(output);
	}
}