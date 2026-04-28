package com.group1.quiz.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenTdbApi {
    @GET("api.php")
    Call<OpenTdbResponse> getQuestions(
            @Query("amount") int amount,
            @Query("category") int category,
            @Query("difficulty") String difficulty,
            @Query("type") String type
    );

    @GET("api_category.php")
    Call<CategoryResponse> getCategories();

}
