package co.edu.javeriana.enrutados.services;

import java.util.List;

import co.edu.javeriana.enrutados.model.Route;
import co.edu.javeriana.enrutados.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EnrutadosApi {

    @GET("users")
    Call<List<User>> getUsers();

    @GET("routes")
    Call<List<Route>> getRoutes();

    @GET("routes/{route}")
    Call<Route> getRoute(@Path("routeId") Long routeId);

    @GET("routes/{route}/points")
    Call<Route> getRoutePoints(@Path("routeId") Long routeId);

    @GET("technicians/{email}/routes")
    Call<List<Route>> getTechnicianRoutes(@Path("email") String email);

    @GET("coordinators/{email}/routes")
    Call<List<Route>> getCoordinatorRoutes(@Path("email") String email);

}
