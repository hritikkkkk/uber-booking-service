package com.hritik.booking_service.config;

import com.hritik.booking_service.client.LocationServiceClient;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {
    @Autowired
    private EurekaClient eurekaClient;


    private String getServiceUrl() {
        return eurekaClient.getNextServerFromEureka("LOCATION-SERVICE", false).getHomePageUrl();
    }
    @Bean
    public LocationServiceClient locationServiceClient() {
        return new Retrofit.Builder()
                .baseUrl(getServiceUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationServiceClient.class);
    }
}
