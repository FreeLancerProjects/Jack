package com.endpoint.Jack.services;

import com.endpoint.Jack.models.AppDataModel;
import com.endpoint.Jack.models.BankDataModel;
import com.endpoint.Jack.models.CategoryModel;
import com.endpoint.Jack.models.CommentDataModel;
import com.endpoint.Jack.models.FollowModel;
import com.endpoint.Jack.models.MessageDataModel;
import com.endpoint.Jack.models.MessageModel;
import com.endpoint.Jack.models.NearDelegateDataModel;
import com.endpoint.Jack.models.NearbyStoreDataModel;
import com.endpoint.Jack.models.NotificationCountModel;
import com.endpoint.Jack.models.NotificationDataModel;
import com.endpoint.Jack.models.OrderDataModel;
import com.endpoint.Jack.models.OrderIdDataModel;
import com.endpoint.Jack.models.OrderModel;
import com.endpoint.Jack.models.PayPalLinkModel;
import com.endpoint.Jack.models.PlaceDetailsModel;
import com.endpoint.Jack.models.PlaceDirectionModel;
import com.endpoint.Jack.models.PlaceGeocodeData;
import com.endpoint.Jack.models.PlaceMapDetailsData;
import com.endpoint.Jack.models.ReviewsCategoryModel;
import com.endpoint.Jack.models.SearchDataModel;
import com.endpoint.Jack.models.SingleCategoryModel;
import com.endpoint.Jack.models.SliderModel;
import com.endpoint.Jack.models.SocialMediaModel;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.models.WatingOrderData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/nearbysearch/json")
    Call<NearbyStoreDataModel> getNearbyStores(@Query(value = "location") String location,
                                               @Query(value = "radius") int radius,
                                               @Query(value = "type") String type,
                                               @Query(value = "language") String language,
                                               @Query(value = "key") String key
    );

    @GET("place/findplacefromtext/json")
    Call<SearchDataModel> getNearbyStoresWithKeyword(@Query(value = "locationbias") String location,
                                                     @Query(value = "inputtype") String inputtype,
                                                     @Query(value = "input") String input,
                                                     @Query(value = "fields") String fields,
                                                     @Query(value = "language") String language,
                                                     @Query(value = "key") String key
    );

    @GET("place/nearbysearch/json")
    Call<NearbyStoreDataModel> getNearbySearchStores(@Query(value = "location") String location,
                                                     @Query(value = "radius") int radius,
                                                     @Query(value = "name") String query,
                                                     @Query(value = "language") String language,
                                                     @Query(value = "key") String key
    );

    @GET("place/details/json")
    Call<PlaceDetailsModel> getPlaceDetails(@Query(value = "placeid") String placeid,
                                            @Query(value = "fields") String fields,
                                            @Query(value = "language") String language,
                                            @Query(value = "key") String key
    );


    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);

    @FormUrlEncoded
    @POST("/api/signup")
    Call<UserModel> signUpWithoutImage(@Field("user_email") String user_email,
                                       @Field("user_phone") String user_phone,
                                       @Field("user_phone_code") String user_phone_code,
                                       @Field("user_full_name") String user_full_name,
                                       @Field("user_gender") String user_gender,
                                       @Field("user_country") String user_country,
                                       @Field("user_age") long user_age

    );


    @Multipart
    @POST("/api/signup")
    Call<UserModel> signUpWithImage(@Part("user_email") RequestBody user_email,
                                    @Part("user_phone") RequestBody user_phone,
                                    @Part("user_phone_code") RequestBody user_phone_code,
                                    @Part("user_full_name") RequestBody user_full_name,
                                    @Part("user_gender") RequestBody user_gender,
                                    @Part("user_country") RequestBody user_country,
                                    @Part("user_age") RequestBody user_age,
                                    @Part MultipartBody.Part image
    );


    @FormUrlEncoded
    @POST("/api/login")
    Call<UserModel> signIn(@Field("user_phone") String user_phone,
                           @Field("user_phone_code") String user_phone_code

    );

    @GET("/api/appDetails")
    Call<AppDataModel> getAppData(@Query("type") int type);

    @FormUrlEncoded
    @POST("/api/updateLocation")
    Call<ResponseBody> updateLocation(@Field("user_id") String user_id,
                                      @Field("user_google_lat") double user_google_lat,
                                      @Field("user_google_long") double user_google_long
    );

    @FormUrlEncoded
    @POST("/api/updateToken")
    Call<ResponseBody> updateToken(@Field("user_id") String user_id,
                                   @Field("user_token_id") String user_token_id,
                                   @Field("soft_type") int soft_type

    );

    @GET("/api/slider")
    Call<SliderModel> getAds();

    @GET("/api/driverList")
    Call<NearDelegateDataModel> getDelegate(@Query("mylat") double lat, @Query("mylong") double lng, @Query("page") int page_index);

    @FormUrlEncoded
    @POST("/api/logout")
    Call<ResponseBody> logOut(@Field("user_id") String user_id,
                              @Field("user_token_id") String user_token_id

    );

    @FormUrlEncoded
    @POST("/api/addOrder")
    Call<OrderIdDataModel> sendOrder(@Field("client_id") String client_id,
                                     @Field("client_address") String client_address,
                                     @Field("client_lat") double client_lat,
                                     @Field("client_long") double client_long,
                                     @Field("order_details") String order_details,
                                     @Field("place_google_id") String place_google_id,
                                     @Field("place_address") String place_address,
                                     @Field("order_type") String order_type,
                                     @Field("place_lat") double place_lat,
                                     @Field("place_long") double place_long,
                                     @Field("order_time_arrival") long order_time_arrival,
                                     @Field("coupon_id") String coupon_id,
                                     @Field("place_name") String place_name

    );

    @Multipart
    @POST("/api/addOrder")
    Call<OrderIdDataModel> sendOrderWithImage(@Part("client_id") RequestBody client_id,
                                              @Part("client_address") RequestBody client_address,
                                              @Part("client_lat") RequestBody client_lat,
                                              @Part("client_long") RequestBody client_long,
                                              @Part("order_details") RequestBody order_details,
                                              @Part("place_google_id") RequestBody place_google_id,
                                              @Part("place_name") RequestBody place_google_name,
                                              @Part("place_address") RequestBody place_address,
                                              @Part("order_type") RequestBody order_type,
                                              @Part("place_lat") RequestBody place_lat,
                                              @Part("place_long") RequestBody place_long,
                                              @Part("order_time_arrival") RequestBody order_time_arrival,
                                              @Part("coupon_id") RequestBody RequestBody,
                                              @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("/api/visit")
    Call<ResponseBody> updateVisit(@Field("type") String type, @Field("day_date") String day_date);

    @Multipart
    @POST("/api/beDriver")
    Call<UserModel> registerDelegate(@Part("user_id") RequestBody user_id,
                                     @Part("user_card_id") RequestBody user_card_id,
                                     @Part("user_address") RequestBody user_address,
                                     @Part("account_num") RequestBody account_num,
                                     @Part MultipartBody.Part user_card_id_image,
                                     @Part MultipartBody.Part user_driving_license,
                                     @Part MultipartBody.Part image_car_front,
                                     @Part MultipartBody.Part image_car_back

    );

    @Multipart
    @POST("/api/profile")
    Call<UserModel> updateImage(@Part("user_id") RequestBody user_id,
                                @Part("user_email") RequestBody user_email,
                                @Part("user_full_name") RequestBody user_full_name,
                                @Part("user_country") RequestBody user_country,
                                @Part("user_gender") RequestBody user_gender,
                                @Part("user_age") RequestBody user_age,
                                @Part("user_address") RequestBody user_address,
                                @Part("user_phone_code") RequestBody user_phone_code,
                                @Part("user_phone") RequestBody user_phone,

                                @Part MultipartBody.Part user_image
    );

    @Multipart
    @POST("/api/profile")
    Call<UserModel> updateProfile(@Part("user_id") RequestBody user_id,
                                  @Part("user_email") RequestBody user_email,
                                  @Part("user_full_name") RequestBody user_full_name,
                                  @Part("user_country") RequestBody user_country,
                                  @Part("user_gender") RequestBody user_gender,
                                  @Part("user_age") RequestBody user_age,
                                  @Part("user_address") RequestBody user_address,
                                  @Part("user_phone_code") RequestBody user_phone_code,
                                  @Part("user_phone") RequestBody user_phone
    );

    @GET("/api/placeOrders")
    Call<WatingOrderData> getWaitingOrders(@Query("place_id") String place_id, @Query("page") int page);

    @GET("/api/clientOrders")
    Call<OrderDataModel> getClientOrders(@Query("user_id") String user_id, @Query("order_type") String order_type, @Query("page") int page);
    @GET("/api/clientOrders")
    Call<OrderModel> getClientOrders(@Query("order_id") String order_id);


    @GET("/api/driverOrders")
    Call<OrderDataModel> getDelegateOrders(@Query("user_id") String user_id, @Query("order_type") String order_type, @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/alerts")
    Call<NotificationCountModel> getNotificationCount(@Field("user_id") String user_id, @Field("type") String type);

    @FormUrlEncoded
    @POST("/api/alerts")
    Call<ResponseBody> readNotification(@Field("user_id") String user_id, @Field("type") String type);


    @GET("/api/notification")
    Call<NotificationDataModel> getNotification(@Query("user_id") String user_id, @Query("user_type") String user_type, @Query("page") int page);


    @FormUrlEncoded
    @POST("/api/driverAction")
    Call<ResponseBody> delegateAccept(@Field("driver_id") String driver_id,
                                      @Field("client_id") String client_id,
                                      @Field("order_id") String order_id,
                                      @Field("type") String type,
                                      @Field("driver_offer") String driver_offer
    );

    @FormUrlEncoded
    @POST("/api/driverAction")
    Call<ResponseBody> delegateRefuse_Finish(@Field("driver_id") String driver_id,
                                             @Field("client_id") String client_id,
                                             @Field("order_id") String order_id,
                                             @Field("type") String type
    );

    @FormUrlEncoded
    @POST("/api/clientAction")
    Call<ResponseBody> clientAccept_Refuse(@Field("client_id") String client_id,
                                           @Field("driver_id") String driver_id,
                                           @Field("order_id") String order_id,
                                           @Field("driver_offer") String driver_offer,
                                           @Field("type") String type
    );

    @FormUrlEncoded
    @POST("/api/clientAction")
    Call<ResponseBody> addRate(@Field("client_id") String client_id,
                               @Field("driver_id") String driver_id,
                               @Field("order_id") String order_id,
                               @Field("client_rate") double client_rate,
                               @Field("type") String type,
                               @Field("client_comment") String client_comment
    );

    @FormUrlEncoded
    @POST("/app/categories/add-rate")
    Call<ResponseBody> addRate(@Field("user_id") String user_id,
                               @Field("category_id") String category_id,
                               @Field("rate") float rate,
                               @Field("comment") String comment
    );

    @GET("/api/comment")
    Call<CommentDataModel> getDelegateComment(@Query("user_id") String user_id, @Query("user_type") String user_type, @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/cancelOrder")
    Call<ResponseBody> clientCancelOrder(@Field("order_id") String order_id
    );

    @FormUrlEncoded
    @POST("/api/changeDriver")
    Call<ResponseBody> resendOrderToDifferentDelegate(@Field("client_id") String client_id,
                                                      @Field("driver_id") String driver_id,
                                                      @Field("order_id") String order_id
    );

    @FormUrlEncoded
    @POST("/api/moveOrder")
    Call<ResponseBody> movementDelegate(@Field("order_id") String order_id,
                                        @Field("order_movement") String order_movement
    );

    @GET("/api/chat")
    Call<MessageDataModel> getChatMessages(@Query("room_id") String room_id, @Query("page") int page);


    @FormUrlEncoded
    @POST("/api/chating")
    Call<MessageModel> sendMessage(@Field("room_id_fk") String room_id_fk,
                                   @Field("from_user") String from_user_id,
                                   @Field("to_user") String to_user_id,
                                   @Field("message") String message,
                                   @Field("message_type") String message_type

    );


    @Multipart
    @POST("/api/chating")
    Call<MessageModel> sendMessageWithImage(@Part("room_id_fk") RequestBody room_id_fk,
                                            @Part("from_user") RequestBody from_user_id,
                                            @Part("to_user") RequestBody to_user_id,
                                            @Part("message") RequestBody message,
                                            @Part("message_type") RequestBody message_type,
                                            @Part MultipartBody.Part messagefile_type


    );

    @Multipart
    @POST("/api/attachBill")
    Call<MessageModel> sendbillWithImage(@Part("room_id_fk") RequestBody room_id_fk,
                                         @Part("from_user") RequestBody from_user_id,
                                         @Part("to_user") RequestBody to_user_id,
                                         @Part("message") RequestBody message,
                                         @Part("message_type") RequestBody message_type,
                                         @Part("bill_amount") RequestBody bill_amount,
                                         @Part("order_id") RequestBody order_id,
                                         @Part("network_value") RequestBody network_value,
                                         @Part MultipartBody.Part messagefile_type


    );

    @FormUrlEncoded
    @POST("/api/Typing")
    Call<MessageModel> typing(@Field("room_id_fk") String room_id_fk,
                              @Field("from_user") String from_user_id,
                              @Field("to_user") String to_user_id,
                              @Field("typing_value") int typing_value

    );

    @FormUrlEncoded
    @POST("/api/confirmCode")
    Call<ResponseBody> validateCode(@Field("user_phone_code") String user_phone_code,
                                    @Field("user_phone") String user_phone,
                                    @Field("confirm_code") String confirm_code
    );

    @FormUrlEncoded
    @POST("/api/resendSms")
    Call<ResponseBody> getSmsCode(@Field("user_phone_code") String user_phone_code,
                                  @Field("user_phone") String user_phone
    );

    @FormUrlEncoded
    @POST("/api/coupon")
    Call<UserModel> getCouponValue(@Field("user_id") String user_id,
                                   @Field("type") String type,
                                   @Field("coupon_code") String coupon_code

    );

    @FormUrlEncoded
    @POST("/api/deleteNote")
    Call<ResponseBody> clientRefuseDelegateOffer(@Field("id_notification") String id_notification);

    @GET("/api/socialMedia")
    Call<SocialMediaModel> getSocialMedia();

    @GET("/api/profile")
    Call<UserModel> getUserDataById(@Query("user_id") String user_id);

    @FormUrlEncoded
    @POST("/api/availableStatus")
    Call<UserModel> updateDelegateAvailable(@Field("user_id") String user_id,
                                            @Field("available") String available
    );

    @GET("directions/json")
    Call<PlaceDirectionModel> getDirection(@Query("origin") String origin,
                                           @Query("destination") String destination,
                                           @Query("transit_mode") String transit_mode,
                                           @Query("key") String key
    );


    @FormUrlEncoded
    @POST("/api/followingDriver")
    Call<FollowModel> getFollowData(@Field("order_id") String order_id,
                                    @Field("driver_id") String driver_id,
                                    @Field("client_id") String client_id
    );

    @GET("/api/banks")
    Call<BankDataModel> getBankAccount();

    @GET("/app/place/categories")
    Call<CategoryModel> getcatogries(@Header("lang") String lang);

    @GET("/app/categories/all-rate")
    Call<ReviewsCategoryModel> getreview(@Query("category_id") String category_id)
            ;

    @GET("/app/place/show")
    Call<SingleCategoryModel> getsinglecat(
            @Header("lang") String lang,
            @Query("category_id") String category_id,
            @Query("page") int page,
            @Query("limit_per_page") int limit_per_page

    );

    @FormUrlEncoded
    @POST("http://sub.quick.com.sa/api/online-payment")
    Call<PayPalLinkModel> getPayPalLink(
            @Field("user_id") String user_id,
            @Field("user_type") String user_type,
            @Field("amount") double amount
    );
    @FormUrlEncoded
    @POST("/api/deleteOrder")
    Call<ResponseBody> DelteOrder(
            @Field("user_id") String user_id,
            @Field("order_id") String order_id
    );
    @FormUrlEncoded
    @POST("http://sub.quick.com.sa/api/online-payment-bill")
    Call<PayPalLinkModel> getbillpay(
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("bill_amount") String bill_amount
    );
}


