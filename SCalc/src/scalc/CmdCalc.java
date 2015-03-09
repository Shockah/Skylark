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
import scommands.CommandStack;
import scommands.TextCommand;
import shocky3.MultilineString;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdCalc extends TextCommand {
	public static final Pattern
		REGEX_VARIABLE_ASSIGN = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*)\\s?=\\s(.*)");
	
	public CmdCalc(Plugin plugin) {
		super(plugin, "calc");
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		String[] spl = input.split(";");
		
		String expression = null;
		Map<String, Double> variables = new HashMap<>();
		
		for (String s : spl) {
			Matcher m = REGEX_VARIABLE_ASSIGN.matcher(s);
			if (m.find()) {
				Box<Double> boxd = new Box<>();
				if (Strings.tryParseDouble( m.group(2), boxd)) {
					variables.put(m.group(1), boxd.value);
				} else {
					return "Nested expressions are not implemented just yet.";
				}
			} else {
				expression = s;
			}
		}
		
		if (expression == null)
			return "Invalid expression.";
		else {
			ExpressionBuilder exprb = new ExpressionBuilder(expression);
			for (String key : variables.keySet())
				exprb.variable(key);
			Expression expr = exprb.build();
			expr.setVariables(variables);
			
			ValidationResult vresult = expr.validate();
			if (vresult.isValid())
				return "" + expr.evaluate();
			else {
				return MultilineString.with(vresult.getErrors());
			}
		}
	}
}