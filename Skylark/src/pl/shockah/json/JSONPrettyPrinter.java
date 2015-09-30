package pl.shockah.json;

import java.util.Map;

public class JSONPrettyPrinter extends JSONPrinter {
	public String print(JSONObject j) {
		StringBuilder sb = new StringBuilder();
		
		sb.append('{');
		if (!j.isEmpty()) {
			sb.append('\n');
			indent(sb,1);
		}
		print(sb,j,1);
		if (!j.isEmpty())
			sb.append('\n');
		sb.append('}');
		return sb.toString();
	}
	public String print(JSONList<?> j) {
		StringBuilder sb = new StringBuilder();
		
		sb.append('[');
		if (!j.isEmpty()) {
			sb.append('\n');
			indent(sb,1);
		}
		print(sb,j,false,1);
		if (!j.isEmpty())
			sb.append('\n');
		sb.append(']');
		return sb.toString();
	}
	protected void print(StringBuilder sb, JSONObject j, int indent) {
		boolean f = true;
		for (Map.Entry<String,Object> entry : j.entries()) {
			if (!f) {
				sb.append(",\n");
				indent(sb,indent);
			}
			
			sb.append('"');
			sb.append(escapeString(entry.getKey()));
			sb.append("\": ");
			
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
				if (!j2.isEmpty()) {
					sb.append('\n');
					indent(sb,indent+1);
				}
				print(sb,j2,indent+1);
				if (!j2.isEmpty()) {
					sb.append('\n');
					indent(sb,indent);
				}
				sb.append('}');
			} else if (o instanceof JSONList<?>) {
				JSONList<?> j2 = (JSONList<?>)o;
				sb.append('[');
				boolean b = j2.holdsBooleans() || j2.holdsNumbers() || (j2.size() == 1 && j2.isNull(0));
				if (!j2.isEmpty() && !b) {
					sb.append('\n');
					indent(sb,indent+1);
				}
				print(sb,j2,b,indent+1);
				if (!j2.isEmpty() && !b) {
					sb.append('\n');
					indent(sb,indent);
				}
				sb.append(']');
			}
			
			f = false;
		}
	}
	protected void print(StringBuilder sb, JSONList<?> j, boolean noNewLines, int indent) {
		boolean f = true;
		for (Object o : j) {
			if (!f) {
				if (noNewLines) {
					sb.append(", ");
				} else {
					sb.append(",\n");
					indent(sb,indent);
				}
			}
			
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
				if (!j2.isEmpty()) {
					sb.append('\n');
					indent(sb,indent+1);
				}
				print(sb,j2,indent+1);
				if (!j2.isEmpty()) {
					sb.append('\n');
					indent(sb,indent);
				}
				sb.append('}');
			} else if (o instanceof JSONList<?>) {
				JSONList<?> j2 = (JSONList<?>)o;
				sb.append('[');
				boolean b = j2.holdsBooleans() || j2.holdsNumbers() || (j2.size() == 1 && j2.isNull(0));
				if (!j2.isEmpty() && !b) {
					sb.append('\n');
					indent(sb,indent+1);
				}
				print(sb,j2,b,indent+1);
				if (!j2.isEmpty() && !b) {
					sb.append('\n');
					indent(sb,indent);
				}
				sb.append(']');
			}
			
			f = false;
		}
	}
	
	protected void indent(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++)
			sb.append('\t');
	}
}