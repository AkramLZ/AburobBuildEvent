package dev.akraml.aburob.data;

import dev.akraml.aburob.json.JsonConfigAdapter;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PlayerData {

    @Getter private final UUID uuid;
    private final JsonConfigAdapter config;
    private final Map<String, Rate> rateMap = new HashMap<>();
    @Getter @Setter private FastBoard board;
    @Getter @Setter private RateProfile rateProfile;
    @Getter @Setter private String name;

    public PlayerData(final UUID uuid, final JsonConfigAdapter config) {
        this.uuid = uuid;
        this.config = config;
        if (config.get("rates", List.class) == null) {
            config.set("rates", List.of(), List.class);
        } else {
            final List<String> list = config.get("rates", List.class);
            for (String string : list) {
                final String[] values = string.split(", ");
                final String name = values[0];
                final int rateVal = Integer.parseInt(values[1]);
                final Rate rate = new Rate(name);
                rate.setRate(rateVal);
                rateMap.put(name, rate);
            }
        }
    }

    public void addRate(final Rate rate) {
        if (rateMap.containsKey(rate.getIssuer())) {
            rateMap.replace(rate.getIssuer(), rate);
        } else {
            rateMap.put(rate.getIssuer(), rate);
        }
        List<String> list = new ArrayList<>();
        for (Rate rate1 : rateMap.values()) {
            list.add(rate1.getIssuer() + ", " + rate1.getRate());
        }
        config.set("rates", list, List.class);
    }

    public double getRate() {
        double rateVal = 0;
        if (rateMap.size() == 0) {
            return 0;
        }
        for (Rate rate : rateMap.values()) {
            rateVal += rate.getRate();
        }
        return rateVal / rateMap.size();
    }

}
