package shocky3;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.events.RemoveModeratedEvent;
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.events.RemovePrivateEvent;
import org.pircbotx.hooks.events.RemoveSecretEvent;
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SetChannelBanEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetModeratedEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import org.pircbotx.hooks.types.GenericCTCPEvent;
import org.pircbotx.hooks.types.GenericChannelEvent;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericDCCEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.hooks.types.GenericUserEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;
import shocky3.pircbotx.AccountNotifyEvent;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.CustomListenerAdapter;
import shocky3.pircbotx.ExtendedJoinEvent;
import shocky3.pircbotx.OutActionEvent;
import shocky3.pircbotx.OutMessageEvent;
import shocky3.pircbotx.OutNoticeEvent;
import shocky3.pircbotx.OutPrivateMessageEvent;

public abstract class ListenerPlugin extends Plugin {
	public final Listener<Bot> listener;
	
	public ListenerPlugin(PluginInfo pinfo) {
		super(pinfo);
		listener = new MyListener(this);
	}
	
	protected void preLoad() {
		for (BotManager bm : botApp.serverManager.botManagers) {
			for (Bot bot : bm.bots) {
				bot.getConfiguration().getListenerManager().addListener(listener);
			}
		}
	}
	protected void preUnload() {
		for (BotManager bm : botApp.serverManager.botManagers) {
			for (Bot bot : bm.bots) {
				bot.getConfiguration().getListenerManager().removeListener(listener);
			}
		}
	}
	
