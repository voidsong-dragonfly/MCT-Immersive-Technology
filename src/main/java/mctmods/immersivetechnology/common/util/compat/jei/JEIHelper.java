package mctmods.immersivetechnology.common.util.compat.jei;

import mctmods.immersivetechnology.api.crafting.*;
import mctmods.immersivetechnology.api.crafting.BoilerRecipe.BoilerFuelRecipe;
import mctmods.immersivetechnology.common.Config.ITConfig.Machines.Multiblock;
import mctmods.immersivetechnology.common.util.compat.ITCompatModule;
import mctmods.immersivetechnology.common.util.compat.jei.boiler.BoilerFuelRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.boiler.BoilerRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.coolingtower.CoolingTowerRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.distiller.DistillerRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.electrolyticcrucbilebattery.ElectrolyticCrucibleBatteryRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.gasturbine.GasTurbineRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.heatexchanger.HeatExchangerRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.highpressuresteamturbine.HighPressureSteamTurbineRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.meltingcrucible.MeltingCrucibleRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.radiator.RadiatorRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.solartower.SolarTowerRecipeCategory;
import mctmods.immersivetechnology.common.util.compat.jei.steamturbine.SteamTurbineRecipeCategory;
import mezz.jei.api.*;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@JEIPlugin
public class JEIHelper implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static IModRegistry modRegistry;
	public static IDrawable slotDrawable;
	public static ITooltipCallback<FluidStack> fluidTooltipCallback = new ITFluidTooltipCallback();

	@SuppressWarnings("deprecation")
	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		registry.register(GenericMultiblockIngredient.class, GenericMultiblockIngredient.list, new GenericMultiblockHelper(), new GenericMultiblockRenderer());
	}

	@SuppressWarnings("rawtypes")
	Map<Class, ITRecipeCategory> categories = new LinkedHashMap<>();

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		jeiHelpers = registry.getJeiHelpers();

		//Recipes
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		slotDrawable = guiHelper.getSlotDrawable();

		if(Multiblock.enable_distiller) categories.put(DistillerRecipe.class, new DistillerRecipeCategory(guiHelper));
		if(Multiblock.enable_boiler) {
			categories.put(BoilerRecipe.class, new BoilerRecipeCategory(guiHelper));
			categories.put(BoilerFuelRecipe.class, new BoilerFuelRecipeCategory(guiHelper));
		}
		if(Multiblock.enable_solarTower) categories.put(SolarTowerRecipe.class, new SolarTowerRecipeCategory(guiHelper));
		if(Multiblock.enable_steamTurbine) categories.put(SteamTurbineRecipe.class, new SteamTurbineRecipeCategory(guiHelper));
		if(Multiblock.enable_coolingTower) categories.put(CoolingTowerRecipe.class, new CoolingTowerRecipeCategory(guiHelper));
		if(Multiblock.enable_gasTurbine) categories.put(GasTurbineRecipe.class, new GasTurbineRecipeCategory(guiHelper));
		if(Multiblock.enable_heatExchanger) categories.put(HeatExchangerRecipe.class, new HeatExchangerRecipeCategory(guiHelper));
		if(Multiblock.enable_highPressureSteamTurbine) categories.put(HighPressureSteamTurbineRecipe.class, new HighPressureSteamTurbineRecipeCategory(guiHelper));
		if(Multiblock.enable_electrolyticCrucibleBattery) categories.put(ElectrolyticCrucibleBatteryRecipe.class, new ElectrolyticCrucibleBatteryRecipeCategory(guiHelper));
		if(Multiblock.enable_meltingCrucible || Multiblock.enable_solarMelter) categories.put(MeltingCrucibleRecipe.class, new MeltingCrucibleRecipeCategory(guiHelper));
		if(Multiblock.enable_radiator) categories.put(RadiatorRecipe.class, new RadiatorRecipeCategory(guiHelper));
			
		registry.addRecipeCategories(categories.values().toArray(new IRecipeCategory[categories.size()]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void register(IModRegistry registryIn) {
		modRegistry = registryIn;

		for(ITRecipeCategory<Object, IRecipeWrapper> cat : categories.values()) {
			cat.addCatalysts(registryIn);
			modRegistry.handleRecipes(cat.getRecipeClass(), cat, cat.getRecipeCategoryUid());
		}

		if(Multiblock.enable_distiller) modRegistry.addRecipes(new ArrayList<Object>((DistillerRecipe.recipeList)), "it.distiller");
		if(Multiblock.enable_boiler) {
			modRegistry.addRecipes(new ArrayList<Object>((BoilerRecipe.recipeList)), "it.boiler");
			modRegistry.addRecipes(new ArrayList<Object>((BoilerRecipe.fuelList)), "it.boilerFuel");
		}
		if(Multiblock.enable_solarTower) modRegistry.addRecipes(new ArrayList<Object>((SolarTowerRecipe.recipeList)), "it.solarTower");
		if(Multiblock.enable_steamTurbine) modRegistry.addRecipes(new ArrayList<Object>((SteamTurbineRecipe.recipeList)), "it.steamTurbine");
		if(Multiblock.enable_coolingTower) modRegistry.addRecipes(new ArrayList<Object>((CoolingTowerRecipe.recipeList)), "it.coolingTower");
		if(Multiblock.enable_gasTurbine) modRegistry.addRecipes(new ArrayList<Object>((GasTurbineRecipe.recipeList)), "it.gasTurbine");
		if(Multiblock.enable_heatExchanger) modRegistry.addRecipes(new ArrayList<Object>((HeatExchangerRecipe.recipeList)), "it.heatExchanger");
		if(Multiblock.enable_highPressureSteamTurbine) modRegistry.addRecipes(new ArrayList<Object>((HighPressureSteamTurbineRecipe.recipeList)), "it.highPressureSteamTurbine");
		if(Multiblock.enable_electrolyticCrucibleBattery) modRegistry.addRecipes(new ArrayList<Object>((ElectrolyticCrucibleBatteryRecipe.recipeList)), "it.electrolyticCrucibleBattery");
		if(Multiblock.enable_meltingCrucible || Multiblock.enable_solarMelter) modRegistry.addRecipes(new ArrayList<Object>((MeltingCrucibleRecipe.recipeList)), "it.meltingCrucible");
		if(Multiblock.enable_radiator) modRegistry.addRecipes(new ArrayList<Object>((RadiatorRecipe.recipeList)), "it.radiator");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		final IRecipeRegistry registry = jeiRuntime.getRecipeRegistry();
		ITCompatModule.jeiAddFunc = recipe -> {
			ITRecipeCategory<Object, ?> factory = getFactory(recipe.getClass());
			if(factory != null)	registry.addRecipe(factory.getRecipeWrapper(recipe), factory.getUid());
		};
		ITCompatModule.jeiRemoveFunc = recipe -> {
			ITRecipeCategory<Object, ?> factory = getFactory(recipe.getClass());
			if(factory != null)	registry.removeRecipe(factory.getRecipeWrapper(recipe), factory.getUid());
		};
	}

	@SuppressWarnings("unchecked")
	private ITRecipeCategory<Object, ?> getFactory(Class<?> recipeClass) {
		ITRecipeCategory<Object, ?> factory = this.categories.get(recipeClass);

		if(factory == null && recipeClass != Object.class)
			factory = getFactory(recipeClass.getSuperclass());

		return factory;
	}

}