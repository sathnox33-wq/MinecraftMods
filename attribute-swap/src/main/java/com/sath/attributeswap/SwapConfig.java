package com.sath.attributeswap;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Settings for the swap, saved to config/attributeswap.properties.
 * Slots are hotbar slots numbered 1-9, the same as the number keys.
 */
public final class SwapConfig {
	private static final SwapConfig INSTANCE = new SwapConfig();

	/** Master switch, flipped by the toggle key. */
	public boolean enabled = true;
	/** Hotbar slot (1-9) that holds the spear. */
	public int spearSlot = 1;
	/** Hotbar slot (1-9) that holds the other item, such as a mace. */
	public int otherSlot = 2;
	/** Holding the spear and hitting an entity lands the hit with the other item. */
	public boolean maceSwap = true;
	/** Holding the other item and clicking does a spear jab (Lunge) and swaps straight back. */
	public boolean lungeSwap = true;

	private SwapConfig() {
	}

	public static SwapConfig get() {
		return INSTANCE;
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("attributeswap.properties");
	}

	public void load() {
		Path file = path();
		if (!Files.exists(file)) {
			save();
			return;
		}

		Properties props = new Properties();
		try (Reader reader = Files.newBufferedReader(file)) {
			props.load(reader);
		} catch (IOException e) {
			AttributeSwapClient.LOGGER.warn("Could not read {}, using defaults", file, e);
			return;
		}

		enabled = Boolean.parseBoolean(props.getProperty("enabled", String.valueOf(enabled)));
		spearSlot = parseSlot(props.getProperty("spearSlot"), spearSlot);
		otherSlot = parseSlot(props.getProperty("otherSlot"), otherSlot);
		maceSwap = Boolean.parseBoolean(props.getProperty("maceSwap", String.valueOf(maceSwap)));
		lungeSwap = Boolean.parseBoolean(props.getProperty("lungeSwap", String.valueOf(lungeSwap)));
	}

	public void save() {
		Properties props = new Properties();
		props.setProperty("enabled", String.valueOf(enabled));
		props.setProperty("spearSlot", String.valueOf(spearSlot));
		props.setProperty("otherSlot", String.valueOf(otherSlot));
		props.setProperty("maceSwap", String.valueOf(maceSwap));
		props.setProperty("lungeSwap", String.valueOf(lungeSwap));

		Path file = path();
		try {
			Files.createDirectories(file.getParent());
			try (Writer writer = Files.newBufferedWriter(file)) {
				props.store(writer, "Attribute Swap settings. Slots are hotbar slots 1-9.");
			}
		} catch (IOException e) {
			AttributeSwapClient.LOGGER.warn("Could not save {}", file, e);
		}
	}

	private static int parseSlot(String value, int fallback) {
		if (value == null) {
			return fallback;
		}
		try {
			int slot = Integer.parseInt(value.trim());
			return slot >= 1 && slot <= 9 ? slot : fallback;
		} catch (NumberFormatException e) {
			return fallback;
		}
	}
}
