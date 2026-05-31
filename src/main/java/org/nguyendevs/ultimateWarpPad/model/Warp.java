package org.nguyendevs.ultimateWarpPad.model;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Warp {

    public static final UUID ADMIN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private UUID owner;
    private String warpId;
    private String warpName;
    private Location location;
    private CostType costType;
    private double cost;
    private int range;
    private Material icon;
    private List<UUID> trustedPlayers;
    private boolean isPublic;
    private WarpType type;
    private int schematicVariant;

    public Warp() {
        this.trustedPlayers = new ArrayList<>();
        this.costType = CostType.FREE;
        this.cost = -1;
        this.range = -1;
        this.icon = Material.NETHER_STAR;
        this.isPublic = false;
        this.schematicVariant = 0;
    }

    public String getCompositeId() {
        UUID id = (owner == null) ? ADMIN_UUID : owner;
        return id.toString() + "_" + warpId;
    }

    public boolean isAdminWarp() {
        return type == WarpType.ADMIN || owner == null;
    }

    public boolean canPlayerUse(UUID playerUUID) {
        if (type == WarpType.ADMIN) return true;
        if (owner != null && owner.equals(playerUUID)) return true;
        return trustedPlayers.contains(playerUUID);
    }

    public boolean isOwner(UUID playerUUID) {
        return owner != null && owner.equals(playerUUID);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getWarpId() {
        return warpId;
    }

    public void setWarpId(String warpId) {
        this.warpId = warpId;
    }

    public String getWarpName() {
        return warpName;
    }

    public void setWarpName(String warpName) {
        this.warpName = warpName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public World getWorld() {
        return location != null ? location.getWorld() : null;
    }

    public double getX() {
        return location != null ? location.getX() : 0;
    }

    public double getY() {
        return location != null ? location.getY() : 0;
    }

    public double getZ() {
        return location != null ? location.getZ() : 0;
    }

    public CostType getCostType() {
        return costType;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<UUID> getTrustedPlayers() {
        return trustedPlayers;
    }

    public void setTrustedPlayers(List<UUID> trustedPlayers) {
        this.trustedPlayers = trustedPlayers;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public WarpType getType() {
        return type;
    }

    public void setType(WarpType type) {
        this.type = type;
    }

    public int getSchematicVariant() {
        return schematicVariant;
    }

    public void setSchematicVariant(int schematicVariant) {
        this.schematicVariant = schematicVariant;
    }
}
