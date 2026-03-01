
//It implements the Spring Security interface UserDetailsService

import org.springframework.stereotype.Service;

import com.example.assessmentapplication.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override // Optional annotation. But best practice.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

}