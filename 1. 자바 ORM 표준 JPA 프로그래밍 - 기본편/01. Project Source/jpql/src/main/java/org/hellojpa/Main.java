package org.hellojpa;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em     = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try{
            Team team1 = new Team();
            team1.setName("팀A");
            em.persist(team1);

            Team team2 = new Team();
            team2.setName("팀B");
            em.persist(team2);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(team1);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(team1);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(team2);
            em.persist(member3);

            em.flush();
            em.clear();

            // String query = "select m from Member m where m.team = :team";

            int resultCount = em.createQuery("update Member m set m.age = 20").executeUpdate();

            System.out.println(resultCount);



            /*
            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                                        .setParameter("username","회원1")
                                        .getResultList();

            for (Member member : resultList) {
                System.out.println(member);
            }

            System.out.println("result = " + resultList.size());

            for (Team team : resultList) {
                System.out.println("team = " + team.getName() + " |members = " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println(" -> member = " + member);
                }
            }*/

            tx.commit();
        }catch(Exception e){
            tx.rollback();
            e.printStackTrace();
        }finally {
            em.close();
        }
        emf.close();
    }


}