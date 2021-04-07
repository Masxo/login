package com.bs.login;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Communicator between activities & fragments
 */
public interface FragmentActivityCommunicator {
    void updateFragment(Fragment fragment);
    void updateDatabase(Map<String,Object> data);
}
