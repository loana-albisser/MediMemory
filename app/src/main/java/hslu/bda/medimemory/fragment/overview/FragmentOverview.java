package hslu.bda.medimemory.fragment.overview;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.fragment.MainActivity;

/**
 * Created by Loana on 04.03.2016.
 */
public class FragmentOverview extends Fragment {

    private ViewGroup root;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentOverviewPagerAdapter adapter;
    private int count = 0;
    private String pagename;
    private Drawable pillPicture;
    private int selectedTabPosition;
    private Collection<Data> allPills;
    private DbAdapter dbAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_overview, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        getIDs(root);
        setEvents();

        allPills = Data.getAllDataFromTable(dbAdapter);

        if (allPills.size() ==0){
            root = (ViewGroup) inflater.inflate(R.layout.fragment_overview_noitem, container, false);
        } else {
            for(Data pill: allPills){
                addPage(pill.getDescription(),pill.getPicture(), pill.getId());
            }
        }
        viewPager.setCurrentItem(0);

        return root;
    }

    @Override
    public void onResume(){


        super.onResume();
    }


   private void getIDs(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        //adapter = new FragmentOverviewPagerAdapter(getFragmentManager(), getActivity());
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
           adapter = new FragmentOverviewPagerAdapter(getChildFragmentManager(),getActivity());
       } else {
           adapter = new FragmentOverviewPagerAdapter(getFragmentManager(),getActivity());
       }
       viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
    }

    private void setEvents() {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();
                Log.d("Selected", "Selected " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
    }

    /**
     * add Page to the viewPager
     * @param pagename name of the pill
     * @param pillPicture picture of the pill
     */
    private void addPage(String pagename, Bitmap pillPicture, int id) {
        Bundle bundle = new Bundle();
        bundle.putString("pagename", pagename);
        bundle.putParcelable("pillPicture", pillPicture);
        bundle.putInt("id", id);
        FragmentOverviewChild fragmentChild = new FragmentOverviewChild();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0){
            tabLayout.setupWithViewPager(viewPager);
        }

        viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();
        count ++;
    }

    private void setupTabLayout() {
        selectedTabPosition = viewPager.getCurrentItem();
        if (tabLayout != null){
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
            }
        }

    }


}
