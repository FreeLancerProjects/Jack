package com.endpoint.Jack.activities_fragments.activity_home.client_home.fragments.fragment_home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.models.BillDataModel;
import com.endpoint.Jack.models.ChatUserModel;
import com.endpoint.Jack.models.OrderDataModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.share.Common;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Currency;
import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Delegate_Current_Order_Details extends Fragment {
    private static final String TAG = "ORDER";
    private ClientHomeActivity activity;
    private ImageView image_back,image_arrow,image_arrow2,image_chat,order_image,image_bill,image_call;
    private LinearLayout ll_back,ll_address,ll_shipment;
    private String current_lang;
    private TextView tv_client_name,tv_address,rest_name,tv_order_details,tv_order_state,tv_order_next_state,tv_location_pickup,tv_location_dropoff;
    private FrameLayout fl_map,fl_update_order_state;
    private OrderDataModel.OrderModel order;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private int order_state;
    private final int IMG1 = 1, IMG2 = 2;
    private Uri uri = null;
    private final String read_permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
 //   private boolean isBillUploaded = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_current_order_details,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Delegate_Current_Order_Details newInstance(OrderDataModel.OrderModel order)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,order);
        Fragment_Delegate_Current_Order_Details fragment_delegate_Current_order_details = new Fragment_Delegate_Current_Order_Details();
        fragment_delegate_Current_order_details.setArguments(bundle);
        return fragment_delegate_Current_order_details;
    }
    private void initView(View view) {
        activity = (ClientHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        Paper.init(activity);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        image_call = view.findViewById(R.id.image_call);

        image_back = view.findViewById(R.id.image_back);
        image_arrow = view.findViewById(R.id.image_arrow);
        image_arrow2 = view.findViewById(R.id.image_arrow2);

        if (current_lang.equals("ar"))
        {
            image_back.setImageResource(R.drawable.ic_right_arrow);
            image_arrow.setImageResource(R.drawable.ic_right_arrow);
            image_arrow2.setImageResource(R.drawable.ic_right_arrow);

        }else
        {
            image_back.setImageResource(R.drawable.ic_left_arrow);
            image_arrow.setImageResource(R.drawable.ic_left_arrow);
            image_arrow2.setImageResource(R.drawable.ic_left_arrow);

        }
        image_chat = view.findViewById(R.id.image_chat);
        order_image = view.findViewById(R.id.order_image);
       // image_bill = view.findViewById(R.id.image_bill);

        ll_back = view.findViewById(R.id.ll_back);
        tv_order_details = view.findViewById(R.id.tv_order_details);
        rest_name = view.findViewById(R.id.tv_rest_name);
        tv_order_state = view.findViewById(R.id.tv_order_state);
        tv_order_next_state = view.findViewById(R.id.tv_order_next_state);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_address = view.findViewById(R.id.tv_address);
        fl_map = view.findViewById(R.id.fl_map);
        fl_update_order_state = view.findViewById(R.id.fl_update_order_state);

        ll_address = view.findViewById(R.id.ll_address);
        ll_shipment = view.findViewById(R.id.ll_shipment);
        tv_location_pickup = view.findViewById(R.id.tv_location_pickup);
        tv_location_dropoff = view.findViewById(R.id.tv_location_dropoff);



        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            this.order = (OrderDataModel.OrderModel) bundle.getSerializable(TAG);
            UpdateUI(this.order);
        }

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });

        image_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                if(userModel.getData().getUser_type().equals(Tags.TYPE_CLIENT)){
                    intent.setData(Uri.parse("tel:" + order.getDriver_user_phone_code()+order.getDriver_user_phone()));}
                else {
                    intent.setData(Uri.parse("tel:" + order.getClient_user_phone_code()+order.getClient_user_phone()));
                }
                activity.startActivity(intent);
            }
        });

        fl_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.DisplayFragmentMapLocationDetails(Double.parseDouble(order.getPlace_lat()), Double.parseDouble(order.getPlace_long()), Double.parseDouble(order.getClient_lat()), Double.parseDouble(order.getClient_long()),order.getClient_address());
            }
        });

        image_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatUserModel chatUserModel = new ChatUserModel(order.getClient_user_full_name(),order.getClient_user_image(),order.getClient_id(),order.getRoom_id_fk(),order.getClient_user_phone_code(),order.getClient_user_phone(),order.getOrder_id(),order.getDriver_offer(),order.getBill_step(),order.getBill_amount());
                activity.NavigateToChatActivity(chatUserModel,"from_fragment");
            }
        });

        fl_update_order_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.UpdateOrderMovement(order.getClient_id(),order.getDriver_id(),order.getOrder_id(),order_state);


            }
        });
        if (order.getOrder_status().equals(String.valueOf(Tags.STATE_ORDER_NEW))) {
            image_call.setVisibility(View.GONE);
        }
        else {
            image_call.setVisibility(View.VISIBLE);
        }

      /*  image_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog();
            }
        });*/


    }

    private void UpdateUI(OrderDataModel.OrderModel order)
    {
        Currency currency = Currency.getInstance(new Locale(current_lang,userModel.getData().getUser_country()));
        tv_client_name.setText(order.getClient_user_full_name());
        tv_order_details.setText(order.getOrder_details()+"\n"+getString(R.string.delivery_cost)+":"+order.getDriver_offer()+currency.getSymbol());
        rest_name.setText(order.getPlace_name());
        /*if (order.getBill_image()!=null&&!TextUtils.isEmpty(order.getBill_image())&&!order.getBill_image().equals("0"))
        {
            isBillUploaded = true;
        }else
        {
            isBillUploaded = false;

        }*/
      //  Log.e("ldldldll",order.getOrder_image());
        order_image.setVisibility(View.GONE);
        if (order.getOrder_image()==null||order.getOrder_image().isEmpty()||order.getOrder_image().equals("0"))
        {
            order_image.setVisibility(View.GONE);
        }else
        {

            Picasso.with(activity).load(Uri.parse(Tags.IMAGE_URL+order.getOrder_image())).fit().into(order_image);
            order_image.setVisibility(View.VISIBLE);

        }

        if (order.getOrder_type().equals("1"))
        {
            tv_address.setText(order.getClient_address());
            ll_address.setVisibility(View.VISIBLE);
          //  ll_shipment.setVisibility(View.GONE);
        }else if (order.getOrder_type().equals("2"))
        {
            tv_location_pickup.setText(order.getPlace_address());
            tv_location_dropoff.setText(order.getClient_address());
            ll_address.setVisibility(View.GONE);
         //   ll_shipment.setVisibility(View.VISIBLE);
        }


        order_state = Integer.parseInt(order.getOrder_status());
        updateOrderState(order_state);

    }


    public void updateOrderState(int state)
    {


        this.order_state = state;
        switch (state)
        {
            case Tags.STATE_CLIENT_ACCEPT_OFFER:
                tv_order_state.setText(getString(R.string.accepted));
                tv_order_next_state.setText(getString(R.string.collect_order));
              //  image_bill.setVisibility(View.GONE);

                break;
            case Tags.STATE_DELEGATE_COLLECTING_ORDER:
                tv_order_state.setText(getString(R.string.collecting_order));
                tv_order_next_state.setText(getString(R.string.collected_order));
               // image_bill.setVisibility(View.GONE);

                break;
            case Tags.STATE_DELEGATE_COLLECTED_ORDER:
                tv_order_state.setText(getString(R.string.collected_order));
                tv_order_next_state.setText(getString(R.string.deliver_order));
               // image_bill.setVisibility(View.VISIBLE);
                break;
            case Tags.STATE_DELEGATE_DELIVERING_ORDER:
                tv_order_state.setText(getString(R.string.delivering_order));
                tv_order_next_state.setText(getString(R.string.delivered_order));
               // image_bill.setVisibility(View.GONE);
                activity.refresh();
                break;



        }



    }


    private void CreateImageAlertDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_image, null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Check_CameraPermission();

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Check_ReadPermission();


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }


    private void Check_ReadPermission() {
        if (ContextCompat.checkSelfPermission(activity, read_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{read_permission}, IMG1);
        } else {
            select_photo(1);
        }
    }

    private void Check_CameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{camera_permission, write_permission}, IMG2);
        } else {
            select_photo(2);

        }

    }

    private void select_photo(int type) {

        Intent intent = new Intent();

        if (type == 1)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else
            {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent,IMG1);

        }else if (type ==2)
        {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMG2);
            }catch (SecurityException e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Toast.makeText(activity,R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG1 && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.getData();

            String path = Common.getImagePath(activity, uri);
            CreateBillAlertDialog(path);
        } else if (requestCode == IMG2 && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                String path = Common.getImagePath(activity, uri);
                CreateBillAlertDialog(path);
            }

        }
