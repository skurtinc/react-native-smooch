package com.smooch.rnsmooch;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.Promise;

import java.util.HashMap;
import java.util.Map;

import io.smooch.core.Smooch;
import io.smooch.core.SmoochCallback;
import io.smooch.core.User;
import io.smooch.ui.ConversationActivity;

public class ReactNativeSmooch extends ReactContextBaseJavaModule {
    @Override
    public String getName() {
        return "SmoochManager";
    }

    public ReactNativeSmooch(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void login(String userId, String jwt) {
        Log.i("SMOOCH LOGIN jwt", jwt);

        Smooch.login(userId, jwt, new SmoochCallback() {
          @Override
          public void run(Response response) {
            Log.i("SMOOCH LOGIN ", String.valueOf(response.getStatus()));
            if (response.getError() != null) {
              Log.i("SMOOCH LOGIN ", response.getError().toString());
              Log.i("SMOOCH LOGIN ", response.getData().toString());
            }
          }
        });
    }

    @ReactMethod
    public void logout() {
        Smooch.logout(new SmoochCallback() {
          @Override
          public void run(Response response) {
            Log.i("SMOOCH LOGOUT", String.valueOf(response.getStatus()));
          }
        });
    }

    @ReactMethod
    public void show() {
        ConversationActivity.show(getReactApplicationContext(), Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @ReactMethod
    public void getUnreadCount(Promise promise) {
        int unreadCount = Smooch.getConversation().getUnreadCount();
        promise.resolve(unreadCount);
    }

    @ReactMethod
    public void setFirstName(String firstName) {
        User.getCurrentUser().setFirstName(firstName);
    }

    @ReactMethod
    public void setLastName(String lastName) {
        User.getCurrentUser().setLastName(lastName);
    }

    @ReactMethod
    public void setEmail(String email) {
        User.getCurrentUser().setEmail(email);
    }

    @ReactMethod
    public void setUserProperties(ReadableMap properties) {
        User.getCurrentUser().addProperties(getUserProperties(properties));
    }

    private Map<String, Object> getUserProperties(ReadableMap properties) {
        ReadableMapKeySetIterator iterator = properties.keySetIterator();
        Map<String, Object> userProperties = new HashMap<>();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = properties.getType(key);
            if (type == ReadableType.Boolean) {
                userProperties.put(key, properties.getBoolean(key));
            } else if (type == ReadableType.Number) {
                userProperties.put(key, properties.getDouble(key));
            } else if (type == ReadableType.String) {
                userProperties.put(key, properties.getString(key));
            }
        }

        return userProperties;
    }

}
