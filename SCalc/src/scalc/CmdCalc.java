package scalc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import pl.shockah.Box;
import pl.shockah.Strings;
import scommands.Command;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdCalc extends Command {
	public static final Pattern
		REGEX_VARIABLE_ASSIGN = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*)\\s?=\\s(.*)");
	
	public CmdCalc(Plugin plugin) {
		super(plugin, "calc");
	}
	
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain) {
		String[] spl = args.split(";");
		
		String expression = null;
		Map<String, Double> variables = new HashMap<>();
		
		for (String s : spl) {
			Matcher m = REGEX_VARIABLE_ASSIGN.matcher(s);
			if (m.find()) {
				Box<Double> boxd = new Box<>();
				if (Strings.tryParseDouble( m.group(2), boxd)) {
					variables.put(m.group(1), boxd.value);
				} else {
					String _s = "Nested expressions are not implemented just yet.";
					if (!chain) e.respond(_s);
					return _s;
				}
			} else {
				expression = s;
			}
		}
		
		if (expression == null) {
			String _s = "Invalid expression.";
			if (!chain) e.respond(_s);
			return _s;
		} else {
			ExpressionBuilder exprb = new ExpressionBuilder(expression);
			for (String key : variables.keySet())
				exprb.variable(key);
			Expression expr = exprb.build();
			expr.setVariables(variables);
			
			ValidationResult vresult = expr.validate();
			if (vresult.isValid()) {
				String _s = "" + expr.evaluate();
				if (!chain) e.respond(_s);
				return _s;
			} else {
				if (!chain)
					for (String error : vresult.getErrors())
						e.respond(error);
				return "<Error.>";
			}
		}
	}
}