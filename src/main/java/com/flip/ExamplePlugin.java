package com.flip;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private GEPriceService gePriceService;
	private Map<Integer, GEPriceService.GEPrice> latestPrices = new HashMap<>();

	@Inject
	private ItemManager itemManager;

	@Inject
	private FlipperOverlay overlay;

	@Inject private OverlayManager overlayManager;

	@Inject
	private ExampleConfig config;

	@Override
	protected void startUp() throws Exception	{
		latestPrices = gePriceService.fetchLatestPrices();
		log.info("Fetched {} prices", latestPrices.size());

		analyzedFlips = analyzer.analyzeFlips(latestPrices, goldStack, itemManager::getItemCompositionName);
		overlayManager.add(overlay);
	}

	public Map<Integer, GEPriceService.GEPrice> getLatestPrices() {
		return latestPrices;
	}

	public List<FlipAnalyzer.FlipOpportunity> getAnalyzedFlips()
	{
		return analyzedFlips;
	}

	private FlipAnalyzer analyzer = new FlipAnalyzer();
	private List<FlipAnalyzer.FlipOpportunity> analyzedFlips = new ArrayList<>();

	public List<Integer> analyzeFlips(int count) {
		return latestPrices.entrySet()
				.stream()
				.sorted((a, b) -> Long.compare(
						(b.getValue().getHigh() - b.getValue().getLow()),
						(a.getValue().getHigh() - a.getValue().getLow())))
				.limit(count)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
