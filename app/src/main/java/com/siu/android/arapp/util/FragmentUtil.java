package com.siu.android.arapp.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by lukas on 6/28/13.
 */
public final class FragmentUtil {

    public static void showDialog(FragmentManager fragmentManager, DialogFragment dialogFragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag(dialogFragment.getClass().getSimpleName());
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        dialogFragment.show(ft, dialogFragment.getClass().getSimpleName());
    }
}
