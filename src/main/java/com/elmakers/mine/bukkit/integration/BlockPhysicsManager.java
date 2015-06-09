package com.elmakers.mine.bukkit.integration;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import uk.thinkofdeath.minecraft.physics.PhysicsPlugin;
import uk.thinkofdeath.minecraft.physics.api.PhysicsAPI;
import uk.thinkofdeath.minecraft.physics.api.PhysicsBlock;

public class BlockPhysicsManager {
    final Plugin plugin;
    final PhysicsAPI api;

    public BlockPhysicsManager(Plugin owningPlugin, Plugin physicsPlugin) {
        this.plugin = owningPlugin;
        this.api = ((PhysicsPlugin)physicsPlugin).getAPI(owningPlugin);
    }

    public void spawnPhysicsBlock(Location location, Material material, short data, Vector velocity) {
        PhysicsBlock block = api.spawnBlock(location, new MaterialData(material, (byte)data));
        if (velocity != null) {
            block.applyForce(velocity);
        }
    }
}