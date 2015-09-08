package tsc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import tsc.aop.Cacheable;
import tsc.dao.IUserDao;
import tsc.entity.User;
import tsc.redis.IBaseRedisDao;

public class UserDAOImpl implements IUserDao {

	@Autowired
	private IBaseRedisDao<Object, Object> baseRedisDao;

	public void saveUser(final User user) {

		baseRedisDao.set("user.uid." + user.getId(), user.getName(), 10000);

	}

	public User getUser(final long id) {
		User user = new User();
		user.setId(id);
		user.setName((String) baseRedisDao.get("user.uid." + id));
		return user;
	}

	@Override
	public boolean exists(long id) {
		return baseRedisDao.exists("user.uid." + id);
	}

	@Override
	public void del(long id) {
		baseRedisDao.del("user.uid." + id);
	}

	@Override
	public void saveUserBySet(User user) {
		// TODO Auto-generated method stub
		baseRedisDao.hset(user.getAge(), user.getName(), user.getCity());
	}

	@Override
	@Cacheable(key = "#user.getAge()", fieldKey = "#user.getName()", expireTime = 3600)
	public User getUserBySet(User user) {
		// TODO Auto-generated method stub
		// User user = new User();
		System.out.println("查询数据库");
		user.setId(user.getId());
		user.setName(user.getName());
		user.setCity("sh11");
		return user;
	}
}