package tsc.aop;


import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import tsc.redis.IBaseRedisDao;
@Aspect
@Component
public class CacheAspect {
//	@Autowired
//	private RedisTemplate<?, ?> redisTemplate;
	@Autowired
	private IBaseRedisDao<Object, Object> baseRedisDao;
	@Around("@annotation(Cacheable)")
	public Object cache(ProceedingJoinPoint pjp) {
		Object result = null;

		Method method = getMethod(pjp);
		Cacheable cacheable = method.getAnnotation(Cacheable.class);
		String key = parseKey(cacheable.key(), method, pjp.getArgs());
		String fieldKey = parseKey(cacheable.fieldKey(), method, pjp.getArgs());

//		HashOperations valueOper = redisTemplate.opsForHash();
//		result = valueOper.get(key, fieldKey);
		result =baseRedisDao.hget(key,fieldKey);
		if (result == null) {
			try {
				result = pjp.proceed();
				//Assert.notNull(fieldKey);
				// redisTemplate.hset(cacheable.key(), fieldKey, result);
				//valueOper.put(key, fieldKey, result);
				baseRedisDao.hset(key,fieldKey,result);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return result;
	}

//	/** * 定义清除缓存逻辑 */
//	@Around(value = "@annotation(org.myshop.cache.annotation.CacheEvict)")
//	public Object evict(ProceedingJoinPoint pjp) {
//		// 和cache类似，使用Jedis.hdel()删除缓存即可...
//		return null;
//
//	}

	/**
	 * 获取被拦截方法对象
	 * 
	 * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象 而缓存的注解在实现类的方法上
	 * 所以应该使用反射获取当前对象的方法对象
	 */
	@SuppressWarnings("rawtypes")
	public Method getMethod(ProceedingJoinPoint pjp) {
		// 获取参数的类型
		Object[] args = pjp.getArgs();
		Class[] argTypes = new Class[pjp.getArgs().length];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		Method method = null;
		try {
			method = pjp.getTarget().getClass()
					.getMethod(pjp.getSignature().getName(), argTypes);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return method;

	}

	/**
	 * 获取缓存的key key 定义在注解上，支持SPEL表达式
	 * 
	 * @param pjp
	 * @return
	 */
	private String parseKey(String key, Method method, Object[] args) {

		// 获取被拦截方法参数名列表(使用Spring支持类库)
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNameArr = u.getParameterNames(method);

		// 使用SPEL进行key的解析
		ExpressionParser parser = new SpelExpressionParser();
		// SPEL上下文
		StandardEvaluationContext context = new StandardEvaluationContext();
		// 把方法参数放入SPEL上下文中
		for (int i = 0; i < paraNameArr.length; i++) {
			context.setVariable(paraNameArr[i], args[i]);
		}
		return parser.parseExpression(key).getValue(context, String.class);
	}

}
