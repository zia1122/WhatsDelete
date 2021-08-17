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
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.NotesAdapter_Instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.NotesAdapter_Messenger;
import com.malam.whatsdelet.nolastseen.hiddenchat.Helper.DatabaseHelper_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Model.Note_instagram;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.WrapContentLinearLayoutManager;
import com.whatsdelete.Test.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

public class Instagram_Frag extends Fragment {
    View root_view;
    Context context;

    private RecyclerView recyclerView;
    public static NotesAdapter_Instagram mAdapter;
    private List<Note_instagram> notesList = new ArrayList<>();
    DatabaseHelper_instagram db;
    LinearLayout noChat;
    ProgressBar progressbar;
    public static UnifiedNativeAd unifiedNativeAd = null;
    Boolean loading_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        if (root_view == null) {
            root_view = LayoutInflater.from(context).inflate(R.layout.fragment_whatsapp_chat, container, false);

            db = new DatabaseHelper_instagram(context);
            LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, new IntentFilter(getResources().getString(R.string.intent_name_instagram)));
            LocalBroadcastManager.getInstance(context).registerReceiver(refresh, new IntentFilter(getResources().getString(R.string.intent_name_refresh_insta)));
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

            mAdapter = new NotesAdapter_Instagram(context, notesList);
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
                            NotesAdapter_Instagram.selected_pos.clear();
                            Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_instagram_pref), false, context);

                        }
                    }, 100);

            } catch (Exception e) {
            }

            try {
                Collections.sort(notesList, new Comparator<Note_instagram>() {
                    @Override
                    public int compare(Note_instagram o1, Note_instagram o2) {
                        return o2.getTime_n_date_4_itration().compareTo(o1.getTime_n_date_4_itration());
                    }
                });
            } catch (NullPointerException ner) {
                Log.d("whatsdelet", "Eception " + ner);
            } catch (ConcurrentModificationException asd) {
            } catch (Exception asd) {
            }
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
                } else if (bg_task.getStatus() != AsyncTask.Status.RUNNING) {
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
                Collections.sort(notesList, new Comparator<Note_instagram>() {
                    @Override
                    public int compare(Note_instagram o1, Note_instagram o2) {
                        return o2.getTime_n_date_4_itration().compareTo(o1.getTime_n_date_4_itration());
                    }
                });
            } catch (NullPointerException ner) {
            } catch (ConcurrentModificationException asdf) {
            } catch (Exception asd) {
            }
            /* new bg_task().execute();*/
        }
    };

    public class bg_task_update extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<Note_instagram> notesList_1 = new ArrayList<>();
            notesList_1.addAll(db.getAllNotes());
            try {

                Collections.sort(notesList_1, new Comparator<Note_instagram>() {
                    @Override
                    public int compare(Note_instagram o1, Note_instagram o2) {
                        return o2.getTime_n_date_4_itration().compareTo(o1.getTime_n_date_4_itration());
                    }
                });
            } catch (NullPointerException ner) {
                Log.d("whatsdelet", "Eception " + ner);
            }
            for (int i = 0; i < notesList_1.size(); i++) {
                Boolean availbe = false;
                try {
                    for (int j = 0; j < notesList.size(); j++) {
                        if (notesList_1.get(i).getNote().equals(notesList.get(j).getNote())) {

                            System.out.println("if condition ==> " + notesList_1.get(i).getNumber());
                            availbe = true;
                        }
                        notesList.get(i).setNumber(notesList_1.get(i).getNumber());
                        notesList.get(i).setNote(notesList_1.get(i).getNote());
                        notesList.get(i).setTime(notesList_1.get(i).getTime());

                        notesList.get(i).setTimestamp(notesList_1.get(i).getTimestamp());
                        notesList.get(i).setTime_n_date_4_itration(notesList_1.get(i).getTime_n_date_4_itration());
                    }
                    if (!availbe) {
                        notesList.add(notesList_1.get(i));
                    }
                } catch (IndexOutOfBoundsException asd) {
                } catch (Exception er) {
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAdapter.notifyDataSetChanged();
            NotesAdapter_Messenger.selected_pos.clear();
        }
    }

    private BroadcastReceiver refresh = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            new bg_task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };
}
