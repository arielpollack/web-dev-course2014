package adapters;

import java.sql.Connection;

/**
 * Created by arielpollack on 12/27/14.
 */
public class BaseJDBCAdepter {

    Connection conn;

    public BaseJDBCAdepter()
    {
        conn = DBManager.getConnection();
    }

    public void createTable() throws Exception {
        throw new Exception("Class hasn't implemented create table method");
    }
}
