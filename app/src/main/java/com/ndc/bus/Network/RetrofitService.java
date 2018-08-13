package com.ndc.bus.Network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("getBusPosByVehId")
    Call<String> getBusPosByVehId(
        @Query("serviceKey") String serviceKey,
        @Query("vehId") String vehId
    );
    /*
    //File Should be sended Through MultiPart and POST
    @POST("/sendImage")
    @Multipart
    Call<ResponseBody> sendImage(
            @Part MultipartBody.Part image,
            @Part("uid") RequestBody uid,
            @Part("mirrorUid") RequestBody mirrorUid
    );

    //In Get Method Data was attached on HEAD, so use GET Method with Search Query(Topic) would be fit
    @GET("/sendCategory")
    Call<String> sendSwitchStatus(
            @Query("uid") String uid,
            @Query("category") String categoryName
    );

    //Path will allow to access {owner}
    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<UserVO>> getUsers(
            @Path("owner") String owner,
            @Path("repo") String repo);

    //Body was used with POST to send Object
    @POST("/sendUser")
    Call<String> sendUser(
            @Body UserVO userVO
    );

    // Field Must be used with FormUrIEncoded which indicates that the request will have its MIME
    // type (a header field that identifies the format of the body of an HTTP request or response)
    // set to application/x-www-form-urlencoded and also that its field names and values will
    // be UTF-8 encoded before being URI-encoded.
    @POST("/sendPost")
    @FormUrlEncoded
    Call<String> sendPost(@Field("title") String title,
                          @Field("body") String body,
                          @Field("userId") String uid
    );
    */
}
