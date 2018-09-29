@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.petblocks.sponge.logic.business.proxy

import com.flowpowered.math.vector.Vector3d
import com.github.shynixn.petblocks.api.business.entity.PetBlock
import com.github.shynixn.petblocks.api.business.proxy.PetProxy
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta
import com.github.shynixn.petblocks.api.persistence.entity.Position
import com.github.shynixn.petblocks.sponge.logic.business.helper.toVector
import org.spongepowered.api.entity.living.ArmorStand
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location

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
class PetProxyImpl(
        /**
         * PetProxy.
         */
        private val petBlock: PetBlock<Any, Any>) : PetProxy {

    /**
     * Gets the meta data.
     */
    override val meta: PetMeta
        get() = petBlock.meta

    /**
     * Sets the entity wearing the pet.
     */
    override fun startWearing() {
        petBlock.wear(petBlock.player)
    }

    /**
     * Stops the current target wearing the pet.
     */
    override fun stopWearing() {
        petBlock.eject(petBlock.player)
    }

    /**
     * Starts riding the pet.zg
     */
    override fun startRiding() {
        petBlock.ride(petBlock.player)
    }

    /**
     * Stops the current target riding the pet.
     */
    override fun stopRiding() {
        petBlock.eject(petBlock.player)
    }

    /**
     * Sets the itemstack on the pet head.
     */
    override fun <I> setHeadItemStack(itemStack: I) {
        if (itemStack !is ItemStack) {
            throw IllegalArgumentException("ItemStack has to be a SpongeItemStack!")
        }

        (petBlock.armorStand as ArmorStand).setHelmet(itemStack)
    }

    /**
     * Gets the itemStack on the pet head.
     */
    override fun <I> getHeadItemStack(): I {
        return ((petBlock.armorStand as ArmorStand).helmet) as I
    }


    /**
     * Teleports the pet to the given [location].
     */
    override fun <L> teleport(location: L) {
        if (location !is Location<*>) {
            throw IllegalArgumentException("Location has to be a SpongeLocation!")
        }

        petBlock.teleport(location)
    }

    /**
     * Gets the location of the pet.
     */
    override fun <L> getLocation(): L {
        return (petBlock.engineEntity as Living).location as L
    }

    /**
     * Removes the pet.
     */
    override fun remove() {
        petBlock.remove()
    }

    /**
     * Sets the velocity of the pet.
     */
    override fun <V> setVelocity(vector: V) {
        if (vector is Position) {
            (petBlock.engineEntity as Living).velocity = vector.toVector()
            return
        }

        if (vector !is Vector3d) {
            throw IllegalArgumentException("Vector has to be a BukkitVector!")
        }

        (petBlock.engineEntity as Living).velocity = vector
    }

    /**
     * Gets the velocity of the pet.
     */
    override fun <V> getVelocity(): V {
        return (petBlock.engineEntity as Living).velocity as V
    }
}