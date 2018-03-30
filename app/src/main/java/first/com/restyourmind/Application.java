package first.com.restyourmind;

import com.parse.Parse;
import com.parse.ParsePush;

/**
 * Created by ASUS on 28/08/2015.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this,
                getResources().getString(R.string.parse_application_id),
                getResources().getString(R.string.parse_client_key));
        ParsePush.subscribeInBackground("Giants");
    }
}
