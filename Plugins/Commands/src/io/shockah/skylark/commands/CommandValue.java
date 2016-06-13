package io.shockah.skylark.commands;

import io.shockah.json.JSONObject;

public class CommandValue<T> {
	public final T result;
	public final String error;
	
	public CommandValue(T result) {
		this(result, null);
	}
	
	private CommandValue(T result, String error) {
		this.result = result;
		this.error = error;
	}
	
	public static <T> CommandValue<T> error(String error) {
		return new CommandValue<T>(null, error);
	}
	
	public String toIRCOutput() {
		if (error != null)
			return error;
		return result == null ? null : result.toString();
	}
	
	public static class JSONData extends CommandValue<JSONObject> {
		public static final String OUTPUT = "output";
		
		public JSONData(JSONObject result) {
			super(result);
		}
		
		@Override
		public String toIRCOutput() {
			if (error != null)
				return error;
			if (result == null)
				return null;
			return result.containsKey(OUTPUT) ? result.get(OUTPUT).toString() : null;
		}
	}
	
	public static class Simple<T> extends CommandValue<T> {
		public final String ircOutput;
		
		public Simple(T result, String ircOutput) {
			super(result);
			this.ircOutput = ircOutput;
		}
		
		@Override
		public String toIRCOutput() {
			if (error != null)
				return error;
			return ircOutput;
		}
	}
}