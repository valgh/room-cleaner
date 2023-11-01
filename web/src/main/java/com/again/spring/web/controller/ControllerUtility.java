package com.again.spring.web.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class ControllerUtility {

    public static ResponseEntity buildResponse(List result) {
        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    public static <T> ResponseEntity buildResponse(Optional<T> result) {
        if (result.isPresent()) {
            T content = result.get();
            if (content instanceof List) {
                return buildResponse((List) content);
            }
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.notFound().build();
    }
}
