package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {

	private Identifier currentDimension = null;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
			client.execute(() -> {
				ClientPlayerEntity player = client.player;
				assert player != null;
                String nomDuJoueur = player.getName().getString();
				client.inGameHud.getChatHud().addMessage(Text.literal("Bienvenue dans le monde, " + nomDuJoueur + " !"));

				if (client.world != null) {
					Identifier dimensionId = client.world.getRegistryKey().getValue();
					String nomDimension = dimensionId.getPath(); // ex : overworld, the_nether, the_end

					client.inGameHud.getChatHud().addMessage(
							Text.literal("Vous êtes entré dans la dimension : " + nomDimension)
					);
				}
			})
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.world != null && client.player != null) {
				Identifier newDim = client.world.getRegistryKey().getValue();

				if (currentDimension == null) {
					currentDimension = newDim;
				}
				if (currentDimension == null || !currentDimension.equals(newDim)) {
					currentDimension = newDim;

					// Joue un son
					client.player.playSound(SoundEvents.ENTITY_PARROT_IMITATE_CREEPER, 2.0F, 1.0F);

					client.inGameHud.getChatHud().addMessage(
							Text.literal("Changement de dimension : " + newDim.getPath())
					);
				}
			}
		});
	}
}