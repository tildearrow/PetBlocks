package com.github.shynixn.petblocks.api.business.service

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
interface GUIService {
    /**
     * Opens the gui for the given [player]. Does nothing when the GUI is already open.
     * @param P the type of the player.
     */
    fun <P> open(player: P, pageName: String? = null)

    /**
     * Closes the gui for the given [player]. Does nothing when the GUI is already closed.
     * @param P the type of the player.
     */
    fun <P> close(player: P)

    /**
     * Returns if the given [inventory] matches the inventory of this service.
     * @param I the type of the inventory.
     * @param relativeSlot which determines half managed gui inventories.
     */
    fun <I> isGUIInventory(inventory: I, relativeSlot: Int = 0): Boolean

    /**
     * Clears all resources the given player has allocated from this service.
     * @param P the type of the player.
     */
    fun <P> cleanResources(player: P)

    /**
     * Executes actions when the given [player] clicks on an [item] at the given [relativeSlot].
     * @param P the type of the player.
     * @param I the type of the inventory.
     */
    fun <P, I> clickInventoryItem(player: P, relativeSlot: Int, item: I)
}