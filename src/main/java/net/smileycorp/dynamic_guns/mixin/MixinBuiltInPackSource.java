package net.smileycorp.dynamic_guns.mixin;

import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.resource.PathPackResources;
import net.smileycorp.dynamic_guns.DynamicGunsLogger;
import net.smileycorp.dynamic_guns.data.PackInfo;
import net.smileycorp.dynamic_guns.data.PackLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BuiltInPackSource.class)
public class MixinBuiltInPackSource {

	@Inject(at = @At("TAIL"), method = "loadPacks", cancellable = true)
	private void loadPacks(Consumer<Pack> packConsumer, CallbackInfo callback) {
		boolean clientSide = (Object)this instanceof ClientPackSource;
		PackLoader.getLoadedPacks().forEach(pack -> {
			try {
				DynamicGunsLogger.logInfo("Trying to load " + (clientSide ? "resource" : "data") + " pack for gunpack " + pack.getPackInfo().getName());
				PackInfo info = pack.getPackInfo();
				if (info.isMod()) return;
				String name = info.getName();
				PackResources resources = info.isArchive() ? new FilePackResources(name, info.getPath().toFile(), true)
						: new PathPackResources(name, true, info.getPath());
				packConsumer.accept(Pack.readMetaAndCreate(name, Component.literal(name), true,
						(str)->resources, clientSide ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN));
				DynamicGunsLogger.logInfo("Loaded" + (clientSide ? "resource" : "data") + " pack for gunpack " + pack.getPackInfo().getName());
			} catch (Exception e) {
				DynamicGunsLogger.logError("Failed loading " + (clientSide ? "resource" : "data") + " pack for gunpack " + pack.getPackInfo().getName(), e);
			}
		});
	}

}
