package com.ciandt.worldwonders.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ciandt.worldwonders.R;
import com.ciandt.worldwonders.helpers.Helpers;
import com.ciandt.worldwonders.model.Bookmark;
import com.ciandt.worldwonders.model.Wonder;
import com.ciandt.worldwonders.repository.WondersRepository;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by andersonr on 27/08/15.
 */
public class WonderDetailFragment extends AppCompatDialogFragment {
    private final String EXTRA_WONDER = "wonder";
    private Wonder wonder;
    WondersRepository repository;
    Bookmark bookmark;
    private ShareActionProvider mShareActionProvider;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView descriptionWonder;
    private TextView linkWonder;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if(Helpers.isTablet(getContext())) {
           return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            return inflater.inflate(R.layout.fragment_detail, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        descriptionWonder = (TextView) view.findViewById(R.id.detail_description_wonder);
        linkWonder = (TextView) view.findViewById(R.id.detail_link_wonder);
        imageView = (ImageView) view.findViewById(R.id.detail_image_wonder);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        Bundle args = getArguments();
        if (args == null) {
            wonder = (Wonder) getActivity().getIntent().getSerializableExtra(EXTRA_WONDER);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            wonder = (Wonder) args.getSerializable(EXTRA_WONDER);
        }
        if (wonder != null) {
            setWonder(wonder);
        }
    }

    static WonderDetailFragment show(Wonder wonder, FragmentManager fragmentManager) {
        WonderDetailFragment wonderDialogFragment = new WonderDetailFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("wonder", wonder);
        wonderDialogFragment.setArguments(bundle);

        wonderDialogFragment.show(fragmentManager, "wonder_dialog");

        return wonderDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail, null);

        Bundle args = getArguments();
        Wonder wonder = (Wonder) args.getSerializable("wonder");

        AlertDialog alertDialog = createAlertDialog(view, wonder);
        onViewCreated(view, savedInstanceState);
        return alertDialog;
    }

    @NonNull
    private AlertDialog createAlertDialog(View view, Wonder wonder) {
        return new AlertDialog
                .Builder(getActivity())
                .setTitle(wonder.getName())
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void setWonder(final Wonder wonder) {
        this.wonder = wonder;
        collapsingToolbarLayout.setTitle(wonder.getName());
        descriptionWonder.setText(wonder.getDescription());
        linkWonder.setText(wonder.getUrl());
        linkWonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlLinkFragment.show(wonder, getFragmentManager());
            }
        });

        setImageOnDetail();
    }

    private void setImageOnDetail() {

        Picasso.with(getContext())
                .load(Helpers.getRawResourceID(getContext(), wonder.getPhoto().split("\\.")[0]))
                .config(Bitmap.Config.ARGB_8888)
                .placeholder(R.raw.place_holder)
                .error(R.raw.place_holder)
                .into(imageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        checkWonderDirections(menu.findItem(R.id.action_direction));

        checkWonderHasBookmark(menu.findItem(R.id.action_bookmark));
    }

    private void checkWonderDirections(MenuItem menuItem) {
        if (wonder.getLatitude() == 0.0 && wonder.getLongitude() == 0.0) {
            menuItem.setVisible(false);
        }
    }

    private void checkWonderHasBookmark(final MenuItem menuItem) {
        repository = new WondersRepository(getContext());
        repository.getBookmarkByWonder(wonder.getId(), new WondersRepository.BookmarkByWonderListener() {
            @Override
            public void onBookmarkByWonder(Exception exception, Bookmark bookmark) {
                checkBookmarkOnWonder(bookmark, menuItem);
            }
        });

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    private void checkBookmarkOnWonder(Bookmark bookmark, final MenuItem menuItem) {
        if (bookmark != null && bookmark.getIdWonders() != 0) {
            this.bookmark = bookmark;
            menuItem.setIcon(R.drawable.ic_bookmark_white_24dp);
        } else {
            menuItem.setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_share:
                shareAction();
                break;

            case R.id.action_bookmark:
                addBookmark(item);

                break;

            case R.id.action_direction:
                directionAction();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareAction() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, wonder.getDescription());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void addBookmark(MenuItem menuItem) {
        repository = new WondersRepository(getContext());
        if(bookmark == null) {
            insertBookmark(menuItem);
        } else {
            removeBookmark(menuItem);
        }
    }

    private void insertBookmark(final MenuItem menuItem) {
        bookmark = new Bookmark();
        bookmark.setIdWonders(wonder.getId());
        repository.insertBookmark(bookmark, new WondersRepository.BookmarkInsertListener() {
            @Override
            public void onBookmarkInsert(Exception exception, Boolean result) {
                isInserted(result, menuItem);
            }
        });
    }

    private void isInserted(Boolean result, MenuItem menuItem) {
        if(result) {
            Toast.makeText(getContext(), "Bookmark salvo com sucesso.", Toast.LENGTH_SHORT).show();
            menuItem.setIcon(R.drawable.ic_bookmark_white_24dp);
        } else {
            Toast.makeText(getContext(), "Erro ao salvar o Bookmark.", Toast.LENGTH_SHORT).show();
            menuItem.setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }
    }

    private void removeBookmark(final MenuItem menuItem) {
        repository.deleteBookmark(bookmark, new WondersRepository.BookmarkDeleteListener() {
            @Override
            public void onBookmarkDelete(Exception exception, Boolean result) {
                isDeleted(result, menuItem);
                bookmark = null;
            }
        });
    }

    private void isDeleted(Boolean result, MenuItem menuItem) {
        if(result) {
            Toast.makeText(getContext(), "Bookmark removido com sucesso.", Toast.LENGTH_SHORT).show();
            menuItem.setIcon(R.drawable.ic_bookmark_border_white_24dp);
        } else {
            Toast.makeText(getContext(), "Erro ao remover o Bookmark.", Toast.LENGTH_SHORT).show();
            menuItem.setIcon(R.drawable.ic_bookmark_white_24dp);
        }
    }

    private void directionAction() {
        //Usar String Builder
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ wonder.getLatitude() + "," +  wonder.getLongitude());

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setData(gmmIntentUri);

        if(mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            mapIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
        }
    }
}
