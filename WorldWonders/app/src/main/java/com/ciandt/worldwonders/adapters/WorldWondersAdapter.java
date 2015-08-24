package com.ciandt.worldwonders.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ciandt.worldwonders.database.WonderDao;
import com.ciandt.worldwonders.ui.fragments.HighlightFragment;
import com.ciandt.worldwonders.model.Wonder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jfranco on 8/23/15.
 */
public class WorldWondersAdapter extends FragmentPagerAdapter {
    int pageCount;
    List<Wonder> listaWonder;

    public WorldWondersAdapter(FragmentManager fragmentManager, ArrayList<Wonder> listWonder) {

        super(fragmentManager);
        this.pageCount = listWonder.size();
        this.listaWonder = listWonder;
    }

    @Override
    public Fragment getItem(int position) {
        HighlightFragment wonderFragment = HighlightFragment.newInstance(listaWonder.get(position));
        return wonderFragment;
    }

    @Override
    /**
     * Quantidade de páginas
     */
    public int getCount() {
        return this.pageCount;
    }
}