package com.hubert.authResource.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
	   @GetMapping
	    @PreAuthorize("hasRole('ROLE_USER')")
	    public ResponseEntity<Principal> get(final Principal principal) {
	        return ResponseEntity.ok(principal);
	    }
}
