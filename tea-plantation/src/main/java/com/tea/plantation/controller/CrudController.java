package com.tea.plantation.controller;

import com.tea.plantation.domain.Identifiable;
import com.tea.plantation.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;


@RequiredArgsConstructor
public abstract class CrudController<Dto extends Identifiable<DtoId>, DtoId> {
    private final MongoRepository<Dto, DtoId> repo;

    @GetMapping("/{id}")
    public ResponseEntity<Dto> getById(@PathVariable DtoId id) {
        var dto = repo.findById(id).orElseThrow(() -> new EntityNotFoundException(location(id)));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<Dto> create(@Validated @RequestBody Dto dto) {
        var saved = repo.save(dto);
        return ResponseEntity.created(location(saved.getId())).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable DtoId id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public abstract String location();

    public URI location(DtoId id) {
        return URI.create(location() + "/" + id);
    }
}
