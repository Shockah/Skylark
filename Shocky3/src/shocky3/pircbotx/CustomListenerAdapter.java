package shocky3.pircbotx;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import shocky3.pircbotx.event.AccountNotifyEvent;
import shocky3.pircbotx.event.ExtendedJoinEvent;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.OutNoticeEvent;
import shocky3.pircbotx.event.OutPrivateMessageEvent;

public class CustomListenerAdapter<T extends PircBotX> extends ListenerAdapter<T> {
	public void onEvent(Event<T> event) throws Exception {
		if (event instanceof ExtendedJoinEvent<?>) {
			onExtendedJoin((ExtendedJoinEvent<T>)event);
		} else if (event instanceof AccountNotifyEvent<?>) {
			onAccountNotify((AccountNotifyEvent<T>)event);
		} else if (event instanceof OutActionEvent<?>) {
			onOutAction((OutActionEvent<T>)event);
		} else if (event instanceof OutActionEvent<?>) {
			onOutMessage((OutMessageEvent<T>)event);
		} else if (event instanceof OutActionEvent<?>) {
			onOutNotice((OutNoticeEvent<T>)event);
		} else if (event instanceof OutActionEvent<?>) {
			onOutPrivateMessage((OutPrivateMessageEvent<T>)event);
		}
		super.onEvent(event);
	}
	
	public void onExtendedJoin(ExtendedJoinEvent<T> event) {}
	public void onAccountNotify(AccountNotifyEvent<T> event) {}
	public void onOutAction(OutActionEvent<T> event) {}
	public void onOutMessage(OutMessageEvent<T> event) {}
	public void onOutNotice(OutNoticeEvent<T> event) {}
	public void onOutPrivateMessage(OutPrivateMessageEvent<T> event) {}
}