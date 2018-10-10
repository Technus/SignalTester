package com.github.technus.dbAdditions;

import com.github.technus.dbAdditions.functionalInterfaces.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryHashMap<Q,K,V> extends ConcurrentHashMap<K,V> {
    protected IFunction<K,V> query;
    protected IQuery<Q,K,V> multiQuery;
    //protected HashMap<Object,IQuery> multiQueryMap =new HashMap<>();
    protected IConsumer<V> reload;

    /**
     * Set Function which asks database for an object
     * @param query function returning a single object based on it's key
     */
    public void setQuery(IFunction<K,V> query) {
        this.query = query;
    }

    ///**
    // * Set Query returning a map of found objects from Map or DB (adds to map)
    // * @param name name to assign to the query
    // * @param multiQuery query returning map of found objects from Map or DB adding new objects to Map
    // * @param <Q> query object and return type
    // */
    //public <Q> void setNamedMultiQuery(String name, IQuery<Q, K, V> multiQuery) {
    //    this.multiQueryMap.put(name,multiQuery);
    //}

    ///**
    // * Set Query returning a map of found objects from Map or DB (adds to map)
    // * @param query desired query object
    // * @param multiQuery query returning map of found objects from Map or DB adding new objects to Map
    // * @param <Q> query object type
    // * @param <T> desired return object type
    // */
    //public <Q,T extends V> void setTypedMultiQuery(Class<T> query, IQuery<Q, K, V> multiQuery) {
    //    this.multiQueryMap.put(query,multiQuery);
    //}

    /**
     * Set Query returning a map of found objects from Map or DB (can add to map)
     * @param multiQuery query returning map of found objects from Map or DB adding new objects to Map
     */
    @SuppressWarnings("unchecked")
    public void setMultiQuery(IQuery<Q, K, V> multiQuery) {
        this.multiQuery=multiQuery;
    }

    /**
     * Set Consumer which loads data from DB to the object
     * @param reload
     */
    public void setReload(IConsumer<V> reload) {
        this.reload = reload;
    }

    /**
     * Gets object from Map or DB (adds to map)
     * @param id
     * @return
     * @throws Exception
     */
    public V getOrQuery(K id) throws Exception{
        V value=get(id);
        if(value==null){
            value=query.act(id);
            if(value!=null) {
                put(id, value);
            }
        }
        return value;
    }

    /**
     * Gets multiple objects from Map or DB (adds to map)
     * @param query
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<K,V> multiGetOrQuery(Q query) throws Exception{
        return multiQuery.ask(query, this);
    }

    ///**
    // * Gets multiple objects from Map or DB (adds to map)
    // * @param name
    // * @param query
    // * @param <Q>
    // * @return
    // * @throws Exception
    // */
    //@SuppressWarnings("unchecked")
    //public <Q> Map<K,V> multiGetOrQuery(String name, Q query) throws Exception{
    //    return ((IQuery<Q,K,V>) multiQueryMap.get(name)).ask(query, this);
    //}

    ///**
    // * Gets multiple objects from Map or DB (adds to map)
    // * @param type
    // * @param query
    // * @param <T>
    // * @param <Q>
    // * @return
    // * @throws Exception
    // */
    //@SuppressWarnings("unchecked")
    //public <T,Q> Map<K,V> multiGetOrQuery(Class<T> type,Q query) throws Exception{
    //    return ((IQuery<Q,K,V>) multiQueryMap.get(type)).ask(query, this);
    //}

    /**
     * Reloads data to the current object from database without making objects
     * @param id
     * @return
     * @throws Exception
     */
    public V reload(K id) throws Exception{
        V value=get(id);
        if(value==null){
            return null;
        }
        reload.eat(value);
        return value;
    }

    /**
     * Reloads data from database without making objects
     * @throws Exception
     */
    public void reloadAll() throws Exception{
        for(V value:values()){
            reload.eat(value);
        }
    }
}
