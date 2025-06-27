package com.t1tanic.homebrew.plex.model.enums;

import lombok.Getter;

@Getter
public enum Country {
    USA("United States"),
    UK("United Kingdom"),
    CANADA("Canada"),
    AUSTRALIA("Australia"),
    GERMANY("Germany"),
    FRANCE("France"),
    JAPAN("Japan"),
    SOUTH_KOREA("South Korea"),
    INDIA("India"),
    CHINA("China"),
    BRAZIL("Brazil"),
    ITALY("Italy"),
    SPAIN("Spain"),
    MEXICO("Mexico"),
    RUSSIA("Russia"),
    NETHERLANDS("Netherlands"),
    SWEDEN("Sweden"),
    NORWAY("Norway"),
    DENMARK("Denmark"),
    FINLAND("Finland"),
    BELGIUM("Belgium"),
    SWITZERLAND("Switzerland"),
    AUSTRIA("Austria"),
    POLAND("Poland"),
    TURKEY("Turkey"),
    ARGENTINA("Argentina"),
    SOUTH_AFRICA("South Africa"),
    NEW_ZEALAND("New Zealand"),
    IRELAND("Ireland"),
    PORTUGAL("Portugal"),
    GREECE("Greece"),
    COLOMBIA("Colombia"),
    PERU("Peru"),
    CHILE("Chile"),
    PHILIPPINES("Philippines"),
    INDONESIA("Indonesia"),
    THAILAND("Thailand"),
    MALAYSIA("Malaysia"),
    VIETNAM("Vietnam"),
    MONGOLIA("Mongolia"),
    NORTH_KOREA("North Korea");

    private final String fullName;

    Country(String fullName) {
        this.fullName = fullName;
    }

}
