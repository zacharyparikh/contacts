package dev.parikh.contact;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@Entity
@ToString
@EqualsAndHashCode
public class Contact {

    @Id
    @GeneratedValue
    private Long id;
    private String first;
    private String last;
    private String phone;
    private String email;

    public Errors getErrors() {
        return new Errors("", "", "", "");
    }

    public Contact() {

    }

    @Builder
    public Contact(String first, String last, String phone, String email) {
        this.first = first;
        this.last = last;
        this.phone = phone;
        this.email = email;
    }

    public record Errors(String first, String last, String phone, String email) {}

}
