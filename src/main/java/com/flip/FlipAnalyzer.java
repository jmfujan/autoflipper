package com.flip;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FlipAnalyzer
{
    public List<FlipOpportunity> analyzeFlips(
            Map<Integer, GEPriceService.GEPrice> priceMap,
            int goldAvailable,
            ItemNameResolver nameResolver)
    {
        List<FlipOpportunity> flips = new ArrayList<>();

        for (Map.Entry<Integer, GEPriceService.GEPrice> entry : priceMap.entrySet())
        {
            int itemId = entry.getKey();
            GEPriceService.GEPrice price = entry.getValue();

            int high = price.getHigh();
            int low = price.getLow();
            int margin = high - low;

            if (margin <= 0 || low <= 0 || high <= 0)
                continue;

            int volume = 10000; // placeholder, can be replaced with real volume data
            int maxAffordableQty = Math.min(volume, goldAvailable / low);
            long totalProfit = (long) margin * maxAffordableQty;
            double roi = (margin / (double) low) * 100;

            flips.add(new FlipOpportunity(
                    itemId,
                    nameResolver.getName(itemId),
                    margin,
                    high,
                    low,
                    volume,
                    maxAffordableQty,
                    totalProfit,
                    roi
            ));
        }

        // Sort by totalProfit descending
        flips.sort(Comparator.comparingLong(FlipOpportunity::getTotalProfit).reversed());
        return flips;
    }

    public interface ItemNameResolver {
        String getName(int itemId);
    }

    @Data
    @AllArgsConstructor
    public static class FlipOpportunity {
        private int itemId;
        private String name;
        private int margin;
        private int highPrice;
        private int lowPrice;
        private int volumeEstimate;
        private int quantityToBuy;
        private long totalProfit;
        private double roiPercent;
    }
}
