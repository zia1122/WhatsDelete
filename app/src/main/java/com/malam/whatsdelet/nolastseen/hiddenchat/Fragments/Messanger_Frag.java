package com.malam.whatsdelet.nolastseen.hiddenchat.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.NotesAdapter_Messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.WrapContentLinearLayoutManager;
import com.whatsdelete.Test.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

public class Messanger_Frag extends Fragment {
    View root_view;
    Context context;

    private RecyclerView recyclerView;
    public static NotesAdapter_Messenger mAdapter;
    private List<Note_messenger> notesList = new ArrayList<>();
    DatabaseHelper_messenger db;
    LinearLayout noChat;
    ProgressBar progressbar;
    Boolean loading_list;
    public static UnifiedNativeAd unifiedNativeAd=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        if (root_view == null) {
            root_view = LayoutInflater.from(context).inflate(R.layout.fragment_whatsapp_chat, container, false);

            db = new DatabaseHelper_messenger(context);
            LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, new IntentFilter(getResources().getString(R.string.intent_name_messenger)));
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh, new IntentFilter(getResources().getString(R.string.intent_name_refresh_messenger)));
            initilize_components();
            new bg_task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
       }
        return root_view;
    }

    private void initilize_components() {

        progressbar = (ProgressBar) root_view.findViewById(R.id.progressbar);
        noChat = (LinearLayout) root_view.findViewById(R.id.noChat);
        recyclerView = root_view.findViewById(R.id.recyclerView);
        /*recyclerView.setLayoutManager(mLayoutManager);*/
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));

    }


    public class bg_task extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading_list = true;
            progressbar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            initlize_list();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAdapter = new NotesAdapter_Messenger(context, notesList);
           // recyclerView.getRecycledViewPool().clear();
            recyclerView.setAdapter(mAdapter);

            progressbar.setVisibility(View.GONE);
            if (notesList.size() < 1) {
                noChat.setVisibility(View.VISIBLE);
            } else noChat.setVisibility(View.GONE);
            try {
                if (loading_list)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loading_list = false;
                            recyclerView.setVisibility(View.VISIBLE);
                            progressbar.setVisibility(View.GONE);
                            NotesAdapter_Messenger.selected_pos.clear();
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_messenger_pref), false, context);
                        }
                    }, 100);
            } catch (Exception e) {
            }
            try {
                Collections.sort(notesList, new Comparator<Note_messenger>() {
                    @Override
                    public int compare(Note_messenger o1, Note_messenger o2) {
                        return o2.getTime_n_date_4_itration().compareTo(o1.getTime_n_date_4_itration());
                    }
                });
            } catch (NullPointerException ner) {
                Log.d("whatsdelet", "Eception " + ner);
            }catch (ConcurrentModificationException asd){}catch (Exception asd){}
        }
    }

    private void initlize_list() {
        notesList.clear();
        notesList.addAll(db.getAllNotes());
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                bg_task bg_task = new bg_task();
                if (bg_task.getStatus() == AsyncTask.Status.FINISHED) {
                    loading_list = false;
                }
                else if (bg_task.getStatus() != AsyncTask.Status.RUNNING){
                    loading_list = false;
                }
            } catch (NullPointerException asd) {
            } catch (Exception asd) {
            }
            if (loading_list) {
                return;
            }
            try {
                notesList.clear();
                notesList.addAll(db.getAllNotes());
                mAdapter.notifyDataSetChanged();
                if (notesList.size() < 1) {
                    noChat.setVisibility(View.VISIBLE);
                } else noChat.setVisibility(View.GONE);
            } catch (NullPointerException asd) {
            } catch (Exception asd) {
            }

            try {
                Collections.sort(notesList, new Comparator<Note_messenger>() {
                    @Override
                    public int compare(Note_messenger o1, Note_messenger o2) {
                        return o2.getTime_n_date_4_itration().compareTo(o1.getTime_n_date_4_itration());
                    }
                });
            } catch (NullPointerException ner) {
                Log.d("whatsdelet", "Eception " + ner);
            } catch (ConcurrentModificationException asdf) {
            } catch (Exception asd) {
            }

        }
    };

    private BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra("package");
            String title_ = intent.getStringExtra("title");
            String text_ = intent.getStringExtra("text");
            String ticker = intent.getStringExtra("ticker");
            new bg_task().execute();
        }
    };

}
