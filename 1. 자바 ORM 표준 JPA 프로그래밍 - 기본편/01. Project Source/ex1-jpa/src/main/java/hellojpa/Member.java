package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Embedded
    private Address homeAddress;

    // 값 타입 컬렉션은 영속성 + 고아 객체 제거 기능이 필수로 가집니다.
    @ElementCollection
    @CollectionTable(name="FAVORITE_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name="FOOD_NAME")
    private Set<String> favoritesFoods = new HashSet<>();

    /*@ElementCollection
    @CollectionTable(name="ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    private List<Address> addressHistory = new ArrayList<>();*/

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

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

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoritesFoods() {
        return favoritesFoods;
    }

    public void setFavoritesFoods(Set<String> favoritesFoods) {
        this.favoritesFoods = favoritesFoods;
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }
}