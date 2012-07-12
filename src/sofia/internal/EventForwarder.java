package sofia.internal;

import android.content.Context;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventForwarder<T>
{
    private T result;

    private View sourceView;
    private Context context;
    private Class<? extends T> returnType;
    private String methodName;
    private Method method;


    // ----------------------------------------------------------
    /**
     * @param view the view that originated the event
     * @param methodName the name of the method to invoke
     * @param args the arguments to pass to the method
     */
    public EventForwarder(View view, Class<? extends T> returnType, String method)
    {
        this.sourceView = view;
        this.returnType = returnType;
        this.methodName = method;
        this.result = null;

        findMethod();
    }


    // ----------------------------------------------------------
    private void findMethod()
    {
        // TODO replace with reflection library

        context = sourceView.getContext();

        Method[] methods = context.getClass().getMethods();
        this.method = null;

        for (Method currentMethod : methods)
        {
            if (currentMethod.getName().equals(methodName))
            {
                this.method = currentMethod;
                break;
            }
        }
    }


    // ----------------------------------------------------------
    public boolean methodWasFound()
    {
        return method != null;
    }


    // ----------------------------------------------------------
    public T result()
    {
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Forwards an event from a view to the context that owns the view, via
     * reflection.
     *
     * @param arguments the arguments to pass to the event handler
     */
    @SuppressWarnings("unchecked")
    public void forward(Object... arguments)
    {
        if (method != null)
        {
            result = null;

            try
            {
                result = (T) method.invoke(context, arguments);
            }
            catch (IllegalArgumentException e)
            {
                method = null;
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e.getTargetException());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            result = null;
        }
    }
}
