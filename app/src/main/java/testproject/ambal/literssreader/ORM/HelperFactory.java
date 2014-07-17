package testproject.ambal.literssreader.ORM;

import android.content.Context;

import com.j256.ormlite.android.apptools.*;
/**
 * Created by Ambal on 17.07.14.
 */
public class HelperFactory{

    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getHelper(){
        return databaseHelper;
    }
    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }
}
