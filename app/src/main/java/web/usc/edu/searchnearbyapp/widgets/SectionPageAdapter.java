package web.usc.edu.searchnearbyapp.widgets;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class SectionPageAdapter extends FragmentPagerAdapter{

    private List<Fragment> mFragmentList= new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();


    public SectionPageAdapter(FragmentManager manager){
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

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public List<Fragment> getmFragmentList() {
        return mFragmentList;
    }

    public List<String> getmFragmentTitleList() {
        return mFragmentTitleList;
    }

    public void setmFragmentList(List<Fragment> mFragmentList) {
        this.mFragmentList = mFragmentList;
    }

    public void setmFragmentTitleList(List<String> mFragmentTitleList) {
        this.mFragmentTitleList = mFragmentTitleList;
    }
}
