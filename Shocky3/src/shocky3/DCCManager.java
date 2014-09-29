package shocky3;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pircbotx.User;

public class DCCManager {
	public final Shocky botApp;
	public Map<User, List<Request>> busyUsers = Collections.synchronizedMap(new HashMap<User, List<Request>>());
	
	public DCCManager(Shocky botApp) {
		this.botApp = botApp;
	}
	
	public Request requestNow(User user, Runnable runnable) {
		synchronized (busyUsers) {
			if (busyUsers.containsKey(user)) {
				return null;
			} else {
				List<Request> list = busyUsers.get(user);
				Request req = new Request(user, runnable);
				list.add(req);
				return req;
			}
		}
	}
	
	public class Request {
		public final User user;
		public final Runnable runnable;
		public final Date date;
		
		public Request(User user, Runnable runnable) {
			this(user, runnable, new Date());
		}
		public Request(User user, Runnable runnable, long time) {
			this(user, runnable, new Date(new Date().getTime() + time));
		}
		public Request(User user, Runnable runnable, Date date) {
			this.user = user;
			this.runnable = runnable;
			this.date = date;
		}
	}
}