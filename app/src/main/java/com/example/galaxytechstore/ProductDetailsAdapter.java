package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {

    private int totalTabs;
    private String productDescription;
    private String productOtherDetails;
    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productDescription, String productOtherDetails, List<ProductSpecificationModel> productSpecificationModelList) {
        super(fm, totalTabs);
        this.productDescription = productDescription;
        this.productOtherDetails = productOtherDetails;
        this.productSpecificationModelList = productSpecificationModelList;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ProductDescriptionFragment descriptionFragment = new ProductDescriptionFragment();
                descriptionFragment.body = productDescription;
                return descriptionFragment;
            case 1:
                ProductSpecificationFragment productSpecificationFragment = new ProductSpecificationFragment();
                productSpecificationFragment.list = productSpecificationModelList;
                return productSpecificationFragment;
            case 2:
                ProductDescriptionFragment descriptionFragment1 = new ProductDescriptionFragment();
                descriptionFragment1.body = productOtherDetails;
                return descriptionFragment1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
