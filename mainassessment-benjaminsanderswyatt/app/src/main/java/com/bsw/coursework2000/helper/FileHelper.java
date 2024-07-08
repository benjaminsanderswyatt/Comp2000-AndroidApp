package com.bsw.coursework2000.helper;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class FileHelper {

    public static final String TABLES_JSON = "tables.json";
    public static final String USERS_JSON = "users.json";
    public static final String REVIEWS_JSON = "reviews.json";
    public static final String MEALS_JSON = "meals.json";

    public static JSONObject readJsonFile(Context context, String filename) {
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            fileInputStream.close();

            return new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeJsonFile(Context context, String fileName, JSONObject jsonObject) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonObject.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getAllTables(Context context){
        try {
            JSONObject jsonObject = FileHelper.readJsonFile(context, TABLES_JSON);

            if (jsonObject != null) {
                JSONObject tablesObject = jsonObject.getJSONObject("tables");

                ArrayList<String> tablesList = new ArrayList<>();

                Iterator<String> keys = tablesObject.keys();
                while (keys.hasNext()) {
                    String tableId = keys.next();

                    tablesList.add(tableId);
                }

                return tablesList.toArray(new String[0]);

            } else {
                //JSON file is empty or doesn't exist
            }
        } catch (JSONException e) {
            //JSON cant read
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getAllMeals(Context context){
        try {
            JSONObject jsonObject = FileHelper.readJsonFile(context, MEALS_JSON);

            if (jsonObject != null) {
                JSONObject mealsObject = jsonObject.getJSONObject("meals");

                ArrayList<String> mealsList = new ArrayList<>();

                Iterator<String> keys = mealsObject.keys();
                while (keys.hasNext()) {
                    String mealId = keys.next();
                    JSONObject meal = mealsObject.getJSONObject(mealId);

                    mealsList.add(meal.getString("name"));
                }

                return mealsList.toArray(new String[0]);

            } else {
                //JSON file is empty or doesn't exist
                Toast.makeText(context, "No tables found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //JSON cant read
            e.printStackTrace();
            Toast.makeText(context, "Error reading JSON", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    public static boolean addReview(Context context, String username, String reviewContent, Integer rating){
        try {
            JSONObject jsonObject = readJsonFile(context, REVIEWS_JSON);

            if (jsonObject == null) {
                //if JSON file is empty or doesn't exist create a new one
                jsonObject = new JSONObject();
                jsonObject.put("reviews", new JSONObject());
            }

            JSONObject reviewsObject = jsonObject.getJSONObject("reviews");

            String reviewsKey = "user " + (reviewsObject.length() + 1);

            JSONObject newReview = new JSONObject();
            newReview.put("username", username);
            newReview.put("content", reviewContent);
            newReview.put("rating", rating);

            reviewsObject.put(reviewsKey, newReview);

            writeJsonFile(context, REVIEWS_JSON, jsonObject);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addMeal(Context context, String name, String diet, String allergies, String description){
        try {
            JSONObject jsonObject = readJsonFile(context, MEALS_JSON);

            if (jsonObject == null) {
                //if JSON file is empty or doesn't exist create a new one
                jsonObject = new JSONObject();
                jsonObject.put("meals", new JSONObject());
            }

            JSONObject mealsObject = jsonObject.getJSONObject("meals");

            String mealsKey = "meal " + (mealsObject.length() + 1);

            JSONObject newMeal = new JSONObject();
            newMeal.put("name", name);
            newMeal.put("diet", diet);
            newMeal.put("allergies", allergies);
            newMeal.put("description", description);

            mealsObject.put(mealsKey, newMeal);

            writeJsonFile(context, MEALS_JSON, jsonObject);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }





    public static boolean addUser(Context context, String username, String phoneNum) {
        try {
            JSONObject jsonObject = readJsonFile(context, USERS_JSON);

            if (jsonObject == null) {
                //if JSON file is empty or doesn't exist create a new one
                jsonObject = new JSONObject();
                jsonObject.put("users", new JSONObject());
            }

            JSONObject usersObject = jsonObject.getJSONObject("users");

            String userKey = "user " + (usersObject.length() + 1);

            JSONObject newUser = new JSONObject();
            newUser.put("username", username);
            newUser.put("phoneNum", phoneNum);

            usersObject.put(userKey, newUser);

            writeJsonFile(context, USERS_JSON, jsonObject);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void removeUser(Context context, String idToRemove) {
        try {
            JSONObject jsonObject = readJsonFile(context, USERS_JSON);

            if (jsonObject != null) {
                //remove user
                JSONObject usersObject = jsonObject.getJSONObject("users");
                usersObject.remove(idToRemove);
                writeJsonFile(context, USERS_JSON, jsonObject);

            } else {
                //JSON file is empty or doesn't exist
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public static boolean addTable(Context context, int size, String accessible, String location) {
        try {
            JSONObject jsonObject = readJsonFile(context, TABLES_JSON);

            if (jsonObject == null) {
                //if JSON file is empty or doesn't exist create a new one
                jsonObject = new JSONObject();
                jsonObject.put("tables", new JSONObject());
            }

            JSONObject tablesObject = jsonObject.getJSONObject("tables");

            String tableKey = "table " + (tablesObject.length() + 1);

            JSONObject newTable = new JSONObject();
            newTable.put("size", size);
            newTable.put("accessible", accessible);
            newTable.put("location", location);

            tablesObject.put(tableKey, newTable);

            writeJsonFile(context, TABLES_JSON, jsonObject);

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }



}

