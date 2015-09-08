package tsc.dao;

import tsc.entity.User;

public interface IUserDao {
	public void saveUser(User user);

	public User getUser(long id);

	public boolean exists(long id);

	public void del(long id);

	public void saveUserBySet(User user);

	public User getUserBySet(User user);
}
