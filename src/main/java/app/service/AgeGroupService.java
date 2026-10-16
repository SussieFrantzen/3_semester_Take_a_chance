package app.service;

public class AgeGroupService {

    public String getAgeGroup(int age) {
        if (age < 6) {
            throw new IllegalArgumentException(
                    "Spilleren skal være mindst 6 år."
            );
        }

        if (age <= 9) return "6-9";
        if (age <= 12) return "10-12";
        if (age <= 15) return "13-15";

        return "16+";
    }
}