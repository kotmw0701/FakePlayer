package jp.kotmw.fp;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class NPC extends Profiles
{
	//NPCのパケット関連の管理クラス

	int entityID;
	Location loc;
	GameProfile gameprofile;
	String name;
	String uuid;
	String prop;

	/**
	 * NPCのデータ作成メソッド
	 * この後にspawn()動かさないと出てこないので注意
	 *
	 * @param name NPCの名前
	 * @param loc 座標
	 *
	 */
	public NPC(String name, Location loc)
	{
		entityID = (int)Math.ceil(Math.random() * 1000) + 2000;
		gameprofile = new GameProfile(UUID.randomUUID(), name);
		this.loc = loc;
		this.name = name;
		this.uuid = getUUID(name);
		this.prop = getProps(uuid, name);
		changeSkin();
	}

	/**
	 * NPCのアニメーション(?)
	 *
	 * @param animation アニメーションID
	 *
	 */
	public void animation(int animation)
	{
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
		setValue(packet, "a", entityID);
		setValue(packet, "b", (byte)animation);
		sendPacket(packet);
	}

	public void changeSkin()
	{
		String value = getValue(prop);
		String signature = getSignature(prop);
		gameprofile.getProperties().put("textures", new Property("textures", value, signature));
	}


	/**
	 * NPCのステータス(?)
	 *
	 * @param status ステータスID
	 *
	 */
	public void status(int status)
	{
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
		setValue(packet, "a", entityID);
		setValue(packet, "b", (byte)status);
		sendPacket(packet);
	}

	/**
	 * NPCの装備
	 *
	 * @param slot 装備させる場所
	 * @param itemstack 対象のアイテム
	 *
	 */
	public void equip(int slot, ItemStack itemstack)
	{
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
		setValue(packet, "a", entityID);
		setValue(packet, "b", slot);
		setValue(packet, "c", itemstack);
		sendPacket(packet);
	}

	/**
	 * 作成済みNPCを召喚させる
	 *
	 */
	public void spawn()
	{
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
		setValue(packet, "a", entityID);
		setValue(packet, "b", gameprofile.getId());
		setValue(packet, "c", getFixLocation(loc.getX()));
		setValue(packet, "d", getFixLocation(loc.getY()));
		setValue(packet, "e", getFixLocation(loc.getZ()));
		setValue(packet, "f", getFixRotation(loc.getYaw()));
		setValue(packet, "g", getFixRotation(loc.getPitch()));
		setValue(packet, "h", 0);
		DataWatcher w = new DataWatcher(null);
		w.a(6, (float)20);
		w.a(10,(byte)127);
		setValue(packet, "i", w);
		addTabList();
		sendPacket(packet);
		headRotation(loc.getYaw(), loc.getPitch());
		NPCmanager.addNPC(this);
	}

	/**
	 * NPCの移動
	 *
	 * @param loc 移動先の座標
	 *
	 */
	public void teleport(Location loc)
	{
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
		setValue(packet, "a", entityID);
		setValue(packet, "b", getFixLocation(loc.getX()));
		setValue(packet, "c", getFixLocation(loc.getY()));
		setValue(packet, "d", getFixLocation(loc.getZ()));
		setValue(packet, "e", getFixRotation(loc.getYaw()));
		setValue(packet, "f", getFixRotation(loc.getPitch()));

		sendPacket(packet);
		headRotation(loc.getYaw(), loc.getPitch());
		this.loc = loc;
	}

	/**
	 * 頭ぐーるぐる
	 *
	 * @param yaw ヤウ
	 * @param pitch ピッチ
	 *
	 */
	public void headRotation(float yaw, float pitch)
	{
		PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
		PacketPlayOutEntityHeadRotation packethead = new PacketPlayOutEntityHeadRotation();
		setValue(packethead, "a", entityID);
		setValue(packethead, "b", getFixRotation(yaw));

		sendPacket(packet);
		sendPacket(packethead);
	}

	/**
	 * NPCの消去(別にいないNPC指定してもエラーは吐かないはず)
	 *
	 * @param そのNPCが存在しない場合にfalseを返す
	 */
	public boolean destroy()
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] {entityID});
		removeTabList();
		sendPacket(packet);
		return NPCmanager.removeNPC(this.getName());
	}

	@SuppressWarnings("unchecked")
	public void addTabList()
	{
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
		players.add(data);
		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
	}

	@SuppressWarnings("unchecked")
	public void removeTabList()
	{
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
		players.add(data);
		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
	}

	public int getFixLocation(double pos)
	{
		return (int)MathHelper.floor(pos * 32.0D);
	}

	public int getEntityID()
	{
		return entityID;
	}

	public byte getFixRotation(float yawpitch)
	{
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}

	public String getName()
	{
		return name;
	}

	public Location getLocation()
	{
		return loc;
	}

	//###############################################################

	//NPCのパケットの基礎(完全コピペだけど気にしないで())

	/**
	 * @param obj Packet~~の変数
	 * @param name 指定したパケットのフィールド
	 * @param value 指定したフィールドに入れる新しい値
	 *
	 */
	public void setValue(Object obj, String name, Object value)
	{
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param obj Packet~~の変数
	 * @param name 指定したパケットのフィールド
	 *
	 * @return フィールドの値を返す
	 *
	 */
	public Object getValue(Object obj, String name)
	{
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param player 対象のプレイヤー
	 * @param packer パケット
	 *
	 */
	@SuppressWarnings("rawtypes")
	public static void sendPacket(Player player, net.minecraft.server.v1_8_R3.Packet packet)
	{
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

	/**
	 * @param packet パケット
	 *
	 */
	@SuppressWarnings("rawtypes")
	public void sendPacket(net.minecraft.server.v1_8_R3.Packet packet)
	{
		for(Player player : Bukkit.getOnlinePlayers())
			sendPacket(player, packet);
	}
}
