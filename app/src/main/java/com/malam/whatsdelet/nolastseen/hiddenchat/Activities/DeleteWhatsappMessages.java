package com.malam.whatsdelet.nolastseen.hiddenchat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.malam.whatsdelet.nolastseen.hiddenchat.Adapter.NotesAdapter;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Instagram_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Messanger_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Status_frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Fragments.Whats_App_Frag;
import com.malam.whatsdelet.nolastseen.hiddenchat.Utilz.Shared;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.whatsdelete.Test.R;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeleteWhatsappMessages extends AppCompatActivity {

    View root_view;
    Context context;

    ViewPager viewPager;
    ImageView backButton;
    public static ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = DeleteWhatsappMessages.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_whatsapp_messages);

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    switch (position) {
                        case 0:
                            MainActivity.title.setText(getResources().getString(R.string.chat));
                            MainActivity.refresh.setVisibility(View.VISIBLE);
                            MainActivity.refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                            MainActivity.delete.setVisibility(View.GONE);
                            MainActivity.selected_tab_position = 0;
                            break;

                    }

                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, context);
                        Whats_App_Frag.mAdapter.notifyDataSetChanged();

                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_instagram_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_instagram_pref), false, context);
                        Instagram_Frag.mAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_messenger_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_messenger_pref), false, context);
                        Messanger_Frag.mAdapter.notifyDataSetChanged();
                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_status_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, context);

                    }




                    MainActivity.selected_items = 0;
                } catch (NullPointerException asd) {
                } catch (Exception er) {
                }
                try {
                    NotesAdapter.selected_list.clear();
                } catch (NullPointerException asd) {
                } catch (Exception er) {
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setVisibility(View.GONE);
        viewPagerTab.setViewPager(viewPager);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            finish();
        });



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    switch (position) {
                        case 0:
                            MainActivity.title.setText(getResources().getString(R.string.chat));
                            MainActivity.refresh.setVisibility(View.VISIBLE);
                            MainActivity.refresh_n_whatsapp_layout.setVisibility(View.VISIBLE);
                            MainActivity.delete.setVisibility(View.GONE);
                            MainActivity.selected_tab_position = 1;
                            break;

                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_pref), false, context);

                    }
                    if (Shared.getInstance().getBooleanValueFromPreference(getResources().getString(R.string.long_pressd_status_pref), false, context)) {
                        Shared.getInstance().saveBooleanToPreferences(getResources().getString(R.string.long_pressd_status_pref), false, context);
                        Status_frag.mReAdapter.notifyDataSetChanged();
                    }



                    MainActivity.selected_items = 0;
                } catch (NullPointerException asd) {
                } catch (Exception er) {
                }
                try {
                    NotesAdapter.selected_list.clear();
                } catch (NullPointerException asd) {
                } catch (Exception er) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int position = 0;
        try {
            position = DeleteWhatsappMessages.this.getIntent().getIntExtra(getResources().getString(R.string.chat_position_from_chathead), 0);
        } catch (NullPointerException asd) {
        } catch (Exception asd) {
        }
        if (position>0){
            viewPager.setCurrentItem(position);
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new Whats_App_Frag(), getResources().getString(R.string.whatsApp));
        viewPager.setAdapter(adapter);
    }
    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static class MyObject implements Comparable<MyObject> {

        private Date dateTime;

        public Date getDateTime() {
            return dateTime;
        }

        public void setDateTime(Date datetime) {
            this.dateTime = datetime;
        }

        @Override
        public int compareTo(MyObject o) {
            return getDateTime().compareTo(o.getDateTime());
        }
    }
}