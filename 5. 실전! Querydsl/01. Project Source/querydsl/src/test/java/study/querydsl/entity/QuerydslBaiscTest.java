package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBaiscTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory query;

    @BeforeEach
    public void testEntity(){
        query = new JPAQueryFactory(em);
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


    }

    @Test
    public void startQuerydsl(){
        // member1을 찾아라
        Member findMember = query.select(member)
                                 .from(member)
                                 .where(member.username.eq("member1")) // 파라미터 바인딩 처리
                                 .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search(){
        // member1을 찾아라
        Member findMember = query.select(member)
                                 .from(member)
                                 .where(member.username.eq("member1"),
                                        member.age.eq(10)) // 파라미터 바인딩 처리
                                 .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch(){
        query.selectFrom(member).fetch();
        // query.selectFrom(member).fetchOne();
        query.selectFrom(member).fetchFirst();
        QueryResults<Member> memberQueryResults = query.selectFrom(member).fetchResults();
        memberQueryResults.getTotal();
        List<Member> results = memberQueryResults.getResults();
        long count = query.selectFrom(member).fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순
     * 2. 회원 이름 오름차순
     * 단 2에서 회원 이름이 없으면 마지막에 출력
     * */
    @Test
    public void sort(){
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> fetch = query.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = fetch.get(0);
        Member member6 = fetch.get(1);
        Member memberNull = fetch.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging1(){
        List<Member> fetch = query.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(fetch.size()).isEqualTo(2);
    }

    @Test
    public void paging2(){
        QueryResults<Member> memberQueryResults = query.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(memberQueryResults.getTotal()).isEqualTo(4);
        assertThat(memberQueryResults.getLimit()).isEqualTo(2);
        assertThat(memberQueryResults.getOffset()).isEqualTo(1);
        assertThat(memberQueryResults.getResults().size()).isEqualTo(2);
    }

    @Test
    public void aggregation(){
        List<Tuple> fetch = query.select(member.count(),
                                         member.age.sum(),
                                         member.age.avg(),
                                         member.age.max(),
                                         member.age.min())
                                 .from(member)
                                 .fetch();

        Tuple tuple = fetch.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @Test
    public void group(){
        List<Tuple> fetch = query.select(team.name, member.age.avg())
                                 .from(member)
                                 .join(member.team, team)
                                 .groupBy(team.name)
                                 .fetch();

        Tuple tupleA = fetch.get(0);
        Tuple tupleB = fetch.get(1);

        assertThat(tupleA.get(team.name)).isEqualTo("teamA");
        assertThat(tupleA.get(member.age.avg())).isEqualTo(15);

        assertThat(tupleB.get(team.name)).isEqualTo("teamB");
        assertThat(tupleB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    public void join(){
        List<Member> fetch = query.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(fetch)
                .extracting("username")
                .containsExactly("member1","member2");

    }
}
