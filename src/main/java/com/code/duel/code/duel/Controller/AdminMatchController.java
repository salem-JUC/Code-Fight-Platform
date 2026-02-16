package com.code.duel.code.duel.Controller;

import com.code.duel.code.duel.DTO.MatchDTO.MatchWithPlayersDTO;
import com.code.duel.code.duel.Model.Match;
import com.code.duel.code.duel.Repository.MatchRepo;
import com.code.duel.code.duel.Repository.UserPlayMatchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/matches")
public class AdminMatchController {

    @Autowired
    MatchRepo matchRepo;

    @Autowired
    UserPlayMatchRepo userPlayMatchRepo;

    @GetMapping
    public ResponseEntity<List<MatchWithPlayersDTO>> getAllMatches() {
        return ResponseEntity.ok(matchRepo.findAllWithPlayers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        userPlayMatchRepo.deleteByMatchId(id);
        matchRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

