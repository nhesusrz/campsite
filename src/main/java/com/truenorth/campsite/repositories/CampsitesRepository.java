package com.truenorth.campsite.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.truenorth.campsite.models.Campsites;

@EnableMongoAuditing
public interface CampsitesRepository extends MongoRepository<Campsites, String> {
	Campsites findBy_id(ObjectId _id);
}
