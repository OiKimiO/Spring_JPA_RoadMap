package org.hellojpa;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em     = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "select m from Member m left join Team t on m.username = t.name";
            List<Member> result = em.createQuery(query, Member.class).getResultList();
            System.out.println(result);


            /*List<MemberDto> resultList = em.createQuery("select new jpql.MemberDto(m.username,m.age) from Member as m", MemberDto.class)
                                        .getResultList();*/

            // Member member1 = resultList.get(0);

            //  = query.getResultList();
            // 결과가 없으면 빈 리스트로 반환함

            // 결과가 정확히 하나가 안나오면 Exception 처리됨
            //Member singleResult = query.getSingleResult();

            // Spring Data JPA ->
            //System.out.println("singleResult = " + singleResult);

            tx.commit();
        }catch(Exception e){
            tx.rollback();
        }finally {
            em.close();
        }
    }


}