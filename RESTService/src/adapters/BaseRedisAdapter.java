package adapters;

import redis.clients.jedis.Jedis;

/**
 * Created by arielpollack on 12/27/14.
 */
public class BaseRedisAdapter {

    protected Jedis jedis;

    public BaseRedisAdapter() { jedis = DBManager.getRedis(); }

}
