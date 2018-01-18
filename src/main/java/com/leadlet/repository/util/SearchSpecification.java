package com.leadlet.repository.util;

import com.leadlet.domain.AppAccount;
import org.springframework.data.jpa.domain.Specification;

import javax.management.Descriptor;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Enumeration;

public class SearchSpecification<T> implements Specification<T> {

    private SearchCriteria criteria;


    public SearchSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
      (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            }else if (root.get(criteria.getKey()).getJavaType() == Long.class){
                return builder.equal(
                    root.<String>get(criteria.getKey()), criteria.getValue() );
                //TODO ygokirmak fix below specific type
            }else if (root.get(criteria.getKey()).getJavaType() == AppAccount.class){
                return builder.equal(
                    root.<String>get(criteria.getKey()), criteria.getValue() );
            }
            else {
                Class c = root.get(criteria.getKey()).getJavaType();
                return builder.equal(root.get(criteria.getKey()), Enum.valueOf(c,criteria.getValue().toString()));
            }
        }
        return null;
    }
}
