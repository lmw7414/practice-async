package dev.be.async.Service;

import java.util.concurrent.Future;

public interface CoffeeUseCase {
    int getPrice(String name);  //Sync
    Future<Integer> getPriceAsync(String name);  //Async
    Future<Integer> getDiscountPriceAsync(Integer price);  //Async
}
