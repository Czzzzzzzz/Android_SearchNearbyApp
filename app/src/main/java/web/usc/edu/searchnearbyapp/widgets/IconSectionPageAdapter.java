package web.usc.edu.searchnearbyapp.widgets;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

public class IconSectionPageAdapter extends SectionPageAdapter {

    protected List<Integer> mIconIdsList = new ArrayList<>();
    protected Context mCtx;

    public IconSectionPageAdapter(FragmentManager fragmentManager, Context ctx) {
        super(fragmentManager);
        mCtx = ctx;
    }


    public void addFragment(Fragment fragment, String title, int iconId) {
        getmFragmentList().add(fragment);
        getmFragmentTitleList().add(title);
        mIconIdsList.add(iconId);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mCtx.getResources().getDrawable(mIconIdsList.get(position));
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString("  " + getmFragmentTitleList().get(position));
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
