package mod.cobbled.pollenpuffs.item

import eu.pb4.polymer.resourcepack.api.PolymerModelData
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import mod.cobbled.pollenpuffs.Cobbledpollenpuffs.MOD_ID
import mod.cobbled.pollenpuffs.effect.ModEffects
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.component.type.FoodComponent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroup.EntryCollector
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object ModItems {

    var PollenPuffCMD: PolymerModelData? = null
    var GoldenPollenPuffCMD: PolymerModelData? = null
    var ArtificialPollenPuffCMD: PolymerModelData? = null

    var POLLENPUFF =  Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "pollen_puff"),
        PollenPuffItem(
            Item.Settings()
                .food(
                    FoodComponent
                        .Builder()
                        .nutrition(5).saturationModifier(0.6F)
                        .alwaysEdible()
                        .snack()
                        .statusEffect(StatusEffectInstance(ModEffects.PERFECT_NUTRITION_ENTRY, 600, 0, true, true),
                            0.5f
                        )
                        .build()
                ),
            Items.HONEYCOMB
        )
    )
    var GOLDENPOLLENPUFF =  Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "golden_pollen_puff"),
        GoldenPollenPuffItem(
            Item.Settings()
                .food(
                    FoodComponent
                        .Builder()
                        .nutrition(7)
                        .saturationModifier(0.8F)
                        .alwaysEdible()
                        .statusEffect(StatusEffectInstance(ModEffects.PERFECT_NUTRITION_ENTRY, 600, 0, true, false),
                            0.5f
                        )
                        .statusEffect(
                            StatusEffectInstance(StatusEffects.REGENERATION, 400, 1, true, false),
                            1f
                        )
                        .build()
                ),
            Items.HONEYCOMB

        )
    )
    var ARTIFICIALGOLDENPOLLENPUFF =  Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "artificial_golden_pollen_puff"),
        ArtificialPollenPuffItem(
            Item.Settings()
                .food(
                    FoodComponent
                        .Builder()
                        .nutrition(7)
                        .saturationModifier(0.8F)
                        .alwaysEdible()
                        .statusEffect(StatusEffectInstance(ModEffects.PERFECT_NUTRITION_ENTRY, 600, 0, true, false),
                            0.5f
                        )
                        .statusEffect(
                            StatusEffectInstance(StatusEffects.REGENERATION, 400, 1, true, false),
                            1f
                        )
                        .build()
                ),
            Items.HONEYCOMB

        )
    )


    fun getModel() {
        PollenPuffCMD = PolymerResourcePackUtils.requestModel(Items.HONEYCOMB, Identifier.of(MOD_ID, "pollen_puff"))
        GoldenPollenPuffCMD = PolymerResourcePackUtils.requestModel(Items.HONEYCOMB, Identifier.of(MOD_ID, "golden_pollen_puff"))
        ArtificialPollenPuffCMD = PolymerResourcePackUtils.requestModel(Items.HONEYCOMB, Identifier.of(MOD_ID, "artificial_golden_pollen_puff"))
    }


    val POLLENPUFFGROUP: ItemGroup = FabricItemGroup.builder()
        .icon(POLLENPUFF::getDefaultStack)
        .displayName(Text.translatable("pollenpuffs.itemGroup.pollenpuffs"))
        .entries(EntryCollector { _, entries ->
            entries.add(POLLENPUFF)
            entries.add(GOLDENPOLLENPUFF)
            entries.add(ARTIFICIALGOLDENPOLLENPUFF)

        })
        .build()

    fun registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "pollenpuff_group"), POLLENPUFFGROUP)
    }

}