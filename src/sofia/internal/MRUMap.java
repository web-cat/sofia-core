/*==========================================================================*\
 |  $Id: MRUMap.java,v 1.3 2010/02/23 17:06:36 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2009-2010 Virginia Tech
 |
 |  This file is part of the Student-Library.
 |
 |  The Student-Library is free software; you can redistribute it and/or
 |  modify it under the terms of the GNU Lesser General Public License as
 |  published by the Free Software Foundation; either version 3 of the
 |  License, or (at your option) any later version.
 |
 |  The Student-Library is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU Lesser General Public License for more details.
 |
 |  You should have received a copy of the GNU Lesser General Public License
 |  along with the Student-Library; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package sofia.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//-------------------------------------------------------------------------
/**
 *  A caching map used internally by various student.* classes.  This map
 *  records the time that each value is "set", along with a maximum
 *  permissible age.  It automatically clears entries older than the
 *  allowable maximum.  It also supports a capacity limit, and automatically
 *  removes the least-recently-used entries to make room for new entries
 *  if the cache is already at capacity.  It uses soft references, so
 *  that memory can be reclaimed by the garbage collector as needed.
 *
 *  @param <K> The type for keys
 *  @param <V> The type for values
 *
 *  @author  Stephen Edwards
 *  @author Last changed by $Author: stedwar2 $
 *  @version $Revision: 1.3 $, $Date: 2010/02/23 17:06:36 $
 */
