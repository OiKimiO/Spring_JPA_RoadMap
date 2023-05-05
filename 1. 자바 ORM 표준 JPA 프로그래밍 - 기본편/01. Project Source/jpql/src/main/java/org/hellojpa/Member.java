package org.hellojpa;

import javax.persistence.*;

@Entity
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"

)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MEMBER_ID")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Member(Long id, String username, int age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public Member() {
    }

    @Override
    public String toString() {
        return "Member{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
