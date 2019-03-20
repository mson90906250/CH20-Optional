package tw.tcnr01.m2008;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class M2008 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2008);
        setupViewComponent();

    }

    private void setupViewComponent() {
        for (int i = 0; i < 4; i++) {
//            String httpname = "http://oldpa88.000webhostapp.com/pic/";
//            String httpname = "http://www.oldpa.tw/pic/"
            String httpname = "http://oldpa88.000webhostapp.com/pic/"
                    + "p" + String.format("%02d", i + 1)
                    + ".jpg";
            String idName = "iv" + String.format("%02d", i + 1);

            String[] imgURL = {"https://www.akc.org/wp-content/themes/akc/component-library/assets/img/welcome.jpg","https://cdn1.medicalnewstoday.com/content/images/articles/322/322868/golden-retriever-puppy.jpg",
            "https://d17fnq9dkz9hgj.cloudfront.net/uploads/2018/03/Pomeranian_01.jpeg","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQPEJMQ5GSRuA7g3GldNkPXhXxornJQ9mfhLYzAQKsKySLas-Vc"};

            int resID = getResources().getIdentifier(idName, "id", getPackageName());
            ImageView showimg = (ImageView) findViewById(resID);
            Picasso.with(this)
                    .load(imgURL[i])
                    .into(showimg);
        }

//        ImageView showimg = (ImageView) findViewById(R.id.iv01);
//        Picasso.with(this).load("Http://xxxxx/pic/p01.jpg").into(showimg);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; 增加items到action bar.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
