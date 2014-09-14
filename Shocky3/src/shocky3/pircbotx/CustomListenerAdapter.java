package shocky3.pircbotx;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.pircbotx.PircBotX;
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
import shocky3.pircbotx.event.QuitEvent2;
import shocky3.pircbotx.event.ServerNoticeEvent;

public class CustomListenerAdapter<T extends PircBotX> extends ListenerAdapter<T> {
	public void onEvent(Event<T> event) throws Exception {
		if (event instanceof ExtendedJoinEvent<?>) {
			onExtendedJoin((ExtendedJoinEvent<T>)event);
		} else if (event instanceof AccountNotifyEvent<?>) {
			onAccountNotify((AccountNotifyEvent<T>)event);
		} else if (event instanceof OutActionEvent<?>) {
			onOutAction((OutActionEvent<T>)event);
		} else if (event instanceof OutMessageEvent<?>) {
			onOutMessage((OutMessageEvent<T>)event);
		} else if (event instanceof OutNoticeEvent<?>) {
			onOutNotice((OutNoticeEvent<T>)event);
		} else if (event instanceof OutPrivateMessageEvent<?>) {
			onOutPrivateMessage((OutPrivateMessageEvent<T>)event);
		}
		
		if (event instanceof NoticeEvent<?>) {
			NoticeEvent<T> e = (NoticeEvent<T>)event;
			if (e.getUser().getServer() == null || e.getUser().getServer().equals("")) {
				onServerNotice(new ServerNoticeEvent<T>(e.getBot(), e.getUser(), e.getMessage()));
				return;
			}
		}
		
		if (event instanceof QuitEvent<?>) {
			QuitEvent<T> e = (QuitEvent<T>)event;
			try {
				Field field = UserSnapshot.class.getDeclaredField("dao");
				field.setAccessible(true);
				
				Field mfield = Field.class.getDeclaredField("modifiers");
				mfield.setAccessible(true);
				mfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				
				field.set(e.getUser(), e.getDaoSnapshot());
				
				mfield.setInt(field, field.getModifiers() | Modifier.FINAL);
			} catch (Exception ex) {ex.printStackTrace();}
		} else if (event instanceof QuitEvent2<?>) {
			onQuit2((QuitEvent2<T>)event);
		}
		
		super.onEvent(event);
	}
	
	public void onExtendedJoin(ExtendedJoinEvent<T> event) {}
	public void onAccountNotify(AccountNotifyEvent<T> event) {}
	public void onOutAction(OutActionEvent<T> event) {}
	public void onOutMessage(OutMessageEvent<T> event) {}
	public void onOutNotice(OutNoticeEvent<T> event) {}
	public void onOutPrivateMessage(OutPrivateMessageEvent<T> event) {}
	public void onServerNotice(ServerNoticeEvent<T> event) {}
	public void onQuit2(QuitEvent2<T> event) {}
}