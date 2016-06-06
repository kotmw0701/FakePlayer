package jp.kotmw.fp;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCAI extends BukkitRunnable
{
	private NPC npc;
	private int i;

	public NPCAI(String name, Player player)
	{
		npc = new NPC(name, player.getLocation());
		npc.spawn();
	}

	@Override
	public void run()
	{
		if(i < NPCmanager.getRecsize())
			npc.teleport(NPCmanager.RePlayLoc(i));
		else
		{
			this.cancel();
			NPCmanager.clearList();
			npc.destroy();
		}
		i++;
	}
}
