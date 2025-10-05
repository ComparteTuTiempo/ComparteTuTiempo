package com.compartetutiempo.backend.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.compartetutiempo.backend.model.enums.Role;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    @Email
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column
    private LocalDate fechaNacimiento;

    @Column(length = 500)
    private String biografia;

    @Column
    private String fotoPerfil;

    @Column(name = "numero_horas", precision = 2)
    @PositiveOrZero
    private Double numeroHoras = 0.0;

    @Column
    private String ubicacion;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private String metodoAutenticacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private Set<Role> roles = new HashSet<>(Set.of(Role.USER));

    @Column(nullable = false)
    private boolean verificado = false; // verificación distinta del ban

    @Transient
    private List<GrantedAuthority> authorities; // no persistido en BD

    // ---- Métodos UserDetails ----
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return correo;
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
        return activo; // 👈 ahora depende del campo "activo"
    }
}
