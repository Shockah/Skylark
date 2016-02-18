package me.shockah.skylark;

import io.shockah.json.JSONObject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

public class Plugin {
	public static final class Info {
		public final JSONObject json;
		
		public Info(JSONObject json) {
			this.json = json;
		}
		
		public String packageName() {
			return json.getString("packageName");
		}
		
		public String baseClass() {
			return json.getString("baseClass");
		}
		
		public List<String> dependsOn() {
			return Collections.unmodifiableList(json.getListOrEmpty("dependsOn").ofStrings());
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
	public static @interface Dependency {
		public String packageName() default "";
	}
}