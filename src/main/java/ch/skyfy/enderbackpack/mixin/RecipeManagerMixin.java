package ch.skyfy.enderbackpack.mixin;

import ch.skyfy.enderbackpack.Configurator;
import ch.skyfy.enderbackpack.EnderBackpack;
import com.google.gson.JsonElement;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "apply", at = @At("HEAD"))
    public void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
        if (!Configurator.getInstance().config.disableCraft)
            map.put(new Identifier("ender_backpack", "backpack"), EnderBackpack.createBackpackRecipe());
    }
}
