@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.petblocks.core.logic.business.extension

import com.github.shynixn.petblocks.api.business.command.PlayerCommand
import com.github.shynixn.petblocks.api.business.enumeration.ChatColor
import com.github.shynixn.petblocks.api.business.service.ConcurrencyService
import com.github.shynixn.petblocks.api.persistence.entity.ChatMessage
import com.github.shynixn.petblocks.core.logic.persistence.entity.ChatMessageEntity

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
 * Creates a new chat message.
 */
fun chatMessage(f: ChatMessage.() -> Unit): ChatMessage {
    val chatMessage = ChatMessageEntity()
    f.invoke(chatMessage)
    return chatMessage
}

/**
 * Merges the args after the first parameter.
 *
 * @param args args
 * @return merged.
 */
fun PlayerCommand.mergeArgs(args: Array<out String>): String {
    val builder = StringBuilder()
    for (i in 1 until args.size) {
        if (builder.isNotEmpty()) {
            builder.append(' ')
        }

        builder.append(args[i])
    }
    return builder.toString()
}

/**
 * Copies the properties from the given [source] into this target property. Both objects have to be the
 * same type or interface type.
 */
fun <T : Any> T.copyPropertiesFrom(source: T) {
    source::class.java.declaredFields.forEach { sourceField ->
        sourceField.isAccessible = true
        val sourceValue = sourceField.get(source)

        if (sourceValue is Int || sourceValue is Double || sourceValue is String || sourceValue is Enum<*>) {
            val targetField = this::class.java.getDeclaredField(sourceField.name)
            targetField.isAccessible = true
            targetField.set(this, sourceValue)
        } else {
            throw RuntimeException("Cannot copy properties from $source")
        }
    }
}

/**
 * Executes the given [f] via the [concurrencyService] synchronized with the server tick.
 */
inline fun Any.sync(concurrencyService: ConcurrencyService, delayTicks: Long = 0L, repeatingTicks: Long = 0L, crossinline f: () -> Unit) {
    concurrencyService.runTaskSync(delayTicks, repeatingTicks, Runnable { f.invoke() })
}

/**
 * Executes the given [f] via the [concurrencyService] asynchronous.
 */
inline fun Any.async(concurrencyService: ConcurrencyService, delayTicks: Long = 0L, repeatingTicks: Long = 0L, crossinline f: () -> Unit) {
    concurrencyService.runTaskAsync(delayTicks, repeatingTicks, Runnable { f.invoke() })
}

/**
 * Translates the given chatColor.
 */
fun String.translateChatColors(): String {
    return ChatColor.translateChatColorCodes('&', this)
}