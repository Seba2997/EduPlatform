package com.eduplatform.apiUsuario.repositories;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eduplatform.apiUsuario.models.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findByEmail(String email);

    List<User> findByActive(Boolean active);
    
}
