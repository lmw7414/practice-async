package dev.be.async.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class Coffee {
    private String name;
    private int price;
}
