package mod.cobbled.pollenpuffs.effect

import mod.cobbled.pollenpuffs.Cobbledpollenpuffs.MOD_ID
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

object ModEffects {
    val PERFECT_NUTRITION = Registry.registerReference(
        Registries.STATUS_EFFECT,
        Identifier.of(MOD_ID,"perfect_nutrition"),
        PerfectNutritionEffect()
    )
    val PERFECT_NUTRITION_ENTRY = Registries.STATUS_EFFECT.entryOf(RegistryKey.of(Registries.STATUS_EFFECT.key, Identifier.of(MOD_ID, "perfect_nutrition")))



    fun registerAll() {
        PERFECT_NUTRITION
        PERFECT_NUTRITION_ENTRY
    }
}