package com.labourlink.service;

import com.labourlink.entity.Favorite;
import com.labourlink.entity.User;
import com.labourlink.repository.FavoriteRepository;
import com.labourlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public List<Favorite> getFavorites() {
        return favoriteRepository.findByContractor(getCurrentUser());
    }

    @Transactional
    public Favorite addFavorite(Long workerId) {
        User contractor = getCurrentUser();
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        if (favoriteRepository.existsByContractorAndWorker(contractor, worker)) {
            throw new IllegalStateException("Already in favorites");
        }
        return favoriteRepository.save(
                Favorite.builder().contractor(contractor).worker(worker).build());
    }

    @Transactional
    public void removeFavorite(Long workerId) {
        User contractor = getCurrentUser();
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        Favorite fav = favoriteRepository.findByContractorAndWorker(contractor, worker)
                .orElseThrow(() -> new IllegalArgumentException("Not in favorites"));
        favoriteRepository.delete(fav);
    }
}
