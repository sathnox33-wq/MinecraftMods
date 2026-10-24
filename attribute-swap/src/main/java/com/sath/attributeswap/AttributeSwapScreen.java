package com.sath.attributeswap;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Settings menu, opened with the menu key (O) or from Mod Menu. */
public class AttributeSwapScreen extends Screen {
	private static final int BUTTON_WIDTH = 240;
	private static final int ROW = 24;
	private static final int WHITE = 0xFFFFFFFF;
	private static final int GRAY = 0xFFA0A0A0;

	private final Screen parent;
	private int top;

	private ButtonWidget enabledButton;
	private ButtonWidget spearButton;
	private ButtonWidget otherButton;
	private ButtonWidget maceButton;
	private ButtonWidget lungeButton;

	public AttributeSwapScreen(Screen parent) {
		super(Text.literal("Attribute Swap"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		SwapConfig config = SwapConfig.get();
		int x = this.width / 2 - BUTTON_WIDTH / 2;
		top = this.height / 2 - 80;

		enabledButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			config.enabled = !config.enabled;
			updateLabels();
		}).dimensions(x, top, BUTTON_WIDTH, 20).build());

		spearButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			config.spearSlot = nextSlot(config.spearSlot, config.otherSlot);
			updateLabels();
		}).dimensions(x, top + ROW, BUTTON_WIDTH, 20).build());

		otherButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			config.otherSlot = nextSlot(config.otherSlot, config.spearSlot);
			updateLabels();
		}).dimensions(x, top + ROW * 2, BUTTON_WIDTH, 20).build());

		maceButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			config.maceSwap = !config.maceSwap;
			updateLabels();
		}).dimensions(x, top + ROW * 3, BUTTON_WIDTH, 20).build());

		lungeButton = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
			config.lungeSwap = !config.lungeSwap;
			updateLabels();
		}).dimensions(x, top + ROW * 4, BUTTON_WIDTH, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
				.dimensions(x, top + ROW * 6 + 8, BUTTON_WIDTH, 20).build());

		updateLabels();
	}

	/** Cycles 1-9, skipping the slot the other setting already uses. */
	private static int nextSlot(int slot, int taken) {
		int next = slot % 9 + 1;
		return next == taken ? next % 9 + 1 : next;
	}

	private void updateLabels() {
		SwapConfig config = SwapConfig.get();
		enabledButton.setMessage(Text.literal("Swap: " + onOff(config.enabled)));
		spearButton.setMessage(Text.literal("Spear slot: " + config.spearSlot + slotItem(config.spearSlot)));
		otherButton.setMessage(Text.literal("Other item slot: " + config.otherSlot + slotItem(config.otherSlot)));
		maceButton.setMessage(Text.literal("Mace swap (hold spear, hit): " + onOff(config.maceSwap)));
		lungeButton.setMessage(Text.literal("Lunge swap (hold other, click): " + onOff(config.lungeSwap)));
	}

	private String slotItem(int slot) {
		if (this.client == null || this.client.player == null) {
			return "";
		}
		ItemStack stack = this.client.player.getInventory().getStack(slot - 1);
		return stack.isEmpty() ? " (empty)" : " (" + stack.getName().getString() + ")";
	}

	private static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, top - 30, WHITE);
		context.drawCenteredTextWithShadow(this.textRenderer,
				Text.literal("Click a slot button to cycle it through 1-9"), this.width / 2, top - 16, GRAY);
		context.drawCenteredTextWithShadow(this.textRenderer,
				Text.literal("Toggle key: J   Menu key: O   (change in Controls)"), this.width / 2, top + ROW * 5 + 6, GRAY);
	}

	@Override
	public void close() {
		SwapConfig.get().save();
		if (this.client != null) {
			this.client.setScreen(parent);
		}
	}
}
