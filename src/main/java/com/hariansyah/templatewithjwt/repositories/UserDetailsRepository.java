package com.hariansyah.templatewithjwt.repositories;

import com.hariansyah.templatewithjwt.entities.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, String> {
}