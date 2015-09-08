package tsc.redis;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Repository;

@Repository
public class BaseRedisDao<K extends Serializable, V extends Serializable>
		implements IBaseRedisDao<K, V> {
	@Autowired
	@Qualifier("redisTemplate")
	protected RedisTemplate<K, V> redisTemplate;
	private RedisSerializer<Object> defaultSerializer = new JdkSerializationRedisSerializer();

	/**
	 * 向redis里面添加key-value格式的数据
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value
	 */
	@Override
	public void set(final K key, final V value, final long expireTime) {
		// TODO Auto-generated method stub
		redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				try {
					// TODO Auto-generated method stub
					byte[] rawKey = defaultSerializer.serialize(key);
					byte[] rawValue = defaultSerializer.serialize(value);
					connection.set(rawKey, rawValue);
					connection.expire(rawKey, expireTime);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return true;
			}
		});

	}

	/**
	 * 根据key从redis里面取出value
	 * 
	 * @param key
	 *            key
	 */
	@Override
	public V get(final K key) {
		// TODO Auto-generated method stub
		return redisTemplate.execute(new RedisCallback<V>() {
			@SuppressWarnings("unchecked")
			@Override
			public V doInRedis(RedisConnection connection)
					throws DataAccessException {
				// TODO Auto-generated method stub
				try {
					byte[] rawKey = defaultSerializer.serialize(key);
					byte[] rawValue = connection.get(rawKey);
					return (V) defaultSerializer.deserialize(rawValue);
				} catch (SerializationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	@Override
	public void del(final K key) {
		// TODO Auto-generated method stub
		redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				// TODO Auto-generated method stub
				try {
					byte[] rawKey = defaultSerializer.serialize(key);
					connection.del(rawKey);
				} catch (SerializationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});

	}

	@Override
	public boolean exists(final K key) {
		// TODO Auto-generated method stub
		return redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				// TODO Auto-generated method stub
				try {
					byte[] rawKey = defaultSerializer.serialize(key);
					return connection.exists(rawKey);
				} catch (SerializationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		});
	}

	@Override
	public void hset(final String key, final String field, final Object o) {
		// TODO Auto-generated method stub
		redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				try {
					// TODO Auto-generated method stub
					byte[] rawKey = defaultSerializer.serialize(key);
					byte[] rawField = defaultSerializer.serialize(field);
					byte[] object = defaultSerializer.serialize(o);
					connection.hSet(rawKey, rawField, object);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return true;
			}
		});
	}

	@Override
	public V hget(final String key, final String field) {
		// TODO Auto-generated method stub
		return redisTemplate.execute(new RedisCallback<V>() {

			@SuppressWarnings("unchecked")
			@Override
			public V doInRedis(RedisConnection connection)
					throws DataAccessException {
				// TODO Auto-generated method stub
				byte[] rawKey = defaultSerializer.serialize(key);
				byte[] rawField = defaultSerializer.serialize(field);
				byte[] object = connection.hGet(rawKey, rawField);
				return (V) defaultSerializer.deserialize(object);
			}
		});
	}

}
