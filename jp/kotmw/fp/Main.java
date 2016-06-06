package jp.kotmw.fp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	private String rec = "fprecmeta";
	@SuppressWarnings("unused")
	private Map<String, Location> before = new HashMap<>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length >= 1) {
			if(sender instanceof Player) {
				Player player = (Player)sender;
				if((args.length == 2) && ("call".equalsIgnoreCase(args[0]))) {
					if(NPCmanager.chechNPC(args[1]))
					{
						player.sendMessage(ChatColor.RED + "既に存在します");
						return false;
					}
					NPC npc = new NPC(args[1], player.getLocation());
					npc.spawn();
				} else if ((args.length == 2) && ("recall".equalsIgnoreCase(args[0]))) {
					NPC npc = NPCmanager.getNPC(args[1]);
					npc.destroy();
				} else if ((args.length == 1) && ("rec".equalsIgnoreCase(args[0]))) {
					player.sendMessage("記録を開始します");
					player.setMetadata(this.rec, new FixedMetadataValue(this, player.getName()));
				} else if ((args.length == 2) && ("replay".equalsIgnoreCase(args[0]))) {
					player.sendMessage("再生します");
					player.removeMetadata(this.rec, this);
					new NPCAI(args[1], player).runTaskTimer(this, 0, 1);
				} else if ((args.length == 1) && ("npcs".equalsIgnoreCase(args[0]))) {
					for(String npcs : NPCmanager.getNPCList()) {
						player.sendMessage(npcs);
					}
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(player.hasMetadata(rec))
			NPCmanager.loc.add(player.getLocation());

	}
}
