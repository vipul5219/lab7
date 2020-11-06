package ca.mobile.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnget = findViewById(R.id.btnGet);

        final EditText editName = findViewById(R.id.editName);
        final EditText editGrade = findViewById(R.id.editGrade);
        final TextView out = findViewById(R.id.output);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(StudentProvider.NAME,editName.getText().toString());
                values.put(StudentProvider.GRADE,editGrade.getText().toString());

                Uri uri = getContentResolver().insert(StudentProvider.CONTENT_URI,values);
                Toast.makeText(MainActivity.this, uri.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        btnget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = managedQuery(StudentProvider.CONTENT_URI,null,null,null,null);
                String msg = "";
                if(c!=null&&c.moveToFirst())
                {

                    do{
                        msg+=c.getString(c.getColumnIndex(StudentProvider.ID))+", "+c.getString(c.getColumnIndex(StudentProvider.NAME))+
                                ", "+c.getString(c.getColumnIndex(StudentProvider.GRADE));

                        msg+="\n";

                    }while (c.moveToNext());
                }
                else {
                    msg = "No data";
                }
                out.setText(msg);
            }
        });
    }
}