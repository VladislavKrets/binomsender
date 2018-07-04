package online.omnia.binomsender;


import java.util.List;

/**
 * Created by lollipop on 12.07.2017.
 */
public interface MySQLDao {
    List<PostBackEntity> getPostbacks(String status, String advName, int from, int offset);
}
