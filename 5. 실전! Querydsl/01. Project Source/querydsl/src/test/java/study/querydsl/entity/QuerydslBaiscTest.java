package study.querydsl.entity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
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

    /**
     * 팀의 이름과 팀의 평균 연령을 구해라.
     * */
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

    /**
     * 팀 A에 소속된 모든 회원을 조회하라
     * */
    @Test
    public void join(){
        List<Member> fetch = query.selectFrom(member)
                                  .leftJoin(member.team, team)
                                  .where(team.name.eq("teamA"))
                                  .fetch();

        assertThat(fetch)
                .extracting("username")
                .containsExactly("member1","member2");
    }

    /**
     * 세타 조인
     * 회원의 이름이 팀이름과 같은 회원 조회
     * */
    @Test
    public void theta_join(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> fetch = query.select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(fetch).extracting("username")
                .containsExactly("teamA","teamB");
    }

    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조회, 회원은 모두 조회
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     * */
    @Test
    public void join_on_filtering(){
        List<Tuple> teamA = query.select(member, team)
                                 .from(member)
                                 .join(member.team, team).where(team.name.eq("teamA"))
                                 //.join(member.team, team).on(team.name.eq("teamA")) 위와 같음
                                 .fetch();

        for (Tuple tuple : teamA) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름이 팀이름과 같은 회원 외부조인
     * */
    @Test
    public void join_on_no_relation(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> fetch = query.select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Autowired
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo(){
        em.flush();
        em.clear();

        Member findMember = query.selectFrom(member)
                              .where(member.username.eq("member1"))
                              .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUse(){
        em.flush();
        em.clear();

        Member findMember = query.selectFrom(member)
                                 .join(member.team, team).fetchJoin()
                                 .where(member.username.eq("member1"))
                                 .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원을 조회
     * */
    @Test
    public void subQuery(){
        QMember memberSub = new QMember("memberSub");

        List<Member> fetch = query.selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(fetch).extracting("age").containsExactly(40);
    }

    /**
     * 나이가 평균 이상인 회원을 조회
     * */
    @Test
    public void subQueryGoe(){
        QMember memberSub = new QMember("memberSub");

        List<Member> fetch = query.selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(fetch).extracting("age").containsExactly(30, 40);
    }

    /**
     * 나이가 10 이상인 회원을 조회
     * */
    @Test
    public void subQueryIn(){
        QMember memberSub = new QMember("memberSub");

        List<Member> fetch = query.selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(fetch).extracting("age").containsExactly(20, 30, 40);
    }

    @Test
    public void selectSubQuery(){
        QMember memberSub = new QMember("memberSub");

        List<Tuple> fetch = query.select(member.username,select(memberSub.age.avg()).from(memberSub))
                                 .from(member)
                                 .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void basicCase(){
        List<String> fetch = query.select(member.age.when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase(){
        List<String> fetch = query.select(new CaseBuilder()
                                            .when(member.age.between(0, 20)).then("0~20살")
                                            .when(member.age.between(21, 30)).then("21~30살")
                                            .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void constant(){
        List<Tuple> a = query.select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : a) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void concat(){
        List<String> fetch = query.select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void simpleProjections(){
        List<String> fetch = query.select(member.username)
                .from(member)
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void tupleProjections(){
        List<Tuple> fetch = query.select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);

            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }

    @Test
    public void findDtoByJPQL(){
        List<MemberDto> resultList = em.createQuery("select new study.querydsl.dto.MemberDto(m.username,m.age) from Member m", MemberDto.class).getResultList();

        for (MemberDto member1 : resultList) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void findDtoByQuerydsl(){
        List<MemberDto> fetch = query.select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : fetch) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findDtoByField(){
        List<MemberDto> fetch = query.select(Projections.fields(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : fetch) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findDtoByConstructor(){
        List<MemberDto> fetch = query.select(Projections.constructor(MemberDto.class, member.username, member.age))
                                     .from(member)
                                     .fetch();

        for (MemberDto memberDto : fetch) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findUserDto(){
        QMember memberSub = new QMember("memberSub");
        List<UserDto> fetch = query.select(Projections.fields(UserDto.class,
                                                              member.username.as("name"),
                                                              ExpressionUtils.as(JPAExpressions.select(memberSub.age.max())
                                                                                                .from(memberSub),"age")
                ))
                .from(member)
                .fetch();
        for (UserDto userDto : fetch) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void findDtoConstructor(){
        QMember memberSub = new QMember("memberSub");
        List<UserDto> fetch = query.select(Projections.constructor(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(JPAExpressions.select(memberSub.age.max())
                                .from(memberSub),"age")
                ))
                .from(member)
                .fetch();
        for (UserDto userDto : fetch) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void findDtoByQueryProjections(){
        List<MemberDto> fetch = query.select(new QMemberDto(member.username, member.age))
                                     .from(member)
                                     .fetch();

        for (MemberDto memberDto : fetch) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void dynamicQuery_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember1(usernameParam,ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameParam));

        if(usernameParam != null){
            builder.and(member.username.eq(usernameParam));
        }

        if(ageParam != null){
            builder.and(member.age.eq(ageParam));
        }

        return query.selectFrom(member)
                    .where(builder)
                    .fetch();

    }

    // 광고상태 isValid, 날짜가 IN: isServicable

    @Test
    public void dynamicQuery_WhereParam(){
        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember2(usernameParam,ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return query.selectFrom(member)
                    // .where(usernameEq(usernameParam), ageEq(ageParam))
                    .where(allEq(usernameParam,ageParam))
                    .fetch();
    }

    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }

    private BooleanExpression usernameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }

    private BooleanExpression allEq(String username, Integer age){
        return usernameEq(username).and(ageEq(age));
    }

    @Test
    public void bulkUpdate(){
        // member1 = 10 -> member1
        // member2 = 20 -> member2
        // member3 = 30 -> member3
        // member4 = 40 -> member4

        long count = query.update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        // member1 = 10 -> 비회원
        // member2 = 20 -> 비회원
        // member3 = 30 -> member3
        // member4 = 40 -> member4
        em.flush();
        em.clear();

        List<Member> fetch = query.selectFrom(member).fetch();

        for (Member fetch1 : fetch) {
            System.out.println(fetch1);
        }

        System.out.println("count = " + count);
    }

    @Test
    public void bulkAdd(){
        long execute = query.update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }

    @Test
    public void bulkMulti(){
        long execute = query.update(member)
                .set(member.age, member.age.multiply(2))
                .execute();
    }

    @Test
    public void bulkDelete(){
        long execute = query.delete(member)
                .where(member.age.gt(18))
                .execute();
    }

    @Test
    public void sqlFunction(){
        List<String> fetch = query.select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                                                               member.username,
                                                                     "member",
                                                                     "M"))
                .from(member)
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void SqlFunction2(){
        List<String> fetch = query.select(member.username)
                .from(member)
                // .where(member.username.eq(Expressions.stringTemplate("function('lower',{0})", member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
    }
}
