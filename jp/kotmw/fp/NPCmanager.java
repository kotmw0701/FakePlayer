package jp.kotmw.fp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

public class NPCmanager
{
	//サーバーに存在するNPCを管理するクラス

	private static Map<String, NPC> npcs = new HashMap<>();
	private static Map<String, EntityNPC> entitynpcs = new HashMap<>();
	public static List<Location> loc = new ArrayList<>();

	public static void addNPC(NPC npc)
	{
		npcs.put(npc.getName(), npc);
	}

	public static void addNPC(EntityNPC npc)
	{
		entitynpcs.put(npc.getName(), npc);
	}

	public static NPC getNPC(String key)
	{
		if(npcs.containsKey(key))
			return npcs.get(key);
		return null;
	}

	public static boolean removeNPC(String key)
	{
		NPC npc = getNPC(key);
		if(npc == null)
			return false;
		npcs.remove(key);
		return true;
	}

	public static boolean chechNPC(String key)
	{
		return npcs.containsKey(key);
	}

	public static int getRecsize()
	{
		return NPCmanager.loc.size();
	}

	public static Location RePlayLoc(int i)
	{
		return NPCmanager.loc.get(i);
	}

	public static void clearList()
	{
		NPCmanager.loc.clear();
	}

	public static List<String> getNPCList()
	{
		List<String> npcs = new ArrayList<String>();
		for(String key : NPCmanager.npcs.keySet()) {
			npcs.add(key);
		}
		return npcs;
	}
}
