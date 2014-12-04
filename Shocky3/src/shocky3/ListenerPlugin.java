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
import shocky3.pircbotx.CustomListenerAdapter;
import shocky3.pircbotx.event.AccountNotifyEvent;
import shocky3.pircbotx.event.ExtendedJoinEvent;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.OutNoticeEvent;
import shocky3.pircbotx.event.OutPrivateMessageEvent;
import shocky3.pircbotx.event.ServerNoticeEvent;

public abstract class ListenerPlugin extends Plugin {
	public final Listener listener;
	
	public ListenerPlugin(PluginInfo pinfo) {
		super(pinfo);
		listener = new MyListener(this);
	}
	
	protected void onEvent(Event e) {}
	protected void onAction(ActionEvent e) {}
	protected void onChannelInfo(ChannelInfoEvent e) {}
	protected void onConnect(ConnectEvent e) {}
	protected void onDisconnect(DisconnectEvent e) {}
	protected void onFinger(FingerEvent e) {}
	protected void onGenericChannel(GenericChannelEvent e) {}
	protected void onGenericChannelMode(GenericChannelModeEvent e) {}
	protected void onGenericChannelUser(GenericChannelUserEvent e) {}
	protected void onGenericCTCP(GenericCTCPEvent e) {}
	protected void onGenericDCC(GenericDCCEvent e) {}
	protected void onGenericMessage(GenericMessageEvent e) {}
	protected void onGenericUser(GenericUserEvent e) {}
	protected void onGenericUserMode(GenericUserModeEvent e) {}
	protected void onHalfOp(HalfOpEvent e) {}
	protected void onIncomingChatRequest(IncomingChatRequestEvent e) {}
	protected void onIncomingFileTransfer(IncomingFileTransferEvent e) {}
	protected void onInvite(InviteEvent e) {}
	protected void onJoin(JoinEvent e) {}
	protected void onKick(KickEvent e) {}
	protected void onMessage(MessageEvent e) {}
	protected void onMode(ModeEvent e) {}
	protected void onMotd(MotdEvent e) {}
	protected void onNickAlreadyInUse(NickAlreadyInUseEvent e) {}
	protected void onNickChange(NickChangeEvent e) {}
	protected void onNotice(NoticeEvent e) {}
	protected void onOp(OpEvent e) {}
	protected void onOwner(OwnerEvent e) {}
	protected void onPart(PartEvent e) {}
	protected void onPing(PingEvent e) {}
	protected void onPrivateMessage(PrivateMessageEvent e) {}
	protected void onQuit(QuitEvent e) {}
	protected void onRemoveChannelBan(RemoveChannelBanEvent e) {}
	protected void onRemoveChannelKey(RemoveChannelKeyEvent e) {}
	protected void onRemoveChannelLimit(RemoveChannelLimitEvent e) {}
	protected void onRemoveInviteOnly(RemoveInviteOnlyEvent e) {}
	protected void onRemoveModerated(RemoveModeratedEvent e) {}
	protected void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent e) {}
	protected void onRemovePrivate(RemovePrivateEvent e) {}
	protected void onRemoveSecret(RemoveSecretEvent e) {}
	protected void onRemoveTopicProtection(RemoveTopicProtectionEvent e) {}
	protected void onServerPing(ServerPingEvent e) {}
	protected void onServerResponse(ServerResponseEvent e) {}
	protected void onSetChannelBan(SetChannelBanEvent e) {}
	protected void onSetChannelKey(SetChannelKeyEvent e) {}
	protected void onSetChannelLimit(SetChannelLimitEvent e) {}
	protected void onSetInviteOnly(SetInviteOnlyEvent e) {}
	protected void onSetModerated(SetModeratedEvent e) {}
	protected void onSetNoExternalMessages(SetNoExternalMessagesEvent e) {}
	protected void onSetPrivate(SetPrivateEvent e) {}
	protected void onSetSecret(SetSecretEvent e) {}
	protected void onSetTopicProtection(SetTopicProtectionEvent e) {}
	protected void onSocketConnect(SocketConnectEvent e) {}
	protected void onSuperOp(SuperOpEvent e) {}
	protected void onTime(TimeEvent e) {}
	protected void onTopic(TopicEvent e) {}
	protected void onUnknown(UnknownEvent e) {}
	protected void onUserList(UserListEvent e) {}
	protected void onUserMode(UserModeEvent e) {}
	protected void onVersion(VersionEvent e) {}
	protected void onVoice(VoiceEvent e) {}
	protected void onWhois(WhoisEvent e) {}
	
	protected void onExtendedJoin(ExtendedJoinEvent e) {}
	protected void onAccountNotify(AccountNotifyEvent e) {}
	protected void onOutAction(OutActionEvent e) {}
	protected void onOutMessage(OutMessageEvent e) {}
	protected void onOutNotice(OutNoticeEvent e) {}
	protected void onOutPrivateMessage(OutPrivateMessageEvent e) {}
	protected void onServerNotice(ServerNoticeEvent e) {}
	
	protected class MyListener extends CustomListenerAdapter {
		public final ListenerPlugin plugin;
		
		public MyListener(ListenerPlugin plugin) {
			this.plugin = plugin;
		}
		
		public void onEvent(Event e) {
			try {
				plugin.onEvent(e);
				super.onEvent(e);
			} catch (Exception ex) {ex.printStackTrace();}
		}
		public void onAction(ActionEvent e) { plugin.onAction(e); }
		public void onChannelInfo(ChannelInfoEvent e) { plugin.onChannelInfo(e); }
		public void onConnect(ConnectEvent e) { plugin.onConnect(e); }
		public void onDisconnect(DisconnectEvent e) { plugin.onDisconnect(e); }
		public void onFinger(FingerEvent e) { plugin.onFinger(e); }
		public void onGenericChannel(GenericChannelEvent e) { plugin.onGenericChannel(e); }
		public void onGenericChannelMode(GenericChannelModeEvent e) { plugin.onGenericChannelMode(e); }
		public void onGenericChannelUser(GenericChannelUserEvent e) { plugin.onGenericChannelUser(e); }
		public void onGenericCTCP(GenericCTCPEvent e) { plugin.onGenericCTCP(e); }
		public void onGenericDCC(GenericDCCEvent e) { plugin.onGenericDCC(e); }
		public void onGenericMessage(GenericMessageEvent e) { plugin.onGenericMessage(e); }
		public void onGenericUser(GenericUserEvent e) { plugin.onGenericUser(e); }
		public void onGenericUserMode(GenericUserModeEvent e) { plugin.onGenericUserMode(e); }
		public void onHalfOp(HalfOpEvent e) { plugin.onHalfOp(e); }
		public void onIncomingChatRequest(IncomingChatRequestEvent e) { plugin.onIncomingChatRequest(e); }
		public void onIncomingFileTransfer(IncomingFileTransferEvent e) { plugin.onIncomingFileTransfer(e); }
		public void onInvite(InviteEvent e) { plugin.onInvite(e); }
		public void onJoin(JoinEvent e) { plugin.onJoin(e); }
		public void onKick(KickEvent e) { plugin.onKick(e); }
		public void onMessage(MessageEvent e) { plugin.onMessage(e); }
		public void onMode(ModeEvent e) { plugin.onMode(e); }
		public void onMotd(MotdEvent e) { plugin.onMotd(e); }
		public void onNickAlreadyInUse(NickAlreadyInUseEvent e) { plugin.onNickAlreadyInUse(e); }
		public void onNickChange(NickChangeEvent e) { plugin.onNickChange(e); }
		public void onNotice(NoticeEvent e) { plugin.onNotice(e); }
		public void onOp(OpEvent e) { plugin.onOp(e); }
		public void onOwner(OwnerEvent e) { plugin.onOwner(e); }
		public void onPart(PartEvent e) { plugin.onPart(e); }
		public void onPing(PingEvent e) { plugin.onPing(e); }
		public void onPrivateMessage(PrivateMessageEvent e) { plugin.onPrivateMessage(e); }
		public void onQuit(QuitEvent e) { plugin.onQuit(e); }
		public void onRemoveChannelBan(RemoveChannelBanEvent e) { plugin.onRemoveChannelBan(e); }
		public void onRemoveChannelKey(RemoveChannelKeyEvent e) { plugin.onRemoveChannelKey(e); }
		public void onRemoveChannelLimit(RemoveChannelLimitEvent e) { plugin.onRemoveChannelLimit(e); }
		public void onRemoveInviteOnly(RemoveInviteOnlyEvent e) { plugin.onRemoveInviteOnly(e); }
		public void onRemoveModerated(RemoveModeratedEvent e) { plugin.onRemoveModerated(e); }
		public void onRemoveNoExternalMessages(RemoveNoExternalMessagesEvent e) { plugin.onRemoveNoExternalMessages(e); }
		public void onRemovePrivate(RemovePrivateEvent e) { plugin.onRemovePrivate(e); }
		public void onRemoveSecret(RemoveSecretEvent e) { plugin.onRemoveSecret(e); }
		public void onRemoveTopicProtection(RemoveTopicProtectionEvent e) { plugin.onRemoveTopicProtection(e); }
		public void onServerPing(ServerPingEvent e) { plugin.onServerPing(e); }
		public void onServerResponse(ServerResponseEvent e) { plugin.onServerResponse(e); }
		public void onSetChannelBan(SetChannelBanEvent e) { plugin.onSetChannelBan(e); }
		public void onSetChannelKey(SetChannelKeyEvent e) { plugin.onSetChannelKey(e); }
		public void onSetChannelLimit(SetChannelLimitEvent e) { plugin.onSetChannelLimit(e); }
		public void onSetInviteOnly(SetInviteOnlyEvent e) { plugin.onSetInviteOnly(e); }
		public void onSetModerated(SetModeratedEvent e) { plugin.onSetModerated(e); }
		public void onSetNoExternalMessages(SetNoExternalMessagesEvent e) { plugin.onSetNoExternalMessages(e); }
		public void onSetPrivate(SetPrivateEvent e) { plugin.onSetPrivate(e); }
		public void onSetSecret(SetSecretEvent e) { plugin.onSetSecret(e); }
		public void onSetTopicProtection(SetTopicProtectionEvent e) { plugin.onSetTopicProtection(e); }
		public void onSocketConnect(SocketConnectEvent e) { plugin.onSocketConnect(e); }
		public void onSuperOp(SuperOpEvent e) { plugin.onSuperOp(e); }
		public void onTime(TimeEvent e) { plugin.onTime(e); }
		public void onTopic(TopicEvent e) { plugin.onTopic(e); }
		public void onUnknown(UnknownEvent e) { plugin.onUnknown(e); }
		public void onUserList(UserListEvent e) { plugin.onUserList(e); }
		public void onUserMode(UserModeEvent e) { plugin.onUserMode(e); }
		public void onVersion(VersionEvent e) { plugin.onVersion(e); }
		public void onVoice(VoiceEvent e) { plugin.onVoice(e); }
		public void onWhois(WhoisEvent e) { plugin.onWhois(e); }
		
		public void onExtendedJoin(ExtendedJoinEvent e) { plugin.onExtendedJoin(e); }
		public void onAccountNotify(AccountNotifyEvent e) { plugin.onAccountNotify(e); }
		public void onOutAction(OutActionEvent e) { plugin.onOutAction(e); }
		public void onOutMessage(OutMessageEvent e) { plugin.onOutMessage(e); }
		public void onOutNotice(OutNoticeEvent e) { plugin.onOutNotice(e); }
		public void onOutPrivateMessage(OutPrivateMessageEvent e) { plugin.onOutPrivateMessage(e); }
		public void onServerNotice(ServerNoticeEvent e) { plugin.onServerNotice(e); }
	}
}