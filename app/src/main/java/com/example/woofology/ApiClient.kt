package com.example.woofology

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ApiClient(var context: Context) {
    var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    interface AllBreedsInterface {
        @get:GET("breeds/list")
        val breeds: Call<AllBreedsResponse>
    }

    interface breedTypeInterface {
        @GET("breed/{type}/images")
        fun getBreedImages(@Path("type") type: String?): Call<AllBreedsResponse>
    }


    fun getAllBreeds(listener: AllBreedsListener) {
        val apiInterface = retrofit.create(
            AllBreedsInterface::class.java
        )
        val call = apiInterface.breeds
        call.enqueue(object : Callback<AllBreedsResponse?> {
            override fun onResponse(
                call: Call<AllBreedsResponse?>,
                response: Response<AllBreedsResponse?>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    return
                }
                listener.onFetch(response.body(), response.message())
            }

            override fun onFailure(call: Call<AllBreedsResponse?>, t: Throwable) {
                listener.onError(t.message)
            }
        })
    }

    fun getBreedImages(listener: AllBreedsListener, type: String?) {
        val apiInterface = retrofit.create(
            breedTypeInterface::class.java
        )
        val call = apiInterface.getBreedImages(type)
        call.enqueue(object : Callback<AllBreedsResponse?> {
            override fun onResponse(
                call: Call<AllBreedsResponse?>,
                response: Response<AllBreedsResponse?>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    return
                }
                listener.onFetch(response.body(), response.message())
            }

            override fun onFailure(call: Call<AllBreedsResponse?>, t: Throwable) {
                listener.onError(t.message)
            }
        })
    }

    companion object {
        private const val BASE_URL = "https://dog.ceo/api/"
    }
}