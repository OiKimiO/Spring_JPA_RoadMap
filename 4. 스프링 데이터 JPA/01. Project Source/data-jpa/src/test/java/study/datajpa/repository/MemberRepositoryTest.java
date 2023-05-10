package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class MemberRepositoryTest {


    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private EntityManager em;
    @Test

    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy(){
        Member m1 = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertThat(findMember.getUsername()).isEqualTo("AAA");
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",20);
        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testQuery2(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void testQuery3(){
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);


        Member m1 = new Member("AAA", 20, teamA);
        memberRepository.save(m1);

        List<MemberDto> usernameList = memberRepository.findMemberDto();
        for (MemberDto memberDto : usernameList) {
            System.out.println("memberDto = " + memberDto);
        }

    }

    @Test
    public void testQuery4(){
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);


        Member m1 = new Member("AAA", 20, teamA);
        memberRepository.save(m1);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member Member : byNames) {
            System.out.println("Member = " + Member);
        }

    }

    @Test
    public void testQuery5(){
        Member m1 = new Member("AAA", 20);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> result = memberRepository.findOptionalByUsername("AAA");
        System.out.println("result = " + result);
    }

    @Test
    public void paging(){
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age = 10;

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));

        // then
        List<Member> content = page.getContent();

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 22));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println(member);

        // then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        // given
        // member1 ->
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 19, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> all = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : all) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint(){
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");

        em.flush();
        // then
    }

    @Test
    public void lock(){
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> lockByUsername = memberRepository.findLockByUsername(member1.getUsername());

        // then
    }

    @Test
    public void callCustom(){
        // given
        List<Member> memberCustom = memberRepository.findMemberCustom();

    }

    @Test
    public void specBasic(){
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List all = memberRepository.findAll(spec);
        Assertions.assertThat(all.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample(){
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        // Probe 생성
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");


        Example<Member> example = Example.of(member,matcher);

        List<Member> all = memberRepository.findAll(example);

        // then
        Assertions.assertThat(all.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    public void projections(){
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        List<NestedClosedProjections> m1 = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : m1) {
            System.out.println(nestedClosedProjections.getUsername());
            System.out.println(nestedClosedProjections.getTeam());
            System.out.println("nestedClosedProjections = " + nestedClosedProjections);
        }
    }

    @Test
    public void nativeQuery(){
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("m1", 0, teamA);
        Member member2 = new Member("m2", 0, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Page<MemberProjection> byNativeProjection = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = byNativeProjection.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }


}
