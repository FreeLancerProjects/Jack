package com.creativeshare.Jack.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.creativeshare.Jack.models.ChatUserModel;
import com.creativeshare.Jack.models.Favourite_location;
import com.creativeshare.Jack.models.QueryModel;
import com.creativeshare.Jack.models.UserModel;
import com.creativeshare.Jack.tags.Tags;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Preferences {

    private static Preferences instance=null;

    private Preferences() {
    }

    public static Preferences getInstance()
    {
        if (instance==null)
        {
            instance = new Preferences();
        }
        return instance;
    }

    public void create_update_userData(Context context, UserModel userModel)
    {
        SharedPreferences preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userData = gson.toJson(userModel);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_data",userData);
        editor.apply();
        create_update_session(context, Tags.session_login);

    }

    public UserModel getUserData(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String user_data = preferences.getString("user_data","");
        UserModel userModel = gson.fromJson(user_data,UserModel.class);
        return userModel;
    }

    public void create_update_session(Context context,String session)
    {
        SharedPreferences preferences = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("state",session);
        editor.apply();

    }

    public String getSession(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        String session = preferences.getString("state", Tags.session_logout);
        return session;
    }


    public void saveLoginFragmentState(Context context,int state)
    {
        SharedPreferences preferences = context.getSharedPreferences("fragment_state",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("state",state);
        editor.apply();
    }
    public int getFragmentState(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("fragment_state",Context.MODE_PRIVATE);
        return preferences.getInt("state",0);
    }

    public void saveQuery(Context context, QueryModel queryModel)
    {
        SharedPreferences preferences = context.getSharedPreferences("search",Context.MODE_PRIVATE);
        String gson = preferences.getString("queries","");


        if (gson.isEmpty())
        {
            List<QueryModel> queryModelList = new ArrayList<>();
            queryModelList.add(queryModel);

            String queryListGson = new Gson().toJson(queryModelList);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("queries",queryListGson);
            editor.apply();
            Log.e("list1",queryModelList.size()+"");

        }else
            {
                List<QueryModel> queryModelList = new Gson().fromJson(gson, new TypeToken<List<QueryModel>>(){}.getType());
                if (queryModelList!=null)
                {

                    if (queryModelList.size()>0)
                    {
                        Log.e("list2",queryModelList.size()+"");

                        if (!isQueryIn(queryModel,queryModelList))
                        {
                            if (queryModelList.size()<10)
                            {
                                queryModelList.add(queryModel);
                            }else
                            {
                                queryModelList.set(0,queryModel);

                            }

                            Log.e("list3",queryModelList.size()+"");

                            String queryListGson = new Gson().toJson(queryModelList);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("queries",queryListGson);
                            editor.apply();
                        }

                    }







                }

            }

    }

    public List<QueryModel> getAllQueries(Context context)
    {
        List<QueryModel> queryModelList = new ArrayList<>();

        SharedPreferences preferences = context.getSharedPreferences("search",Context.MODE_PRIVATE);
        String gson = preferences.getString("queries","");

        if (!gson.isEmpty())
        {
            List<QueryModel> queryModelList2 = new Gson().fromJson(gson, new TypeToken<List<QueryModel>>(){}.getType());

            queryModelList.addAll(queryModelList2);
        }

        return queryModelList;

    }

    private boolean isQueryIn(QueryModel queryModel,List<QueryModel> queryModelList)
    {
        boolean isIn = false;
        for (QueryModel queryModel1 :queryModelList)
        {
            if (queryModel1.getKeyword().equals(queryModel.getKeyword()))
            {
                isIn = true;
            }
        }

        return isIn;
    }

    public void SaveFavouriteLocation(Context context,Favourite_location favourite_location)
    {
        String gson = new Gson().toJson(favourite_location);
        SharedPreferences preferences = context.getSharedPreferences("fav_loc",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("location",gson);
        editor.apply();

    }

    public Favourite_location getFavouriteLocation(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("fav_loc",Context.MODE_PRIVATE);
        String gson = preferences.getString("location","");
        Favourite_location favourite_location = new Gson().fromJson(gson,Favourite_location.class);
        return favourite_location;
    }

    public void ClearFavoriteLocation(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("fav_loc",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveVisitTime(Context context,String time)
    {
        SharedPreferences preferences = context.getSharedPreferences("visit",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("time",time);
        editor.apply();
    }



    public String getVisitTime(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("visit",Context.MODE_PRIVATE);
        return preferences.getString("time","");
    }


    public void saveChatUserData(Context context, ChatUserModel chatUserModel)
    {
        SharedPreferences preferences = context.getSharedPreferences("chat_user",Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String  chat_model = gson.toJson(chatUserModel);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("chat_data",chat_model);
        editor.apply();

    }



    public ChatUserModel getChatUserData(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("chat_user",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(preferences.getString("chat_data",""),ChatUserModel.class);
    }

    public void clearChatUserData(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("chat_user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void ClearUserData(Context context)
    {
        SharedPreferences preferences1 = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.clear();
        editor1.apply();

        SharedPreferences preferences2 = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.clear();
        editor2.apply();

        SharedPreferences preferences5 = context.getSharedPreferences("search",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor5 = preferences5.edit();
        editor5.clear();
        editor5.apply();
    }

}
