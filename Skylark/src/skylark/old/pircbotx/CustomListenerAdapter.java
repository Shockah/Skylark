package skylark.old.pircbotx;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NoticeEvent;
import skylark.old.pircbotx.event.AccountNotifyEvent;
import skylark.old.pircbotx.event.ExtendedJoinEvent;
import skylark.old.pircbotx.event.OutActionEvent;
import skylark.old.pircbotx.event.OutMessageEvent;
import skylark.old.pircbotx.event.OutNoticeEvent;
import skylark.old.pircbotx.event.OutPrivateMessageEvent;
import skylark.old.pircbotx.event.ServerNoticeEvent;
import skylark.old.pircbotx.event.Whois2Event;

public class CustomListenerAdapter extends ListenerAdapter {
	public void onEvent(Event event) throws Exception {
		if (event instanceof ExtendedJoinEvent)
			onExtendedJoin((ExtendedJoinEvent)event);
		else if (event instanceof AccountNotifyEvent)
			onAccountNotify((AccountNotifyEvent)event);
		else if (event instanceof OutActionEvent)
			onOutAction((OutActionEvent)event);
		else if (event instanceof OutMessageEvent)
			onOutMessage((OutMessageEvent)event);
		else if (event instanceof OutNoticeEvent)
			onOutNotice((OutNoticeEvent)event);
		else if (event instanceof OutPrivateMessageEvent)
			onOutPrivateMessage((OutPrivateMessageEvent)event);
		else if (event instanceof Whois2Event)
			onWhois2((Whois2Event)event);
		
		if (event instanceof NoticeEvent) {
			NoticeEvent e = (NoticeEvent)event;
			if (e.getUser() == null || e.getUser().getServer() == null || e.getUser().getServer().equals("")) {
				onServerNotice(new ServerNoticeEvent(e.getBot(), e.getUserHostmask(), e.getUser(), e.getMessage()));
				return;
			}
		}
		
		//TODO: make sure it's fixed in latest PircBotX
		/*if (event instanceof QuitEvent) {
			QuitEvent e = (QuitEvent)event;
			Reflection.run(r -> {
				r.setFinalFieldValue(UserSnapshot.class, "dao", e.getUser(), e.getDaoSnapshot());
			});
		}*/
		
		super.onEvent(event);
	}
	
	public void onExtendedJoin(ExtendedJoinEvent event) { }
	public void onAccountNotify(AccountNotifyEvent event) { }
	public void onOutAction(OutActionEvent event) { }
	public void onOutMessage(OutMessageEvent event) { }
	public void onOutNotice(OutNoticeEvent event) { }
	public void onOutPrivateMessage(OutPrivateMessageEvent event) { }
	public void onWhois2(Whois2Event event) { }
	public void onServerNotice(ServerNoticeEvent event) { }
}