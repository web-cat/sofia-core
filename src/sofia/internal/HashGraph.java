package sofia.internal;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

// -------------------------------------------------------------------------
/**
 * A directed graph that is represented as a {@code HashMap} that maps each
 * vertex to a {@code HashSet} of the vertices that it has edges to.
 *
 * @param <V> the type of object that represents vertices in the graph
 *
 * @author  Tony Allevato
 * @version 2011.11.26
 */
public class HashGraph<V>
{
    //~ Instance/static variables .............................................

    private HashMap<V, HashSet<V>> outEdges;
    private HashMap<V, HashSet<V>> inEdges;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new empty graph.
     */
    public HashGraph()
    {
        outEdges = new HashMap<V, HashSet<V>>();
        inEdges = new HashMap<V, HashSet<V>>();
    }


    // ----------------------------------------------------------
    /**
     * Adds a vertex to the graph if it does not already exist. This is mostly
     * useful when you need to add an isolated vertex (with no in- or
     * out-edges).
     *
     * @param vertex the vertex to add
     */
    public void addVertex(V vertex)
    {
        if (!outEdges.containsKey(vertex))
        {
            outEdges.put(vertex, new HashSet<V>());
        }

        if (!inEdges.containsKey(vertex))
        {
            inEdges.put(vertex, new HashSet<V>());
        }
    }


    // ----------------------------------------------------------
    /**
     * Adds a directed edge to the graph, also adding the vertices if they do
     * not exist.
     *
     * @param source the source vertex of the edge
     * @param destination the destination vertex of the edge
     */
    public void addEdge(V source, V destination)
    {
        addVertex(source);
        addVertex(destination);

        outEdges.get(source).add(destination);
        inEdges.get(destination).add(source);
    }


    // ----------------------------------------------------------
    /**
     * Removes an edge from the graph.
     *
     * @param source the source vertex of the edge
     * @param destination the destination vertex of the edge
     */
    public void removeEdge(V source, V destination)
    {
        outEdges.get(source).remove(destination);
        inEdges.get(destination).remove(source);
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether there is an edge from the source to the
     * destination.
     *
     * @param source the source vertex
     * @param destination the destination
     * @return true if there is an edge, otherwise false
     */
    public boolean pointsTo(V source, V destination)
    {
        if (outEdges.containsKey(source))
        {
            return outEdges.get(source).contains(destination);
        }
        else
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the collection of vertices to which the specified vertex points.
     *
     * @param vertex the vertex
     * @return the collection of vertices to which the specified vertex points
     */
    public Collection<V> outVertexSet(V vertex)
    {
        HashSet<V> adjacent = outEdges.get(vertex);

        return adjacent != null ? adjacent : Collections.<V>emptySet();
    }


    // ----------------------------------------------------------
    /**
     * Gets the collection of vertices that point to the specified vertex.
     *
     * @param vertex the vertex
     * @return the collection of vertices that point to the specified vertex
     */
    public Collection<V> inVertexSet(V vertex)
    {
        HashSet<V> adjacent = inEdges.get(vertex);

        return adjacent != null ? adjacent : Collections.<V>emptySet();
    }


    // ----------------------------------------------------------
    /**
     * Gets the set of vertices in the graph.
     *
     * @return the set of vertices in the graph
     */
    public Set<V> vertexSet()
    {
        return outEdges.keySet();
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the specified vertex is a source (that
     * is, it has no edges coming into it).
     *
     * @param vertex the vertex
     * @return true if the vertex is a source, otherwise false
     */
    public boolean isSource(V vertex)
    {
        return inEdges.get(vertex).isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Gets the set of source vertices in the graph; that is, vertices with no
     * incoming edges.
     *
     * @return the set of source vertices in the graph
     */
    public Set<V> sourceVertexSet()
    {
        HashSet<V> sources = new HashSet<V>();

        for (V vertex : vertexSet())
        {
            if (isSource(vertex))
            {
                sources.add(vertex);
            }
        }

        return sources;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the specified vertex is a sink (that is,
     * it has no edges going out of it).
     *
     * @param vertex the vertex
     * @return true if the vertex is a sink, otherwise false
     */
    public boolean isSink(V vertex)
    {
        return outEdges.get(vertex).isEmpty();
    }


    // ----------------------------------------------------------
    /**
     * Gets the set of sink vertices in the graph; that is, vertices with no
     * outgoing edges.
     *
     * @return the set of sink vertices in the graph
     */
    public Set<V> sinkVertexSet()
    {
        HashSet<V> sinks = new HashSet<V>();

        for (V vertex : vertexSet())
        {
            if (isSink(vertex))
            {
                sinks.add(vertex);
            }
        }

        return sinks;
    }
}
