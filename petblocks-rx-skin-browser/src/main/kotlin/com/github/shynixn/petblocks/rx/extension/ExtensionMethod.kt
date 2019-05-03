@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.petblocks.rx.extension

import com.github.shynixn.petblocks.api.PetBlocksApi
import com.github.shynixn.petblocks.api.business.enumeration.ChatColor
import com.github.shynixn.petblocks.api.business.enumeration.Version
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.util.*

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


/**
 * Changes the displayname of the itemstack.
 * Gets an empty string if the displayName is not present.
 */
var ItemStack.displayName: String
    get() {
        if (this.itemMeta != null && (this.itemMeta!!.displayName as String?) != null) {
            return itemMeta!!.displayName
        }

        return ""
    }
    set(value) {
        val meta = itemMeta
        meta!!.setDisplayName(ChatColor.translateChatColorCodes('&', value))
        itemMeta = meta
    }


/**
 * Gets the skin of an itemstack.
 */
var ItemStack.skin: String?
    get() {
        val currentMeta = this.itemMeta as? SkullMeta ?: return null

        if (!currentMeta.owner.isNullOrEmpty()) {
            return currentMeta.owner
        }

        val cls = Class.forName(
            "org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull".replace(
                "VERSION",
                getServerVersion().bukkitId
            )
        )
        val real = cls.cast(currentMeta)
        val field = real.javaClass.getDeclaredField("profile")
        field.isAccessible = true
        val profile = field.get(real) as GameProfile

        return profile.properties.get("textures").toTypedArray()[0].value
    }
    set(value) {
        val currentMeta = this.itemMeta as? SkullMeta ?: return

        if (value == null) {
            return
        }

        var newSkin = value

        if (newSkin.length > 32) {
            val cls = Class.forName(
                "org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull".replace(
                    "VERSION",
                    getServerVersion().bukkitId
                )
            )
            val real = cls.cast(currentMeta)
            val field = real.javaClass.getDeclaredField("profile")
            val newSkinProfile = GameProfile(UUID.randomUUID(), null)

            if (newSkin.contains("textures.minecraft.net")) {
                if (!newSkin.startsWith("http://")) {
                    newSkin = "http://$newSkin"
                }

                newSkin = Base64Coder.encodeString("{textures:{SKIN:{url:\"$newSkin\"}}}")
            }

            newSkinProfile.properties.put("textures", Property("textures", newSkin))
            field.isAccessible = true
            field.set(real, newSkinProfile)
            itemMeta = SkullMeta::class.java.cast(real)
        } else if (value.isNotEmpty()) {
            currentMeta.owner = value
            itemMeta = currentMeta
        }
    }

/**
 * Gets the server version the plugin is running on.
 */
fun getServerVersion(): Version {
    try {
        if (Bukkit.getServer() == null || Bukkit.getServer().javaClass.getPackage() == null) {
            return Version.VERSION_UNKNOWN
        }

        val version = Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]

        for (versionSupport in Version.values()) {
            if (versionSupport.bukkitId == version) {
                return versionSupport
            }
        }

    } catch (e: Exception) {
    }

    return Version.VERSION_UNKNOWN
}


/**
 * Just for displaying purposes.
 */
class WrapperThread(private val async: Boolean = false) {
    fun toScheduler(): Scheduler {
        if (async) {
            return Schedulers.from { r ->
                val plugin = PetBlocksApi.resolve(Plugin::class.java)
                plugin.server.scheduler.runTaskAsynchronously(plugin, r)
            }
        } else {
            return Schedulers.from { r ->
                val plugin = PetBlocksApi.resolve(Plugin::class.java)
                plugin.server.scheduler.runTask(plugin, r)
            }
        }
    }
}

/**
 * Gets a virtualized reference to the minecraft async thread.
 * This is just a wrapper in order to display the internal useage better.
 */
val AsyncMinecraftThread = WrapperThread(true)

/**
 * Gets a virtualized reference to the minecraft ui thread.
 * This is just a wrapper in order to display the internal useage better.
 */
val UIMinecraftThread = WrapperThread(true)