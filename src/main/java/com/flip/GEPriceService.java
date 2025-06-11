package com.flip;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Singleton;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

@Slf4j
@Singleton
public class GEPriceService
{
    private static final String API_URL = "https://prices.runescape.wiki/api/v1/osrs/latest";

    private final Gson gson = new Gson();

    public Map<Integer, GEPrice> fetchLatestPrices()
    {
        try
        {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            MapWrapper wrapper = gson.fromJson(reader, MapWrapper.class);
            reader.close();

            return wrapper.data;
        }
        catch (Exception e)
        {
            log.error("Failed to fetch GE prices: ", e);
            return new HashMap<>();
        }
    }

    @Data
    private static class MapWrapper
    {
        Map<Integer, GEPrice> data;
    }

    @Data
    public static class GEPrice
    {
        private int high;
        private int low;
        private long highTime;
        private long lowTime;
    }
}
