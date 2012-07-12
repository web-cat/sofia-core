package sofia.internal;

import android.view.View;
import android.view.ViewGroup;

public abstract class ViewVisitor
{
    public void accept(View view)
    {
        boolean processChildren = visit(view);

        if (processChildren && view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++)
            {
                View child = group.getChildAt(i);
                accept(child);
            }
        }
    }


    protected abstract boolean visit(View view);
}
