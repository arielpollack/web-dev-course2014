package adapters;

import models.User;
import models.UserBuilder;
import redis.clients.jedis.Transaction;

/**
 * Created by arielpollack on 12/27/14.
 */

public class UsersRedisAdapter extends BaseRedisAdapter {

    protected String UID_Prefix = "user:";

    public UsersRedisAdapter() {
        super();
    }

    public User getUserWithID(String id)
    {
        String idNumber = jedis.get("uid:"+id+":id_number");
        if (idNumber == null || idNumber.length() == 0)
        {
            return null;
        }

        String email = jedis.get("uid:"+id+":email");
        String phone = jedis.get("uid:"+id+":phone");
        String firstName = jedis.get("uid:"+id+":fname");
        String lastName = jedis.get("uid:"+id+":lname");

        User user = new UserBuilder().firstName(firstName).lastName(lastName)
                                    .phone(phone).email(email)
                                    .idNumber(idNumber)
                                    .buildUser();

        return user;
    }

    public Boolean insert(User user)
    {
        Transaction t = jedis.multi();
        t.set("uid:"+user.getId()+":id_number", user.getIdNumber());
        t.set("uid:"+user.getId()+":fname", user.getFirstName());
        t.set("uid:"+user.getId()+":lname", user.getLastName());
        t.set("uid:"+user.getId()+":phone", user.getPhone());
        t.set("uid:"+user.getId()+":email", user.getEmail());

        t.set("id_number:"+user.getIdNumber()+":uid", user.getId());
        t.exec();

        return true;
    }


}
