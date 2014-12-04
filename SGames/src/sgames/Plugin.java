package sgames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pircbotx.User;
import pl.shockah.PairNonOrdered;
import shocky3.BotManager;
import shocky3.JSONUtil;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static sident.Plugin pluginIdent;
	@Dependency protected static scommands.Plugin pluginCmd;
	
	public final List<User> busy = Collections.synchronizedList(new ArrayList<User>());
	public final List<Scores> scores = Collections.synchronizedList(new ArrayList<Scores>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginCmd.provider.add(
			new CmdGames(this)
		);
		
		DBCollection dbc = botApp.collection(this);
		synchronized (scores) {
			for (DBObject dbo : JSONUtil.all(dbc.find())) {
				scores.add(Scores.read(JSONUtil.fromDBObject(dbo)));
			}
		}
	}
	
	public Scores getFor(User user1, User user2) {
		BotManager manager = ((Bot)user1.getBot()).manager;
		String acc1 = pluginIdent.getMostCredibleAccount(manager, user1);
		String acc2 = pluginIdent.getMostCredibleAccount(manager, user2);
		
		synchronized (scores) {
			int index = scores.indexOf(new PairNonOrdered<>(acc1, acc2));
			if (index == -1) {
				Scores sc = new Scores(acc1, acc2);
				scores.add(sc);
				return sc;
			} else {
				Scores sc = scores.get(index);
				if (sc.account1.equals(acc2)) sc = sc.reverse();
				return sc;
			}
		}
	}
	
	public boolean isBusy(User user) {
		return busy.contains(user);
	}
	public boolean areBusy(List<User> list) {
		synchronized (busy) {
			for (User user : list) {
				if (isBusy(user)) return true;
			}
			return false;
		}
	}
	public boolean areBusy(User me, List<User> list) {
		synchronized (busy) {
			if (isBusy(me)) return true;
			return areBusy(list);
		}
	}
}