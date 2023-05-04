package org.hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class Main {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em     = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

}