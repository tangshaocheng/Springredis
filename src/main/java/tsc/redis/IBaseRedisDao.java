package tsc.redis;

public interface IBaseRedisDao<K, V> {
	public void set(final K key, final V value, final long expireTime);

	public V get(final K key);

	public void del(final K key);

	public boolean exists(final K key);

	public void hset(final String key, final String field, final Object o);
	
	public V hget(final String key, final String field);

	//public <T> T hget(String key, String field, Class<T> clazz);

	//public void hdel(String key, String... field);
}
