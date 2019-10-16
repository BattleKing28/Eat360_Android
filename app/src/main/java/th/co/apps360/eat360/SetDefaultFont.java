package th.co.apps360.eat360;

/**
 * Created by jakkrit.p on 15/09/2015.
 */

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;

public final class SetDefaultFont {



    public static void setDefaultFont(Context context,String staticTypefaceFieldName, String fontAssetName) {

        final Typeface regular = Typeface.createFromAsset(context.getAssets(),fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,final Typeface newTypeface) {


        Map<String, Typeface> newMap = new HashMap<>();
        newMap.put("sans-serif", newTypeface);

        if(isVersionGreaterOrEqualToLollipop()){
            try {
                final Field staticField = Typeface.class.getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                Utils.setDebug("crash", e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                Utils.setDebug("crash", e.getLocalizedMessage());
            }
        }else{
            try {
                final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                Utils.setDebug("crash", e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                Utils.setDebug("crash", e.getLocalizedMessage());
            }
        }


    }



    private static  boolean isVersionGreaterOrEqualToLollipop(){

        int apiVersion = android.os.Build.VERSION.SDK_INT;

        if(apiVersion >= 21){ // android 5.0 or up
            return true;
        }else
            return false;
    }

}