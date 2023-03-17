package com.tea.common.controller;

import com.tea.common.domain.Identifiable;
import com.tea.common.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;


@RequiredArgsConstructor
public abstract class CrudController<Dto extends Identifiable<DtoId>, DtoId,
                                     Service extends BasicService<?, Dto, DtoId, ?>> {
    protected final Service service;

    @GetMapping("/{id}")
    public ResponseEntity<Dto> getById(@PathVariable DtoId id) {
        Dto dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<Dto> create(@Validated @RequestBody Dto dto) {
        Dto saved = service.create(dto);
        return ResponseEntity.created(location(saved.getId())).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable DtoId id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    public abstract String location();

    public URI location(DtoId id) {
        return URI.create(location() + "/" + id);
    }
}
