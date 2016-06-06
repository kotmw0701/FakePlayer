package jp.kotmw.fp;

import java.util.UUID;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class EntityNPC extends Profiles{

	public EntityNPC(Location loc, String name, Player player) {
		entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
		MinecraftServer nmsServer = ((CraftServer)Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
		this.gameprofile = new GameProfile(UUID.randomUUID(), name);
		EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameprofile, new PlayerInteractManager(nmsWorld));
		this.npc = npc;
		this.loc = loc;
		this.name = name;
		this.uuid = getUUID(name);
		this.prop = getProps(uuid, name);
		changeSkin();
		testspawn(loc);
		NPCmanager.addNPC(this);
	}

	int entityID;
	Location loc;
	GameProfile gameprofile;
	String name;
	String uuid;
	String prop;
	EntityPlayer npc;



	public void testspawn(Location loc) {
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		for(Player player : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
		}
	}

	public void changeSkin() {
		String value = getValue(prop);
		String signature = getSignature(prop);
		gameprofile.getProperties().put("textures", new Property("textures", value, signature));
	}

	public String getName() {
		return name;
	}
}
