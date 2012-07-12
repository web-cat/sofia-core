package sofia.internal;

import java.util.List;
import java.util.AbstractList;
import java.util.Map;

// -------------------------------------------------------------------------
/**
 * An implementation of {@link List} that maps a list of Java objects into a
 * list of {@link ReflectiveMap}s so that their properties can be accessed
 * using key-value notation. Useful when adapting a list of plain objects into
 * a {@code SimpleAdapter} for a {@code ListView} or similar UI component.
 *
 * @author  Tony Allevato
 * @version 2011.10.08
 */
public class ReflectiveListWrapper extends AbstractList<Map<String, Object>>
{
    // ----------------------------------------------------------
    private List<?> list;


    // ----------------------------------------------------------
    public ReflectiveListWrapper(List<?> list)
    {
        this.list = list;
    }


    // ----------------------------------------------------------
    @Override
    public Map<String, Object> get(int index)
    {
        return new ReflectiveMap(list.get(index));
    }


    // ----------------------------------------------------------
    @Override
    public int size()
    {
        return list.size();
    }
}
