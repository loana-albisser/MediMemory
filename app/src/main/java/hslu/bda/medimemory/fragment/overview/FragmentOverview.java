package hslu.bda.medimemory.fragment.overview;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;

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
        Bitmap defaultPillPicture = BitmapFactory.decodeResource(getResources(), R.drawable.example_pill);

        allPills = Data.getAllDataFromTable(dbAdapter);

        for(Data pill: allPills){
            addPage(pill.getDescription(),pill.getPicture(), pill.getId());
        }

        if (count ==0){
           addPage("Noch kein Medikament erfasst",defaultPillPicture,0);
        }
        viewPager.setCurrentItem(0);

        return root;
    }

    private void getIDs(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        adapter = new FragmentOverviewPagerAdapter(getFragmentManager(), getActivity());
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
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
        }
    }


}
