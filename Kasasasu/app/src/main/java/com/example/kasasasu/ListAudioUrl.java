package com.example.kasasasu;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shoubin on 8/26/16.
 */
public class ListAudioUrl extends Fragment {

    String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

    private List<String> lstFile =new ArrayList<String>();

    public void GetFiles(String Path, String Extension,boolean IsIterative)
    {
        File[] files =new File(Path).listFiles();

        for (File f : files) {
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))
                    lstFile.add(f.getPath());

                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && !f.getPath().contains("/."))
                GetFiles(f.getPath(), Extension, IsIterative);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = getActivity();
        View main_view = inflater.inflate(R.layout.audio_list, null);
        GetFiles(mFileName, "3gp", false);

        ListView audioList = (ListView) main_view.findViewById(R.id.audioListView);

        audioList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lstFile));

        return main_view;
    }

}
