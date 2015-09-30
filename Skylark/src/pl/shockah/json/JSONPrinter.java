package pl.shockah.json;

import java.util.Map;
import pl.shockah.StrBuffer;

public class JSONPrinter {
	public String print(JSONObject j) {
		StringBuilder sb = new StringBuilder();
		
		sb.append('{');
		print(sb,j);
		sb.append('}');
		return sb.toString();
	}
	public String print(JSONList<?> j) {
		StringBuilder sb = new StringBuilder();
		
		sb.append('[');
		print(sb,j);
		sb.append(']');
		return sb.toString();
	}
	protected void print(StringBuilder sb, JSONObject j) {
		boolean f = true;
		for (Map.Entry<String,Object> entry : j.entries()) {
			if (!f)
				sb.append(",");
			
			sb.append('"');
			sb.append(escapeString(entry.getKey()));
			sb.append("\":");
			
			Object o = entry.getValue();
			if (o == null) {
				sb.append("null");
			} else if (o instanceof Boolean) {
				sb.append(""+o);
			} else if (o instanceof Number) {
				sb.append(""+o);
			} else if (o instanceof String) {
				sb.append('"'+escapeString(""+o)+'"');
			} else if (o instanceof JSONObject) {
				JSONObject j2 = (JSONObject)o;
				sb.append('{');
				print(sb,j2);
				sb.append('}');
			} else if (o instanceof JSONList<?>) {
				JSONList<?> j2 = (JSONList<?>)o;
				sb.append('[');
				print(sb,j2);
				sb.append(']');
			}
			
			f = false;
		}
	}
	protected void print(StringBuilder sb, JSONList<?> j) {
		boolean f = true;
		for (Object o : j) {
			if (!f)
				sb.append(",");
			
			if (o == null) {
				sb.append("null");
			} else if (o instanceof Boolean) {
				sb.append(""+o);
			} else if (o instanceof Number) {
				sb.append(""+o);
			} else if (o instanceof String) {
				sb.append('"'+escapeString(""+o)+'"');
			} else if (o instanceof JSONObject) {
				JSONObject j2 = (JSONObject)o;
				sb.append('{');
				print(sb,j2);
				sb.append('}');
			} else if (o instanceof JSONList<?>) {
				JSONList<?> j2 = (JSONList<?>)o;
				sb.append('[');
				print(sb,j2);
				sb.append(']');
			}
			
			f = false;
		}
	}
	
	protected String escapeString(String s) {
		StrBuffer strb = new StrBuffer();
		strb.append(s);
		strb.setPos(0);
		
		while (!strb.atEnd()) {
			char c = strb.readChar();
			if (c == '\r') {
				strb.seek(-1);
				strb.delete(1);
				strb.append("\\r");
			} else if (c == '\n') {
				strb.seek(-1);
				strb.delete(1);
				strb.append("\\n");
			} else if (c == '\t') {
				strb.seek(-1);
				strb.delete(1);
				strb.append("\\t");
			} else if (c == '"') {
				strb.seek(-1);
				strb.delete(1);
				strb.append("\\\"");
			} else if (c == '\\') {
				strb.seek(-1);
				strb.delete(1);
				strb.append("\\\\");
			}
		}
		return strb.toString();
	}
}