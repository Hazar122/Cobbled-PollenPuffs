package mod.cobbled.pollenpuffs.server

import mod.cobbled.pollenpuffs.Cobbledpollenpuffs.LOGGER
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer

object ServerScheduler {
    private val tasks = mutableListOf<ScheduledTask>()

    init {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            val iterator = tasks.iterator()
            while (iterator.hasNext()) {

                val task = iterator.next()
                task.ticksRemaining--
                if (task.ticksRemaining <= 0) {
                    task.action(server)
                    iterator.remove()
                }
            }
        }
    }

    fun schedule(delayTicks: Int, action: (MinecraftServer) -> Unit) {
        tasks += ScheduledTask(delayTicks, action)
    }

    private data class ScheduledTask(
        var ticksRemaining: Int,
        val action: (MinecraftServer) -> Unit
    )
}