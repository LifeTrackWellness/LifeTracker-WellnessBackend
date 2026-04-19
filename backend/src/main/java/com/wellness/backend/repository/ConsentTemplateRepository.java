package com.wellness.backend.repository;

import com.wellness.backend.model.ConsentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentTemplateRepository extends JpaRepository<ConsentTemplate, Long>
{


}
