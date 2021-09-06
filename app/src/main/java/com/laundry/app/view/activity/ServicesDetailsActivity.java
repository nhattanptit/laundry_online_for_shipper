package com.laundry.app.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.laundry.app.R;
import com.laundry.app.control.ApiServiceOperator;
import com.laundry.app.control.DataController;
import com.laundry.app.databinding.ServicesDetailsActivityBinding;
import com.laundry.app.dto.ordercreate.OrderResponse;
import com.laundry.app.dto.ordercreate.OrderServiceDetailForm;
import com.laundry.app.dto.servicelist.ServiceListDto;
import com.laundry.app.dto.sevicedetail.ServiceDetailDto;
import com.laundry.app.dto.sevicedetail.ServicesDetailResponse;
import com.laundry.app.view.adapter.ServicesOrderAdapter;
import com.laundry.base.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServicesDetailsActivity extends BaseActivity<ServicesDetailsActivityBinding> implements ServicesOrderAdapter.IServiceDetailCallback {

    private static final String TAG = "ServiceDetailFragment";
    public static final String KEY_SEND_DATA = "KEY_SEND_DATA";
    private final DataController mDataController = new DataController();
    private ServiceListDto mServiceListDto;
    private List<ServiceDetailDto> mServiceDetails = new ArrayList<>();
    private final ServicesOrderAdapter mServicesOrderAdapter = new ServicesOrderAdapter();

    @Override
    protected int getLayoutResource() {
        return R.layout.services_details_activity;
    }

    @Override
    public void onPreInitView() {
        mServiceListDto = (ServiceListDto) getIntent().getSerializableExtra(KEY_SEND_DATA);
    }

    @Override
    public void onInitView() {
        beforeCallApi();
        binding.servicesDetailRecycle.setAdapter(mServicesOrderAdapter);
        if (mServiceListDto != null) {
            binding.toolbar.setTitle(mServiceListDto.name);
            mDataController.getServicesDetail(mServiceListDto.id, new ServiceDetailCallBack());
        }

        binding.toolbar.setToolbarListener(view -> {
            onBackPressed();
        });

        mServicesOrderAdapter.setCallback(this);
    }

    @Override
    public void onViewClick() {

        binding.bookButton.setOnClickListener(view -> {
            List<ServiceDetailDto> serviceDetailDtos = new ArrayList<>();
            Intent intent = new Intent(this, OrderConfirmActivity.class);
            for (int i = 0; i < mServiceDetails.size(); i++) {
                if (mServiceDetails.get(i).quantity > 0)
                    serviceDetailDtos.add(mServiceDetails.get(i));
            }
            intent.putExtra("DTO", (Serializable) serviceDetailDtos);
            startActivity(intent);
            List<OrderServiceDetailForm> list = new ArrayList<>();
            list.add(new OrderServiceDetailForm(3, 1));
            mDataController.createOrder(this, 3, 1, list, "Ha Noi",
                    new ApiServiceOperator.OnResponseListener<OrderResponse>() {
                        @Override
                        public void onSuccess(OrderResponse body) {
                            Log.d(TAG, "onSuccess: " + body.toString());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClickItem(int position, ServiceDetailDto item) {
        binding.money.setText(grandTotal(mServiceDetails) + "$");
    }

    private Double grandTotal(List<ServiceDetailDto> list) {
        Double totalPrice = 0.0;
        for (int i = 0; i < list.size(); i++) {
            totalPrice += list.get(i).totalPrice;
        }
        return totalPrice;
    }

    private void beforeCallApi() {
        binding.progressBar.maskviewLayout.setVisibility(View.VISIBLE);
    }

    private void afterCallApi() {
        binding.priceLayout.setVisibility(View.VISIBLE);
        binding.progressBar.maskviewLayout.setVisibility(View.GONE);
    }

    private class ServiceDetailCallBack implements ApiServiceOperator.OnResponseListener<ServicesDetailResponse> {
        @Override
        public void onSuccess(ServicesDetailResponse body) {
            mServicesOrderAdapter.submitList(body.servicesDetails);
            mServiceDetails = body.servicesDetails;
            afterCallApi();
        }

        @Override
        public void onFailure(Throwable t) {
            afterCallApi();
        }
    }
}