@file:Suppress("MemberVisibilityCanBePrivate")

package ch.skyfy.enderbackpack

import ch.skyfy.enderbackpack.client.screen.BackpackScreenHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path


//class EnderBackpack : ModInitializer {
//
//    companion object {
//        const val MOD_ID: String = "ender_backpack"
//        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
//        val LOGGER: Logger = LogManager.getLogger(EnderBackpack::class.java)
//
//        val l = HandledScreens.register(ScreenHandlerType { syncId_, playerInventory ->
//            BackpackScreenHandler(syncId_, playerInventory)
//        }, BackPackProvider())
//
////        val BACKPACK_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple<BackpackScreenHandler?>(net.minecraft.util.Identifier(ch.skyfy.enderbackpack.EnderBackpack.MODID, "backpack_screen"), SimpleClientHandlerFactory<BackpackScreenHandler?> (
////        { syncId:kotlin.Int, playerInventory:PlayerInventory? -> BackpackScreenHandler(syncId, playerInventory) }))
//    }
//
//    override fun onInitialize() {
//        TODO("Not yet implemented")
//    }
//
//}