package com.compa.repository;

import com.compa.model.RuleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleTemplateRepository extends JpaRepository<RuleTemplate, Long> {


}
