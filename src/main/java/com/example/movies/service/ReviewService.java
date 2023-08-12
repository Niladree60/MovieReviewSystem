package com.example.movies.service;

import com.example.movies.MovieNotFoundException;
import com.example.movies.MovieRepository;
import com.example.movies.model.Review;
import com.example.movies.ReviewRepository;
import com.example.movies.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;


import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MovieRepository movieRepository;


    public Review createReview(String reviewBody, String imdbId) {
        Review review = reviewRepository.insert(new Review(reviewBody));

        mongoTemplate.update(Movie.class)
                .matching(Criteria.where("imdbId").is(imdbId))
                .apply(new Update().push("reviewIds").value(review))
                .first();

        return review;
    }

    public List<Review> readReviews(String imdbId) {
        Movie movie = mongoTemplate.findOne(Query.query(Criteria.where("imdbId").is(imdbId)), Movie.class);

        if (movie != null && movie.getReviewIds() != null && !movie.getReviewIds().isEmpty()) {

            return movie.getReviewIds();
        }

        return new ArrayList<>();
    }

    public Review updateReview(String imdbId, String updatedReviewBody) {
        Movie movie = mongoTemplate.findOne(Query.query(Criteria.where("imdbId").is(imdbId)), Movie.class);

        if (movie != null && movie.getReviewIds() != null && !movie.getReviewIds().isEmpty()) {
            Review review = movie.getReviewIds().get(0);
            review.setBody(updatedReviewBody);

            mongoTemplate.save(movie);
        } else {
            throw new RuntimeException("Movie not found with IMDb ID: " + imdbId);
        }
        return null;
    }


    public void deleteReviews(String imdbId) {
        Movie movie = movieRepository.findMovieByImdbId(imdbId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with IMDb ID: " + imdbId));

        List<Review> reviews = movie.getReviewIds();
        for (Review review : reviews) {
            reviewRepository.delete(review);
        }

        reviews.clear();
    }

}
