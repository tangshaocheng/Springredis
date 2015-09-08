import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tsc.dao.IUserDao;
import tsc.entity.User;

public class RedisTest {
	@Test
	public void testAddUesr() {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:/applicationContext.xml");
		IUserDao userDao = (IUserDao) ac.getBean("userDAO");
		// User user1 = new User();
		// user1.setId(1);
		// user1.setName("obama");
		// System.out.println(userDao.exists(1));
		// userDao.saveUser(user1);
		// userDao.del(1);
		// System.out.println(userDao.exists(1));
		// User user2 = userDao.getUser(3);
		// System.out.println(user2.getName());
		User user = new User();
		user.setId(5);
		user.setName("tom");
		user.setAge("20");
		// user.setCity("sh");
		// userDao.saveUserBySet(user);
		userDao.getUserBySet(user);
		System.out.println(user.getAge());
	}

}
