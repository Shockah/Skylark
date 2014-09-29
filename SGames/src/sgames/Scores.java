package sgames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.shockah.Pair;
import pl.shockah.PairNonOrdered;
import pl.shockah.Util;
import pl.shockah.json.JSONObject;

public class Scores {
	public static Scores read(JSONObject j) {
		String acc1 = j.getString("acc1");
		String acc2 = j.getString("acc2");
		Scores ret = new Scores(acc1, acc2);
		
		JSONObject jWins = j.getObject("wins", null);
		if (jWins != null) {
			for (String key : jWins.keys()) {
				ret.wins.put(key, jWins.getInt(key));
			}
		}
		
		JSONObject jLoses = j.getObject("loses", null);
		if (jWins != null) {
			for (String key : jLoses.keys()) {
				ret.loses.put(key, jLoses.getInt(key));
			}
		}
		
		JSONObject jDetails = j.getObject("details", null);
		if (jWins != null) {
			for (String key : jDetails.keys()) {
				List<JSONObject> list = new ArrayList<>();
				ret.details.put(key, list);
				for (JSONObject jDetail : jDetails.getList(key).ofObjects()) {
					list.add(jDetail);
				}
			}
		}
		
		return ret;
	}
	
	public final String account1, account2;
	public final Map<String, Integer>
		wins = Collections.synchronizedMap(new HashMap<String, Integer>()),
		loses = Collections.synchronizedMap(new HashMap<String, Integer>());
	public final Map<String, List<JSONObject>> details = Collections.synchronizedMap(new HashMap<String, List<JSONObject>>());
	
	public Scores(String account1, String account2) {
		this.account1 = account1;
		this.account2 = account2;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Scores) {
			Scores s = (Scores)other;
			return Util.equals(account1, s.account1) && Util.equals(account2, s.account2);
		} else if (other instanceof Pair<?, ?>) {
			Pair<?, ?> p = (Pair<?, ?>)other;
			if (p instanceof PairNonOrdered<?>) {
				PairNonOrdered<?> p2 = (PairNonOrdered<?>)p;
				return p2.equals(new PairNonOrdered<>(account1, account2));
			} else {
				return p.equals(new Pair<>(account1, account2));
			}
		}
		return false;
	}
	
	public synchronized Scores reverse() {
		Scores ret = new Scores(account2, account1);
		ret.wins.putAll(loses);
		ret.loses.putAll(wins);
		return ret;
	}
	
	public int totalWins() {
		int wins = 0;
		for (Map.Entry<String, Integer> entry : this.wins.entrySet()) {
			wins += entry.getValue();
		}
		return wins;
	}
	public int totalLoses() {
		int loses = 0;
		for (Map.Entry<String, Integer> entry : this.loses.entrySet()) {
			loses += entry.getValue();
		}
		return loses;
	}
}