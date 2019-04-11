package com.example.placenotebook;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<Bitmap> placesImage;  //eski kayıtlara mainden tıklandıgında detailine gecmek icin



    /*menuyu tanıt*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);
        return super.onCreateOptionsMenu(menu);
    }



    /*menu itemına tıklandığında*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_place){
            Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
            intent.putExtra("info","new");                               //eski resim mi görüntülenecek-yeni mi eklenecek
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);


        /* uygulama acıldıgında eski kaydedilenlerin gelmesi icin-dataları almak icin arraylist olustur adapterla bagla
        * database islemleri ile dataları al bagladıgın arraylere aktar */
        final ArrayList<String> placesName = new ArrayList<String>();
        placesImage = new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,placesName);
        listView.setAdapter(arrayAdapter);

        try{
            DetailActivity.database = this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
            DetailActivity.database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, image BLOB)");
            Cursor cursor = DetailActivity.database.rawQuery("SELECT * FROM places",null);       //data cekmek incn cursor |
            int nameIx = cursor.getColumnIndex("name");
            int imageIx = cursor.getColumnIndex("image");
            cursor.moveToFirst();

            while (cursor != null){
                placesName.add(cursor.getString(nameIx));                                                   //buldugun ismi nameix e ekle

                byte[] byteArray = cursor.getBlob(imageIx);
                Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                placesImage.add(image);                                                                     //buldugun imageı imageix e ekle
                cursor.moveToNext();
                arrayAdapter.notifyDataSetChanged();                                                        //data degistiyse güncelle getir
            }

        }catch (Exception e){
            e.printStackTrace();
        }





        /* eski kayıtlara tıklandıgında onun detail sayfası acılıcak vs o sayfaya image text aktarma */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("name" , placesName.get(position));
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });

    }
}
