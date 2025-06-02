package mod.cobbled.pollenpuffs

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import mod.cobbled.pollenpuffs.effect.ModEffects
import mod.cobbled.pollenpuffs.item.ModItems
import mod.cobbled.pollenpuffs.server.ServerScheduler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.FoodComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.TypedActionResult
import org.slf4j.LoggerFactory
import kotlin.random.Random

object Cobbledpollenpuffs : ModInitializer {
	const val MOD_ID = "cobbled-pollenpuffs"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		LOGGER.info("Initializing Cobbled Pollen Puffs Mod")

		PolymerResourcePackUtils.markAsRequired()
		PolymerResourcePackUtils.addModAssets(MOD_ID)

		ModItems.getModel()
		ModItems.registerItemGroups()

		ModEffects.registerAll()

		ServerScheduler

		UseItemCallback.EVENT.register { player, world, hand ->
			val stack = player.getStackInHand(hand)
			val food = stack.get(DataComponentTypes.FOOD)
			val nbtComp = stack.get(DataComponentTypes.CUSTOM_DATA)
			val hasEffect = player.hasStatusEffect(ModEffects.PERFECT_NUTRITION_ENTRY)

			if (!world.isClient && food != null) {
				val nbtCopy: NbtCompound = nbtComp?.copyNbt() ?: NbtCompound()
				val isModded = nbtCopy.getBoolean("isFoodMod")

				if (hasEffect && !isModded) {
					val scaledNutrition = (food.nutrition * 2f).toInt().coerceAtLeast(1)
					val scaledSaturation = food.saturation * 2f

					val newFood = FoodComponent(
						scaledNutrition,
						scaledSaturation,
						food.canAlwaysEat,
						food.eatSeconds,
						food.usingConvertsTo,
						food.effects
					)

					stack.set(DataComponentTypes.FOOD, newFood)
					nbtCopy.putBoolean("isFoodMod", true)
					stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCopy))

					return@register TypedActionResult.success(stack, world.isClient)
				} else if (!hasEffect && isModded) {
					val scaledNutrition = (food.nutrition * 0.5f).toInt().coerceAtLeast(1)
					val scaledSaturation = food.saturation * 0.5f

					val revertedFood = FoodComponent(
						scaledNutrition,
						scaledSaturation,
						food.canAlwaysEat,
						food.eatSeconds,
						food.usingConvertsTo,
						food.effects
					)
					stack.set(DataComponentTypes.FOOD, revertedFood)
					nbtCopy.putBoolean("isFoodMod", false)
					stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtCopy))
					return@register TypedActionResult.success(stack, world.isClient)
				}
			}
            return@register TypedActionResult.pass(stack)
		}

		CobblemonEvents.POKEMON_ENTITY_LOAD.subscribe { event: PokemonEntityLoadEvent ->
			dropPuff(event.pokemonEntity)
		}
		CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe { event ->
			dropPuff(event.entity)
		}
		CobblemonEvents.POKEMON_SENT_POST.subscribe{ event ->
			dropPuff(event.pokemonEntity)
		}
	}



	fun dropPuff(pe: PokemonEntity){
		if (pe.pokemon.species.name == "Cutiefly" || pe.pokemon.species.name == "Ribombee") {
			pe.schedulingTracker.addTask(
				pe.taskBuilder()
					.interval((60 * (5 + Random.nextInt(6))).toFloat())
					.infiniteIterations()
					.execute { task ->
						if (!pe.world.isClient) {
							if (Random.nextFloat() < 0.5f) {
								if ((pe.pokemon.species.name == "Ribombee") && pe.pokemon.shiny) {
									if (Random.nextFloat() < 0.1f) {
										pe.dropStack(ItemStack(ModItems.GOLDENPOLLENPUFF))
									} else {
										pe.dropStack(ItemStack(ModItems.POLLENPUFF))
									}
								} else {
									pe.dropStack(ItemStack(ModItems.POLLENPUFF))
								}

							}
						}
					}
					.build())
		}
	}
}