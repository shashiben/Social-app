package com.shashi.luffy.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shashi.luffy.Fragments.MessageTabs.AllChats;
import com.shashi.luffy.Fragments.MessageTabs.Favouriteschats;
import com.shashi.luffy.Fragments.MessageTabs.Onlinepeople;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments=new ArrayList<>();
    ArrayList<String> strings=new ArrayList<>();
    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AllChats();
            case 1:
                return new Onlinepeople();
            case 2:
                return new Favouriteschats();
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
