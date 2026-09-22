package com.compa.repository;

import com.compa.model.ConsentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentTemplateRepository extends JpaRepository<ConsentTemplate, Long>
{


}
