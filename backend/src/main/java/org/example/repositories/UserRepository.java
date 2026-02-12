package org.example.repositories;

import org.example.models.User;
import org.example.repositories.projections.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query (" SELECT u FROM User u " )
    public List<User> getAllUsers();

//    @Query ()
//    public Boolean userRestaurantExist(Long restaurantId);

    @Query("SELECT u.id AS id, " +
            "u.email AS email, " +
            "u.password AS password, " +
            "u.role AS role, " +
            "u.name AS name, " +
            "u.lastName AS lastName, " +
            "r.id AS restaurantId " +
            "FROM User u " +
            "LEFT JOIN u.restaurant r " +
            "WHERE u.email = :email")
    Optional<UserProjection> getUserByEmail(@Param("email") String email);


    boolean existsUserByEmail(String email);
}
