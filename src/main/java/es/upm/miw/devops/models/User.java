package es.upm.miw.devops.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "family_name", nullable = false)
    private String familyName;

    @Column(name = "email")
    private String email;

    @Column(name = "identity")
    private String identity;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private Boolean active;

    public User() {
        // Empty for framework/deserialization
    }

    @SuppressWarnings("java:S107")
    public User(String id, String firstName, String familyName, String email, String identity,
                String address, String city, String province, String postalCode, Role role, Boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
        this.email = email;
        this.identity = identity;
        this.address = address;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.role = role;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", email='" + email + '\'' +
                ", identity='" + identity + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
