/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network;

import com.appster.turtle.model.BankModel;
import com.appster.turtle.model.ForgotPassword;
import com.appster.turtle.model.Interest;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.model.PlaceApiModel;
import com.appster.turtle.model.SignIn;
import com.appster.turtle.model.SignUp;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.request.AddEditNote;
import com.appster.turtle.network.request.AddRoomRequest;
import com.appster.turtle.network.request.CardRequest;
import com.appster.turtle.network.request.CreateMeetupRequest;
import com.appster.turtle.network.request.CreateTextPostRequest;
import com.appster.turtle.network.request.DeleteNotesRequest;
import com.appster.turtle.network.request.DeleteNotificationRequest;
import com.appster.turtle.network.request.NotificationReadRequest;
import com.appster.turtle.network.request.PaymentRequest;
import com.appster.turtle.network.request.Poll;
import com.appster.turtle.network.request.PostCommentRequest;
import com.appster.turtle.network.request.PostNoteRequest;
import com.appster.turtle.network.request.RemoveAttachmentRequest;
import com.appster.turtle.network.request.ReportRequest;
import com.appster.turtle.network.request.SendInviteRequest;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.request.VerificationImage;
import com.appster.turtle.network.request.auth.ChangePasswordRequest;
import com.appster.turtle.network.request.auth.ForgotPasswordRequest;
import com.appster.turtle.network.request.auth.SignInRequest;
import com.appster.turtle.network.request.auth.VerifyCode;
import com.appster.turtle.network.response.AddInterestResponse;
import com.appster.turtle.network.response.AddNotesReviewResponse;
import com.appster.turtle.network.response.AddRoomNewResponse;
import com.appster.turtle.network.response.AttachmentResponse;
import com.appster.turtle.network.response.BankPostResponse;
import com.appster.turtle.network.response.BankVerificationImageResponse;
import com.appster.turtle.network.response.BaseId;
import com.appster.turtle.network.response.BaseMessageResponse;
import com.appster.turtle.network.response.ChangePasswordResponse;
import com.appster.turtle.network.response.ChooseAvatarResponse;
import com.appster.turtle.network.response.CreateMeetupResponse;
import com.appster.turtle.network.response.CreateTextPostResponse;
import com.appster.turtle.network.response.DeleteCardResponse;
import com.appster.turtle.network.response.DurationResponse;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.GetActivitiesResponse;
import com.appster.turtle.network.response.GetBankDetailResponse;
import com.appster.turtle.network.response.GetCardsResponse;
import com.appster.turtle.network.response.GetCommentResponse;
import com.appster.turtle.network.response.GetCommentsResponse;
import com.appster.turtle.network.response.GetDormResponse;
import com.appster.turtle.network.response.GetGraduationResponse;
import com.appster.turtle.network.response.GetGreekLifeResponse;
import com.appster.turtle.network.response.GetImageResponse;
import com.appster.turtle.network.response.GetMajorResponse;
import com.appster.turtle.network.response.GetRoomResponse;
import com.appster.turtle.network.response.GetRoomsResponse;
import com.appster.turtle.network.response.GetTagsResponse;
import com.appster.turtle.network.response.GetUserProfile;
import com.appster.turtle.network.response.HomeTopRoomsResponse;
import com.appster.turtle.network.response.IdResponse;
import com.appster.turtle.network.response.InterestResponse;
import com.appster.turtle.network.response.JoinRoomResponse;
import com.appster.turtle.network.response.LikedByResponse;
import com.appster.turtle.network.response.Meetup;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.network.response.NotesResponse;
import com.appster.turtle.network.response.NotesReview;
import com.appster.turtle.network.response.NotesReviewListResponse;
import com.appster.turtle.network.response.NotificationReadUnreadResponse;
import com.appster.turtle.network.response.NotificationsResponse;
import com.appster.turtle.network.response.PaymentResponse;
import com.appster.turtle.network.response.PostCardResponse;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.network.response.RemoveMyNotesResponse;
import com.appster.turtle.network.response.RoomResponse;
import com.appster.turtle.network.response.SearchPopularUserResponse;
import com.appster.turtle.network.response.SearchRoomsNewResponse;
import com.appster.turtle.network.response.SearchRoomsResponse;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.network.response.SignInResponse;
import com.appster.turtle.network.response.SignUpResponse;
import com.appster.turtle.network.response.TopRoomsResponse;
import com.appster.turtle.network.response.UnreadNotifCountResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.network.response.UserProfileResponse;
import com.appster.turtle.network.response.VerifyEmailResponse;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@SuppressWarnings("ALL")
public interface ApiService {

