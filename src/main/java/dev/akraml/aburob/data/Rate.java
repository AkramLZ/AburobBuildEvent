package dev.akraml.aburob.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Rate {

    @Getter private final String issuer;
    @Getter @Setter
    private double rate = 0;

}