	protected void onEvent(Event<Bot> e) {}
	protected void onAction(ActionEvent<Bot> e) {}
	protected void onChannelInfo(ChannelInfoEvent<Bot> e) {}
	protected void onConnect(ConnectEvent<Bot> e) {}
	protected void onDisconnect(DisconnectEvent<Bot> e) {}
	protected void onFinger(FingerEvent<Bot> e) {}
	protected void onGenericChannel(GenericChannelEvent<Bot> e) {}
	protected void onGenericChannelMode(GenericChannelModeEvent<Bot> e) {}
	protected void onGenericChannelUser(GenericChannelUserEvent<Bot> e) {}
	protected void onGenericCTCP(GenericCTCPEvent<Bot> e) {}
	protected void onGenericDCC(GenericDCCEvent<Bot> e) {}
	protected void onGenericMessage(GenericMessageEvent<Bot> e) {}
	protected void onGenericUser(GenericUserEvent<Bot> e) {}
	protected void onGenericUserMode(GenericUserModeEvent<Bot> e) {}
	protected void onHalfOp(HalfOpEvent<Bot> e) {}
	protected void onIncomingChatRequest(IncomingChatRequestEvent<Bot> e) {}
	protected void onIncomingFileTransfer(IncomingFileTransferEvent<Bot> e) {}
	protected void onInvite(InviteEvent<Bot> e) {}
	protected void onJoin(JoinEvent<Bot> e) {}
	protected void onKick(KickEvent<Bot> e) {}
	protected void onMessage(MessageEvent<Bot> e) {}
	protected void onMode(ModeEvent<Bot> e) {}
	protected void onMotd(MotdEvent<Bot> e) {}
	protected void onNickAlreadyInUse(NickAlreadyInUseEvent<Bot> e) {}
	protected void onNickChange(NickChangeEvent<Bot> e) {}
	protected void onNotice(NoticeEvent<Bot> e) {}
	protected void onOp(OpEvent<Bot> e) {}
	protected void onOwner(OwnerEvent<Bot> e) {}
	protected void onPart(PartEvent<Bot> e) {}
	protected void onPing(PingEvent<Bot> e) {}
	protected void onPrivateMessage(PrivateMessageEvent<Bot> e) {}
	protected void onQuit(QuitEvent<Bot> e) {}
	protected void onRemoveChannelBan(RemoveChannelBanEvent<Bot> e) {}
	protected void onRemoveChannelKey(RemoveChannelKeyEvent<Bot> e) {}
	protected void onRemoveChannelLimit(RemoveChannelLimitEvent<Bot> e) {}
	protected void onRemoveInviteOnly(RemoveInviteOnlyEvent<Bot> e) {}
	protected void onRemoveModerated(RemoveModeratedEvent<Bot> e) {}
	protected void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent<Bot> e) {}
	protected void onRemovePrivate(RemovePrivateEvent<Bot> e) {}
	protected void onRemoveSecret(RemoveSecretEvent<Bot> e) {}
	protected void onRemoveTopicProtection(RemoveTopicProtectionEvent<Bot> e) {}
	protected void onServerPing(ServerPingEvent<Bot> e) {}
	protected void onServerResponse(ServerResponseEvent<Bot> e) {}
	protected void onSetChannelBan(SetChannelBanEvent<Bot> e) {}
	protected void onSetChannelKey(SetChannelKeyEvent<Bot> e) {}
	protected void onSetChannelLimit(SetChannelLimitEvent<Bot> e) {}
	protected void onSetInviteOnly(SetInviteOnlyEvent<Bot> e) {}
	protected void onSetModerated(SetModeratedEvent<Bot> e) {}
	protected void onSetNoExternalMessages(SetNoExternalMessagesEvent<Bot> e) {}
	protected void onSetPrivate(SetPrivateEvent<Bot> e) {}
	protected void onSetSecret(SetSecretEvent<Bot> e) {}
	protected void onSetTopicProtection(SetTopicProtectionEvent<Bot> e) {}
	protected void onSocketConnect(SocketConnectEvent<Bot> e) {}
	protected void onSuperOp(SuperOpEvent<Bot> e) {}
	protected void onTime(TimeEvent<Bot> e) {}
	protected void onTopic(TopicEvent<Bot> e) {}
	protected void onUnknown(UnknownEvent<Bot> e) {}
	protected void onUserList(UserListEvent<Bot> e) {}
	protected void onUserMode(UserModeEvent<Bot> e) {}
	protected void onVersion(VersionEvent<Bot> e) {}
	protected void onVoice(VoiceEvent<Bot> e) {}
	protected void onWhois(WhoisEvent<Bot> e) {}
	
	protected void onExtendedJoin(ExtendedJoinEvent<Bot> e) {}
	protected void onAccountNotify(AccountNotifyEvent<Bot> e) {}
	protected void onOutAction(OutActionEvent<Bot> e) {}
	protected void onOutMessage(OutMessageEvent<Bot> e) {}
	protected void onOutNotice(OutNoticeEvent<Bot> e) {}
	protected void onOutPrivateMessage(OutPrivateMessageEvent<Bot> e) {}
	
	protected class MyListener extends CustomListenerAdapter<Bot> {
		public final ListenerPlugin plugin;
		
		public MyListener(ListenerPlugin plugin) {
			this.plugin = plugin;
		}
		
		public void onEvent(Event<Bot> e) {
			try {
				plugin.onEvent(e);
				super.onEvent(e);
			} catch (Exception ex) {}
		}
		public void onAction(ActionEvent<Bot> e) { plugin.onAction(e); }
		public void onChannelInfo(ChannelInfoEvent<Bot> e) { plugin.onChannelInfo(e); }
		public void onConnect(ConnectEvent<Bot> e) { plugin.onConnect(e); }
		public void onDisconnect(DisconnectEvent<Bot> e) { plugin.onDisconnect(e); }
		public void onFinger(FingerEvent<Bot> e) { plugin.onFinger(e); }
		public void onGenericChannel(GenericChannelEvent<Bot> e) { plugin.onGenericChannel(e); }
		public void onGenericChannelMode(GenericChannelModeEvent<Bot> e) { plugin.onGenericChannelMode(e); }
		public void onGenericChannelUser(GenericChannelUserEvent<Bot> e) { plugin.onGenericChannelUser(e); }
		public void onGenericCTCP(GenericCTCPEvent<Bot> e) { plugin.onGenericCTCP(e); }
		public void onGenericDCC(GenericDCCEvent<Bot> e) { plugin.onGenericDCC(e); }
		public void onGenericMessage(GenericMessageEvent<Bot> e) { plugin.onGenericMessage(e); }
		public void onGenericUser(GenericUserEvent<Bot> e) { plugin.onGenericUser(e); }
		public void onGenericUserMode(GenericUserModeEvent<Bot> e) { plugin.onGenericUserMode(e); }
		public void onHalfOp(HalfOpEvent<Bot> e) { plugin.onHalfOp(e); }
		public void onIncomingChatRequest(IncomingChatRequestEvent<Bot> e) { plugin.onIncomingChatRequest(e); }
		public void onIncomingFileTransfer(IncomingFileTransferEvent<Bot> e) { plugin.onIncomingFileTransfer(e); }
		public void onInvite(InviteEvent<Bot> e) { plugin.onInvite(e); }
		public void onJoin(JoinEvent<Bot> e) { plugin.onJoin(e); }
		public void onKick(KickEvent<Bot> e) { plugin.onKick(e); }
		public void onMessage(MessageEvent<Bot> e) { plugin.onMessage(e); }
		public void onMode(ModeEvent<Bot> e) { plugin.onMode(e); }
		public void onMotd(MotdEvent<Bot> e) { plugin.onMotd(e); }
		public void onNickAlreadyInUse(NickAlreadyInUseEvent<Bot> e) { plugin.onNickAlreadyInUse(e); }
		public void onNickChange(NickChangeEvent<Bot> e) { plugin.onNickChange(e); }
		public void onNotice(NoticeEvent<Bot> e) { plugin.onNotice(e); }
		public void onOp(OpEvent<Bot> e) { plugin.onOp(e); }
		public void onOwner(OwnerEvent<Bot> e) { plugin.onOwner(e); }
		public void onPart(PartEvent<Bot> e) { plugin.onPart(e); }
		public void onPing(PingEvent<Bot> e) { plugin.onPing(e); }
		public void onPrivateMessage(PrivateMessageEvent<Bot> e) { plugin.onPrivateMessage(e); }
		public void onQuit(QuitEvent<Bot> e) { plugin.onQuit(e); }
		public void onRemoveChannelBan(RemoveChannelBanEvent<Bot> e) { plugin.onRemoveChannelBan(e); }
		public void onRemoveChannelKey(RemoveChannelKeyEvent<Bot> e) { plugin.onRemoveChannelKey(e); }
		public void onRemoveChannelLimit(RemoveChannelLimitEvent<Bot> e) { plugin.onRemoveChannelLimit(e); }
		public void onRemoveInviteOnly(RemoveInviteOnlyEvent<Bot> e) { plugin.onRemoveInviteOnly(e); }
		public void onRemoveModerated(RemoveModeratedEvent<Bot> e) { plugin.onRemoveModerated(e); }
		public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent<Bot> e) { plugin.onRemoveNoExternalMessages(e); }
		public void onRemovePrivate(RemovePrivateEvent<Bot> e) { plugin.onRemovePrivate(e); }
		public void onRemoveSecret(RemoveSecretEvent<Bot> e) { plugin.onRemoveSecret(e); }
		public void onRemoveTopicProtection(RemoveTopicProtectionEvent<Bot> e) { plugin.onRemoveTopicProtection(e); }
		public void onServerPing(ServerPingEvent<Bot> e) { plugin.onServerPing(e); }
		public void onServerResponse(ServerResponseEvent<Bot> e) { plugin.onServerResponse(e); }
		public void onSetChannelBan(SetChannelBanEvent<Bot> e) { plugin.onSetChannelBan(e); }
		public void onSetChannelKey(SetChannelKeyEvent<Bot> e) { plugin.onSetChannelKey(e); }
		public void onSetChannelLimit(SetChannelLimitEvent<Bot> e) { plugin.onSetChannelLimit(e); }
		public void onSetInviteOnly(SetInviteOnlyEvent<Bot> e) { plugin.onSetInviteOnly(e); }
		public void onSetModerated(SetModeratedEvent<Bot> e) { plugin.onSetModerated(e); }
		public void onSetNoExternalMessages(SetNoExternalMessagesEvent<Bot> e) { plugin.onSetNoExternalMessages(e); }
		public void onSetPrivate(SetPrivateEvent<Bot> e) { plugin.onSetPrivate(e); }
		public void onSetSecret(SetSecretEvent<Bot> e) { plugin.onSetSecret(e); }
		public void onSetTopicProtection(SetTopicProtectionEvent<Bot> e) { plugin.onSetTopicProtection(e); }
		public void onSocketConnect(SocketConnectEvent<Bot> e) { plugin.onSocketConnect(e); }
		public void onSuperOp(SuperOpEvent<Bot> e) { plugin.onSuperOp(e); }
		public void onTime(TimeEvent<Bot> e) { plugin.onTime(e); }
		public void onTopic(TopicEvent<Bot> e) { plugin.onTopic(e); }
		public void onUnknown(UnknownEvent<Bot> e) { plugin.onUnknown(e); }
		public void onUserList(UserListEvent<Bot> e) { plugin.onUserList(e); }
		public void onUserMode(UserModeEvent<Bot> e) { plugin.onUserMode(e); }
		public void onVersion(VersionEvent<Bot> e) { plugin.onVersion(e); }
		public void onVoice(VoiceEvent<Bot> e) { plugin.onVoice(e); }
		public void onWhois(WhoisEvent<Bot> e) { plugin.onWhois(e); }
		
		public void onExtendedJoin(ExtendedJoinEvent<Bot> e) { plugin.onExtendedJoin(e); }
		public void onAccountNotify(AccountNotifyEvent<Bot> e) { plugin.onAccountNotify(e); }
		public void onOutAction(OutActionEvent<Bot> e) { plugin.onOutAction(e); }
		public void onOutMessage(OutMessageEvent<Bot> e) { plugin.onOutMessage(e); }
		public void onOutNotice(OutNoticeEvent<Bot> e) { plugin.onOutNotice(e); }
		public void onOutPrivateMessage(OutPrivateMessageEvent<Bot> e) { plugin.onOutPrivateMessage(e); }
	}
}