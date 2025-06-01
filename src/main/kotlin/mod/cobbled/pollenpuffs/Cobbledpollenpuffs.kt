package mod.cobbled.pollenpuffs

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent
import com.cobblemon.mod.common.util.player
import com.cobblemon.mod.common.util.server
import eu.pb4.polymer.core.api.item.PolymerRecipe
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import mod.cobbled.pollenpuffs.effect.ModEffects
import mod.cobbled.pollenpuffs.item.ModItems
import mod.cobbled.pollenpuffs.server.ServerScheduler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.FoodComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RawShapedRecipe
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeEntry
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.collection.DefaultedList
import org.slf4j.LoggerFactory
import java.rmi.registry.Registry
import kotlin.random.Random

object Cobbledpollenpuffs : ModInitializer {
	const val MOD_ID = "cobbled-pollenpuffs"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		LOGGER.info("Initializing Cobbled Pollen Puffs Mod")

		PolymerResourcePackUtils.markAsRequired()
		PolymerResourcePackUtils.addModAssets(MOD_ID)

		//ModItems.registerAll()
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
			val pokemon = event.pokemonEntity
			LOGGER.info("Pokemon entity loaded: ${pokemon.pokemon.species.name}")
			if (pokemon.pokemon.species.name == "Cutiefly" || pokemon.pokemon.species.name == "Ribombee") {
				pokemon.schedulingTracker.addTask(
				pokemon.taskBuilder()
					.interval((20 * (1 + Random.nextInt(3))).toFloat())
					.infiniteIterations()
					.execute { task ->
						if (!pokemon.world.isClient) {
							if (Random.nextFloat() < 0.5f) {
								if ((pokemon.pokemon.species.name == "Ribombee") && pokemon.pokemon.shiny) {
									if (Random.nextFloat() < 0.1f) {
										pokemon.dropStack(ItemStack(ModItems.GOLDENPOLLENPUFF))
									} else {
										pokemon.dropStack(ItemStack(ModItems.POLLENPUFF))
									}
								} else {
									pokemon.dropStack(ItemStack(ModItems.POLLENPUFF))
								}

							}
						}
					}
					.build())
				LOGGER.info("Pollen puff drop task set up for ${pokemon.pokemon.species.name}")
			}
		}

	}
}