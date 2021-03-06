package io.shockah.skylark;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import io.shockah.skylark.event.AccountNotifyEvent;
import io.shockah.skylark.event.ExtendedJoinEvent;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.event.OutActionEvent;
import io.shockah.skylark.event.OutMessageEvent;
import io.shockah.skylark.event.OutNoticeEvent;
import io.shockah.skylark.event.OutPrivateMessageEvent;
import io.shockah.skylark.event.ServerNoticeEvent;
import io.shockah.skylark.event.Whois2Event;

public class SkylarkListenerAdapter extends ListenerAdapter {
	@Override
	public void onEvent(Event event) throws Exception {
		if (event instanceof GenericUserMessageEvent) {
			onGenericUserMessage((GenericUserMessageEvent)event);
		} else {
			if (event instanceof MessageEvent)
				onGenericUserMessage(new GenericUserMessageEvent((MessageEvent)event));
			else if (event instanceof ActionEvent)
				onGenericUserMessage(new GenericUserMessageEvent((ActionEvent)event));
			else if (event instanceof PrivateMessageEvent)
				onGenericUserMessage(new GenericUserMessageEvent((PrivateMessageEvent)event));
			else if (event instanceof NoticeEvent)
				onGenericUserMessage(new GenericUserMessageEvent((NoticeEvent)event));
		}
		
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
	public void onGenericUserMessage(GenericUserMessageEvent event) { }
}