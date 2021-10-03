import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.jmailen.kotlinter") version "3.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("dev.s7a.gradle.minecraft.server") version "1.0.1"
}

version = "1.0.0"

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

val shadowImplementation: Configuration by configurations.creating
configurations["implementation"].extendsFrom(shadowImplementation)

dependencies {
    shadowImplementation(kotlin("stdlib"))
    implementation("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    shadowImplementation("com.github.sya-ri:EasySpigotAPI:2.4.0") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
}

tasks.withType<ShadowJar> {
    configurations = listOf(shadowImplementation)
    archiveClassifier.set("")
}

configure<BukkitPluginDescription> {
    name = project.name
    version = project.version.toString()
    main = "com.github.frango28.mcplugin.homingarrow.Main"
    author= "Frango28"
}

task<dev.s7a.gradle.minecraft.server.tasks.LaunchMinecraftServerTask>("buildAndLaunchServer") {
    dependsOn("shadowJar") // ビルドタスク (build, jar, shadowJar, ...)
    doFirst {
        copy {
            from(buildDir.resolve("libs/${project.name}-${project.version}.jar")) // build/libs/example.jar
            into(buildDir.resolve("MinecraftPaperServer/plugins")) // build/MinecraftPaperServer/plugins
        }
    }
    jvmArgument.addAll("-Xms6G", "-Xmx6G")
    jarUrl.set("https://papermc.io/api/v1/paper/1.17.1/latest/download")
    jarName.set("server.jar")
    serverDirectory.set(buildDir.resolve("MinecraftPaperServer")) // build/MinecraftPaperServer
    nogui.set(false)
}
