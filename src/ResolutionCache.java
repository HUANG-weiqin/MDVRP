import java.util.*;


public class ResolutionCache<K,V> {
    int capacity;
    double backupLimit;
    Hashtable<K,V> backup = new Hashtable<>();
    Hashtable<K,V> map = new Hashtable<>();
    public ResolutionCache(int capacity){
        this.capacity = capacity;
        this.backupLimit = capacity * 0.5f;
    }
    public V get(K key){
        return map.get(key);
    }
    public void put(K key,V val){
        if(map.size()>capacity){
            map = backup;
            backup = new Hashtable<>();
        }
        map.put(key,val);
        if(map.size()>=backupLimit)
            backup.put(key,val);
    }

    public boolean containsKey(K key){
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public void clear(){
        map.clear();
        backup.clear();
    }
}
