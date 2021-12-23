package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.commands.CmdBackpack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class SingleBackpack implements ModInitializer {

    @Override
    public void onInitialize() {
        registerCommand();
    }

    public void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> CmdBackpack.register(dispatcher));
    }
}
