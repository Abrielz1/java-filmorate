package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {

    String error;

    String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
//
//    public String getError() {
//        return error;
//    }
//
//    public String getDescription() {
//        return description;
//    }
}
