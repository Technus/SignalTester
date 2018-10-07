package java.com.github.technus.signalTester.db;

import java.com.github.technus.signalTester.functionalInterfaces.IConsumer;
import java.com.github.technus.signalTester.functionalInterfaces.IFunction;

public class MutableQueryHashMap<Q,K,V> extends QueryHashMap<Q,K,V> {
    protected IConsumer<V> upsert;
    protected IFunction<V,K> insert;

    /**
     * Sets upsert action
     * @param upsert Consumer which updates document to database based on given object - should always have key
     */
    public void setUpsert(IConsumer<V> upsert) {
        this.upsert = upsert;
    }

    /**
     * Sets insert action
     * @param insert Function which inserts V to database, updates V with new K assigned from database, gets K and returns it
     */
    public void setInsert(IFunction<V, K> insert) {
        this.insert = insert;
    }

    /**
     * Updates data from POJO to DB
     * @param key key to get obj to update
     * @return upserted object for convience
     * @throws Exception
     */
    public V upsert(K key) throws Exception{
        V value=get(key);
        if (value != null) {
            upsert.eat(value);
            return value;
        }
        return null;
    }

    /**
     * Updates whole collection POJO->DB
     * @throws Exception
     */
    public void upsertAll() throws Exception{
        for(V value:values()){
            upsert.eat(value);
        }
    }

    /**
     * Inserts new object to DB gets the K from V or DB and returns K
     * @param value
     * @return
     * @throws Exception
     */
    public K insert(V value) throws Exception{
        K key=insert.act(value);
        put(key,value);
        return key;
    }
}
