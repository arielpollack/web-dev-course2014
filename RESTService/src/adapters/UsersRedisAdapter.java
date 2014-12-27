package adapters;

import models.User;
import models.UserBuilder;

/**
 * Created by arielpollack on 12/27/14.
 */

public class UsersRedisAdapter extends BaseRedisAdapter {

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
        jedis.set("uid:"+user.getId()+":id_number", user.getIdNumber());
        jedis.set("uid:"+user.getId()+":fname", user.getFirstName());
        jedis.set("uid:"+user.getId()+":lname", user.getLastName());
        jedis.set("uid:"+user.getId()+":phone", user.getPhone());
        jedis.set("uid:"+user.getId()+":email", user.getEmail());

        jedis.set("id_number:"+user.getIdNumber()+":uid", user.getId().toString());

        return true;
    }


}
