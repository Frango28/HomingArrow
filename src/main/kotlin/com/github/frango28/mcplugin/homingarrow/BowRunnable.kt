package com.github.frango28.mcplugin.homingarrow

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class BowRunnable(val shooter:Player,val arrow: Arrow,val force:Float):BukkitRunnable() {
    override fun run() {
        if(shooter.location.distance(arrow.location)>Main.arrowLoadDistance) cancel()
        arrow.velocity = shooter.location.direction.multiply(force)
    }

    override fun cancel() {
        super.cancel()
        arrow.remove()
    }
}