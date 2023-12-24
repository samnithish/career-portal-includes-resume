package com.sam.careerportal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long>{
	
	public Candidate findByCandidateEmail(String value);

}
