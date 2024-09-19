    package org.grupouno.parking.it4.model;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.util.Collection;
    import java.util.List;


    @Entity
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Table(name = "\"user\"")
    @ToString
    public class User implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        private long userId;
        private String name;
        private String surname;
        private long age;
        private String dpi;
        private String email;
        private String password;
        private boolean status;
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
        private Profile idProfile;

        @Transient
        private Collection<? extends GrantedAuthority> authorities;
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities != null ? authorities : List.of();
        }
        
        public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        public Long getUserId() {
            return userId;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId=" + userId +
                    ", name='" + name + '\'' +
                    ", surname='" + surname + '\'' +
                    ", age=" + age +
                    ", dpi='" + dpi + '\'' +
                    ", email='" + email + '\'' +
                    ", status=" + status +
                    ", idProfile=" + (idProfile != null ? idProfile.getProfileId() : "null") +
                    '}';
        }
    }
