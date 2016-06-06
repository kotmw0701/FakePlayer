package jp.kotmw.fp;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;


public class Profiles {

	public String getSignature(String prop) {
		return getProperty(prop, 0);
	}

	public String getValue(String prop) {
		return getProperty(prop, 1);
	}

	private String getProperty(String prop, int i) {
		return prop.split(",")[i];
	}

	public String getProps(String uuid, String name) {
		try {
			String address = "https://sessionserver.mojang.com/session/minecraft/profile/id?unsigned=false";
			URL url = new URL(address.replace("id", uuid));
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String text = "";
			String imputLine;
			while ((imputLine = reader.readLine()) != null) {
				text = text + imputLine;
			}
			reader.close();

			String prop = text.replace("{", "").replace("}", "")
					.replace("[", "").replace("]", "").replace(":", "").replace("name", "").replace("value", "")
					.replace("\"", "").replace("id" + uuid + "," + name + ",propertiessignature","")
					.replace(",textures", "");
			return prop;
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}

	public String getUUID(String name) {
		try {
			String address = "https://api.mojang.com/users/profiles/minecraft/id";
			URL url = new URL(address.replace("id", name));
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String text = "";
			String imputLine;
			while ((imputLine = reader.readLine()) != null) {
				text = text + imputLine;
			}
			reader.close();

			String uuid = text.replace("{", "").replace("}", "")
					.replace(":", "").replace("name", "").replace("id", "")
					.replace(":", "").replace(name, "").replace("\"", "");
			return uuid.split(",")[0];
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
	}
}