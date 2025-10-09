package com.hritik.booking_service.config;

import com.hritik.booking_service.client.LocationServiceClient;
import com.hritik.booking_service.client.UberSocketClient;
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


    private String getServiceUrl(String serviceName) {
        return eurekaClient.getNextServerFromEureka(serviceName, false).getHomePageUrl();
    }

    @Bean
    public LocationServiceClient locationServiceClient() {
        return new Retrofit.Builder()
                .baseUrl(getServiceUrl("LOCATION-SERVICE"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationServiceClient.class);
    }

    @Bean
    public UberSocketClient uberSocketApi() {
        String serviceUrl = getServiceUrl("UBER-SOCKET-SERVICE");
        System.out.println("Service url for socket" + serviceUrl);

        return new Retrofit.Builder()
                .baseUrl(serviceUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UberSocketClient.class);
    }
}
