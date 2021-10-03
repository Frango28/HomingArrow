package com.github.frango28.mcplugin.homingarrow

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.def.DefaultConfigResource
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.event.events
import com.github.syari.spigot.api.scheduler.runTaskTimer
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Main : JavaPlugin(){
    companion object{
        internal lateinit var plugin: JavaPlugin
        internal lateinit var console: ConsoleCommandSender
        val arrows:MutableMap<Arrow,BukkitRunnable> = mutableMapOf()
        var arrowSpeed:Float = 1.0f
        var arrowLoadDistance:Int = 80
    }

    override fun onEnable() {
        plugin = this
        console = server.consoleSender
        loadConfig(console)
        registerListeners()
    }

    override fun onDisable() {

    }

    private fun loadConfig(sender:CommandSender){
        val c = plugin.config(sender,"config",DefaultConfigResource(plugin,"config.yml"))
        val arrowSpeed:Float = c.get("ArrowSpeed", ConfigDataType.Float,false)?:0.0f
        Main.arrowSpeed = arrowSpeed
        val arrowLoadDistance:Int = c.get("ArrowLoadDistance", ConfigDataType.Int,false)?:80
        Main.arrowLoadDistance = arrowLoadDistance
        sender.sendMessage("${ChatColor.GRAY}[${plugin.name}]${ChatColor.AQUA} 設定を読み込みました。")
    }

    private fun registerListeners(){
        command("homing-arrow"){
            tab {
                argument{
                    addAll("reload")
                }
            }
            execute {
                when(args.lowerOrNull(0)){
                    "reload"->{
                        loadConfig(sender)
                    }
                    else->{
                        sender.sendMessage("${ChatColor.GRAY}[${plugin.name}]${ChatColor.RED} 引数が不正です")
                    }
                }
            }
        }
        events {
            event<EntityShootBowEvent> { e ->
                val player = (e.entity as? Player)?:return@event
                val arrow = (e.projectile as? Arrow)?:return@event
                arrow.isGlowing = true
                arrows[arrow] = BowRunnable(player,arrow,e.force).apply {
                    runTaskTimer(plugin,1,1)
                }
            }
            event<ProjectileHitEvent> {e->
                val arrow = (e.entity as? Arrow)?:return@event
                arrows.remove(arrow)?.cancel()
            }
        }
    }
}
