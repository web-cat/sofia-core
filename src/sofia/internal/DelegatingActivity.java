package sofia.internal;

import java.lang.reflect.Field;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

public class DelegatingActivity extends Activity
{
    private Activity realActivity;


    // ----------------------------------------------------------
    private Method findMethod(String methodName)
    {
        return findMethod(realActivity.getClass(), methodName);
    }


    // ----------------------------------------------------------
    private Method findMethod(Class<?> klass, String methodName)
    {
        if (Object.class.equals(klass))
        {
            return null;
        }
        else
        {
            for (Method method : realActivity.getClass().getDeclaredMethods())
            {
                if (methodName.equals(method.getName()))
                {
                    method.setAccessible(true);
                    return method;
                }
            }

            return findMethod(klass.getSuperclass(), methodName);
        }
    }


    // ----------------------------------------------------------
    private void delegate(String methodName, Object... args)
    {
        Method method = findMethod(methodName);

        if (method != null)
        {
            try
            {
                method.invoke(realActivity, args);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Class<? extends Activity> activityClass =
            (Class<? extends Activity>) intent.getExtras().get("activityClass");

        try
        {
            realActivity = activityClass.newInstance();

            Field mWindow = Activity.class.getDeclaredField("mWindow");
            mWindow.setAccessible(true);
            mWindow.set(realActivity, getWindow());

            delegate("onCreate", savedInstanceState);
        }
        catch (Exception e)
        {
            Log.e("Sofia-DelegatingActivity", "Could not create activity", e);
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();

        delegate("onStart");
    }


    // ----------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();

        delegate("onResume");
    }
}