    @POST("auth/signin")
    Observable<SignInResponse> signIn(@Body SignIn signIn);

    @POST("auth/signin")
    Observable<SignInResponse> signIn(@Body SignInRequest request);

    @POST("auth/forgotPass")
    Observable<BaseResponse> forgotPassword(@Body ForgotPassword forgotPassword);

    @POST("auth/forgotPass")
    Observable<BaseResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @GET("user/rooms")
    Observable<GetRoomsResponse> getRooms(@Query("page") int page, @Query("size") int size);

    @GET("user/search")
    Observable<SearchUserResponse> searchUser(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/search")
    Observable<SearchUserResponse> searchUser(@Query("page") int page, @Query("size") int size, @Query("q") String query, @Query("searchMembers") boolean searchMembers);


    @GET("user/friend")
    Observable<SearchUserResponse> getFriends(@Query("page") int page, @Query("myFriend") int myFriend, @Query("size") int size, @Query("q") String query);


    @GET("user/profile/getAllConnections")
    Observable<LikedByResponse> getConnections(@Query("page") int page, @Query("size") int size, @Query("userId") int userId);


    @GET("master/getInterest")
    Observable<GetTagsResponse> getTags(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @POST("user/rooms/")
    Observable<GetRoomResponse> addEditRoom(@Body AddRoomRequest addRoomRequest);


    @Multipart
    @POST("user/posts/media/")
    Observable<PostsResponse> addImagePost(@Part("media\"; filename=\"media.jpg") RequestBody media,
                                           @PartMap() HashMap<String, RequestBody> map
    );


    @Multipart
    @POST("user/posts/media/")
    Observable<PostsResponse> addVideoPost(@Part("media\"; filename=\"media.mp4") RequestBody media,
                                           @PartMap() HashMap<String, RequestBody> map
    );


    @Multipart
    @POST("user/posts/media/")
    Observable<PostsResponse> addGifPost(@Part("media\"; filename=\"media.gif") RequestBody media,
                                         @PartMap() HashMap<String, RequestBody> map
    );

    @POST("user/posts/polls/")
    Observable<CreateMeetupResponse> postPoll(@Body Poll postPollDetailsForm);

    @GET("master/meetups/duration")
    Observable<DurationResponse> getDurations();

    @POST("user/posts/meetups/")
    Observable<CreateMeetupResponse> createMeetup(@Body CreateMeetupRequest meetupRequest);

    //0 attending, 1 not attending, 2 interested
    @POST("user/posts/meetups/respond")
    Observable<Meetup> meetupStatusChanged(@Query("postId") int postId, @Query("status") int status, @Query("reset") boolean reset);

    @POST("user/posts/text/")
    Observable<CreateTextPostResponse> createTextPost(@Body CreateTextPostRequest textPostRequest);

    @FormUrlEncoded
    @POST("user/posts/polls/respond")
    Observable<PostResponse> respondPoll(@Field("postId") Integer postId, @Field("ansId") Integer ansId);

    @FormUrlEncoded
    @POST("user/posts/{id}/like")
    Observable<PostResponse> likeDislikePost(@Path("id") Integer postId, @Field("reaction") Integer reaction);


    @GET("user/posts/likedBy")
    Observable<LikedByResponse> getLikedBy(@Query("postId") int postId, @Query("commentId") int commentId, @Query("page") int page, @Query("size") int size);


    @GET("user/posts/likedBy")
    Observable<LikedByResponse> getLikedReactionBy(@Query("postId") int postId, @Query("reaction") int reaction, @Query("page") int page, @Query("size") int size);

    @GET("user/rooms/new/members")
    Observable<SearchUserResponse> searchNewMembers(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("q") String query);

    @POST("user/rooms/invite/")
    Observable<BaseResponse> sendInvite(@Body SendInviteRequest sendInviteRequest);

    @POST("user/rooms/leave/{id}")
    Observable<BaseResponse> leaveRoom(@Path("id") Integer roomId);

    @GET("user/rooms/members")
    Observable<SearchUserResponse> getMembers(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("q") String query);

    @POST("user/rooms/remove/")
    Observable<UserActionResponse> removeUser(@Body UserActionRequest userActionRequest);

    @POST("user/rooms/make/admin/")
    Observable<UserActionResponse> makeAdmin(@Body UserActionRequest userActionRequest);


    @GET("user/rooms/{id}")
    Observable<GetRoomResponse> getRoom(@Path("id") Integer roomId);


    @Multipart
    @POST("user/rooms/uploadImage")
    Observable<GetRoomResponse> addRoomImage(@Part("image\"; filename=\"image.jpg") RequestBody image,
                                             @PartMap() HashMap<String, RequestBody> map
    );

    @POST("auth/checkEmail")
    Observable<IdResponse> checkEmail(@Body SignUp emailForm);

    @GET("auth/uname")
    Observable<IdResponse> userId(@Query("q") String q);

    @POST("auth/signup")
    Observable<SignUpResponse> signUp(@Body SignUp signUp);

    @POST("user/profile/email/verify/")
    Observable<VerifyEmailResponse> verifyEmail(@Body VerifyCode verifyCode);

    @GET("user/activity/")
    Observable<GetActivitiesResponse> getActivities(@Query("currentPage") int currentPage, @Query("noOfResults") int noOfResults);

    @POST("user/notes/review/")
    Observable<AddNotesReviewResponse> addNotesReview(@Body NotesReview review);

    @GET("user/notes/review/")
    Observable<NotesReviewListResponse> getNotesReview(@Query("id") int id, @Query("page") int page, @Query("size") int size);

    @Multipart
    @POST("user/profile/image/")
    Observable<ChooseAvatarResponse> imageUpdate(@Part("image\"; filename=\"image.jpg") RequestBody media, @Part("croppedImage\"; filename=\"croppedImage.jpg") RequestBody cropMedia, @PartMap() HashMap<String, RequestBody> map);

    @GET("user/notes/all/search")
    Observable<NotesResponse> searchNotes(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/notes/self/search")
    Observable<NotesResponse> searchSelfNotes(@Query("page") int page, @Query("size") int size, @Query("q") String query, @Query("type") int type);

    @POST("user/notes/")
    Observable<NoteResponse> addEditNotes(@Body AddEditNote addEditNoteRequest);


    @POST("user/notes/")
    Observable<NoteResponse> addNotes(@Body NotesModel addEditNoteRequest);

    @Multipart
    @POST("user/notes/attachment/add/")
    Observable<AttachmentResponse> addAttachment(@Part("attachment\"; filename=\"attachment.jpg") RequestBody attachment, @PartMap() HashMap<String, RequestBody> map);

    @Multipart
    @POST("user/notes/snippet/")
    Observable<NoteResponse> addSnippet(@Part("image\"; filename=\"image.jpg") RequestBody image,
                                        @PartMap() HashMap<String, RequestBody> map
    );

    @POST("user/notes/attachment/remove/")
    Observable<VerifyEmailResponse> remAttachment(@Body RemoveAttachmentRequest request);

    @POST("user/notes/post/")
    Observable<NoteResponse> postNotes(@Body PostNoteRequest request);

    @GET("user/notes/{id}")
    Observable<NoteResponse> noteDetails(@Path("id") int noteId);

    @GET("master/getInterest")
    Observable<InterestResponse> searchInterests(@Query("q") String query, @Query("page") int page, @Query("size") int size);

    @GET("master/popular/interest")
    Observable<InterestResponse> getPopularInterest();

    @POST("master/addInterest")
    Observable<AddInterestResponse> addInterest(@Body Interest interest);


    @GET("master/getMasterSkill")
    Observable<InterestResponse> searchSkills(@Query("q") String query, @Query("page") int page, @Query("size") int size);

    @GET("master/popular/skills")
    Observable<InterestResponse> getPopularSkills();


    @POST("master/addMasterSkill")
    Observable<AddInterestResponse> addSkills(@Body Interest skill);

    @POST("user/profile/")
    Observable<UserProfileResponse> updateUser(@Body UserBaseModel userBaseModel);


    @POST("user/profile/changePassword")
    Observable<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("user/rooms/report")
    Observable<UserActionResponse> reportFlagRoomMember(@Body UserActionRequest request);

    @POST("user/rooms/block")
    Observable<UserActionResponse> blockUser(@Body UserActionRequest request);

    @POST("user/posts/flag/")
    Observable<UserActionResponse> flagPost(@Body UserActionRequest request);

    @POST("user/posts/delete/")
    Observable<UserActionResponse> deletePost(@Body UserActionRequest request);


    @GET("user/rooms/set/fav")
    Observable<BaseResponse> favRoom(@Query("roomId") Integer roomId, @Query("fav") boolean fav);


    //Add TnC API
    //Add Privacy Policy API

    @POST("user/card/")
    Observable<PostCardResponse> addCard(@Body CardRequest cardForm);

    @GET("user/card/getCards")
    Observable<GetCardsResponse> getCards(@Query("page") int page, @Query("size") int size);

    @POST("user/card/deleteCard/{id}")
    Observable<DeleteCardResponse> deleteCards(@Path("id") String id);

    @POST("user/bank/addUserBankAccount")
    Observable<BankPostResponse> postBankDetail(@Body BankModel bankDetailsAddForm);

    @POST("user/bank/updateBankDetail")
    Observable<BankPostResponse> updateBankDetail(@Body BankModel bankDetailsAddForm);

    @GET("user/bank/bankDetail")
    Observable<GetBankDetailResponse> getBankDetail();

    @Multipart
    @POST("user/bank/uploadVerificationImage")
    Observable<BankVerificationImageResponse> addBankImage(@Part("image\"; filename=\"image.jpg") RequestBody media

    );

    @POST("user/notes/remove/")
    Observable<RemoveMyNotesResponse> removeMyNotes(@Body DeleteNotesRequest baseId);


    @POST("user/bank/uploadVerificationImage")
    Observable<BankVerificationImageResponse> addBankImage(@Body VerificationImage image

    );

    @POST("user/notes/{id}/paymentByCard")
    Observable<PaymentResponse> paymentByCard(@Path("id") int id, @Body PaymentRequest paymentRequest);

    @GET("master/profile/images")
    Observable<GetImageResponse> getProfileImages();


    @GET("user/notification/")
    Observable<NotificationsResponse> getNotification(@Query("currentPage") int currentPage, @Query("noOfResults") int noOfResults);

    @GET("user/notification/getUnReadCount")
    Observable<UnreadNotifCountResponse> markRead();


    @GET("user/notification/getUnViewCount")
    Observable<UnreadNotifCountResponse> markUnreadCount();

    @Multipart
    @POST("user/notes/edit/")
    Observable<NoteResponse> editNotes(@Query("remAttachmentIds") String remAttachmentIds, @PartMap HashMap<String, RequestBody> map, @Part("snippet\"; filename=\"snippet.jpg") RequestBody snippet);

    @GET("user/rooms/top")
    Observable<TopRoomsResponse> topRooms(@Query("t") Integer numberOfPopularRooms);

    @GET("user/rooms/search")
    Observable<SearchRoomsResponse> listRooms(@Query("filter") int filter, @Query("page") int page, @Query("size") int size, @Query("q") String query);


    @POST("user/notes/{id}/resend/")
    Observable<BaseMessageResponse> resendEmail(@Path("id") int id);

    @GET("user/profile/getMajorDiscipline")
    Observable<GetMajorResponse> getMajor(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/profile/getMinorDiscipline")
    Observable<GetMajorResponse> getMinor(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/profile/getDorm")
    Observable<GetDormResponse> getDorm(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("master/university/greeklife")
    Observable<GetGreekLifeResponse> getGreekLife();

    @GET("master/getMasterAcademicYear")
    Observable<GetGraduationResponse> getGraduationYear(@Query("page") int page, @Query("size") int size);

    @POST("user/friend/request/{id}")
    Observable<FriendRequestResponse> actionFriendRequest(@Path("id") int id, @Query("q") int q);

    @GET("user/profile/{id}")
    Observable<GetUserProfile> getUserProfile(@Path("id") int id);

    @POST("user/rooms/join/request/{status}")
    Observable<BaseResponse> actionRoomRequests(@Path("status") int status, @Body SendInviteRequest roomUserForm);

    @POST("user/rooms/join/request/{status}")
    Observable<RoomResponse> actionRoomRequestNotification(@Path("status") int status, @Body SendInviteRequest roomUserForm);

    @POST("user/rooms/request/{id}")
    Observable<JoinRoomResponse> joinRoomRequest(@Path("id") int id);


    @GET("user/rooms/join/request")
    Observable<SearchUserResponse> getRoomRequests(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/rooms/top")
    Observable<HomeTopRoomsResponse> getTopRooms(@Query("t") Integer numberOfPopularRooms, @Query("q") String query);

    @GET("user/search/home")
    Observable<SearchPopularUserResponse> searchPopularUser(@Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/rooms/search")
    Observable<SearchRoomsNewResponse> listRoomsForSearch(@Query("filter") int filter, @Query("page") int page, @Query("size") int size, @Query("q") String query);

    @POST("user/rooms/request/{id}")
    Observable<AddRoomNewResponse> addRoom(@Path("id") Integer roomId);

    @POST("user/profile/resend/code/")
    Observable<VerifyEmailResponse> resendResponse();

    @GET("user/rooms/home")
    Observable<GetRoomsResponse> getHomeRooms(@Query("page") int page, @Query("size") int size, @Query("filter") int filter, @Query("home") boolean home);

    @POST("user/profile/flag/")
    Observable<UserActionResponse> reportFlagUser(@Body ReportRequest reportRequest);


    @GET("user/friend/mutual")
    Observable<LikedByResponse> getMutualFriends(@Query("page") int page, @Query("size") int size, @Query("id") int id);

    @GET("user/rooms/posts")
    Observable<PostsResponse> getPosts(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("q") String query);

    @GET("user/rooms/posts")
    Observable<PostsResponse> getPosts(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("q") String query, @Query("filter") int filter);


    @GET("user/rooms/posts")
    Observable<PostsResponse> getPosts(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("postType") int postType, @Query("filter") int filter);

    @GET("user/rooms/posts")
    Observable<PostsResponse> getPosts(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("postType") int postType);

    @GET("user/rooms/posts")
    Observable<PostsResponse> getPostsByFilter(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("filter") int filter);

    @GET("user/rooms/home/posts")
    Observable<PostsResponse> getHomePosts(@Query("page") int page, @Query("size") int size, @Query("filter") int filter, @Query("home") boolean home);

    @GET("user/posts/{id}")
    Observable<PostResponse> getPost(@Path("id") int id);

    @GET("user/posts/comment")
    Observable<GetCommentsResponse> getComments(@Query("postId") int postId, @Query("page") int page, @Query("size") int size);

    @POST("user/posts/comment/")
    Observable<GetCommentResponse> postComments(@Body PostCommentRequest body);

    @POST("user/posts/{id}/like")
    Observable<PostResponse> likePost(@Path("id") int id, @Query("reaction") int reaction);

    @POST("user/posts/comment/delete/")
    Observable<BaseResponse> deleteComment(@Body BaseId baseId);

    @POST("user/posts/comment/flag/")
    Observable<GetCommentResponse> flagComment(@Body UserActionRequest baseId);

    @POST("user/posts/{id}/likeComment")
    Observable<GetCommentResponse> likeComment(@Path("id") int commentId, @Query("reaction") int likedStatus);


    @GET("user/profile/getAllPosts")
    Observable<PostsResponse> getUserPost(@Query("userId") int userId, @Query("page") int page, @Query("size") int size);

    @POST("user/profile/logOut")
    Observable<BaseResponse> logout();


    @GET("user/profile/getAllRooms")
    Observable<GetRoomsResponse> getUserRooms(@Query("userId") int userId, @Query("page") int page, @Query("size") int size);


    @GET("user/rooms/posts")
    Observable<PostsResponse> getSearchPost(@Query("roomId") int roomId, @Query("page") int page, @Query("size") int size, @Query("filter") int filter, @Query("postType") int postType, @Query("q") String q);

    @POST("user/rooms/invitation")
    Observable<JoinRoomResponse> roomInvite(@Query("roomId") int roomId, @Query("accept") boolean accept);


    @POST("user/notification/markRead")
    Observable<NotificationReadUnreadResponse> markAsRead(@Body NotificationReadRequest readRequest);

    @GET("json?type=(citie)")
    Observable<PlaceApiModel> getPlaceApi(@Query(value = "input", encoded = true) String input, @Query(value = "key", encoded = true) String key);

    @POST("user/rooms/invitation")
    Observable<NotificationReadUnreadResponse> accpectRejectRoom(@Query("roomId") int roomId, @Query("accept") boolean accept);

    @POST("user/rooms/notification/")
    Observable<UserActionResponse> setRoomNotification(@Query("roomId") int roomId, @Query("enable") boolean enable);

    @POST("user/notification/deleteNotification")
    Observable<UserActionResponse> deleteNotification(@Body DeleteNotificationRequest request);
}