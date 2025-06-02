package mod.cobbled.pollenpuffs.item

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.Item.Settings
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.random.Random

class ArtificialPollenPuffItem(settings: Settings, polymerItem: Item) : SimplePolymerItem(settings, polymerItem) {

    override fun getPolymerItem(itemStack: ItemStack?, player: ServerPlayerEntity?): Item? {
        return super.getPolymerItem(itemStack, player)
    }

    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayerEntity?): Int {
        return ModItems.ArtificialPollenPuffCMD!!.value()

    }

    override fun finishUsing(stack: ItemStack?, world: World?, user: LivingEntity?): ItemStack? {
        if (user != null && !world!!.isClient) {
            val toReduce = listOf(
                StatusEffects.SLOWNESS,
                StatusEffects.LEVITATION,
                StatusEffects.SLOW_FALLING,
                StatusEffects.MINING_FATIGUE,
            )

            for (effect in toReduce) {
                val status = user.getStatusEffect(effect)
                if (status != null) {
                    val newDuration = (status.duration * 0.2).toInt() // cut duration in half
                    if (newDuration > 20) {
                        user.removeStatusEffect(effect)
                        user.addStatusEffect(
                            StatusEffectInstance(effect, newDuration, status.amplifier, status.isAmbient, status.shouldShowParticles())
                        )
                    } else {
                        user.removeStatusEffect(effect)
                    }
                }
            }

            user.heal(if (Random.nextFloat() < 0.2f) 3f else 2.5f)
        }

        return super.finishUsing(stack, world, user)
    }
    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        type: TooltipType
    ) {
        tooltip.add(Text.literal("Restores Health").formatted(Formatting.GRAY))
        tooltip.add(Text.literal("Chance to Apply Perfect Nutrition").formatted(Formatting.GRAY))
        tooltip.add(Text.literal("Gives Regen for 20 seconds").formatted(Formatting.GRAY))
        tooltip.add(Text.literal("Reduces Negative Effects").formatted(Formatting.GRAY))

    }

    override fun getEatSound(): SoundEvent {
        return SoundEvents.ENTITY_BEE_POLLINATE
    }

}