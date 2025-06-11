package com.flip;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class FlipperOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final ItemManager itemManager;
    private final ExamplePlugin plugin;

    @Inject
    public FlipperOverlay(Client client, ItemManager itemManager, ExamplePlugin plugin) {
        this.client = client;
        this.itemManager = itemManager;
        this.plugin = plugin;

        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        List<FlipAnalyzer.FlipOpportunity> flips = plugin.getAnalyzedFlips();
        if (flips == null || flips.isEmpty()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Auto Flipper")
                    .right("No flips")
                    .build());
            return panelComponent.render(graphics);
        }

        for (int i = 0; i < Math.min(5, flips.size()); i++) {
            FlipAnalyzer.FlipOpportunity f = flips.get(i);
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(f.getName())
                    .right("↑" + f.getMargin() + " x" + f.getQuantityToBuy())
                    .build());
        }

        return panelComponent.render(graphics);
    }
}
