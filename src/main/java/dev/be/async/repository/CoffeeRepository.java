package dev.be.async.repository;

import dev.be.async.domain.Coffee;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CoffeeRepository {

    private Map<String, Coffee> coffeeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        coffeeMap.put("latte", Coffee.builder().name("latte").price(3500).build());
        coffeeMap.put("mocha", Coffee.builder().name("mocha").price(3600).build());
        coffeeMap.put("americano", Coffee.builder().name("americano").price(2000).build());
    }

    public int getPriceByName(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return coffeeMap.get(name).getPrice();
    }

}
