package io.shockah.skylark.commands;

import io.shockah.json.JSONObject;

public class CommandValue<T> {
	public final T result;
	
	public CommandValue(T result) {
		this.result = result;
	}
	
	public String toIRCOutput() {
		return result == null ? null : result.toString();
	}
	
	public static class JSONData extends CommandValue<JSONObject> {
		public static final String OUTPUT = "output";
		
		public JSONData(JSONObject result) {
			super(result);
		}
		
		@Override
		public String toIRCOutput() {
			if (result == null)
				return null;
			return result.containsKey(OUTPUT) ? result.get(OUTPUT).toString() : null;
		}
	}
}