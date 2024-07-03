package beans;

import java.time.LocalDate;
import java.util.Objects;

import beans.enums.Role;

public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String gender;
    private LocalDate birthDate;
    private Role role;
    private boolean blocked; // Novo polje za blokiranje korisnika

    public User() {
        super();
    }

    public User(int id, String username, String password, String name, String surname, String gender,
                LocalDate birthDate, Role role, boolean blocked) { // Ažurirani konstruktor
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.birthDate = birthDate;
        this.role = role;
        this.blocked = blocked; // Inicijalizacija novog polja
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isBlocked() { // Getter za blocked polje
        return blocked;
    }

    public void setBlocked(boolean blocked) { // Setter za blocked polje
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", name=" + name + ", surname="
                + surname + ", gender=" + gender + ", birthDate=" + birthDate + ", role=" + role + ", blocked=" + blocked + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthDate, gender, id, name, password, role, surname, username, blocked);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(birthDate, other.birthDate) && Objects.equals(gender, other.gender) && id == other.id
                && Objects.equals(name, other.name) && Objects.equals(password, other.password)
                && Objects.equals(surname, other.surname) && Objects.equals(username, other.username)
                && role == other.role && blocked == other.blocked;
    }
}
