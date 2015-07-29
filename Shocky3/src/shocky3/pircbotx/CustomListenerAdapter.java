package shocky3.pircbotx;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.snapshot.UserSnapshot;
import shocky3.pircbotx.event.AccountNotifyEvent;
import shocky3.pircbotx.event.ExtendedJoinEvent;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.OutNoticeEvent;
import shocky3.pircbotx.event.OutPrivateMessageEvent;
import shocky3.pircbotx.event.ServerNoticeEvent;

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
		
		if (event instanceof NoticeEvent) {
			NoticeEvent e = (NoticeEvent)event;
			if (e.getUser() == null || e.getUser().getServer() == null || e.getUser().getServer().equals("")) {
				onServerNotice(new ServerNoticeEvent(e.getBot(), e.getUserHostmask(), e.getUser(), e.getMessage()));
				return;
			}
		}
		
		if (event instanceof QuitEvent) {
			QuitEvent e = (QuitEvent)event;
			try {
				Field field = UserSnapshot.class.getDeclaredField("dao");
				field.setAccessible(true);
				
				Field mfield = Field.class.getDeclaredField("modifiers");
				mfield.setAccessible(true);
				mfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				
				field.set(e.getUser(), e.getDaoSnapshot());
				
				mfield.setInt(field, field.getModifiers() | Modifier.FINAL);
			} catch (Exception ex) {
				ex.printStackTrace();
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
	public void onServerNotice(ServerNoticeEvent event) { }
}