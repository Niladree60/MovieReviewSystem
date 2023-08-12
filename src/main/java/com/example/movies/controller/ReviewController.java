package com.example.movies.controller;

import com.example.movies.MovieNotFoundException;
import com.example.movies.model.Review;
import com.example.movies.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")

public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Map<String , String> payload) {
        return new ResponseEntity<Review>(reviewService.createReview(payload.get("reviewBody"),payload.get("imdbId") ) , HttpStatus.CREATED);
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<List<Review>> readReviews(@PathVariable String imdbId) {
        List<Review> reviews = reviewService.readReviews(imdbId);
        return ResponseEntity.status(reviews.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK).body(reviews);
    }

    @PutMapping("/update/{imdbId}")
    public ResponseEntity<Review> updateReview(@PathVariable String imdbId, @RequestBody Map<String, String> payload) {
        String updatedReviewBody = payload.get("reviewBody");

        try {
            Review updatedReview = reviewService.updateReview(imdbId, updatedReviewBody);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{imdbId}/reviews")
    public ResponseEntity<String> deleteReview(@PathVariable String imdbId) {
        try {
            reviewService.deleteReviews(imdbId);
            return ResponseEntity.ok("All reviews for IMDb ID " + imdbId + " have been deleted.");
        } catch (MovieNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }


}
