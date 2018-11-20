package com.truenorth.campsite.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.truenorth.campsite.models.Reservations;

public interface ReservationsRepository extends MongoRepository<Reservations, String> {
	Reservations findBy_id(ObjectId _id);

}
