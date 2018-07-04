package online.omnia.binomsender;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by lollipop on 31.08.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        MySQLDaoImpl mySQLDao = MySQLDaoImpl.getInstance();
        TrackerEntity tracker;
        List<PostBackEntity> postBackEntities;
        BinomTracker binomTracker;
        FileWriter writer = new FileWriter("binom.log", true);
        List<PostBackEntity> openPostbacks;
        for (int i = 0; true; i+=100) {
            postBackEntities = mySQLDao.getPostbacks("approved", "CityAds2", i, i+99);
            if (postBackEntities.isEmpty()) break;
            for (PostBackEntity currentPostBackEntity : postBackEntities) {
                openPostbacks = mySQLDao.getPostbackByClickId(currentPostBackEntity.getClickId(), "open");
                for (PostBackEntity postBackEntity : openPostbacks) {
                    tracker = MySQLDaoImpl.getInstance().getTracker(postBackEntity.getPrefix());
                    binomTracker = new BinomTracker(tracker.getDomain() + "/");
                    postBackEntity.setStatus("repaired");

                    try {
                        String url = binomTracker.sendPostback(postBackEntity);
                        writer.write(url);
                        writer.flush();
                        System.out.println(url);
                    } catch (NoClickIdException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        writer.close();
        MySQLDaoImpl.getSessionFactory().close();
    }
}
