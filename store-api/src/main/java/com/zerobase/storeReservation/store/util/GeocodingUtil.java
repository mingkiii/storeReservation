package com.zerobase.storeReservation.store.util;

import static com.zerobase.storeReservation.store.exception.ErrorCode.WRONG_ADDRESS;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zerobase.storeReservation.store.exception.CustomException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeocodingUtil {
    private static final String API_URL =
        "http://api.vworld.kr/req/address?service=address&request=getCoord";
    @Value("${api.key}")
    private String apiKey;

    public double[] geoCoding(String location) {
        String epsg = "epsg:4326";
        String SEARCH_TYPE = "parcel";

        StringBuilder sb = new StringBuilder(API_URL);
        sb.append("&format=json");
        sb.append("&crs=").append(epsg);
        sb.append("&key=").append(apiKey);
        sb.append("&type=").append(SEARCH_TYPE);
        sb.append("&address=")
            .append(URLEncoder.encode(location, StandardCharsets.UTF_8));

        try {
            URL url = new URL(sb.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(reader);
            JsonObject jsResponse = (JsonObject) jsonObject.get("response");
            JsonObject jsResult = (JsonObject) jsResponse.get("result");
            JsonObject jsPoint = (JsonObject) jsResult.get("point");

            double[] coords = new double[2];
            coords[0] = jsPoint.get("y").getAsFloat();
            coords[1] = jsPoint.get("x").getAsFloat();

            return coords;
        } catch (IOException e) {
            throw new CustomException(WRONG_ADDRESS);
        }
    }
}
