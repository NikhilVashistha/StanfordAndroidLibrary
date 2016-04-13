package android.stanford.stanfordandroidlibrary;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;
import stanford.androidlib.*;

@AutoSaveFields
public class MainActivity extends SimpleActivity {
    private String field1;
    private int field2;
    private double field3;
    private boolean field4;
    private ArrayList<String> field5;
    private int[] field6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // values to save/restore
        field1 = "hello";
        field2 = 42;
        field3 = 3.14;
        field4 = true;
        field5 = new ArrayList<>();
        field5.add("yay");
        field5.add("boo");
        field6 = new int[] {64, 17, -3, 0, 9};

        ListView myList = findListView(R.id.mylist);
        ArrayList<String> lines = readFileLines(R.raw.listcontents);
        listSetItems(myList, lines);
        myList.setOnItemClickListener(this);
    }

    @Override
    public void onSpeechToTextReady(String spokenText) {
        toast("You said " + spokenText);
    }

    @Override
    public void onItemClick(ListView list, int index) {
        ArrayList<String> items = listGetItems(list);
        items.remove(index);
        listSetItems(list, items);

//        String item = String.valueOf(list.getItemAtPosition(index));
//        toast("You clicked " + item);
//        speak("You clicked " + item);
//
//        // replace the items
//        ListView myList = findListView(R.id.mylist);
//        ArrayList<String> items = listGetItems(myList);
//        Collections.reverse(items);
//        listSetItems(myList, items);
    }

    public void playSound1(View view) {
        soundHelper(R.raw.cowabunga);
    }

    public void playSound2(View view) {
        soundHelper(R.raw.tmnt);
    }

    private void soundHelper(int id) {
        CheckBox loop = findCheckBox(R.id.loopbox);
        if (soundIsPlaying(id)) {
            soundStop(id);
        } else {
            if (loop.isChecked()) {
                soundLoop(id);
            } else {
                soundPlay(id);
            }
        }
    }

    public void takePhotoClick(View view) {
        takePhoto();
    }

    @Override
    public void onPhotoReady(Bitmap bitmap) {
        ImageView photoShow = findImageView(R.id.photoshow);
        photoShow.setImageBitmap(bitmap);
    }

    public void speakClick(View view) {
        speak("Hooray");
        speechToText("Say your name");
    }
}
