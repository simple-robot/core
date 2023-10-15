/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.timestamp

import love.forte.simbot.utils.TimeUnit
import java.time.Instant


/**
 * 基于 [Instant] 的 [Timestamp] 实现。
 *
 * @property instant [Instant]
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class InstantTimestamp(public val instant: Instant) : Timestamp {
    override val milliseconds: Long
        get() = instant.toEpochMilli()

    override fun timeAs(unit: TimeUnit): Long {
        return when (unit) {
            TimeUnit.MILLISECONDS -> milliseconds
            TimeUnit.SECONDS -> instant.epochSecond
            else -> unit.convert(milliseconds, TimeUnit.MILLISECONDS)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Timestamp) return false
        if (other is InstantTimestamp) return instant == other.instant

        return milliseconds == other.milliseconds
    }

    override fun hashCode(): Int = instant.hashCode()
    override fun toString(): String = "InstantTimestamp(milliseconds=$milliseconds, instant=$instant)"
}
