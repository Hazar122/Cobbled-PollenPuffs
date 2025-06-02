package mod.cobbled.pollenpuffs.effect

import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import mod.cobbled.pollenpuffs.Cobbledpollenpuffs
import mod.cobbled.pollenpuffs.effect.ModEffects.PERFECT_NUTRITION_ENTRY
import mod.cobbled.pollenpuffs.item.ModItems
import mod.cobbled.pollenpuffs.server.ServerScheduler
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class PerfectNutritionEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 0xFFD580), PolymerStatusEffect {


    override fun getPolymerReplacement(player: ServerPlayerEntity?): StatusEffect? {
        return null
    }

    override fun onApplied(entity: LivingEntity?, amplifier: Int) {
        if (entity is ServerPlayerEntity) {
            val duration = entity.activeStatusEffects[PERFECT_NUTRITION_ENTRY]?.duration ?: return
            entity.sendMessage(Text.of("🌼 You feel the perfect nutrition for ${duration / 20} seconds!"), false)
            val playerUUID = entity.uuid
            ServerScheduler.schedule(duration) { server ->
                val player = server.playerManager.getPlayer(playerUUID)
                if (player != null) {
                    player.sendMessage(Text.of("🛑 The perfect nutrition fades away..."), false)
                } else {
                    Cobbledpollenpuffs.LOGGER.warn("Message could not be sent to player with UUID $playerUUID because they are not online.")
                }
            }
        }
    }

    override fun getPolymerIcon(player: ServerPlayerEntity?): ItemStack? {
        return ItemStack(ModItems.POLLENPUFF)
    }


}