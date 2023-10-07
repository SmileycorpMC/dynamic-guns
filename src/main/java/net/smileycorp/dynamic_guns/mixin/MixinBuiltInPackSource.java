package net.smileycorp.dynamic_guns.mixin;

import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.resource.PathPackResources;
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
		PackLoader.getLoadedPacks().forEach(pack -> {
			PackInfo info = pack.getPackInfo();
			if (info.isFromMod()) return;
			String name = info.getName();
			PathPackResources resources = new PathPackResources(name, true, info.getPath());
			packConsumer.accept(Pack.readMetaAndCreate(name, Component.literal(name), true,
					(str)->resources, (Object)this instanceof ClientPackSource ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN));
		});
	}

}
