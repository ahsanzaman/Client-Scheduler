package model;

public class Country {
    private long countryId;
    private String country;

    public Country(long countryId, String country) {
        this.countryId = countryId;
        this.country = country;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
