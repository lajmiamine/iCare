package first.com.restyourmind;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private CalendarView calendar;

    ArrayList<CalendarEvent> arrayList = new ArrayList<CalendarEvent>();
    ArrayList<CalendarEvent> arrayListByDay = new ArrayList<>();
    ListView listView;

    JSONObject jObj=null;

    int[] currentDate;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar_test);
        currentDate = getCurrentDate();

        new gettingCalendar().execute();
        initializeCalendar();

        listView = (ListView) findViewById(R.id.listView);
        String[] values = new String[] { "Select a day" };
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initializeCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendar);
        calendar.setShowWeekNumber(false);
        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                arrayListByDay = new ArrayList<CalendarEvent>();
                for (int i=0;i<arrayList.size();i++){
                    if (arrayList.get(i).isTheDay(year,month+1,day) == true) {
                        arrayListByDay.add(arrayList.get(i));
                    }
                }
                if (arrayListByDay.size()!= 0) {
                    final UsersAdapter adapter = new UsersAdapter(MainActivity.this, arrayListByDay);
                    listView.setAdapter(adapter);
                }
                else {
                    ArrayList<String> arrayListNoEvents = new ArrayList<String>();
                    String[] values = new String[] { "No events" };
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, values);
                    listView.setAdapter(adapter);
                }

            }
        });
    }

    public class UsersAdapter extends ArrayAdapter<CalendarEvent> {
        public UsersAdapter(Context context, ArrayList<CalendarEvent> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            CalendarEvent event = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
            }

            TextView Date = (TextView) convertView.findViewById(R.id.Date);
            TextView ID = (TextView) convertView.findViewById(R.id.ID);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

            Date.setText("Medicine taken at:"+event.hour+":"+event.minute);
            ID.setText("From the drawer of "+String.valueOf(event.FK_ID_Drawer));

            return convertView;
        }
    }

    public int[] getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
        Date date = new Date();
        String currentDate = dateFormat.format(date);

        int[] cDate= new int[5];
        String[] s =currentDate.split("\\.");
        cDate[0]=Integer.parseInt(s[0]);
        cDate[1]=Integer.parseInt(s[1]);
        cDate[2]=Integer.parseInt(s[2]);
        String[] s1 = s[3].split("\\:");
        cDate[3] = Integer.parseInt(s1[0]);
        cDate[4]=Integer.parseInt((s1[1]));

        return cDate;
    }


    /*****************************Getting calendar****************************/

    class gettingCalendar extends AsyncTask<String, String, Void>
    {
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            progressDialog.setMessage("Fetching");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    gettingCalendar.this.cancel(true);
                }
            });
        }

        protected Void doInBackground(String... params){

            Thread t = new Thread() {

                public void run() {
                    Looper.prepare(); //For Preparing Message Pool for the child Thread
                    HttpClient client = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10001); //Timeout Limit
                    HttpResponse response;
                    JSONObject json = new JSONObject();

                    try {
                        HttpPost post = new HttpPost("http://first-contracting.com/php_files/getCalandara.php");
                        StringEntity se = new StringEntity( json.toString());
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        post.setEntity(se);
                        response = client.execute(post);

                        if(response!=null){
                            InputStream in = response.getEntity().getContent();
                            String is = convertStreamToString(in);
                            progressDialog.dismiss();
                            Log.d("result", is);

                            try {
                                jObj = new JSONObject(is);
                            } catch (JSONException e) {
                                Log.e("JSON Parser", "Error parsing data " + e.toString());
                            }

                            int Events_Number = jObj.getInt("Events_Number");
                            JSONObject jo = null;

                            for (int i = 1; i<Events_Number+1;i++){
                                jo = jObj.getJSONObject(""+i);
                                arrayList.add(new CalendarEvent(jo));
                            }
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    Looper.loop(); //Loop in the message queue
                }
            };

            t.start();

            return null;
        }

        protected void onPostExecute(Void v) {
        }
    }

    static String convertStreamToString(java.io.InputStream is)
    {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String getReadableDateString(long time)
    {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
        return format.format(date).toString();
    }
    /************************************************************************/

    private void toDate(String hour, String minutes) {

    }

}