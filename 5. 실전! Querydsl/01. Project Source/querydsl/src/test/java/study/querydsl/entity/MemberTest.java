package study.querydsl.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    public void testEntity(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member user1 = new Member("member1", 10, teamA);
        Member user2 = new Member("member2", 20, teamA);

        Member user3 = new Member("member3", 30, teamB);
        Member user4 = new Member("member4", 40, teamB);

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);

        /*
        em.flush();
        em.clear();

        List<Member> selectMFromMemberM = em.createQuery("select m from Member m",Member.class).getResultList();
        for (Member o : selectMFromMemberM) {
            System.out.println("o = " + o);
        }
        */

    }

    @Test
    public void startJPQL(){
        // member1을 찾아라
        Member findMember = em.createQuery("select m from Member m where m.username = :username",Member.class)
                              .setParameter("username", "member1")
                              .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}