public class MRUMap<K, V>
    implements Map<K, V>
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new MRUMap.
     * @param maxCapacity The limit on the maximum number of entries this
     *                    map should hold (or zero if there is no limit).  If
     *                    a non-zero limit is given, then least recently used
     *                    entries will be removed to make room for new entries
     *                    once the map reaches this size.
     * @param ageLimitInSeconds The maximum amount of time to hold any one
     *                    entry (or zero if there is no limit).  If a non-zero
     *                    limit is given, then entries that have been stored
     *                    in the map longer than this amount of time will
     *                    automatically be removed.  The age of an entry is
     *                    automatically updated each time the put() method
     *                    is used on the corresponding key.
     */
    public MRUMap(int maxCapacity, long ageLimitInSeconds, Recycler<V> recycler)
    {
        this.recycler = recycler;

        int initialCap = (int)(maxCapacity / 0.75f + 1);
        if (initialCap < 128)
        {
            initialCap = 128;
        }
        map = new CustomMap<K, Data<K, V>>(initialCap);
        staleRefs = new ReferenceQueue<V>();
        initializeMRU();
        capacity = maxCapacity;
        ageLimit = ageLimitInSeconds * 1000;
//		checkInvariant("MRUMap");
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    /**
     * Empty the map by removing all its elements.
     */
    public void clear()
    {
        map.clear();
        // Empty the stale reference queue completely
        while (staleRefs.poll() != null);
        initializeMRU();
//		checkInvariant("clear");
    }


    // ----------------------------------------------------------
    /**
     * Check to see if a key is in the map.
     * @param key The key to check.
     * @return True if an entry for the key is stored in the map.
     */
    public boolean containsKey(Object key)
    {
        clearOldEntries();
        Data<K, V> val = map.get(key);
//		checkInvariant("containsKey");
        return val != null;
    }


    // ----------------------------------------------------------
    /**
     * Check to see if a key is in the map.
     * @param key The key to check.
     * @return True if an entry for the key is stored in the map.
     */
    public long keyLastSetTime(Object key)
    {
        clearOldEntries();
        Data<K, V> val = map.get(key);
        if (val != null)
        {
            return val.creationTime();
        }
//		checkInvariant("keyLastSetTime");
        return 0;
    }


    // ----------------------------------------------------------
    /**
     * Check to see if a value is in the map.  This operation is
     * <b>unsupported</b> by this class.
     * @param value The value to check.
     * @return Always throws an UnsupportedOperationException.
     */
    public boolean containsValue(Object value)
    {
        throw new UnsupportedOperationException();
    }


    // ----------------------------------------------------------
    /**
     * Get a set of all entries stored in this map.
     * @return A set of all key/value pairs stored in the map.
     */
    public Set<Map.Entry<K, V>> entrySet()
    {
        clearOldEntries();
        Set<Map.Entry<K, V>> result =
            new java.util.HashSet<Map.Entry<K, V>>(size());
        for (Map.Entry<K, Data<K, V>> e : map.entrySet())
        {
            result.add(new Entry(e));
        }
//		checkInvariant("entrySet");
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Look up the value stored for a given key.
     * @param key The key to look up.
     * @return The value associated with the key, or null if there is none.
     */
    public V get(Object key)
    {
        clearOldEntries();
        V result = null;
        Data<K, V> val = map.get(key);
        if (val != null)
        {
            result = getValue(val);
        }
//		checkInvariant("get");
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Look up the value stored for a given key.
     * @param key The key to look up.
     * @return The value associated with the key, or null if there is none.
     */
    public ValueWithTimestamp<V> getTimestampedValue(Object key)
    {
        clearOldEntries();
        ValueWithTimestamp<V> result = null;
        Data<K, V> val = map.get(key);
        if (val != null)
        {
            result = new ValueWithTimestamp<V>();
            result.value = getValue(val);
            result.timestamp = val.creationTime();
        }
//      checkInvariant("get");
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Look up the timestamp associated with the cached value for a given key.
     * @param key The key to look up.
     * @return The timestamp value associated with the key,
     * or 0 if there is none.
     */
    public long getTimestampFor(Object key)
    {
        clearOldEntries();
        long result = 0L;
        Data<K, V> val = map.get(key);
        if (val != null)
        {
            result = val.creationTime();
        }
//      checkInvariant("get");
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Check to see if the map has any entries at all.
     * @return True iff the map's size is zero.
     */
    public boolean isEmpty()
    {
        clearOldEntries();
//		checkInvariant("isEmpty");
        return map.isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Get a set of all the keys stored in this map.
     * @return A set of all this map's keys.
     */
    public Set<K> keySet()
    {
        clearOldEntries();
//		checkInvariant("keySet");
        return map.keySet();
    }


    // ----------------------------------------------------------
    /**
     * Set the value associated with a given key.
     * @param key The key to associate.
     * @param value The value to associate with the key.
     * @return The previous value associated with the given key, or null if
     * there was no value associated with the key prior to the call.
     */
    public V put(K key, V value)
    {
        clearOldEntries();
        if (value == null)
        {
            remove(key);
            return value;
        }
        V oldValue = internalRemove(key);
        Data<K, V> val =
            new Data<K, V>(key, value, staleRefs, ageSentinel.newer);
        map.put(key, val);

//		checkInvariant("put");
        return oldValue;
    }


    // ----------------------------------------------------------
    /**
     * Set the value associated with a given key.
     * @param key The key to associate.
     * @param value The value to associate with the key.
     * @return The timestamp associated with the newly inserted value,
     * or zero is the value inserted was null.
     */
    public long putReturningTimestamp(K key, V value)
    {
        clearOldEntries();
        if (value == null)
        {
            remove(key);
            return 0L;
        }
        internalRemove(key);
        Data<K, V> val =
            new Data<K, V>(key, value, staleRefs, ageSentinel.newer);
        map.put(key, val);

//      checkInvariant("put");
        return val.creationTime();
    }


    // ----------------------------------------------------------
    /**
     * Add all the associations stored in the given map to this map.
     * @param otherMap The map to copy key/value pairs from.
     */
    public void putAll(Map<? extends K, ? extends V> otherMap)
    {
        clearOldEntries();
        for (Map.Entry<? extends K, ? extends V> entry : otherMap.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
//		checkInvariant("putAll");
    }


    // ----------------------------------------------------------
    /**
     * Remove an entry from the map.
     * @param key The key for the association to remove.
     * @return The value that was associated with the key prior to the call,
     * or null if there was none.
     */
    public V remove(Object key)
    {
        clearOldEntries();
        V result = internalRemove(key);
//		checkInvariant("remove");
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Get the number of entries stored in the map.
     * @return The number of entries.
     */
    public int size()
    {
        clearOldEntries();
//		checkInvariant("size");
        return map.size();
    }


    // ----------------------------------------------------------
    /**
     * Get a set of all the values stored in this map.
     * @return A set of all the values.
     */
    public Collection<V> values()
    {
        clearOldEntries();
        ArrayList<V> result = new ArrayList<V>(size());
        for (Data<K, V> data : map.values())
        {
            result.add(data.get());
        }
//		checkInvariant("values");
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable representation of this map.
     * @return A human-readable representation of this map.
     */
    public String toString()
    {
        return map.toString();
    }


    // ----------------------------------------------------------
    /**
     * Represents a value and a time stamp bundled together, so they
     * can be returned as a single value.
     * @param <V> The type of the value
     */
    public static class ValueWithTimestamp<V>
    {
        /** The value to store. */
        public V value;
        /** The time at which this value was last changed. */
        public long timestamp;
    }


    //~ Private classes and methods ...........................................

    // ----------------------------------------------------------
    private void initializeMRU()
    {
        ageSentinel = new Data<K, V>(null, null, null);
        // Form a circular list
        ageSentinel.addCreatedAfter(ageSentinel);
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void clearOldEntries()
    {
        if (ageLimit > 0)
        {
            long time = System.currentTimeMillis();
//		    System.out.println("clearOldEntries() at "
//			    + formatter.format(new java.util.Date(time)));
            Data<K, V> oldestNode = ageSentinel.older;
            while (oldestNode != ageSentinel
                && (time - oldestNode.creationTime() > ageLimit))
            {
                K keyToRemove = oldestNode.getKey();
                oldestNode = oldestNode.older;
                internalRemove(keyToRemove);
            }
        }

        // Now drain any garbage-collected references
        Data<K, V> stale = (Data<K, V>)staleRefs.poll();
        while (stale != null)
        {
            internalRemove(stale);
            stale = (Data<K, V>)staleRefs.poll();
        }
    }


    // ----------------------------------------------------------
    private V internalRemove(Data<K, V> val)
    {
        if (val == null)
        {
            return null;
        }

        val.clear();        // prevent val from being added to ReferenceQueue
        V result = null;
        if (val != null)
        {
            // defensive, since we should get here only once per instance
            if (val.newer != null && val.older != null)
            {
                val.removeFromAgeChain();
            }

            map.remove(val.getKey());
            result = val.get();
        }

        if (recycler != null)
        {
            recycler.recycle(result);
        }

        return result;
    }


    // ----------------------------------------------------------
    private V internalRemove(Object key)
    {
        return internalRemove(map.get(key));
    }


    // ----------------------------------------------------------
    private V getValue(Data<K, V> node)
    {
        return node.get();
    }


    // ----------------------------------------------------------
//	private V setValue(Data<K, V> node, V value)
//	{
//		node.touchCreationTime();
//		node.removeFromAgeChain();
//		node.addCreatedAfter(ageSentinel.newer);
//		return node.setValue(value);
//	}


    // ----------------------------------------------------------
//	private void checkInvariant(String opName)
//	{
//		assert map != null;
//
//		assert mruSentinel != null;
//		assert mruSentinel.next != null;
//		assert mruSentinel.previous != null;
//		assert mruSentinel.older == null;
//		assert mruSentinel.newer == null;
//
//		assert ageSentinel != null;
//		assert ageSentinel.next == null;
//		assert ageSentinel.previous == null;
//		assert ageSentinel.older != null;
//		assert ageSentinel.newer != null;
//
//		Data<K, V> walker = mruSentinel.next;
//		System.out.println("Checking MRUMap on " + opName + "()  at "
//				+ formatter.format(
//				    new java.util.Date(System.currentTimeMillis()))
//				+ " with " + map.size() + " entries");
//		System.out.println("Walking MRU chain:");
//		int count1 = 0;
//		while (walker != mruSentinel)
//		{
//			count1++;
//			System.out.println("    " + count1 + ": " + walker.getKey()
//				+ " => " + walker.getValueNoTouch() + " ("
//				+ walker.creationTime + ": "
//				+ formatter.format(new java.util.Date(walker.creationTime))
//				+ ")");
//			assert walker == walker.previous.next;
//			assert walker == walker.next.previous;
//			walker = walker.next;
//		}
//		System.out.println("Walking Age chain:");
//		int count2 = 0;
//		walker = ageSentinel.older;
//		while (walker != ageSentinel)
//		{
//			count2++;
//			System.out.println("    " + count2 + ": " + walker.getKey()
//				+ " => " + walker.getValueNoTouch() + " ("
//				+ walker.creationTime + ": "
//				+ formatter.format(new java.util.Date(walker.creationTime))
//				+ ")");
//			assert walker == walker.newer.older;
//			assert walker == walker.older.newer;
//			walker = walker.older;
//		}
//		assert count1 == count2;
//	}



    // ----------------------------------------------------------
//	private static void sleep(long millis)
//	{
//		try
//		{
//			Thread.sleep(millis);
//		}
//		catch (InterruptedException e) { /* ignore */ }
//	}


     // ----------------------------------------------------------
//	public static void main(String[] args)
//	{
//		MRUMap<String, String> cache = new MRUMap<String, String>(5, 1);
//
//		System.out.println("inserting first entry.");
//		cache.put("first key", "first value");
//		sleep(300);
//
//		System.out.println("inserting second entry.");
//		cache.put("second key", "second value");
//		sleep(300);
//
//		System.out.println("inserting third entry.");
//		cache.put("third key", "third value");
//		sleep(250);
//
//		while (cache.size() > 0)
//		{
//			System.out.println("Time = "
//				+ formatter.format(
//					new java.util.Date(System.currentTimeMillis())));
//			System.out.println("Checking for first entry");
//			System.out.println(
//				"first entry found = " + cache.containsKey("first key"));
//			System.out.println("Checking for second entry");
//			System.out.println(
//				"second entry found = " + cache.containsKey("second key"));
//			System.out.println("Checking for third entry");
//			System.out.println(
//				"third entry found = " + cache.containsKey("third key"));
//			sleep(60);
//		}
//	}


    public static interface Recycler<V>
    {
        public void recycle(V value);
    }


    // ----------------------------------------------------------
    /**
     * This class is a feather-weight wrapper around an entry from
     * the underlying map, to be used in collections returned by
     * {@link MRUMap#entrySet()}.
     */
    private class Entry
        implements Map.Entry<K, V>
    {
        // ----------------------------------------------------------
        public Entry(Map.Entry<K, Data<K, V>> contents)
        {
            inner = contents;
        }

        // ----------------------------------------------------------
        public K getKey()
        {
            return inner.getKey();
        }

        // ----------------------------------------------------------
        public V getValue()
        {
            Data<K, V> result = inner.getValue();
            return result == null
                ? null
                : MRUMap.this.getValue(result);
        }

        // ----------------------------------------------------------
        public V setValue(V value)
        {
            throw new UnsupportedOperationException();
        }

        // ----------------------------------------------------------
        public String toString()
        {
            return inner.toString();
        }


        private Map.Entry<K, Data<K, V>> inner;
    }


    // ----------------------------------------------------------
    /**
     * This class is a customized value wrapper used in the underlying
     * map.  The underlying map associates keys to these nodes, rather
     * than to raw V values.  These nodes support threading of values in
     * the map--both threading for an MRU list and threading for an age-based
     * list.
     */
    private static class Data<K, V>
        extends SoftReference<V>
    {
        // ----------------------------------------------------------
        public Data(K theKey, V val, ReferenceQueue<V> q)
        {
            super(val, q);
            key = theKey;
            older = null;
            newer = null;
            touchCreationTime();
        }

        // ----------------------------------------------------------
        public Data(
            K theKey,
            V val,
            ReferenceQueue<V> q,
            Data<K, V> createdBefore)
        {
            this(theKey, val, q);
            addCreatedAfter(createdBefore);
        }

        // ----------------------------------------------------------
        public void touchCreationTime()
        {
            creationTime = System.currentTimeMillis();
        }

        // ----------------------------------------------------------
        public void removeFromAgeChain()
        {
            newer.older = older;
            older.newer = newer;
            newer = null;
            older = null;
        }

        // ----------------------------------------------------------
        public void addCreatedAfter(Data<K, V> node)
        {
            newer = node;
            older = node.older;
            node.older = this;
            if (older != null)
            {
                older.newer = this;
            }
        }

        // ----------------------------------------------------------
        public K getKey()
        {
            return key;
        }

        // ----------------------------------------------------------
        public long creationTime()
        {
            return creationTime;
        }

        // ----------------------------------------------------------
        public String toString()
        {
            V val = get();
            return val == null ? "null" : val.toString();
        }

        // ----------------------------------------------------------
        private final K key;
        private long creationTime;
        private Data<K, V> older;
        private Data<K, V> newer;
    }


    // ----------------------------------------------------------
    /**
     * This class is a customized value wrapper used in the underlying
     * map.  The underlying map associates keys to these nodes, rather
     * than to raw V values.  These nodes support threading of values in
     * the map--both threading for an MRU list and threading for an age-based
     * list.
     */
    private class CustomMap<Key, Value extends Data<?, ?>>
        extends LinkedHashMap<Key, Value>
    {
        private static final long serialVersionUID = 5242766963754863168L;


        // ----------------------------------------------------------
        public CustomMap(int initialCapacity)
        {
            super(initialCapacity, 0.75f, true);
        }


        // ----------------------------------------------------------
        protected boolean removeEldestEntry(
            java.util.Map.Entry<Key, Value> eldest)
        {
            boolean result = capacity > 0 && size() > capacity;
            if (result)
            {
                eldest.getValue().removeFromAgeChain();
            }
            return result;
        }
    }


    //~ Instance/static variables .............................................

    private Data<K, V> ageSentinel;
    private Map<K, Data<K, V>> map;
    private int  capacity;
    private long ageLimit;
    private ReferenceQueue<V> staleRefs;
    private Recycler<V> recycler;
//	private static java.text.SimpleDateFormat formatter =
//		new java.text.SimpleDateFormat("HH:mm:ss.SSS");
}
