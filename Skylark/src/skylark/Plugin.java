package skylark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.pircbotx.PircBotX;
import com.mongodb.DBCollection;

public abstract class Plugin {
	public final Skylark botApp;
	public final PluginInfo pinfo;
	
	public Plugin(PluginInfo pinfo) {
		this.botApp = pinfo.botApp;
		this.pinfo = pinfo;
	}
	
	protected void onLoad() { }
	protected void postLoad() { }
	
	protected void onUnload() { }
	
	protected void onBotStarted(BotManager manager, PircBotX bot) { }
	protected void onSettingUpdated(String setting) { }
	
	public final DBCollection collection() {
		return botApp.collection(this);
	}
	
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
	public static @interface Dependency {
		public String packageName() default "";
	}
}