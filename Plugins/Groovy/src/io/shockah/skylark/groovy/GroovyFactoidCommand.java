package io.shockah.skylark.groovy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import groovy.lang.GroovyShell;
import groovy.lang.Tuple;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONPrinter;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.AbstractFactoidCommand;
import io.shockah.skylark.factoids.db.Factoid;

public class GroovyFactoidCommand<T, R> extends AbstractFactoidCommand<T, R> {
	public final GroovyPlugin plugin;
	
	public GroovyFactoidCommand(GroovyPlugin plugin, Factoid factoid) {
		super(factoid);
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T convertToInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		return (T)input;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return (T)input;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<R> call(CommandCall call, T input) {
		try {
			JSONObject storeData = factoid.getStoreData();
			
			Map<String, Object> variables = new LinkedHashMap<>();
			variables.put("call", call);
			variables.put("user", call.event.getUser());
			variables.put("channel", call.event.getChannel());
			variables.put("input", input);
			variables.put("store", storeData);
			GroovyShell shell = plugin.getShell(variables, new UserGroovySandboxImpl(), call.event);
			Object result = shell.evaluate(factoid.raw);
			
			if (result instanceof CommandResult<?>)
				return (CommandResult<R>)result;
			
			if (result instanceof Tuple) {
				Tuple tuple = (Tuple)result;
				if (tuple.size() == 2)
					return (CommandResult<R>)CommandResult.of(tuple.get(0), tuple.get(1).toString());
			}
			
			CommandResult<R> ret = (CommandResult<R>)CommandResult.of(result);
			JSONObject newStoreData = (JSONObject)plugin.turnIntoJSONValue(shell.getVariable("store"));
			
			JSONPrinter printer = new JSONPrinter();
			String jsonOld = storeData == null ? null : printer.toString(storeData);
			String jsonNew = newStoreData == null ? null : printer.toString(newStoreData);
			if (!Objects.equals(jsonOld, jsonNew))
				factoid.setStoreData(newStoreData);
			
			return ret;
		} catch (Exception e) {
			return CommandResult.error(e.getMessage());
		}
	}
}