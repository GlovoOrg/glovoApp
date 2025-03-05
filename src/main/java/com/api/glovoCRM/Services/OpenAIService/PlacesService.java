package com.api.glovoCRM.Services.OpenAIService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PlacesService {

    private final OkHttpClient client = new OkHttpClient();

    @Value("${places.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    public String getNearbyRestaurants(double latitude, double longitude) {
        String url = BASE_URL + "?location=" + latitude + "," + longitude +
                "&radius=5000&keyword=restaurant&key=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                JSONArray results = json.optJSONArray("results");

                if (results == null || results.isEmpty()) {
                    return "❌ Не удалось найти рестораны поблизости.";
                }

                StringBuilder restaurantsList = new StringBuilder("🍽 Советуем посетить:\n");
                for (int i = 0; i < Math.min(3, results.length()); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String name = place.optString("name", "Неизвестное место");
                    double rating = place.optDouble("rating", 0.0);
                    String placeId = place.optString("place_id", "");

                    String review = placeId.isEmpty() ? null : getPlaceReview(placeId);

                    restaurantsList.append("🔹 *").append(name).append("*\n")
                            .append("⭐ Рейтинг: ").append(rating).append("\n")
                            .append(review != null ? "💬 Отзыв: " + review + "\n\n" : "\n");
                }
                return restaurantsList.toString();
            }
        } catch (IOException e) {
            log.error("Ошибка при запросе ресторанов", e);
        }
        return "⚠ Ошибка при получении данных о ресторанах.";
    }

    public String getNearByMarkets(double latitude, double longitude) {
        String url = BASE_URL + "?location=" + latitude + "," + longitude +
                "&radius=5000&keyword=supermarket&key=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                JSONArray results = json.optJSONArray("results");

                if (results == null || results.isEmpty()) {
                    return "❌ Не удалось найти маркеты поблизости.";
                }

                StringBuilder marketsList = new StringBuilder("🏪 Ближайшие маркеты:\n");
                for (int i = 0; i < Math.min(3, results.length()); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String name = place.optString("name", "Неизвестное место");
                    double rating = place.optDouble("rating", 0.0);
                    String placeId = place.optString("place_id", "");

                    String review = placeId.isEmpty() ? null : getPlaceReview(placeId);

                    marketsList.append("🔹 *").append(name).append("*\n")
                            .append("⭐ Рейтинг: ").append(rating).append("\n")
                            .append(review != null ? "💬 Отзыв: " + review + "\n\n" : "\n");
                }
                return marketsList.toString();
            }
        } catch (IOException e) {
            log.error("Ошибка при запросе супермаркетов", e);
        }
        return "⚠ Ошибка при получении данных о маркетах.";
    }

    public String getNearByFastFood(double userLatitude, double userLongitude) {
        String url = BASE_URL + "?location=" + userLatitude + "," + userLongitude +
                "&radius=5000&keyword=fast_food&key=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                JSONArray results = json.optJSONArray("results");

                if (results == null || results.isEmpty()) {
                    return "❌ Не удалось найти сети быстрых питании поблизости.";
                }

                StringBuilder fastFoodList = new StringBuilder("🍔 Заведение быстрого питание:\n");
                for (int i = 0; i < Math.min(3, results.length()); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String name = place.optString("name", "Неизвестное место");
                    double rating = place.optDouble("rating", 0.0);
                    String placeId = place.optString("place_id", "");

                    String review = placeId.isEmpty() ? null : getPlaceReview(placeId);

                    fastFoodList.append("🔹 *").append(name).append("*\n")
                            .append("⭐ Рейтинг: ").append(rating).append("\n")
                            .append(review != null ? "💬 Отзыв: " + review + "\n\n" : "\n");
                }
                return fastFoodList.toString();
            }
        } catch (IOException e) {
            log.error("Ошибка при запросе фастфуда", e);
        }
        return "⚠ Ошибка при получении данных о фаст-фуд.";
    }

    private String getPlaceReview(String placeId) {
        String url = DETAILS_URL + "?place_id=" + placeId + "&fields=reviews&language=ru&key=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                JSONObject json = new JSONObject(response.body().string());
                JSONObject result = json.optJSONObject("result");

                if (result != null) {
                    JSONArray reviews = result.optJSONArray("reviews");
                    if (reviews != null && !reviews.isEmpty()) {
                        String review = reviews.getJSONObject(0).optString("text", "");
                        return review.length() > 60 ? review.substring(0, 60) + "..." : review;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при получении отзыва", e);
        }
        return "Нет отзывов.";
    }

    public List<String> callPlacesAPI(double latitude, double longitude) {
        // Логика вызова Google Places API
        return new ArrayList<>(); // Временно возвращаем пустой список
    }

    public List<String> getNearbyCategories(double latitude, double longitude) {
        List<String> categories = callPlacesAPI(latitude, longitude);
        return categories != null ? categories : new ArrayList<>(); // Вместо null возвращаем пустой список
    }

}

