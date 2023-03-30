package dev.akraml.aburob.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RateProfile {

    private final PlayerData playerData;
    @Getter private List<PlayerData> rateQueue = new ArrayList<>();

}
