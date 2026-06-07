package org.nguyendevs.ultimateWarpPad.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.nguyendevs.ultimateWarpPad.model.CostType;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private Connection connection;
    private final ExecutorService dbExecutor;
    private final Map<String, World> worldCache = new HashMap<>();

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dbExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "UWP-DB-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    public void buildWorldCache() {
        worldCache.clear();
        for (World world : Bukkit.getWorlds()) {
            worldCache.put(world.getName(), world);
        }
    }

    public CompletableFuture<Void> init() {
        return CompletableFuture.runAsync(() -> {
            try {
                connect();
                createTables();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
                e.printStackTrace();
            }
        }, dbExecutor);
    }

    private void connect() throws SQLException {
        String file = plugin.getConfig().getString("database.file", "warps");
        String dbPath = plugin.getDataFolder().getAbsolutePath() + "/" + file;
        String user = plugin.getConfig().getString("database.username", "sa");
        String pass = plugin.getConfig().getString("database.password", "");

        connection = DriverManager.getConnection("jdbc:h2:file:" + dbPath + ";DB_CLOSE_DELAY=-1", user, pass);
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS warps (" +
                    "composite_id VARCHAR(100) PRIMARY KEY, " +
                    "owner_uuid VARCHAR(36) NOT NULL, " +
                    "warp_id VARCHAR(50) NOT NULL, " +
                    "warp_name VARCHAR(100) NOT NULL, " +
                    "world VARCHAR(50) NOT NULL, " +
                    "x DOUBLE NOT NULL, " +
                    "y DOUBLE NOT NULL, " +
                    "z DOUBLE NOT NULL, " +
                    "cost_type VARCHAR(10) NOT NULL DEFAULT 'XP', " +
                    "cost DOUBLE NOT NULL DEFAULT -1, " +
                    "range_val INT NOT NULL DEFAULT -1, " +
                    "icon VARCHAR(50) NOT NULL DEFAULT 'NETHER_STAR', " +
                    "is_public TINYINT(1) NOT NULL DEFAULT 0, " +
                    "warp_type VARCHAR(10) NOT NULL DEFAULT 'PLAYER', " +
                    "schematic_variant INT NOT NULL DEFAULT 0" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS warp_trusted (" +
                    "composite_id VARCHAR(100) NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "PRIMARY KEY (composite_id, player_uuid)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS warp_terrain (" +
                    "composite_id VARCHAR(100) NOT NULL, " +
                    "idx INT NOT NULL, " +
                    "block_data TEXT NOT NULL, " +
                    "PRIMARY KEY (composite_id, idx)" +
                    ")");
        }
    }

    public CompletableFuture<List<Warp>> loadAllWarps() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Warp> warpMap = new LinkedHashMap<>();
            String sql = "SELECT w.*, t.player_uuid FROM warps w LEFT JOIN warp_trusted t ON w.composite_id = t.composite_id ORDER BY w.composite_id";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String cid = rs.getString("composite_id");
                    Warp warp = warpMap.get(cid);

                    if (warp == null) {
                        try {
                            warp = resultSetToWarp(rs);
                            if (warp != null) {
                                warpMap.put(cid, warp);
                            }
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to load warp: " + e.getMessage());
                        }
                    }

                    String trustedUuid = rs.getString("player_uuid");
                    if (trustedUuid != null && warp != null) {
                        warp.getTrustedPlayers().add(UUID.fromString(trustedUuid));
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load warps: " + e.getMessage());
                e.printStackTrace();
            }
            return new ArrayList<>(warpMap.values());
        }, dbExecutor);
    }

    private Warp resultSetToWarp(ResultSet rs) throws SQLException {
        String ownerStr = rs.getString("owner_uuid");
        UUID owner = Warp.ADMIN_UUID.toString().equals(ownerStr) ? null : UUID.fromString(ownerStr);
        String warpId = rs.getString("warp_id");
        String worldName = rs.getString("world");
        World world = worldCache.get(worldName);
        if (world == null) return null;

        Warp warp = new Warp();
        warp.setOwner(owner);
        warp.setWarpId(warpId);
        warp.setWarpName(rs.getString("warp_name"));
        warp.setLocation(new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));
        warp.setCostType(CostType.valueOf(rs.getString("cost_type")));
        warp.setCost(rs.getDouble("cost"));
        warp.setRange(rs.getInt("range_val"));
        warp.setIcon(Material.matchMaterial(rs.getString("icon")));
        if (warp.getIcon() == null) warp.setIcon(Material.NETHER_STAR);
        warp.setPublic(rs.getBoolean("is_public"));
        warp.setType(WarpType.valueOf(rs.getString("warp_type")));
        warp.setSchematicVariant(rs.getInt("schematic_variant"));
        return warp;
    }

    public CompletableFuture<Void> saveWarp(Warp warp) {
        return CompletableFuture.runAsync(() -> {
            try {
                String sql = "REPLACE INTO warps " +
                        "(composite_id, owner_uuid, warp_id, warp_name, world, x, y, z, " +
                        "cost_type, cost, range_val, icon, is_public, warp_type, schematic_variant) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    String ownerStr = (warp.getOwner() == null) ? Warp.ADMIN_UUID.toString() : warp.getOwner().toString();

                    ps.setString(1, warp.getCompositeId());
                    ps.setString(2, ownerStr);
                    ps.setString(3, warp.getWarpId());
                    ps.setString(4, warp.getWarpName());
                    ps.setString(5, warp.getWorld().getName());
                    ps.setDouble(6, warp.getX());
                    ps.setDouble(7, warp.getY());
                    ps.setDouble(8, warp.getZ());
                    ps.setString(9, warp.getCostType().name());
                    ps.setDouble(10, warp.getCost());
                    ps.setInt(11, warp.getRange());
                    ps.setString(12, warp.getIcon().name());
                    ps.setBoolean(13, warp.isPublic());
                    ps.setString(14, warp.getType().name());
                    ps.setInt(15, warp.getSchematicVariant());
                    ps.executeUpdate();
                }

                saveTrustedPlayers(warp);
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save warp: " + e.getMessage());
                e.printStackTrace();
            }
        }, dbExecutor);
    }

    private void saveTrustedPlayers(Warp warp) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String deleteSql = "DELETE FROM warp_trusted WHERE composite_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setString(1, warp.getCompositeId());
                ps.executeUpdate();
            }

            if (!warp.getTrustedPlayers().isEmpty()) {
                String insertSql = "INSERT INTO warp_trusted (composite_id, player_uuid) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    for (UUID uuid : warp.getTrustedPlayers()) {
                        ps.setString(1, warp.getCompositeId());
                        ps.setString(2, uuid.toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public CompletableFuture<Void> deleteWarp(Warp warp) {
        return CompletableFuture.runAsync(() -> {
            try {
                String deleteTrusted = "DELETE FROM warp_trusted WHERE composite_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(deleteTrusted)) {
                    ps.setString(1, warp.getCompositeId());
                    ps.executeUpdate();
                }

                String deleteWarp = "DELETE FROM warps WHERE composite_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(deleteWarp)) {
                    ps.setString(1, warp.getCompositeId());
                    ps.executeUpdate();
                }

                String deleteTerrain = "DELETE FROM warp_terrain WHERE composite_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(deleteTerrain)) {
                    ps.setString(1, warp.getCompositeId());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to delete warp: " + e.getMessage());
                e.printStackTrace();
            }
        }, dbExecutor);
    }

    public CompletableFuture<Void> saveTerrainSnapshot(String compositeId, List<String> blockDataStrings) {
        return CompletableFuture.runAsync(() -> {
            try {
                String deleteSql = "DELETE FROM warp_terrain WHERE composite_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                    ps.setString(1, compositeId);
                    ps.executeUpdate();
                }

                String insertSql = "INSERT INTO warp_terrain (composite_id, idx, block_data) VALUES (?, ?, ?)";
                connection.setAutoCommit(false);
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    for (int i = 0; i < blockDataStrings.size(); i++) {
                        ps.setString(1, compositeId);
                        ps.setInt(2, i);
                        ps.setString(3, blockDataStrings.get(i));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                try { connection.rollback(); } catch (SQLException ignored) {}
                plugin.getLogger().severe("Failed to save terrain snapshot: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
            }
        }, dbExecutor);
    }

    public CompletableFuture<List<String>> loadTerrainSnapshot(String compositeId) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> result = new ArrayList<>();
            String sql = "SELECT block_data FROM warp_terrain WHERE composite_id = ? ORDER BY idx ASC";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, compositeId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getString("block_data"));
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load terrain snapshot: " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }, dbExecutor);
    }

    public void close() {
        dbExecutor.shutdown();
        try {
            if (!dbExecutor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                dbExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            dbExecutor.shutdownNow();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("SHUTDOWN COMPACT");
                } catch (SQLException e) {
                    plugin.getLogger().warning("Failed to compact database: " + e.getMessage());
                }
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to close database: " + e.getMessage());
        }
    }
}