if(BillDataModel.getBill_step()!=null){
        order.setBill_step(BillDataModel.getBill_step());
        order.setBill_amount(BillDataModel.getTotla_Cost());
    }}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    select_photo(IMG1);
                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == IMG2) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    select_photo(2);

                } else {
                    Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        String path = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "title", null);
            return Uri.parse(path);

        } catch (SecurityException e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();

        }
        return null;
    }


    private void CreateBillAlertDialog(String image_path)
    {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_bill_photo,null);
        ImageView image = view.findViewById(R.id.image);
        EditText edt_order_cost = view.findViewById(R.id.edt_order_cost);
     //  Button btn_upload = view.findViewById(R.id.btn_upload);

        if (image_path != null) {
            Picasso.with(activity).load(new File(image_path)).fit().into(image);

        } else {
            Picasso.with(activity).load(uri).fit().into(image);

        }

      /*  btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost = edt_order_cost.getText().toString().trim();
                if (!TextUtils.isEmpty(cost))
                {
                    dialog.dismiss();
                    edt_order_cost.setError(null);
                    Common.CloseKeyBoard(activity,edt_order_cost);
                    uploadBill(cost);
                }else
                {
                    edt_order_cost.setError(getString(R.string.field_req));
                }

            }
        });*/

        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();
    }

 /*   private void uploadBill(String cost)
    {

        RequestBody order_id_part = Common.getRequestBodyText(order.getOrder_id());
        RequestBody cost_part = Common.getRequestBodyText(cost);
        MultipartBody.Part image_part = Common.getMultiPart(activity,uri,"bill_image");

        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.show();
        Api.getService(Tags.base_url)
                .payment(order_id_part,cost_part,image_part)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful())
                        {
                            isBillUploaded = true;
                            uri = null;
                            Toast.makeText(activity, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                        }else
                        {
                            try {
                                Log.e("Error_code",response.code()+""+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }*/


}
