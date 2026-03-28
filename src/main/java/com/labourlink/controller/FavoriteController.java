package com.labourlink.controller;

import com.labourlink.entity.Favorite;
import com.labourlink.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONTRACTOR')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getFavorites());
    }

    @PostMapping("/{workerId}")
    public ResponseEntity<Favorite> add(@PathVariable Long workerId) {
        return ResponseEntity.ok(favoriteService.addFavorite(workerId));
    }

    @DeleteMapping("/{workerId}")
    public ResponseEntity<Void> remove(@PathVariable Long workerId) {
        favoriteService.removeFavorite(workerId);
        return ResponseEntity.noContent().build();
    }
}
