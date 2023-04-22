//package com.example.euc;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.fragment.app.Fragment;
//
//public class HomeFragment extends Fragment {
//
//    private ImageView addDeviceButton;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
////        // Get a reference to the add device button ImageView
////        addDeviceButton = view.findViewById(R.id.toAddFragment);
////
////        // Set an OnClickListener to launch the AddFragment when the button is clicked
////        addDeviceButton.setOnClickListener(v -> {
////            FragmentManager fragmentManager = getParentFragmentManager();
////            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////            fragmentTransaction.replace(R.id.addFragmentContainer, new AddFragment());
////            fragmentTransaction.addToBackStack(null);
////            fragmentTransaction.commit();
////        });
//
//        // Return the inflated view for the fragment
//        return view;
//    }
//
//}