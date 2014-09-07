package shocky3.pircbotx;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;

public class CustomListenerAdapter<T extends PircBotX> extends ListenerAdapter<T> {
	public void onEvent(Event<T> event) throws Exception {
		if (event instanceof ExtendedJoinEvent<?>) {
			onExtendedJoin((ExtendedJoinEvent<T>)event);
		} else if (event instanceof AccountNotifyEvent<?>) {
			onAccountNotify((AccountNotifyEvent<T>)event);
		}
		super.onEvent(event);
	}
	
	public void onExtendedJoin(ExtendedJoinEvent<T> event) {}
	public void onAccountNotify(AccountNotifyEvent<T> event) {}
}