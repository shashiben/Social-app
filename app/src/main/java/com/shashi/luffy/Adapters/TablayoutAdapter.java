package com.shashi.luffy.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shashi.luffy.Profile_Options.fragments.AboutFragment;
import com.shashi.luffy.Profile_Options.fragments.FollowersFragment;
import com.shashi.luffy.Profile_Options.fragments.FollowingFragment;
import com.shashi.luffy.Profile_Options.fragments.StatusFragment;
import com.shashi.luffy.Profile_Options.fragments.UploadFragment;

import java.util.ArrayList;

public class TablayoutAdapter extends FragmentPagerAdapter  {
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private ArrayList<String> strings=new ArrayList<>();
    public TablayoutAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new StatusFragment();
            case 1:
                return  new UploadFragment();
            case 2:
                return new FollowersFragment();
            case 3:
                return new FollowingFragment();
            case 4:
                return new AboutFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    public void add(Fragment fr,String str){
        fragments.add(fr);
        strings.add(str);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return strings.get(position);
    }
}
