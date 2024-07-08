package com.bsw.coursework2000.helper;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class DataInitializer {

    public static void initializeData(Context context) {
        //if the test data is already there no need to initialize again
        if (!isFileExists(context, "tables.json")) {
            addTables(context);
        }

        if (!isFileExists(context, "users.json")) {
            addUsers(context);
        }

        if (!isFileExists(context, "reviews.json")) {
            addReviews(context);
        }

        if (!isFileExists(context, "meals.json")) {
            addMeals(context);
        }
    }

    private static boolean isFileExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    private static void addTables(Context context) {
        try {
            String tableDataJson = loadJsonFromAssets(context, "tables.json");

            if (!tableDataJson.isEmpty()) {
                JSONObject tablesJson = new JSONObject(tableDataJson);
                JSONObject tablesObject = tablesJson.getJSONObject("tables");

                Iterator<String> keys = tablesObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject table = tablesObject.getJSONObject(key);

                    FileHelper.addTable(context, table.getInt("size"), table.getString("accessible"), table.getString("location"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addUsers(Context context) {
        try {
            String userDataJson = loadJsonFromAssets(context, "users.json");

            if (!userDataJson.isEmpty()) {
                JSONObject usersJson = new JSONObject(userDataJson);
                JSONObject usersObject = usersJson.getJSONObject("users");

                Iterator<String> keys = usersObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject user = usersObject.getJSONObject(key);

                    FileHelper.addUser(context, user.getString("username"), user.getString("phoneNum"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addReviews(Context context) {
        try {
            String reviewDataJson = loadJsonFromAssets(context, "reviews.json");

            if (!reviewDataJson.isEmpty()) {
                JSONObject reviewsJson = new JSONObject(reviewDataJson);
                JSONObject reviewsObject = reviewsJson.getJSONObject("reviews");

                Iterator<String> keys = reviewsObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject review = reviewsObject.getJSONObject(key);

                    FileHelper.addReview(context, review.getString("username"), review.getString("content"), review.getInt("rating"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addMeals(Context context) {
        try {
            String mealDataJson = loadJsonFromAssets(context, "meals.json");

            if (!mealDataJson.isEmpty()) {
                JSONObject mealsJson = new JSONObject(mealDataJson);
                JSONObject mealsObject = mealsJson.getJSONObject("meals");

                Iterator<String> keys = mealsObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject meal = mealsObject.getJSONObject(key);

                    FileHelper.addMeal(context, meal.getString("name"), meal.getString("diet"), meal.getString("allergies"), meal.getString("description"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private static String loadJsonFromAssets(Context context, String fileName) {
        StringBuilder jsonStringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStringBuilder.toString();
    }
}
