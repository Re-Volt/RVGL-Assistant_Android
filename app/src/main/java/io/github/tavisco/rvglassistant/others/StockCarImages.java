package io.github.tavisco.rvglassistant.others;

import java.util.HashMap;

import io.github.tavisco.rvglassistant.R;

public class StockCarImages {
    private static final StockCarImages INSTANCE = new StockCarImages();

    private HashMap<String, Integer> cars = new HashMap<>();

    private StockCarImages() {
        cars.put("Adeon", R.drawable.adeon);
        cars.put("AMW", R.drawable.amw);
        cars.put("Aquasonic", R.drawable.aquasonic);
        cars.put("Bertha Ballistics", R.drawable.bertha);
        cars.put("Candy Pebbles", R.drawable.candy);
        cars.put("Col. Moss", R.drawable.col_moss);
        cars.put("Cougar", R.drawable.cougar);
        cars.put("Dr. Grudge", R.drawable.dr_grudge);
        cars.put("Dust Mite", R.drawable.dust);
        cars.put("R6 Turbo", R.drawable.r6);
        cars.put("Evil Weasel", R.drawable.evil);
        cars.put("Genghis Kar", R.drawable.genghis);
        cars.put("Harvester", R.drawable.harvester);
        cars.put("Humma", R.drawable.humma);
        cars.put("Mouse", R.drawable.mouse);
        cars.put("NY 54", R.drawable.ny);
        cars.put("Mystery", R.drawable.mystery);
        cars.put("Phat Slug", R.drawable.phat);
        cars.put("Sprinter XL", R.drawable.sprinter);
        cars.put("Panga", R.drawable.panga);
        cars.put("Pole Poz", R.drawable.pole);
        cars.put("Zipper", R.drawable.zipper);
        cars.put("Panga TC", R.drawable.panga_tc);
        cars.put("Volken Turbo", R.drawable.volken);
        cars.put("Pest Control", R.drawable.pest);
        cars.put("RC Bandit", R.drawable.rc);
        cars.put("Rotor", R.drawable.rotor);
        cars.put("Toyeca", R.drawable.toyeca);
        cars.put("RC San", R.drawable.rc_san);
        cars.put("Probe UFO", R.drawable.ufo);
    }

    public static StockCarImages getInstance() {
        return INSTANCE;
    }

    public HashMap<String, Integer> getCarsImgs() {
        return cars;
    }

}
